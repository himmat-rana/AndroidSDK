package io.supportgenie.androidlibrary.view.common

import android.view.inputmethod.EditorInfo
import com.google.android.material.textfield.TextInputEditText

fun TextInputEditText.onSubmit(func: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        println("inside setOnEditorActionListener actionId $actionId")
        // IME_ACTION_UNSPECIFIED is a hack, should be removed later
        if ((actionId == EditorInfo.IME_ACTION_UNSPECIFIED) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_SEND)) {
            func()
        }

        true

    }
}
