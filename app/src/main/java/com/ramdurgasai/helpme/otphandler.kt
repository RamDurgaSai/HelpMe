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

    fun makeToast(text: String) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            run {
                Toast.makeText(context, "Otp: $text Copied !", Toast.LENGTH_LONG).show()
            }
        }

    }

    companion object {
        fun isotp(message: String): Boolean {
            val pattern = Regex("One Time Password to login to your Swiggy Account")
            return pattern.containsMatchIn(message)
        }
        fun isLoggedOutMsg(message: String): Boolean {
            val pattern = Regex("You have been logged out as you were out of network")
            return pattern.containsMatchIn(message)
        }

        fun isotpMessage(message: String): String? {
            if (isotp(message)) return message
            return null
        }
    }
}