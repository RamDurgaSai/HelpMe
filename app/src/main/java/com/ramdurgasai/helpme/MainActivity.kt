package com.ramdurgasai.helpme

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.pengrad.telegrambot.TelegramBot
import java.io.*
import kotlin.time.ExperimentalTime


@ExperimentalTime
class MainActivity : AppCompatActivity() {
    lateinit var sp:SharedPreferences
    lateinit var channelIdTextInput:TextView
    lateinit var botTokenTextInput:TextView
    lateinit var saveButton:Button
    lateinit var dndModeButton:Switch
    lateinit var broadcastSwitch:Switch
    lateinit var NotificationAccessButton:Button



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sp =  getSharedPreferences("PRIVATE", MODE_PRIVATE)
        //Init Widgets
        channelIdTextInput = findViewById<TextView>(R.id.channel_id_text_input)
        botTokenTextInput = findViewById<TextView>(R.id.bot_token_text_input)
        saveButton = findViewById<Button>(R.id.save_button)
        dndModeButton = findViewById<Switch>(R.id.dndSwitch)
        broadcastSwitch = findViewById<Switch>(R.id.broadcastSwitch)
        NotificationAccessButton = findViewById<Button>(R.id.NotificationAccessButton)



        //Ask Permissions
        askPermissions()
        ////////////////

        //Load Values To Widgets
        loadDefaultToWidgets()
        ///////////////////////

        //Set Click Listeners
        setOnClickListeners()
        ////////////////////////////

        // Start Telegram Service
        startTelegramSerivce()
        ///////////////////////

    }


    override fun onResume() {
        super.onResume()
        loadDefaultToWidgets()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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
    fun loadDefaultToWidgets(){
        // Load Values to Widgets
        channelIdTextInput.text = sp.getString("channelId","")
        botTokenTextInput.text = sp.getString("botToken","")
        dndModeButton.isChecked = sp.getBoolean("DND",false)
        broadcastSwitch.isChecked = sp.getBoolean("broadcast",true)
        if (NotificationManagerCompat.getEnabledListenerPackages (getApplicationContext()).contains(getApplicationContext().getPackageName())){
            NotificationAccessButton.visibility = View.INVISIBLE
        }else{
            NotificationAccessButton.text = "Enable Notification Access"
            NotificationAccessButton.visibility = View.VISIBLE
        }

    }
    fun setOnClickListeners(){
        val editor = sp.edit()
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
                Toast.makeText(this,"Updated Successfully-1",Toast.LENGTH_LONG).show()
                startTelegramSerivce()

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
        broadcastSwitch.setOnClickListener { view: View? -> when(view?.id){
            R.id.broadcastSwitch -> {
                editor.putBoolean("broadcast", broadcastSwitch.isChecked).apply()
                if(broadcastSwitch.isChecked){
                    Toast.makeText(this,"Sending Broadcast are Enabled",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this,"Sending Broadcast are Disabled",Toast.LENGTH_LONG).show()
                }
            }
        } }
        NotificationAccessButton.setOnClickListener { view: View? -> when(view?.id){
            R.id.NotificationAccessButton->{
                startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))


            }
        } }

    }
    fun startTelegramSerivce(){
        if (sp.getString("botToken","") != "" && !isServiceRunning(this,TelegramService::class.java)){
            val intent = Intent(applicationContext, TelegramService::class.java)
            intent.putExtra("otp", "")
            ContextCompat.startForegroundService(this, intent)

        }
    }
    fun askPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS), 111)
        }

    }




}
