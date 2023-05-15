package com.example.word.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "record")
data class Record(
    @PrimaryKey val wordId: Int,
    /**
     * 3记过但记得，2记过，1记过但忘了，数字越小，复习优先级越高
     */
    var state: Int,
    /**
     * 和 Word表 tag值 相同
     */
    var tag: String,
    /**
     * 第一次记忆
     */
    val startDate: Long,
    /**
     * 最后一次记忆，数字越小，复习优先级越高，并且优先于state。
     */
    var lastDate: Long
)
