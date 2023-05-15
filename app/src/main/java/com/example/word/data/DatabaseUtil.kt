package com.example.word.data

import com.tencent.mmkv.MMKV

object DatabaseUtil {
    data class Dict(
        val id: String,
        val name: String
    )

    val dictLists = listOf(
        Dict("zk", "中考词汇"),
        Dict("gk", "高考词汇"),
        Dict("cet4", "四级词汇"),
        Dict("cet6", "六级词汇"),
        Dict("ky", "考研词汇"),
        Dict("ielts", "雅思词汇"),
        Dict("toefl", "托福词汇"),
        Dict("gre", "GRE词汇"),
    )

    var curDictId: String
        get() = MMKV.defaultMMKV().decodeString("dictId") ?: "ky"
        set(value) {
            if (dictLists.find { it.id == value } != null)
                MMKV.defaultMMKV().encode("dictId", value)
        }
    val curDict: Dict
        get() = dictLists.find { it.id == curDictId }!!
}