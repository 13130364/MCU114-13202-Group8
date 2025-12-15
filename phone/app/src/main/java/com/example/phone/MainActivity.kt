package com.example.phone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {
    private lateinit var txtShow: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtShow=findViewById(R.id.txtShow)
        val btnZero=findViewById<Button>(R.id.btnZero)
        val btnOne=findViewById<Button>(R.id.btnOne)
        val btnTwo=findViewById<Button>(R.id.btnTwo)
        val btnThree=findViewById<Button>(R.id.btnThree)
        val btnFour=findViewById<Button>(R.id.btnFour)
        val btnFive=findViewById<Button>(R.id.btnFive)
        val btnSix=findViewById<Button>(R.id.btnSix)
        val btnSeven=findViewById<Button>(R.id.btnSeven)
        val btnEight=findViewById<Button>(R.id.btnEight)
        val btnNine=findViewById<Button>(R.id.btnNine)
        val btnClear=findViewById<Button>(R.id.btnClear)
        val btnCall=findViewById<Button>(R.id.btnCall)


        btnZero.setOnClickListener(myListener)
        btnOne.setOnClickListener(myListener)
        btnTwo.setOnClickListener(myListener)
        btnThree.setOnClickListener(myListener)
        btnFour.setOnClickListener(myListener)
        btnFive.setOnClickListener(myListener)
        btnSix.setOnClickListener(myListener)
        btnSeven.setOnClickListener(myListener)
        btnEight.setOnClickListener(myListener)
        btnNine.setOnClickListener(myListener)
        btnClear.setOnClickListener(myListener)
        btnCall.setOnClickListener(myListener)
    }

    private val myListener = View.OnClickListener { v ->
        val s: String = txtShow.text.toString()
        when (v.id) {
            R.id.btnZero -> {
                txtShow.text = s + "0"
            }

            R.id.btnOne -> {
                txtShow.text = s + "1"
            }

            R.id.btnTwo -> {
                txtShow.text = s + "2"
            }

            R.id.btnThree -> {
                txtShow.text = s + "3"
            }

            R.id.btnFour -> {
                txtShow.text = s + "4"
            }

            R.id.btnFive -> {
                txtShow.text = s + "5"
            }

            R.id.btnSix -> {
                txtShow.text = s + "6"
            }

            R.id.btnSeven -> {
                txtShow.text = s + "7"
            }

            R.id.btnEight -> {
                txtShow.text = s + "8"
            }

            R.id.btnNine -> {
                txtShow.text = s + "9"
            }

            R.id.btnClear -> {
                txtShow.text = "電話號碼："
            }

            R.id.btnCall -> {
                Toast.makeText(this, "call", Toast.LENGTH_SHORT).show()
            }
        }
    }
}