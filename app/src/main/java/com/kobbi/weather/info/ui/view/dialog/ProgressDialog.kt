package com.kobbi.weather.info.ui.view.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.os.postDelayed
import com.kobbi.weather.info.R

class ProgressDialog(context: Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_progress)
        setCancelable(false)
    }

    override fun show() {
        super.show()
        Handler(Looper.getMainLooper()).postDelayed(1500) {
            dismiss()
        }
    }
}