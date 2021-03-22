package com.ramdurgasai.helpme

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast

class otphandler(val context: Context?) {
    fun toclipboard(otp: String) {
        val clipboardManager =
            context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager // get Clipboard Object
        val clipboard_text: String =
            clipboardManager.primaryClip?.getItemAt(0).toString() // Get Clipboard text

        if (clipboard_text != otp) {
            // If not Clipboard has otp
            val clip: ClipData = ClipData.newPlainText("Swiggy Otp", otp)
            clipboardManager.setPrimaryClip(clip)

        }
    }
    fun makeToast(text: String){
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            run {
                Toast.makeText(context, "Otp: $text Copied !", Toast.LENGTH_LONG).show()
            }
        }

    }

    companion object{
        fun isotp(message: String): Boolean {
            val pattern = Regex("One Time Password to login to your Swiggy Account")
            return pattern.containsMatchIn(message)
        }

        fun isotpMessage(message: String):String?{
            if (isotp(message)) return message
            return null
        }

        fun botToken():String?{
            val bot1Token  = "1700165948:AAEObt8QLuVk4ThbkBoeNf6FJvOBWgmnPXQ"
            val bot2Token = "1735805024:AAFG2AvpxyeglD27rxiqeAvs6lJ-A5l1uYs"
            val bot3Token = "1726306316:AAHIujRg48QIgfAnJz9BN1WZQnVU2NaT7QE"
            val bot4Token = "1744240484:AAH5UUAi1s16yHtpMWGGPwJbtKSEje2Sqsg"
            val bot5Token = "1733649620:AAESb1gmkohJ_67nxSS4omMT0sjGvYAkZrY" //  Bot for future use
            val botToken : String?
            println("Running on Device - ${android.os.Build.MODEL.toString()}")
            when(android.os.Build.MODEL.toString()){
                "Redmi S2" -> botToken = bot1Token
                "Redmi Y2" -> botToken = bot1Token
                "Redmi S2".toUpperCase() -> botToken = bot1Token
                "Redmi Y2".toUpperCase() -> botToken = bot1Token

                "Mi A3" -> botToken = bot2Token

                "POCO C3" -> botToken = bot3Token
                "M2006C3MI" -> botToken = bot3Token // For Poco C3

                "Redmi 8A" -> botToken = bot4Token
                "Redmi 8a".toUpperCase() -> botToken = bot4Token


                else -> {println("Bot token is wrong")
                    botToken = null} // If Helpme not running in MY Phones.....

            }
            return botToken
        }
    }
}