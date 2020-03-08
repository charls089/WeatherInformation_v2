package com.kobbi.weather.info.ui.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
        isCancelable = false
        val binding: DialogSelectAreaBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_select_area, container, false)
        with(binding) {
            activity?.run {
                jusoVm = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                    .create(JusoViewModel::class.java).apply {
                        clickEnd.observe(this@run, Observer {
                            this@SelectPlaceDialog.dismiss()
                        })
                        clickClose.observe(this@run, Observer {
                            this@SelectPlaceDialog.dismiss()
                        })
                        jusoList.observe(this@run, Observer {
                            context?.applicationContext?.let { context ->
                                rvDialogAreaList.startAnimation(
                                    AnimationUtils.loadAnimation(
                                        context,
                                        R.anim.translate_up
                                    )
                                )
                            }
                        })
                    }
            }
            lifecycleOwner = this@SelectPlaceDialog
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes?.apply {
            width = ConstraintLayout.LayoutParams.MATCH_PARENT
            height = ConstraintLayout.LayoutParams.MATCH_PARENT
        }
        dialog?.window?.attributes = params as android.view.WindowManager.LayoutParams
    }
}