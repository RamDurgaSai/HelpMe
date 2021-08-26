package com.ramdurgasai.helpme

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlin.time.ExperimentalTime

@ExperimentalTime
class BootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val sharedPreference = context?.getSharedPreferences("PRIVATE",Context.MODE_PRIVATE)
        val botToken = sharedPreference?.getString("botToken" ,null)
        if(botToken != null ){
            context.startService(Intent(context, TelegramService::class.java).putExtra("otp",""))
        }
    }

}
