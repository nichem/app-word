package com.example.word

import android.animation.ValueAnimator
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
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
        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        binding.tvVersion.text = "v$versionName"
        initDatabase()
        initProgress()
        binding.btnSetting.setOnClickListener {
            toActivity(SettingActivity::class.java)
        }
        binding.tvNew.setOnClickListener {
            ReciteActivity.start(this, DatabaseUtil.curDictId, true)
        }
        binding.tvOld.setOnClickListener {
            ReciteActivity.start(this, DatabaseUtil.curDictId, false)
        }
        binding.tvProgress.setOnLongClickListener {
            showAskDialog("警告", "是否清除该词书所有记录？") {
                if (it) {
                    clearRecords(DatabaseUtil.curDictId)
                }
            }
            true
        }
        binding.btnSearch.setOnClickListener {
            search()
        }
        binding.etSearch.setOnEditorActionListener { _, actionId, event ->
            if (event?.keyCode == KeyEvent.KEYCODE_ENTER || EditorInfo.IME_ACTION_DONE == actionId) {
                // 在这里执行回车键按下时的操作
                search()
                true;
            } else false;
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

    override fun onResume() {
        super.onResume()
        initProgress()
    }

    private fun clearRecords(dictId: String) {
        lifecycleScope.launch {
            enableProgressDialog(true, "清除记录中", false)
            withContext(Default) {
                AppRep.clearRecordsByDictId(dictId)
            }
            enableProgressDialog(false)
            initProgress()
        }
    }

    private fun search() {
        val searchKey = binding.etSearch.text.toString().trim()
        if (searchKey.isBlank()) {
            binding.etSearch.setText("")
            binding.etSearch.hint = "搜索词为空"
        }
        SearchActivity.start(this, searchKey)
    }

}