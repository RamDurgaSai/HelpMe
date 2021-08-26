package com.ramdurgasai.helpme

import android.app.Notification
import android.content.Intent
import android.os.*
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.annotation.RequiresApi
import com.ramdurgasai.helpme.LoggedMessage.Companion.loggedOutMessages
import kotlin.time.ExperimentalTime

@ExperimentalTime
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class NotificationListener : NotificationListenerService() {

    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val extraBundle: Bundle? = sbn?.notification?.extras ?: null
        val tittle = extraBundle?.get(Notification.EXTRA_TITLE).toString()

        if(tittle in loggedOutMessages) { LoggedMessage(getApplicationContext()).alert(tittle) }

    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {

    }

}