package com.example.dynamicisland

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import android.view.Gravity
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class IslandOverlayService : Service() {
    private lateinit var windowManager: WindowManager
    private var islandView: LinearLayout? = null
    private var expanded = false
    private var batteryText = "Battery ready"
    private var callText = "No active call"
    private var notificationText = "Waiting for notifications"
    private var mediaText = "No media playing"
    private var receiverRegistered = false

    private val systemReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_BATTERY_CHANGED -> {
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100).coerceAtLeast(1)
                    val percent = (level * 100 / scale).coerceIn(0, 100)
                    val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) != 0
                    batteryText = if (plugged) "Charging · $percent%" else "Battery · $percent%"
                }
                TelephonyManager.ACTION_PHONE_STATE_CHANGED -> {
                    callText = when (intent.getStringExtra(TelephonyManager.EXTRA_STATE)) {
                        TelephonyManager.EXTRA_STATE_RINGING -> "Incoming call"
                        TelephonyManager.EXTRA_STATE_OFFHOOK -> "Call in progress"
                        else -> "No active call"
                    }
                }
                ACTION_NOTIFICATION_UPDATE -> {
                    notificationText = intent.getStringExtra(EXTRA_NOTIFICATION_TEXT) ?: notificationText
                    mediaText = intent.getStringExtra(EXTRA_MEDIA_TEXT) ?: mediaText
                }
            }
            renderIsland()
        }
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
        startForeground(FOREGROUND_ID, buildForegroundNotification())
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
            addAction(ACTION_NOTIFICATION_UPDATE)
        }
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(systemReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            registerReceiver(systemReceiver, filter)
        }
        receiverRegistered = true
        val battery = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        battery?.let { systemReceiver.onReceive(this, it) }
        notificationText = IslandNotificationListenerService.latestNotification
        mediaText = IslandNotificationListenerService.latestMedia
        renderIsland()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        if (receiverRegistered) runCatching { unregisterReceiver(systemReceiver) }
        islandView?.let { view -> runCatching { windowManager.removeView(view) } }
        islandView = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun renderIsland() {
        if (!SettingsAccess.canDrawOverlays(this)) return
        val oldView = islandView
        if (oldView != null) {
            runCatching { windowManager.removeView(oldView) }
            islandView = null
        }

        val prefs = getSharedPreferences(PREFS, MODE_PRIVATE)
        val accent = if (prefs.getBoolean(KEY_VIOLET_ACCENT, false)) Color.rgb(182, 160, 255) else Color.rgb(184, 243, 107)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(18), dp(11), dp(18), dp(11))
            background = GradientDrawable().apply {
                setColor(Color.rgb(7, 8, 11))
                cornerRadius = dp(if (expanded) 28 else 40).toFloat()
                setStroke(dp(1), Color.rgb(49, 52, 64))
            }
            elevation = dp(12).toFloat()
        }
        val headline = TextView(this).apply {
            text = if (expanded) "●  DYNAMIC ISLAND" else "●"
            setTextColor(accent)
            textSize = if (expanded) 11f else 12f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }
        root.addView(headline)
        if (expanded) {
            addInfo(root, mediaText, Color.WHITE)
            addInfo(root, notificationText, Color.rgb(190, 194, 207))
            addInfo(root, callText, if (callText == "No active call") Color.rgb(140, 145, 163) else Color.rgb(255, 181, 171))
            addInfo(root, batteryText, accent)
            addInfo(root, "Tap to collapse", Color.rgb(140, 145, 163))
        }
        root.setOnClickListener {
            expanded = !expanded
            renderIsland()
        }
        val width = if (expanded) dp(318) else dp(78)
        val params = WindowManager.LayoutParams(
            width,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            android.graphics.PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = dp(8)
        }
        runCatching {
            windowManager.addView(root, params)
            islandView = root
        }.onFailure {
            stopSelf()
        }
    }

    private fun addInfo(parent: LinearLayout, value: String, color: Int) {
        parent.addView(TextView(this).apply {
            text = value.take(70)
            setTextColor(color)
            textSize = 12f
            gravity = Gravity.CENTER
            maxLines = 1
            setPadding(0, dp(4), 0, dp(2))
        })
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Dynamic Island", NotificationManager.IMPORTANCE_LOW).apply {
                description = "Keeps the floating Dynamic Island active"
                setShowBadge(false)
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun buildForegroundNotification(): Notification {
        val openApp = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else 0
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Dynamic Island is active")
            .setContentText("Tap the floating island to expand or collapse")
            .setContentIntent(openApp)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    companion object {
        const val ACTION_NOTIFICATION_UPDATE = "com.example.dynamicisland.NOTIFICATION_UPDATE"
        const val EXTRA_NOTIFICATION_TEXT = "notification_text"
        const val EXTRA_MEDIA_TEXT = "media_text"
        const val PREFS = "dynamic_island_settings"
        const val KEY_VIOLET_ACCENT = "violet_accent"
        const val KEY_OVERLAY_ENABLED = "overlay_enabled"
        private const val CHANNEL_ID = "dynamic_island_active"
        private const val FOREGROUND_ID = 1401
    }
}

private object SettingsAccess {
    fun canDrawOverlays(context: Context): Boolean = android.provider.Settings.canDrawOverlays(context)
}
