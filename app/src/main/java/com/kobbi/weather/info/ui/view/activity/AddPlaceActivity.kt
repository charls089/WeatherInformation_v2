package com.kobbi.weather.info.ui.view.activity

import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.kobbi.weather.info.R
import com.kobbi.weather.info.databinding.ActivityAddPlaceBinding
import com.kobbi.weather.info.presenter.listener.OnLocationListener
import com.kobbi.weather.info.presenter.location.LocationManager
import com.kobbi.weather.info.presenter.viewmodel.PlaceViewModel
import com.kobbi.weather.info.ui.view.dialog.SelectPlaceDialog
import com.kobbi.weather.info.util.LocationUtils
import kotlin.concurrent.thread

class AddPlaceActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mGoogleMap: GoogleMap? = null
    private var mPlaceVm: PlaceViewModel? = null
    private var mIsMultiChoice: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityAddPlaceBinding>(
            this, R.layout.activity_add_place
        )
        mPlaceVm = PlaceViewModel(application).apply {
            isMultiCheck.observe(this@AddPlaceActivity,
                Observer { isMulti ->
                    mIsMultiChoice = isMulti
                    invalidateOptionsMenu()
                })
            clickPosition.observe(this@AddPlaceActivity, Observer { address ->
                val latLng = LocationUtils.convertAddress(applicationContext, address)
                setMapView(latLng)
            })
        }
        binding.placeVm = mPlaceVm
        binding.lifecycleOwner = this
        val mapView = supportFragmentManager.findFragmentById(R.id.fragment_map_view)
        mapView?.let { mapFragment ->
            if (mapFragment is SupportMapFragment) mapFragment.getMapAsync(this)
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mGoogleMap = googleMap
        LocationManager.getLocation(applicationContext, object : OnLocationListener {
            override fun onComplete(responseCode: Int, location: Location?) {
                location?.let {
                    setMapView(LatLng(it.latitude, it.longitude))
                } ?: setMapView(LatLng(37.5642135, 127.0016985))
            }
        })
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_add_place -> {
                thread {
                    runOnUiThread {
                        SelectPlaceDialog().show(
                            supportFragmentManager,
                            SelectPlaceDialog.TAG
                        )
                    }
                }
            }
            R.id.action_remove_place -> {
                AlertDialog.Builder(this).run {
                    setTitle(R.string.title_dialog_delete)
                    setMessage(R.string.info_delete_message)
                    setCancelable(false)
                    setPositiveButton(R.string.symbol_yes) { dialog, _ ->
                        mPlaceVm?.deletePlace()
                        dialog.dismiss()
                    }
                    setNegativeButton(R.string.symbol_cancel) { dialog, _ ->
                        mPlaceVm?.clearSelectedPlace()
                        dialog.dismiss()
                    }
                    create().show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setMapView(latLng: LatLng?) {
        mGoogleMap?.let { googleMap ->
            latLng?.let {
                val markerOptions = MarkerOptions().apply {
                    position(latLng)
                }
                googleMap.clear()
                googleMap.addMarker(markerOptions)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
        }
    }
}
