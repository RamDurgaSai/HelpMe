package com.ramdurgasai.helpme


import android.app.ActivityManager
import android.content.*
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.telephony.SmsMessage
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.ramdurgasai.helpme.loggedmsg.Companion.loggedOutMessages
import com.ramdurgasai.helpme.otphandler.Companion.isotpMessage
import kotlin.time.ExperimentalTime
import com.ramdurgasai.helpme.otphandler.Companion.botToken

@ExperimentalTime
class SmsListener : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        val smsText: String = getTextFromSms(intent?.extras)

        when(smsText){
            in loggedOutMessages -> loggedmsg(context).alert(smsText) //If it is logged out message
            isotpMessage(smsText) -> otpHandler(smsText.slice(6..13),context) // If it is Otp

        }
        }

    fun otpHandler(otp: String,context: Context?){
        val otphandler = otphandler(context)
        otphandler.toclipboard(otp)
        otphandler.makeToast(otp)

        if(botToken() != null){
            context?.startService(Intent(context,telegramservice::class.java).putExtra("otp",otp))
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
    fun isServiceRunning(context: Context?, serviceClass: Class<*>): Boolean {
        val manager = context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }


    companion object {
        private val TAG = SmsListener::class.java.simpleName
    }

}
