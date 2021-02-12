package com.ramdurgasai.helpme

import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class NLService : NotificationListenerService() {
    //private StatusBarNotification[] mStatusBarNotification;
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Inside on create")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        TAG = "onNotificationPosted"
        Log.d(
            TAG,
            "id = " + sbn.id + "Package Name" + sbn.packageName +
                    "Post time = " + sbn.postTime + "Tag = " + sbn.tag
        )
        Toast.makeText(getApplicationContext(),sbn.packageName,Toast.LENGTH_LONG).show()

    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        TAG = "onNotificationRemoved"
        Log.d(
            TAG,
            "id = " + sbn.id + "Package Name" + sbn.packageName +
                    "Post time = " + sbn.postTime + "Tag = " + sbn.tag
        )
    }

    companion object {
        var TAG = "NotificationListenerTesting"
    }
}