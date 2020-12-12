package com.example.helloworld

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class QrCode : AppCompatActivity() {
    private var btnGenerate: Button?=null
    private var btnScan: Button?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code)
        init()

    }
    private fun init(){
        btnGenerate= findViewById(R.id.btn_generate_qr) as Button
        btnScan= findViewById(R.id.btn_scan_qr) as Button

        btnGenerate!!.setOnClickListener { startActivity(Intent(this@QrCode,GeneratedQr::class.java)) }
        btnScan!!.setOnClickListener { startActivity(Intent(this@QrCode,ScanQr::class.java)) }
    }
}