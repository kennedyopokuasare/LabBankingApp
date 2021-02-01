package com.mobicomp2020.labbankingapp

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.room.Room
import com.mobicomp2020.bankingapp.db.AppDatabase
import com.mobicomp2020.bankingapp.db.PaymentInfo
import com.mobicomp2020.labbankingapp.databinding.ActivityPaymentBinding
import java.util.*


class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnAccept.setOnClickListener {
            //validate entry values here
            if (binding.txtDate.text.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Date should not be empty and should be in dd.mm.yyyy format",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val paymentInfo = PaymentInfo(
                null,
                name = binding.txtRecipient.text.toString(),
                accountNumber = binding.txtAccount.text.toString(),
                date = binding.txtDate.text.toString(),
                amount = binding.txtAmount.text.toString()
            )

            //convert date  string value to Date format using dd.mm.yyyy
            // here it is asummed that date is in dd.mm.yyyy
            val dateparts = paymentInfo.date.split(".").toTypedArray()
            val paymentCalender = GregorianCalendar(
                dateparts[2].toInt(),
                dateparts[1].toInt() - 1,
                dateparts[0].toInt()
            )

            AsyncTask.execute {
                //save payment to room datbase
                val db = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    getString(R.string.dbFileName)
                ).build()
                val uuid = db.paymentDao().insert(paymentInfo).toInt()
                db.close()

                // payment happens in the future set reminder
                if (paymentCalender.timeInMillis > Calendar.getInstance().timeInMillis) {
                    // payment happens in the future set reminder
                    val message =
                        "Pay ${paymentInfo.name}  ${paymentInfo.amount} into account ${paymentInfo.accountNumber}"
                    PaymentHistory.setRemnder(
                        applicationContext,
                        uuid,
                        paymentCalender.timeInMillis,
                        message
                    )
                }
            }

            if(paymentCalender.timeInMillis>Calendar.getInstance().timeInMillis){
                Toast.makeText(
                    applicationContext,
                    "Reminder for future payment saved.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            finish()
        }
    }
}