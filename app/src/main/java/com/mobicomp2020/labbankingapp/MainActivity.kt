package com.mobicomp2020.labbankingapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.mobicomp2020.labbankingapp.databinding.ActivityMainBinding
import com.mobicomp2020.labbankingapp.databinding.ActivityPaymentBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        binding.btnLogin.setOnClickListener {
            Log.d("Lab", "Login Button Clicked")

            //Authentication goes here

            //save loginstatus

            applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference),
                Context.MODE_PRIVATE
            ).edit().putInt("LoginStatus", 1).apply()

            startActivity(
                Intent(applicationContext, MenuActivity::class.java)
            )
        }

        checkLoginStatus()
    }

    override fun onResume() {
        super.onResume()
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        val loginStatus = applicationContext.getSharedPreferences(
            getString(R.string.sharedPreference),
            Context.MODE_PRIVATE
        ).getInt("LoginStatus", 0)
        if (loginStatus == 1) {
            startActivity(Intent(applicationContext, MenuActivity::class.java))
        }
    }
}