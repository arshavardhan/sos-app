package com.example.shaketosos

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlin.math.sqrt

class ShakeService : Service(), SensorEventListener, LocationListener {
    private val TAG = "ShakeService"
    private lateinit var sensorManager: SensorManager
    private var lastShakeTimestamps = LongArray(0)
    private var shakeCount = 0
    private var firstShakeTime = 0L
    private lateinit var locManager: LocationManager
    private var currentLocation: Location? = null

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL)
        locManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000L, 1f, this)
        } catch (ex: SecurityException) {
            Log.w(TAG, "Location permission missing: ${'$'}ex")
        }
        startInForeground()
    }

    private fun startInForeground() {
        val channelId = "shaketosos_channel"
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(channelId, "ShakeToSOS", NotificationManager.IMPORTANCE_LOW)
            nm.createNotificationChannel(ch)
        }
        val intent = Intent(this, MainActivity::class.java)
        val pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notif = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Shake-to-SOS is active")
            .setContentText("Monitoring device shakes")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentIntent(pending)
            .setOngoing(true)
            .build()
        startForeground(101, notif)
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        try { locManager.removeUpdates(this) } catch (e: Exception) {}
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onSensorChanged(event: SensorEvent) {
        val ax = event.values[0].toDouble()
        val ay = event.values[1].toDouble()
        val az = event.values[2].toDouble()
        val g = sqrt(ax*ax + ay*ay + az*az) / 9.81
        // simple threshold
        if (g > 2.2) {
            val now = SystemClock.elapsedRealtime()
            if (firstShakeTime == 0L) firstShakeTime = now
            shakeCount++
            if (now - firstShakeTime <= 5000 && shakeCount >= 3) {
                // trigger SOS
                sendSOS()
                // reset
                shakeCount = 0
                firstShakeTime = 0L
            } else if (now - firstShakeTime > 5000) {
                // reset window
                firstShakeTime = now
                shakeCount = 1
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun sendSOS() {
        val lat = currentLocation?.latitude ?: 0.0
        val lon = currentLocation?.longitude ?: 0.0
        val mapsLink = "https://maps.google.com?q=${'$'}lat,${'$'}lon"
        val message = "SOS! I need help. Location: ${'$'}mapsLink"
        // Open SMS app (prefilled) - note: launching activities from service requires FLAG_ACTIVITY_NEW_TASK
        val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = android.net.Uri.parse("smsto:") // no number prefilled
            putExtra("sms_body", message)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            startActivity(smsIntent)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to open SMS app: ${'$'}e")
        }
        // WhatsApp intent (if installed)
        val waIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
            setPackage("com.whatsapp")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            startActivity(waIntent)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to open WhatsApp: ${'$'}e")
        }
    }

    // LocationListener
    override fun onLocationChanged(location: Location) {
        currentLocation = location
    }
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) {}
}
