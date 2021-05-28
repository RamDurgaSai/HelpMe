package com.ramdurgasai.helpme


import android.content.*
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import androidx.annotation.RequiresApi
import com.ramdurgasai.helpme.loggedmsg.Companion.loggedOutMessages
import com.ramdurgasai.helpme.otphandler.Companion.isotpMessage
import kotlin.time.ExperimentalTime

@ExperimentalTime
class SmsListener : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        val smsText: String = getTextFromSms(intent?.extras)

        when(smsText){
            in loggedOutMessages -> loggedmsg(context).alert(smsText) //If it is logged out message
            isotpMessage(smsText) -> otpHandler(extractOtp(smsText),context)

        }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    fun otpHandler(otp: String, context: Context?){
        val otphandler = otphandler(context)
        otphandler.toclipboard(otp)
        otphandler.makeToast(otp)
        val sharedPreference = context?.getSharedPreferences("PRIVATE",Context.MODE_PRIVATE)
        val botToken = sharedPreference?.getString("botToken" ,null)
        if(botToken != null ){
            context.startService(Intent(context,telegramservice::class.java).putExtra("otp",otp))
        }


    }
    fun extractOtp(smsText: String):String {

            val re = Regex("\\[(.*?)\\]")
            val result: MatchResult? = re.find(smsText)

            return result?.value?.slice(1..8).toString()
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

}
