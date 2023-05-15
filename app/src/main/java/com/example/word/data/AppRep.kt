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

    fun getWordsByDictId(dictId: String): List<Word> {
        return wordDao.getWordsByBnc("%$dictId%")
    }

    fun generateRecordById(id: Int): Record {
        var record = recordDao.findRecordById(id)
        if (record == null) {
            val time = System.currentTimeMillis()
            val word = findWordById(id)!!
            record = Record(id, 1, word.tag, time, time)
            recordDao.insert(record)
        }
        return record
    }

    fun updateRecord(record: Record) {
        recordDao.update(record)
    }

    fun existRecord(id: Int): Boolean = recordDao.findRecordById(id) != null

    fun findWordById(id: Int): Word? {
        return wordDao.findById(id)
    }

    fun getRecordWordsByDictId(dictId: String): List<Word> {
        val records = recordDao.getRecordsByTag("%$dictId%")
        return records.mapNotNull {
            findWordById(it.wordId)
        }
    }

    fun clearRecordsByDictId(dictId: String) {
        val records = recordDao.getRecordsByTag("%$dictId%")
        records.forEach {
            recordDao.delete(it)
        }

    }
}