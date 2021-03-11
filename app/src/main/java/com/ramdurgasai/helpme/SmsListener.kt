package com.ramdurgasai.helpme


import android.content.*
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import android.widget.Toast
import androidx.annotation.RequiresApi

class SmsListener : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        val smsText: String = getTextFromSms(intent?.extras)

        //If it is logged out message Then this statement take care of all
        loggedmsg(context).alert(smsText)

        //If sms is a Otp from swiggy to login
        if(isotp(smsText)){
            // If Sms Contains otp
            val otp :String = smsText.slice(6..13) // Extracting Otp from Sms
            val clipboardManager = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager // get Clipboard Object
            val clipboard_text :String = clipboardManager.primaryClip?.getItemAt(0).toString() // Get Clipboard text

            if(clipboard_text != otp){
                // If not Clipboard has otp
                val clip: ClipData = ClipData.newPlainText("Swiggy Otp", otp)
                clipboardManager.setPrimaryClip(clip)
                Toast.makeText(context,"Otp : " + otp + " Copied!",Toast.LENGTH_LONG).show()
            }
        }

    }



    private fun getTextFromSms(extras: Bundle?): String {
        val pdus = extras?.get("pdus") as Array<*>
        val format = extras.getString("format")
        var txt = ""
        for (pdu in pdus) {
            val smsmsg = getSmsMsg(pdu as ByteArray?, format)
            val submsg = smsmsg?.displayMessageBody
            submsg?.let { txt = "$txt$it" }
        }
        return txt
    }

    private fun getSmsMsg(pdu: ByteArray?, format: String?): SmsMessage? {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> SmsMessage.createFromPdu(pdu, format)
            else -> SmsMessage.createFromPdu(pdu)
        }
    }

    companion object {
        private val TAG = SmsListener::class.java.simpleName
    }

    private fun isotp(message : String):Boolean{
        val pattern = Regex("One Time Password to login to your Swiggy Account")
        return pattern.containsMatchIn(message)
    }
}
