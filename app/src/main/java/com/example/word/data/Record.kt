package com.example.word.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "record")
data class Record(
    @PrimaryKey val wordId: Int,
    /**
     * 3多次记得，2记得，1不记得,0多次不记得，数字越小，复习优先级越高
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
