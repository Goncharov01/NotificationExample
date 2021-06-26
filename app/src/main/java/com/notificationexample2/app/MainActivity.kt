package com.notificationexample2.app

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.notificationexample2.app.databinding.ActivityMainBinding
import org.json.JSONException


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val NOTIFICATION_CHANNEL_ID = "10001"
    private val default_notification_channel_id = "default"
    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onNewIntent(intent)

        requestQueue = Volley.newRequestQueue(this)

        binding.button.setOnClickListener {
            createNotification()
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val extras = intent!!.extras
        if (extras != null) {
            if (extras.containsKey("NotificationMessage")) {
                val msg = extras.getString("NotificationMessage")
                println("@@@@@@@@@@@@@$msg")
                jsonParse()
            }
        }
    }

    fun createNotification() {
        val notificationIntent = Intent(this@MainActivity, MainActivity::class.java)
        notificationIntent.putExtra("NotificationMessage", "I am from Notification")
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        notificationIntent.action = Intent.ACTION_MAIN
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val resultIntent = PendingIntent.getActivity(this@MainActivity, 0, notificationIntent, 0)
        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this@MainActivity, default_notification_channel_id)
                .setSmallIcon(R.drawable.arrow_up_float)
                .setContentTitle("Test")
                .setContentText("Hello! This is my first push notification")
                .setContentIntent(resultIntent)
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance)
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            assert(mNotificationManager != null)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
        assert(mNotificationManager != null)
        mNotificationManager.notify(System.currentTimeMillis().toInt(), mBuilder.build())
    }

    private fun jsonParse() {
        val url = "https://rickandmortyapi.com/api"
        val request =
                JsonObjectRequest(Request.Method.GET, url, null, Response.Listener { response ->
                    try {
//            val jsonArray = response.getJSONArray("employees")
//            for (i in 0 until jsonArray.length()) {
//                val employee = jsonArray.getJSONObject(i)
//                val firstName = employee.getString("firstname")
//                val age = employee.getInt("age")
//                val mail = employee.getString("mail")
//            }
                        println(response.getString("characters"))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener { error -> error.printStackTrace() })
        requestQueue?.add(request)
    }

}