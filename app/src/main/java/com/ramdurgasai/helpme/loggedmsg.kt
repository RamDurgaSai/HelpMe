package com.ramdurgasai.helpme

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.time.LocalTime


class loggedmsg(val context: Context?) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun alert(text: String?) {
        if (isDutyTime() && text in loggedOutMessages) {
            // If Logged Out In Duty Time
            Toast.makeText(context, "Duty is Stopped Check The App Once", Toast.LENGTH_SHORT).show()

            if(isServiceRunning(context,mediaservice::class.java)){
                // If Already alerting user....( Service already running)
                Toast.makeText(context,"Again Duty is stopped ... Check the Once",Toast.LENGTH_LONG).show()

            }else{
                context?.startService(Intent(context,mediaservice::class.java))
            }


        } else if (text in loggedOutMessages) {
            // If logged out when not in Duty
            Toast.makeText(context, "Duty is Stopped Check The App Once", Toast.LENGTH_SHORT).show()
            Toast.makeText(context, "It's not Duty Time ... So I am not making any noise", Toast.LENGTH_LONG).show()

        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isDutyTime(): Boolean {
        //Gets The Current Time
        // If Current Time is  Duty Time then returns true else false
        val currentHour: Int? = LocalTime.now().hour
        val currentMinute: Int? = LocalTime.now().minute

        if (currentHour != null && currentMinute != null) {
            if (currentHour == 10) return currentMinute in 30..59
            if (currentHour == 23) return currentMinute in 0..30
            return currentHour in 10..23
        } else return false

        return false
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