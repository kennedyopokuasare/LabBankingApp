package com.mobicomp2020.labbankingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.mobicomp2020.labbankingapp.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    binding= ActivityMenuBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        binding.txtNewPayment.setOnClickListener {
            var paymentIntent = Intent(applicationContext, PaymentActivity::class.java)
            startActivity(paymentIntent)
        }

       binding.txtPaymentHistory.setOnClickListener {
            startActivity(Intent(applicationContext,PaymentHistory::class.java))
        }
    }
}