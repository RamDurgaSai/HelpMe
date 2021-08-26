package com.ramdurgasai.helpme

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.time.LocalTime
import kotlin.time.ExperimentalTime


@ExperimentalTime
class LoggedMessage(val context: Context?) {


    @RequiresApi(Build.VERSION_CODES.O)
    fun alert(text: String?) {// If Logged Out In Duty Time
        val sharedPreference = context?.getSharedPreferences("PRIVATE",Context.MODE_PRIVATE)
        val isDNDActive = sharedPreference?.getBoolean("DND", false)

        if (isDutyTime() && text in loggedOutMessages) {
            Toast.makeText(context, "Duty is Stopped Check The App Once", Toast.LENGTH_SHORT).show()
            val intent = Intent(context,MediaService::class.java)


            if(isDNDActive == true){ // If DND Mode is ON
                Toast.makeText(context, "It's DND Time  ...That's Why I am not making any noise", Toast.LENGTH_LONG).show()

                intent.putExtra("OnlyVibrate",isDNDActive)

            }

            if(isServiceRunning(context,MediaService::class.java)){
                // If Already alerting user....( Service already running)
                Toast.makeText(context,"Again Duty is stopped ..... Check the Once",Toast.LENGTH_LONG).show()

            }else{// Starting Service
                context?.startService(intent)
            }


        } else if (text in loggedOutMessages) {
            // If logged out when not in Duty
            Toast.makeText(context, "Duty is Stopped Check The App Once", Toast.LENGTH_SHORT).show()
            Toast.makeText(context, "It's not Duty Time ... That's Why I am not making any noise", Toast.LENGTH_LONG).show()


        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isDutyTime(): Boolean {
        //Gets The Current Time
        // If Current Time is  Duty Time then returns true else false
        val currentHour: Int = LocalTime.now().hour
        val currentMinute: Int = LocalTime.now().minute
        if (currentHour == 10) return currentMinute in 30..59
        // Decreased Time from 11:30 to 11:00
        return currentHour in 10..22

    }

    fun isServiceRunning(context: Context?, serviceClass: Class<*>): Boolean {
        val manager = context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
    companion object{
        val loggedOutMessages: MutableList<String> = mutableListOf("Off Duty", "Duty Stopped", "You have been logged out as you were out of network. Please check your network and turn on gps/location services to login again.\n" +
                "-Swiggy") // A list contains all logged out messages...

    }
}