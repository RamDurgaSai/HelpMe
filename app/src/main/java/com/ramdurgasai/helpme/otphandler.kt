package com.ramdurgasai.helpme

import android.content.*
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast

class OtpHandler(val context: Context?,val text: String) {
    val otp: String = extractOtp(text)


    fun toclipboard() {
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

    fun makeToast(text: String) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            run {
                Toast.makeText(context, text, Toast.LENGTH_LONG).show()
            }
        }

    }

    fun sendBroadcast() {
        val sp =  context?.getSharedPreferences("PRIVATE",Context.MODE_PRIVATE)
        val isBroadCastEnabled = sp?.getBoolean("broadcast",true)
        if(isBroadCastEnabled == true) {
            val intent = Intent()
            intent.action = "com.ramdurgasai.helpme.SMS_RETRIEVED"
            intent.putExtra("com.ramdurgasai.helpme.SMS_BODY", text)
            context?.sendBroadcast(intent)
            Log.d("otpHandler","Broadcast Sent Successfully")
        }

    }

    companion object {
        fun extractOtp(smsText: String):String {

            val re = Regex("\\[(.*?)\\]")
            val result: MatchResult? = re.find(smsText)

            return result?.value?.slice(1..8).toString()
        }

        fun isotp(message: String): Boolean {
            val pattern = Regex("pgLJ0BdTNB3")
            return pattern.containsMatchIn(message)
        }
        fun isLoggedOutMsg(message: String): Boolean {
            val pattern = Regex("You have been logged out as you were out of network")
            return pattern.containsMatchIn(message)
        }
        fun textFromOtp(otp: String):String{
            return "Use [$otp] as One Time Password to login to your Swiggy Account pgLJ0BdTNB3"
        }
        fun isotpMessage(message: String): String? {
            if (isotp(message)) return message
            return null
        }
    }
}