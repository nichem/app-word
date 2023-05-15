package com.example.word

import android.graphics.Color
import androidx.lifecycle.lifecycleScope
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.word.base.BaseActivity
import com.example.word.data.AppRep
import com.example.word.data.DatabaseUtil
import com.example.word.databinding.ActivitySettingBinding
import com.example.word.databinding.ListDictBinding
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class SettingActivity : BaseActivity<ActivitySettingBinding>() {
    override fun initView() {
        binding.rvDict.adapter = adapter
        adapter.setList(DatabaseUtil.dictLists)
        adapter.addChildClickViewIds(R.id.tv_cover)
        adapter.setOnItemChildClickListener { adapter, view, position ->
            val dict = adapter.data[position] as DatabaseUtil.Dict
            if (dict.id != DatabaseUtil.curDictId)
                showAskDialog("提示", "是否使用词书《${dict.name}》?") {
                    if (it) {
                        DatabaseUtil.curDictId = dict.id
                        setResult(1)
                        finish()
                    }
                }
            else finish()
        }
    }

    private val adapter =
        object : BaseQuickAdapter<DatabaseUtil.Dict, BaseViewHolder>(R.layout.list_dict) {
            override fun convert(holder: BaseViewHolder, item: DatabaseUtil.Dict) {
                val binding = ListDictBinding.bind(holder.itemView)
                binding.tvCover.setBackgroundColor(generateRandomColor())
                binding.tvCover.text =
                    if (DatabaseUtil.curDictId == item.id) item.name + "\n（当前）" else item.name
                val id = item.id
                binding.tvInfo.tag = id
                lifecycleScope.launch {
                    val count = withContext(Default) { AppRep.getWordCount(id) }
                    if (binding.tvInfo.tag == id) {
                        binding.tvInfo.text = "${count}词"
                    }
                }
            }

        }

    fun generateRandomColor(): Int {
        val random = Random.Default

        val r = random.nextInt(256)
        val g = random.nextInt(256)
        val b = random.nextInt(256)

        return Color.parseColor(String.format("#%02X%02X%02X", r, g, b))
    }
}