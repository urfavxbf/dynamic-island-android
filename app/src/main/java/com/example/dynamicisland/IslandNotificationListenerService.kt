package com.example.dynamicisland

import android.app.Notification
import android.app.PendingIntent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.content.Intent

class IslandNotificationListenerService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val notification = sbn?.notification ?: return
        val extras = notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()
        if (title.isBlank() && text.isBlank()) return

        val packageName = sbn.packageName.orEmpty()
        val isMedia = notification.category == Notification.CATEGORY_TRANSPORT ||
            notification.extras.containsKey("android.mediaSession")
        if (isMedia) {
            latestMedia = listOf(title, text).filter { it.isNotBlank() }.joinToString(" · ").take(70).ifBlank { "Media is active" }
        } else {
            latestNotification = listOf(title, text).filter { it.isNotBlank() }.joinToString(": ").take(70)
        }
        sendOverlayUpdate()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        sendOverlayUpdate()
    }

    private fun sendOverlayUpdate() {
        runCatching {
            sendBroadcast(Intent(IslandOverlayService.ACTION_NOTIFICATION_UPDATE).apply {
                setPackage(packageName)
                putExtra(IslandOverlayService.EXTRA_NOTIFICATION_TEXT, latestNotification)
                putExtra(IslandOverlayService.EXTRA_MEDIA_TEXT, latestMedia)
            })
        }
    }

    companion object {
        @Volatile var latestNotification: String = "Waiting for notifications"
            private set
        @Volatile var latestMedia: String = "No media playing"
            private set
    }
}
