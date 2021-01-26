package com.mobicomp2020.bankingapp.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(PaymentInfo::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun paymentDao(): PaymentDao
}