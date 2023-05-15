package com.example.word.base

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.word.R
import com.example.word.databinding.RootBinding
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {
    open val binding: T by lazy {
        val clazz =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>
        val inflate = clazz.getDeclaredMethod(
            "inflate", LayoutInflater::class.java
        )
        inflate.invoke(null, layoutInflater) as T
    }
    private lateinit var rootBinding: RootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootBinding = RootBinding.inflate(layoutInflater)
        rootBinding.layout.addView(binding.root)
        setContentView(rootBinding.root)
        supportActionBar?.hide()
        initView()
    }

    abstract fun initView()

    fun toActivity(clazz: Class<*>) {
        startActivity(Intent(this, clazz))
    }

    private val requestMap = mutableMapOf<Int, (Intent?) -> Unit>()

    fun toActivityForResult(clazz: Class<*>, requestCode: Int = 1, callback: (Intent?) -> Unit) {
        requestMap[requestCode] = callback
        startActivityForResult(Intent(this, clazz), requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (key in requestMap.keys) {
            if (key == requestCode) {
                requestMap[key]?.invoke(data)
                break
            }
        }
    }

    fun toActivityWithData(clazz: Class<*>, key: String, data: String) {
        startActivity(Intent(this, clazz).apply {
            putExtra(key, data)
        })
    }

    fun showAskDialog(title: String, msg: String, block: (isConfirm: Boolean) -> Unit) {
        AlertDialog.Builder(this).setTitle(title).setMessage(msg)
            .setPositiveButton("确认") { _, _ -> block(true) }
            .setNegativeButton("取消") { _, _ -> block(false) }.show()
    }

    fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun enableProgressDialog(enable: Boolean, text: String = "", cancelable: Boolean = false) {
        enableProgressDialogInner(enable, text, cancelable)
    }

    private var progressDialogCancelable = false
    private var progressIsShowing = false
    private fun enableProgressDialogInner(
        enable: Boolean,
        text: String = "",
        cancelable: Boolean = false
    ) {
        val isVisible = if (enable) View.VISIBLE else View.GONE
        rootBinding.cvDialog.visibility = isVisible
        rootBinding.layoutDialog.visibility = isVisible
        if (enable) rootBinding.pw.spin() else rootBinding.pw.stopSpinning()
        progressIsShowing = enable
        rootBinding.tvDialogContent.text = text
        progressDialogCancelable = cancelable
    }

    override fun onBackPressed() {
        if (progressIsShowing) {
            if (progressDialogCancelable) enableProgressDialogInner(false)
        } else super.onBackPressed()
    }
}
