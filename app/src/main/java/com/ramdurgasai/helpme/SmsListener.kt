package com.ramdurgasai.helpme


import android.content.*
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import androidx.annotation.RequiresApi
import com.ramdurgasai.helpme.LoggedMessage.Companion.loggedOutMessages
import com.ramdurgasai.helpme.OtpHandler.Companion.isotpMessage
import kotlin.time.ExperimentalTime

@ExperimentalTime
class SmsListener : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        val smsText: String = getTextFromSms(intent?.extras)

        //Starting Telegram Service
        startTelegramService(context,"")

        when(smsText){
            in loggedOutMessages -> LoggedMessage(context).alert(smsText) //If it is logged out message
            isotpMessage(smsText) -> onOtpComes(smsText,context)


        }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onOtpComes(text: String, context: Context?){

        //Handling Otp
        val otphandler = OtpHandler(context,text)
        otphandler.toclipboard()
        otphandler.makeToast("Otp: ${otphandler.otp} Copied !")
        otphandler.sendBroadcast()

        //Starting Telegram Service
        startTelegramService(context,otphandler.otp)


    }
    private fun startTelegramService(context: Context?,otp: String){
        //Starting Telegram Service
        val sharedPreference = context?.getSharedPreferences("PRIVATE",Context.MODE_PRIVATE)
        val botToken = sharedPreference?.getString("botToken" ,null)
        if(botToken != null ){
            context.startService(Intent(context,TelegramService::class.java).putExtra("otp",otp))
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

}
