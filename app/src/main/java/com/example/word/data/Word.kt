package com.example.word.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stardict")
data class Word(
    @PrimaryKey val id: Int,
    val word: String,
    val phonetic: String,
    /**
     * 英文解释
     */
    val definition: String,
    /**
     * 中文翻译
     */
    val translation: String,
    @ColumnInfo(name = "pos")
    val _pos: String,
    /**
     * 柯林斯星级 1-5，代表词频，5最高
     */
    val collins: Int,
    /**
     * 是否是牛津三千核心词汇
     */
    val oxford: Int,
    /**
     * 字符串标签：zk/中考，gk/高考，cet4/四级 等等标签，空格分割
     */
    val tag: String,
    /**
     * 英国国家语料库词频顺序
     */
    val bnc: Int,
    /**
     * 当代语料库词频顺序
     */
    val frq: Int,
    /**
     *时态复数等变换，使用 "/" 分割不同项目
     */
    val exchange: String,

    @ColumnInfo(name = "detail")
    val _detail: String,
    @ColumnInfo(name = "audio")
    val _audio: String,

    ) {
    fun getTagString(): String {
        var tagString = ""
        DatabaseUtil.dictLists.forEach {
            if (it.id in tag) tagString += "${it.name},"
        }
        if (oxford != 0) tagString += "牛津三千核心词汇,"
        return tagString.substring(0, tagString.length)
    }
}
