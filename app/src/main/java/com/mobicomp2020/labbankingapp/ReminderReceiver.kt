package com.mobicomp2020.labbankingapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver :BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        // Retrieve data from intent
        val uid = intent?.getIntExtra("uid", 0)
        val text = intent?.getStringExtra("message")


        PaymentHistory.showNofitication(context!!,text!!)
    }
}