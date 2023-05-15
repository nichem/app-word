package com.example.word.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface WordDao {
    @Query("select * from stardict where lower(word) like lower(:word)")
    fun findByLikeWord(word: String): List<Word>

    @Query("select * from stardict where id = :id")
    fun findById(id: Int): Word?

    @Query("select * from stardict where tag like :tag")
    fun findWordsOfDict(tag: String): List<Word>

    @Query("select count(*) from stardict where tag like :tag")
    fun getWordCountOfDict(tag: String): Int
}