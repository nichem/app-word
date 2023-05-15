package com.example.word.data

import androidx.room.Room
import com.example.word.MyApplication.Companion.app

object AppRep {
    private val wordDB = Room
        .databaseBuilder(app, WordDatabase::class.java, "dict.db")
        .build()

    private val userDB = Room.databaseBuilder(app, UserDatabase::class.java, "user.db")
        .build()

    private val wordDao = wordDB.getWordDao()
    private val recordDao = userDB.getRecordDao()

    fun getWordCount(dictId: String): Int {
        return wordDao.getWordCountOfDict("%$dictId%")
    }

    fun getRememberWordCount(dictId: String): Int {
        return recordDao.getRememberWordCountOfDict("%$dictId%")
    }
}