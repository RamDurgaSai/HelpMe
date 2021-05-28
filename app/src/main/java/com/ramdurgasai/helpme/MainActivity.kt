package com.ramdurgasai.helpme

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.*
import kotlin.time.ExperimentalTime
import com.pengrad.telegrambot.TelegramBot
import java.lang.Exception


@ExperimentalTime
class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get Shared Preference
        val sp =  getSharedPreferences("PRIVATE",Context.MODE_PRIVATE)
        val editor = sp.edit()

        // Init Widgets
        val channelIdTextInput = findViewById<TextView>(R.id.channel_id_text_input)
        val botTokenTextInput = findViewById<TextView>(R.id.bot_token_text_input)
        val saveButton = findViewById<Button>(R.id.save_button)
        val dndModeButton = findViewById<Switch>(R.id.dndSwitch)

        // Load Values to Widgets
        channelIdTextInput.text = sp.getString("channelId","")
        botTokenTextInput.text = sp.getString("botToken","")
        dndModeButton.isChecked = sp.getBoolean("DND",false)

        //Set Click Listeners
        saveButton.setOnClickListener{view: View? -> when(view?.id){
            R.id.save_button -> {
                val botToken = botTokenTextInput.text.toString()
                val channelId = channelIdTextInput.text.toString()
                if (botToken == "" || channelId == "" ){
                    Toast.makeText(this,"botToken or channelId can't be Empty",Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                try {
                    val bot = TelegramBot(botToken)
                }catch (e: Exception){
                    Toast.makeText(this,"botToken is invalid/not accepted",Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                editor.putString("botToken",botToken).apply()
                editor.putString("channelId",channelId).apply()
                Toast.makeText(this,"Updated Successfully",Toast.LENGTH_LONG).show()

            }
        }
        }

        dndModeButton.setOnClickListener { view: View? -> when(view?.id){
            R.id.dndSwitch -> {
                editor.putBoolean("DND", dndModeButton.isChecked).apply()
                if(dndModeButton.isChecked){
                    Toast.makeText(this,"DND is Activated",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this,"DND is Deactivated",Toast.LENGTH_LONG).show()
                }
            }
        } }




        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS), 111)
        }

        // Starting A  Telegram Service
        if (sp.getString("botToken","") != "" && !isServiceRunning(this,telegramservice::class.java)){
            val intent = Intent(applicationContext, telegramservice::class.java)
            intent.putExtra("otp", "")
            ContextCompat.startForegroundService(this, intent)

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            Toast.makeText(this, "Permission Granted ... I can Work Now", Toast.LENGTH_LONG).show()
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


}
