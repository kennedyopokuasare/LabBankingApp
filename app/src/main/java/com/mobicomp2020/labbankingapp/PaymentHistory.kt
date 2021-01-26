package com.mobicomp2020.labbankingapp

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.room.Room
import com.mobicomp2020.bankingapp.db.AppDatabase
import com.mobicomp2020.bankingapp.db.PaymentInfo
import com.mobicomp2020.labbankingapp.databinding.ActivityPaymentHistoryBinding


class PaymentHistory : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var binding: ActivityPaymentHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentHistoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        listView = binding.historyListView

        //update userInterface
        refreshListView()

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, id ->
            //retrieve selected Item

            val selectedPayment = listView.adapter.getItem(position) as PaymentInfo
            val message =
                "Do you want to delete ${selectedPayment.amount} payment, on ${selectedPayment.date} to ${selectedPayment.name} ?"

            // Show AlertDialog to delete the reminder
            val builder = AlertDialog.Builder(this@PaymentHistory)
            builder.setTitle("Delete reminder?")
                .setMessage(message)
                .setPositiveButton("Delete") { _, _ ->
                    // Update UI


                    //delete from database
                    AsyncTask.execute {
                        val db = Room
                            .databaseBuilder(
                                applicationContext,
                                AppDatabase::class.java,
                                getString(R.string.dbFileName)
                            )
                            .build()
                        db.paymentDao().delete(selectedPayment.uid!!)
                    }


                    //refresh payments list
                    refreshListView()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    // Do nothing
                    dialog.dismiss()
                }
                .show()

        }
    }

    override fun onResume() {
        super.onResume()
        refreshListView()
    }

    private fun refreshListView() {
        var refreshTask = LoadPaymentInfoEntries()
        refreshTask.execute()

    }

    inner class LoadPaymentInfoEntries : AsyncTask<String?, String?, List<PaymentInfo>>() {
        override fun doInBackground(vararg params: String?): List<PaymentInfo> {
            val db = Room
                .databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    getString(R.string.dbFileName)
                )
                .build()
            val paymentInfos = db.paymentDao().getPaymentInfos()
            db.close()
            return paymentInfos
        }

        override fun onPostExecute(paymentInfos: List<PaymentInfo>?) {
            super.onPostExecute(paymentInfos)
            if (paymentInfos != null) {
                if (paymentInfos.isNotEmpty()) {
                    val adaptor = PaymentHistoryAdaptor(applicationContext, paymentInfos)
                    listView.adapter = adaptor
                } else {
                    listView.adapter = null
                    Toast.makeText(applicationContext, "No items now", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


    companion object {
        //val paymenthistoryList = mutableListOf<PaymentInfo>()
    }
}