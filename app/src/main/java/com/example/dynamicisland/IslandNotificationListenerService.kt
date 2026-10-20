package com.example.dynamicisland

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class IslandNotificationListenerService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        // Notification integration will be implemented after permission setup is verified.
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // Reserved for notification dismissal handling.
    }
}
