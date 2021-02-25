package com.mobicomp2020.labbankingapp


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.room.Room
import com.mobicomp2020.bankingapp.db.AppDatabase
import com.mobicomp2020.bankingapp.db.PaymentInfo
import com.mobicomp2020.labbankingapp.databinding.ActivityPaymentBinding
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class PaymentActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var reminderCalender: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        //hide keyboard when the dateTextBox is clicked
        binding.txtDate.inputType = InputType.TYPE_NULL
        binding.txtDate.isClickable=true

        //show date and time dialog

        binding.txtDate.setOnClickListener {
            reminderCalender = GregorianCalendar.getInstance()
            DatePickerDialog(
                this,
                this,
                reminderCalender.get(Calendar.YEAR),
                reminderCalender.get(Calendar.MONTH),
                reminderCalender.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


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

            val paymentCalender=GregorianCalendar.getInstance()
            val dateFormat="dd.MM.yyyy HH:mm" // change this format to dd.MM.yyyy if you have not time in your date.
            // a better way of handling dates but requires API version 26 (Build.VERSION_CODES.O)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val formatter = DateTimeFormatter.ofPattern(dateFormat)
                val date = LocalDateTime.parse(paymentInfo.date, formatter)

                paymentCalender.set(Calendar.YEAR,date.year)
                paymentCalender.set(Calendar.MONTH,date.monthValue-1)
                paymentCalender.set(Calendar.DAY_OF_MONTH,date.dayOfMonth)
                paymentCalender.set(Calendar.HOUR_OF_DAY,date.hour)
                paymentCalender.set(Calendar.MINUTE,date.minute)

            } else {
                if(dateFormat.contains(":")){
                    // if your date contains hours and minutes and its in the format dd.mm.yyyy HH:mm
                    val dateparts = paymentInfo.date.split(" ").toTypedArray()[0].split(".").toTypedArray()
                    val timeparts = paymentInfo.date.split(" ").toTypedArray()[1].split(":").toTypedArray()

                    paymentCalender.set(Calendar.YEAR,dateparts[2].toInt())
                    paymentCalender.set(Calendar.MONTH,dateparts[1].toInt()-1)
                    paymentCalender.set(Calendar.DAY_OF_MONTH,dateparts[0].toInt())
                    paymentCalender.set(Calendar.HOUR_OF_DAY, timeparts[0].toInt())
                    paymentCalender.set(Calendar.MINUTE, timeparts[1].toInt())

                } else{
                    //no time part
                    //convert date  string value to Date format using dd.mm.yyyy
                    // here it is assumed that date is in dd.mm.yyyy
                    val dateparts = paymentInfo.date.split(".").toTypedArray()
                    paymentCalender.set(Calendar.YEAR,dateparts[2].toInt())
                    paymentCalender.set(Calendar.MONTH,dateparts[1].toInt()-1)
                    paymentCalender.set(Calendar.DAY_OF_MONTH,dateparts[0].toInt())
                }
            }





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
                    //val m="Today is ${paymentInfo.date}"
                    val message =
                        "Pay ${paymentInfo.name}  ${paymentInfo.amount} into account ${paymentInfo.accountNumber}"

                    PaymentHistory.setReminderWithWorkManager(
                        applicationContext,
                        uuid,
                        paymentCalender.timeInMillis,
                        message
                    )

                }
            }

            if (paymentCalender.timeInMillis > Calendar.getInstance().timeInMillis) {
                Toast.makeText(
                    applicationContext,
                    "Reminder for future payment saved.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            finish()
        }
    }


    override fun onDateSet(
        dailogView: DatePicker?,
        selectedYear: Int,
        selectedMonth: Int,
        selectedDayOfMonth: Int
    ) {
        reminderCalender.set(Calendar.YEAR, selectedYear)
        reminderCalender.set(Calendar.MONTH, selectedMonth)
        reminderCalender.set(Calendar.DAY_OF_MONTH, selectedDayOfMonth)
        val simleDateFormat = SimpleDateFormat("dd.MM.yyyy")
        binding.txtDate.setText(simleDateFormat.format(reminderCalender.time))

        // if you want to show time picker after the date
        // you dont need this,change dateFormat value to dd.MM.yyyy
        TimePickerDialog(
            this,
            this,
            reminderCalender.get(Calendar.HOUR_OF_DAY),
            reminderCalender.get(Calendar.MINUTE),
            true
        ).show()
    }

    override fun onTimeSet(view: TimePicker?, selectedhourOfDay: Int, selectedMinute: Int) {
        reminderCalender.set(Calendar.HOUR_OF_DAY, selectedhourOfDay)
        reminderCalender.set(Calendar.MINUTE, selectedMinute)
        val simleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
        binding.txtDate.setText(simleDateFormat.format(reminderCalender.time))
    }


}