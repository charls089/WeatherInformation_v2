package com.kobbi.weather.info.ui.view.activity

import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kobbi.weather.info.R
import com.kobbi.weather.info.databinding.ActivityAddPlaceBinding
import com.kobbi.weather.info.presenter.viewmodel.PlaceViewModel
import com.kobbi.weather.info.ui.view.dialog.MapViewDialog
import com.kobbi.weather.info.ui.view.dialog.SelectPlaceDialog
import com.kobbi.weather.info.util.DLog
import com.kobbi.weather.info.util.LocationUtils
import com.kobbi.weather.info.util.Utils

class AddPlaceActivity : AppCompatActivity() {
    private val mPlaceVm: PlaceViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(PlaceViewModel::class.java)
    }
    private var mIsMultiChoice: Boolean = false
    private var mIsLimit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityAddPlaceBinding>(
            this, R.layout.activity_add_place
        ).run {
            placeVm = mPlaceVm.apply {
                isMultiCheck.observe(this@AddPlaceActivity,
                    Observer { isMulti ->
                        mIsMultiChoice = isMulti
                        invalidateOptionsMenu()
                    })
                clickPosition.observe(this@AddPlaceActivity, Observer { address ->
                    val latLng = LocationUtils.convertAddress(applicationContext, address)
                    MapViewDialog(latLng).show(supportFragmentManager, MapViewDialog.TAG)
                    DLog.d(
                        tag = "PlaceViewModel",
                        message = "clickPosition.observe() --> latLng : $latLng"
                    )
                })
                place.observe(this@AddPlaceActivity, Observer {
                    mIsLimit = it.size >= 5
                })
            }
            lifecycleOwner = this@AddPlaceActivity
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        if (mIsMultiChoice) {
            menuInflater.inflate(R.menu.add_place_delete, menu)
        } else {
            menuInflater.inflate(R.menu.add_place, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_place -> {
                if (mIsLimit) {
                    Utils.showAlertDialog(
                        this@AddPlaceActivity,
                        R.string.title_add_place_maximum,
                        R.string.info_add_place_maximum,
                        DialogInterface.OnClickListener { dialog, _ ->
                            dialog.dismiss()
                        })
                    return false
                }
                SelectPlaceDialog().show(supportFragmentManager, SelectPlaceDialog.TAG)
            }
            R.id.action_remove_place -> {
                Utils.showAlertDialog(
                    this@AddPlaceActivity,
                    R.string.title_dialog_delete,
                    R.string.info_delete_message,
                    DialogInterface.OnClickListener { dialog, _ ->
                        mPlaceVm.deletePlace()
                        dialog.dismiss()
                    },
                    DialogInterface.OnClickListener { dialog, _ ->
                        mPlaceVm.clearSelectedPlace()
                        dialog.dismiss()
                    })
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
