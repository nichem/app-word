package com.example.word.util

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.example.word.SearchActivity

fun TextView.addSelectionMenuOption(option: String, block: (selectionText: String) -> Unit) {
    customSelectionActionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menu?.add(option)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            if (item?.title == option) {
                val start = selectionStart
                val end = selectionEnd
                val selectionText = text.substring(start, end)
                block(selectionText)
                mode?.finish()
            }
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            clearFocus()
        }
    }
}