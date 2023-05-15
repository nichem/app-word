package com.example.word

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.word.base.BaseActivity
import com.example.word.data.AppRep
import com.example.word.data.DatabaseUtil
import com.example.word.data.Word
import com.example.word.databinding.ActivityReciteBinding
import com.example.word.databinding.ItemWordBinding
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ReciteActivity : BaseActivity<ActivityReciteBinding>() {
    companion object {
        private const val KEY_DICT_ID = "dictId"
        private const val KEY_IS_NEW = "isNew"
        fun start(context: Context, dictId: String, isNew: Boolean) {
            context.startActivity(Intent(context, ReciteActivity::class.java).apply {
                putExtra(KEY_DICT_ID, dictId)
                putExtra(KEY_IS_NEW, isNew)
            })
        }
    }

    private lateinit var dictId: String
    private var isNew: Boolean = true
    private var words = emptyList<Word>()
    private var curWordIndexLiveData = MutableLiveData(0)
    private lateinit var wordBinding: ItemWordBinding
    override fun initView() {
        dictId = intent.getStringExtra(KEY_DICT_ID) ?: DatabaseUtil.curDictId
        isNew = intent.getBooleanExtra(KEY_IS_NEW, true)
        binding.tvTitle.text = if (isNew) "来点新的" else "看看旧的"
        initWords()
        initWordView()
        curWordIndexLiveData.observe(this) {
            Timber.d("index:$it")
            if (it < 0 || it >= words.size) {
                wordBinding.word = Word.none()
            } else {
                wordBinding.word = words[it]
            }
            wordBinding.tvPage.text = "${it + 1}/${words.size}"
        }
    }

    private fun initWordView() {
        val view = layoutInflater.inflate(R.layout.item_word, binding.layoutWord, false)
        wordBinding = DataBindingUtil.bind(view)!!
        wordBinding.tvForget.setOnClickListener {
            nextWord(false)
        }
        wordBinding.tvRemember.setOnClickListener {
            nextWord(true)
        }
        wordBinding.tvShowTranslation.setOnClickListener {
            wordBinding.isShowTranslation = !(wordBinding.isShowTranslation ?: false)
            wordBinding.tvShowTranslation.text =
                if (wordBinding.isShowTranslation == true) "不看翻译" else "看下翻译"
        }
        binding.layoutWord.addView(wordBinding.root)
    }

    private fun initWords() {
        lifecycleScope.launch {
            enableProgressDialog(true, "初始化词书", false)
            words = withContext(Default) { getWords() }
            repeat(10) {
                if (it < words.size)
                    Timber.d("word:${words[it]}")
            }
            val curIndex = curWordIndexLiveData.value ?: 0
            val nextIndex = withContext(Default) { findNextNewWordIndex(words, curIndex) }
            curWordIndexLiveData.postValue(nextIndex)
            enableProgressDialog(false)
        }
    }

    private fun getWords(): List<Word> {
        return if (isNew)
            AppRep.getWordsByDictId(dictId)
        else
            AppRep.getRecordWordsByDictId(dictId)
    }

    private fun findNextNewWordIndex(words: List<Word>, curIndex: Int): Int {
        if (isNew) {
            var nextIndex = curIndex
            while (
                nextIndex < words.size &&
                AppRep.existRecord(words[nextIndex].id)
            ) {
                nextIndex++
            }
            return if (nextIndex < words.size) nextIndex
            else -1
        } else {
            if (words.isEmpty()) return -1
            return (curIndex + 1) % words.size
        }
    }

    private fun nextWord(isRemember: Boolean) {
        lifecycleScope.launch {
            enableProgressDialog(true, "加载下个单词")
            wordBinding.isShowTranslation = false
            withContext(Default) {
                val curIndex = curWordIndexLiveData.value ?: -1
                if (curIndex >= 0 && curIndex < words.size) {
                    val word = words[curIndex]
                    val record = AppRep.generateRecordById(word.id)
                    val time = System.currentTimeMillis()
                    if (isRemember) {
                        record.state = if (record.state == 2) 3 else 2
                        record.lastDate = time
                    } else {
                        record.state = if (record.state == 1) 0 else 1
                        record.lastDate = time
                    }
                    AppRep.updateRecord(record)
                }
                curWordIndexLiveData.postValue(findNextNewWordIndex(words, curIndex))
            }
            enableProgressDialog(false)
        }
    }

}