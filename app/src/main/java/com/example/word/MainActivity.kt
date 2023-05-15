package com.example.word

import android.animation.ValueAnimator
import androidx.lifecycle.lifecycleScope
import com.example.word.base.BaseActivity
import com.example.word.data.AppRep
import com.example.word.data.DatabaseUtil
import com.example.word.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.FileOutputStream

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun initView() {
        initDatabase()
        initProgress()
        binding.btnSetting.setOnClickListener {
            toActivityForResult(SettingActivity::class.java) {

                initProgress()
            }
        }
    }

    private fun initProgress() {
        lifecycleScope.launch {
            val dictId = DatabaseUtil.curDictId

            val rememberCount = withContext(Default) { AppRep.getRememberWordCount(dictId) }
            val totalCount = withContext(Default) { AppRep.getWordCount(dictId) }

            Timber.d("rememberCount:$rememberCount totalCount:$totalCount")
            binding.tvProgressLabel.text = DatabaseUtil.curDict.name
            val valueAnimator = ValueAnimator.ofInt(0, rememberCount)
            valueAnimator.duration = 500L
            valueAnimator.addUpdateListener {
                val value = it.animatedValue as Int
                binding.tvProgress.text = "$value/$totalCount"
            }
            valueAnimator.start()
        }
    }

    private fun initDatabase() {

        lifecycleScope.launch(Dispatchers.IO) {
            val fileNameList = listOf("dict.db", "dict.db-shm", "dict.db-wal")
            val dbSize = getDatabasePath(fileNameList[0]).length()
            if (dbSize < 10 * 1024 * 1024) {
                enableProgressDialog(true, "初始化数据库请稍等...")
                fileNameList.forEach {
                    Timber.d("copy $it")
                    copyFileToDatabase(it)
                }
            }
            withContext(Dispatchers.Main) { enableProgressDialog(false) }
        }
    }

    private fun copyFileToDatabase(fileName: String) {
        val file = getDatabasePath(fileName)
        val ins = assets.open(fileName)
        Timber.d("copy to ${file.path}")
        val outs = FileOutputStream(file)
        ins.copyTo(outs)
        ins.close()
        outs.close()
    }

}