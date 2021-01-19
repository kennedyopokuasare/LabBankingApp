package com.mobicomp2020.labbankingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        findViewById<TextView>(R.id.txtNewPayment).setOnClickListener {
            var paymentIntent = Intent(applicationContext, PaymentActivity::class.java)
            startActivity(paymentIntent)
        }
    }
}