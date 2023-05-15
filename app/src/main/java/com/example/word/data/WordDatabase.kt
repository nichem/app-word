package com.example.word.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Word::class], version = 2, exportSchema = false)
abstract class WordDatabase : RoomDatabase() {
    abstract fun getWordDao(): WordDao
}