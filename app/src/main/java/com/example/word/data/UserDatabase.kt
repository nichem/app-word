package com.example.word.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Record::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {
    abstract fun getRecordDao(): RecordDao
}