package com.ramdurgasai.helpme

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream

@RequiresApi(Build.VERSION_CODES.O)
class Imei(val context: Context) {
    var imei: String?
   init {
        imei = getIMEI()
   }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getIMEI(): String? {
        println("Running in Android Version :" + Build.VERSION.SDK_INT.toString())
        val cmdSerialNO : String = "getprop ro.serialno"
        val cmdIMEI: String =  "service call iphonesubinfo 1 | cut -c 52-66 | tr -d '.[:space:]'"
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ) {
            // If Running in Android Q lower devices...

            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as
                    TelephonyManager
            return telephonyManager.imei
        }else{
            try {
                return runCommandAsSu(cmdIMEI)
            }catch (e: java.lang.Exception){e.printStackTrace()}
        }
        // YOU SHOULD NEVER REACH HERE
        return null
    }
    fun runCommandAsSu(vararg strings: String): String? {
        var res: String? = ""
        var outputStream: DataOutputStream? = null
        var response: InputStream? = null
        try {
            val su = Runtime.getRuntime().exec("su")
            outputStream = DataOutputStream(su.outputStream)
            response = su.inputStream
            for (s in strings) {
                outputStream.writeBytes(
                    """
                    $s
                    
                    """.trimIndent()
                )
                outputStream.flush()
            }
            outputStream.writeBytes("exit\n")
            outputStream.flush()
            try {
                su.waitFor()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            res = readFully(response)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return res
    }
    @Throws(IOException::class)
    fun readFully(`is`: InputStream): String? {
        val baos = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var length = 0
        while (`is`.read(buffer).also({ length = it }) != -1) {
            baos.write(buffer, 0, length)
        }
        return baos.toString("UTF-8")
    }
}