package com.kobbi.weather.info.ui.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.kobbi.weather.info.R
import com.kobbi.weather.info.databinding.DialogSelectAreaBinding
import com.kobbi.weather.info.presenter.viewmodel.JusoViewModel


class SelectPlaceDialog : DialogFragment() {

    companion object {
        const val TAG = "SelectPlaceDialog"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding: DialogSelectAreaBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_select_area, container, false)
        activity?.application?.let { application ->
            val viewModel = JusoViewModel(application).apply {
                clickEnd.observe(this@SelectPlaceDialog, Observer {
                    this@SelectPlaceDialog.dismiss()
                })
                clickClose.observe(this@SelectPlaceDialog, Observer {
                    this@SelectPlaceDialog.dismiss()
                })
            }
            binding.run {
                jusoVm = viewModel
                lifecycleOwner = this@SelectPlaceDialog
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes
        params?.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        params?.height = ConstraintLayout.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = params as android.view.WindowManager.LayoutParams
    }
}