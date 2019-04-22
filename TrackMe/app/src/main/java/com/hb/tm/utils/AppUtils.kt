package com.hb.tm.utils

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout


object AppUtils {

    fun setupInput(input: TextInputLayout) {
        input.editText!!.setOnKeyListener { _, _, _ ->
            if (isNotEmpty(input.editText!!.text)) {
                input.error = null
            }
            false
        }
        input.editText!!.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isNotEmpty(input.editText!!.text)) {
                    input.error = null
                }
            }
        })
    }

    private fun isNotEmpty(text: Editable?): Boolean {
        return text != null && text.isNotEmpty()
    }
}
