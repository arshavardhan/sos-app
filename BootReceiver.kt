package com.example.shaketosos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action
        if (action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Boot completed - starting ShakeService")
            val serviceIntent = Intent(context, ShakeService::class.java)
            // Service will check SharedPreferences for enabled flag
            context.startForegroundService(serviceIntent)
        }
    }
}
