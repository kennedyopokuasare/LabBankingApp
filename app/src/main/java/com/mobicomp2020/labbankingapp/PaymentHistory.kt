package com.mobicomp2020.labbankingapp

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.room.Room
import com.mobicomp2020.bankingapp.db.AppDatabase
import com.mobicomp2020.bankingapp.db.PaymentInfo
import com.mobicomp2020.labbankingapp.databinding.ActivityPaymentHistoryBinding
import kotlin.random.Random


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
                    // cancel pending time based reminder
                    cancelReminder(applicationContext, selectedPayment.uid!!)


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

        fun showNofitication(context: Context, message: String) {

            val CHANNEL_ID = "BANKING_APP_NOTIFICATION_CHANNEL"
            var notificationId = Random.nextInt(10, 1000) + 5
            // notificationId += Random(notificationId).nextInt(1, 500)

            var notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_money_24)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setGroup(CHANNEL_ID)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Notification chancel needed since Android 8
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = context.getString(R.string.app_name)
                }
                notificationManager.createNotificationChannel(channel)
            }

            notificationManager.notify(notificationId, notificationBuilder.build())

        }

        fun setRemnder(context: Context, uid: Int, timeInMillis: Long, message: String) {
            val intent = Intent(context, ReminderReceiver::class.java)
            intent.putExtra("uid", uid)
            intent.putExtra("message", message)

            // create a pending intent to a  future action with a uniquie request code i.e uid
            val pendingIntent =
                PendingIntent.getBroadcast(context, uid, intent, PendingIntent.FLAG_ONE_SHOT)

            //create a service to moniter and execute the fure action.
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(AlarmManager.RTC, timeInMillis, pendingIntent)
        }

        fun cancelReminder(context: Context, pendingIntentId: Int) {

            val intent = Intent(context, ReminderReceiver::class.java)
            val pendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    pendingIntentId,
                    intent,
                    PendingIntent.FLAG_ONE_SHOT
                )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
        }
    }
}
