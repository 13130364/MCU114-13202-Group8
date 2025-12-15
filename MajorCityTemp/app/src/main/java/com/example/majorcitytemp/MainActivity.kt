package com.example.majorcitytemp

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MainActivity : AppCompatActivity() {

    // 1. Define Data Classes for Gson Parsing
    // JSON Structure: { "main": { "temp": 30.5, ... }, ... }
    data class WeatherResponse(val main: MainData, val name: String)
    data class MainData(val temp: Double, val humidity: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnGetTemp = findViewById<Button>(R.id.btnGetTemp)

        btnGetTemp.setOnClickListener {
            fetchWeather()
        }
    }

    private fun fetchWeather() {
        // *** IMPORTANT: REPLACE WITH YOUR OWN OPENWEATHERMAP API KEY ***
        val apiKey = "Your API Key Here"
        val city = "Taipei"
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$apiKey"

        val client = OkHttpClient()

        // Network operations must run on a background thread (Dispatchers.IO)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Create the Request (Defaults to GET method)
                val request = Request.Builder()
                    .url(url)
                    .build()

                // Execute the request
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseData = response.body?.string()

                    if (responseData != null) {
                        // Parse JSON using Gson
                        val gson = Gson()
                        val weatherData = gson.fromJson(responseData, WeatherResponse::class.java)

                        // Switch back to Main Thread to update UI
                        withContext(Dispatchers.Main) {
                            showAlertDialog(
                                "Current Weather",
                                "City: ${weatherData.name}\n" +
                                        "Temperature: ${weatherData.main.temp}°C\n" +
                                        "Humidity: ${weatherData.main.humidity}%"
                            )
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showAlertDialog("Error", "Failed to get data. Code: ${response.code}")
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    showAlertDialog("Error", "Network error: ${e.message}")
                }
            }
        }
    }

    private fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
