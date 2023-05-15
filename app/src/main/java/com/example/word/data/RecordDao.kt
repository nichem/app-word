package com.example.word.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecordDao {
    @Update
    fun update(word: Record)

    @Insert
    fun insert(word: Record)

    @Query("select * from record where wordId = :id")
    fun findRecordById(id: Int): Record?

//    @Query("select * from record where tag like :tag and state > 1")
//    fun findRememberWordsOfDict(tag: String): List<Record>

    @Query("select count(*) from record where tag like :tag and state > 1")
    fun getRememberWordCountOfDict(tag: String): Int

    @Query("select * from record where tag like :tag order by state,lastDate")
    fun getRecordsByTag(tag: String): List<Record>

    @Delete
    fun delete(record: Record)
}