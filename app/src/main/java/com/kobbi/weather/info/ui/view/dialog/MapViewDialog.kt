package com.kobbi.weather.info.ui.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.kobbi.weather.info.R

class MapViewDialog(private val latLng: LatLng?) : DialogFragment(), OnMapReadyCallback {
    companion object {
        const val TAG = "MapViewDialog"
    }

    private var mGoogleMap: GoogleMap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_map_vew, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
        fragmentManager?.findFragmentById(R.id.fragment_map_view)?.let { mapFragment ->
            if (mapFragment is SupportMapFragment)
                mapFragment.getMapAsync(this)
        }
    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes?.apply {
            width = FrameLayout.LayoutParams.MATCH_PARENT
            height = 1000
        }
        dialog?.window?.attributes = params as android.view.WindowManager.LayoutParams
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentManager?.run {
            findFragmentById(R.id.fragment_map_view)?.let { mapFragment ->
                if (mapFragment is SupportMapFragment)
                    beginTransaction().remove(mapFragment).commit()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mGoogleMap = googleMap
        setMapView(latLng)
    }

    private fun setMapView(latLng: LatLng?) {
        mGoogleMap?.let { googleMap ->
            latLng?.let {
                googleMap.clear()
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
        }
    }
}