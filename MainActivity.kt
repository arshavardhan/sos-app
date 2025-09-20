package com.example.shaketosos

import android.content.Intent
import android.os.Bundle
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    private val CHANNEL = "shaketosos/foreground"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "setServiceEnabled" -> {
                    val enabled = (call.argument<Boolean>("enabled") ?: true)
                    val intent = Intent(this, ShakeService::class.java)
                    intent.putExtra("enabled", enabled)
                    if (enabled) {
                        startForegroundService(intent)
                    } else {
                        stopService(intent)
                    }
                    result.success(null)
                }
                else -> result.notImplemented()
            }
        }
    }
}
