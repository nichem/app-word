package com.example.word

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.word.base.BaseActivity
import com.example.word.data.AppRep
import com.example.word.data.Word
import com.example.word.databinding.ActivitySearchBinding
import com.example.word.databinding.ItemSearchWordBinding
import com.example.word.databinding.ItemWordBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class SearchActivity : BaseActivity<ActivitySearchBinding>() {
    companion object {
        private const val KEY_SEARCH_KEY = "searchKey"
        fun start(context: Context, searchKey: String) {
            context.startActivity(Intent(context, SearchActivity::class.java).apply {
                putExtra(KEY_SEARCH_KEY, searchKey)
            })
        }
    }

    private lateinit var wordBinding: ItemWordBinding

    override fun initView() {
        initWordView()
        val searchKey = intent.getStringExtra(KEY_SEARCH_KEY) ?: ""
        binding.etSearch.setText(searchKey)
        binding.btnSearch.setOnClickListener {
            search()
        }
        binding.etSearch.setOnEditorActionListener { _, actionId, event ->
            Timber.d("actionId:$actionId")
            if (event?.keyCode == KeyEvent.KEYCODE_ENTER || EditorInfo.IME_ACTION_DONE == actionId) {
                // 在这里执行回车键按下时的操作
                search()
                true
            } else false

        }
        binding.rvSearchList.adapter = adapter
        adapter.setOnItemClickListener { _, _, position ->
            binding.layoutWord.visibility = View.VISIBLE
            val word = adapter.data[position]
            wordBinding.word = word
        }
        search()
    }

    private fun initWordView() {
        val view = layoutInflater.inflate(R.layout.item_word, binding.layoutWord, false)
        wordBinding = DataBindingUtil.bind(view)!!
        wordBinding.isShowTranslation = true
        wordBinding.layoutBtn.visibility = View.INVISIBLE
        binding.layoutWord.addView(wordBinding.root)
    }

    private val adapter =
        object : BaseQuickAdapter<Word, BaseViewHolder>(R.layout.item_search_word) {
            override fun convert(holder: BaseViewHolder, item: Word) {
                val binding = ItemSearchWordBinding.bind(holder.itemView)
                binding.tvWord.text = item.word
                binding.tvTranslation.text =
                    item.translation.ifBlank { item.definition }
            }

        }

    private fun search() {
        lifecycleScope.launch {
            enableProgressDialog(true, "搜索中", false)
            val searchKey = binding.etSearch.text.toString().trim()
            if (searchKey.isBlank()) {
                binding.etSearch.setText("")
                binding.etSearch.hint = "搜索词为空"
                enableProgressDialog(false)
            }
            val words = withContext(Default) { AppRep.findWordsByWordLike(searchKey) }
            adapter.setList(words)
            enableProgressDialog(false)
        }
    }

    override fun onBackPressed() {
        if (binding.layoutWord.visibility == View.VISIBLE) {
            binding.layoutWord.visibility = View.GONE
        } else super.onBackPressed()
    }
}