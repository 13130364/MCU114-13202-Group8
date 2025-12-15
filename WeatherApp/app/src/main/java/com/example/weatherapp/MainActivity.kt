package com.example.weatherapp // ★請保持您原本的 package 名稱
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder
import com.google.android.gms.location.Priority

class MainActivity : AppCompatActivity() {

    private lateinit var edCity: EditText
    private lateinit var btnSearch: Button
    private lateinit var tvResult: TextView
    private lateinit var imgWeather: ImageView

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // ★ 請務必確認 API KEY 是正確的
    private val API_KEY = "2b8b63fd60cafcd5db30ca68091cfc60"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val btnCity1 = findViewById<Button>(R.id.btn_city1)
        val btnCity2 = findViewById<Button>(R.id.btn_city2)
        val btnCity3 = findViewById<Button>(R.id.btn_city3)
        val btnCity4 = findViewById<Button>(R.id.btn_city4)
        // ... (其他的按鈕綁定) ...


        // ★ 修改第一顆按鈕的點擊事件
        btnCity1.setOnClickListener {
            checkPermissionAndGetLocation()
        }

        edCity = findViewById(R.id.ed_city)
        btnSearch = findViewById(R.id.btn_search)
        tvResult = findViewById(R.id.tv_result)
        imgWeather = findViewById(R.id.img_weather)





        btnCity2.setOnClickListener {
            edCity.setText("台北市")
            getWeather("台北市")
        }

        btnCity3.setOnClickListener {
            edCity.setText("新北市")
            getWeather("新北市")
        }

        btnCity4.setOnClickListener {
            edCity.setText("基隆市")
            getWeather("基隆市")
        }

        btnSearch.setOnClickListener {
            val city = edCity.text.toString().trim()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            if (city.isEmpty()) {
                Toast.makeText(this, "請輸入城市名稱", Toast.LENGTH_SHORT).show()
            } else {
                tvResult.text = "查詢中..."
                getWeather(city)
            }
        }
    }

    private fun checkPermissionAndGetLocation() {
        // 檢查是否有定位權限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // 如果沒有權限，就跳出視窗問使用者
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            return
        }

        // 如果有權限，就開始抓位置
        tvResult.text = "正在定位中..."
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    // 抓到了！取得經緯度
                    val lat = location.latitude
                    val lon = location.longitude
                    // 用經緯度去查天氣
                    getWeatherByLocation(lat, lon)
                } else {
                    tvResult.text = "定位失敗：無法取得位置\n(請試著在模擬器開啟 Google Maps 更新一下)"
                }
            }
            .addOnFailureListener {
                tvResult.text = "定位錯誤：${it.message}"
            }
    }

    @SuppressLint("SetTextI18n")
    private fun getWeatherByLocation(lat: Double, lon: Double) {
        // 注意：這裡的網址變數變成 lat=...&lon=...
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=$API_KEY&units=metric&lang=zh_tw"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = URL(url).readText()
                val jsonObject = JSONObject(result)
                val main = jsonObject.getJSONObject("main")
                val weatherArray = jsonObject.getJSONArray("weather")
                val weatherObj = weatherArray.getJSONObject(0)

                val rainObj = jsonObject.optJSONObject("rain")
                val rain1h = rainObj?.optDouble("1h") ?: 0.0

                val weatherDescription = weatherArray.getJSONObject(0).getString("description")
                val temp = main.getDouble("temp")
                val minTemp = main.getDouble("temp_min")
                val maxTemp = main.getDouble("temp_max")

                // 抓取回傳的城市名稱 (API 會告訴我們這裡是哪裡)
                val locationName = jsonObject.getString("name")

                withContext(Dispatchers.Main) {
                    tvResult.text = """
                    目前位置：$locationName
                    天氣狀況：$weatherDescription
                    目前氣溫：${String.format("%.1f", temp)}°C
                    最低氣溫：${String.format("%.1f", minTemp)}°C
                    最高氣溫：${String.format("%.1f", maxTemp)}°C
                    降雨：${String.format("%.1f", rain1h)} mm (近1hr)
                """.trimIndent()

                    // 低溫特報邏輯
                    if (temp < 10) {
                        Toast.makeText(this@MainActivity, "🥶 低溫特報：現在低於 10 度，請注意保暖！", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    tvResult.text = "查詢失敗：${e.message}"
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getWeather(city: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val encodedCity = URLEncoder.encode(city, "UTF-8")
                val url = "https://api.openweathermap.org/data/2.5/weather?q=$encodedCity&appid=$API_KEY&units=metric&lang=zh_tw"
                val jsonString = URL(url).readText()
                val jsonObject = JSONObject(jsonString)
                val main = jsonObject.getJSONObject("main")
                val weatherArray = jsonObject.getJSONArray("weather")
                val weatherObj = weatherArray.getJSONObject(0)

                val rainObj = jsonObject.optJSONObject("rain")
                val rain1h = rainObj?.optDouble("1h") ?: 0.0

                // 1. 原本只有 temp，現在多抓 min 和 max
                val temp = main.getDouble("temp")
                val minTemp = main.getDouble("temp_min") // ★ 新增
                val maxTemp = main.getDouble("temp_max") // ★ 新增

                val description = weatherObj.getString("description")
                val iconCode = weatherObj.getString("icon")

                withContext(Dispatchers.Main) {
                    // 2. 呼叫 updateUI 時，把這兩個新數據傳進去
                    updateUI(city, temp, minTemp, maxTemp, rain1h, description, iconCode)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    tvResult.text = "請重新輸入正確城市名稱"
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(city: String, temp: Double, minTemp: Double, maxTemp: Double, rain1h: Double, description: String, iconCode: String) {
        tvResult.text = """
            地點：$city
            天氣狀況：$description
            目前氣溫：${String.format("%.1f", temp)}°C
            最低氣溫：${String.format("%.1f", minTemp)}°C
            最高氣溫：${String.format("%.1f", maxTemp)}°C
            降雨：${String.format("%.1f", rain1h)} mm (近1hr)
        """.trimIndent()

        val anim = AlphaAnimation(0.1f, 1.0f)
        anim.duration = 1000
        imgWeather.startAnimation(anim)

        // 使用 Glide 載入圖示
        val iconUrl = "https://openweathermap.org/img/wn/$iconCode@4x.png" // 使用 4x 比較清晰

        Glide.with(this@MainActivity) // ★ 使用 this@MainActivity 比較安全
            .load(iconUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.stat_notify_error)
            .into(imgWeather)

        if (temp >= 30) {
            Toast.makeText(this, " 高溫特報：現在高於 30 度，請注意防曬！", Toast.LENGTH_LONG).show()
        } else if (temp <= 15 && temp>=0) {
            Toast.makeText(this, "🥶 低溫特報：現在低於 15 度，請注意保暖！", Toast.LENGTH_LONG).show()
        }
    }
}