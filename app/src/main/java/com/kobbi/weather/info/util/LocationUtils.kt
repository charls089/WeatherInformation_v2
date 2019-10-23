package com.kobbi.weather.info.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.kobbi.weather.info.presenter.model.data.GridData
import java.io.IOException
import java.util.*
import kotlin.math.*

class LocationUtils private constructor() {
    companion object {
        private const val TAG = "LocationUtils"
        private const val RE = 6371.00877 // 지구 반경(km)
        private const val GRID = 5.0 // 격자 간격(km)
        private const val SLAT1 = 30.0 // 투영 위도1(degree)
        private const val SLAT2 = 60.0 // 투영 위도2(degree)
        private const val OLON = 126.0 // 기준점 경도(degree)
        private const val OLAT = 38.0 // 기준점 위도(degree)
        private const val XO = 43.0 // 기준점 X좌표(GRID)
        private const val YO = 136.0 // 기1준점 Y좌표(GRID)

        private const val DEGRAD = Math.PI / 180.0
        private const val RADDEG = 180.0 / Math.PI

        private const val re = RE / GRID
        private const val slat1 = SLAT1 * DEGRAD
        private const val slat2 = SLAT2 * DEGRAD
        private const val olon = OLON * DEGRAD
        private const val olat = OLAT * DEGRAD

        fun convertAddress(context: Context, address: String): LatLng? {
            return try {
                val filterAddress =
                    if (address.contains("세종")) address.replace("세종특별자치시", "연기군") else address
                val list = Geocoder(context).getFromLocationName(filterAddress, 1)
                DLog.d(TAG, message = "convertAddress($filterAddress).list : $list")
                if (list.isNotEmpty()) {
                    val data = list[0]
                    LatLng(data.latitude, data.longitude)
                } else {
                    null
                }

            } catch (e: IOException) {
                DLog.e(TAG, message = e.message)
                null
            }
        }

        fun convertGrid(latLng: LatLng?): GridData? {
            latLng?.let {
                val latitude = latLng.latitude
                val longitude = latLng.longitude
                return convertGrid(latitude, longitude)
            }
            return null
        }

        private fun convertGrid(latitude: Double, longitude: Double): GridData {
            var sn = tan(Math.PI * 0.25 + slat2 * 0.5) / tan(Math.PI * 0.25 + slat1 * 0.5)
            sn = ln(cos(slat1) / cos(slat2)) / ln(sn)
            var sf = tan(Math.PI * 0.25 + slat1 * 0.5)
            sf = sf.pow(sn) * cos(slat1) / sn
            var ro = tan(Math.PI * 0.25 + olat * 0.5)
            ro = re * sf / ro.pow(sn)

            var ra = tan(Math.PI * 0.25 + latitude * DEGRAD * 0.5)
            ra = re * sf / ra.pow(sn)
            var theta = longitude * DEGRAD - olon
            if (theta > Math.PI) theta -= 2.0 * Math.PI
            if (theta < -Math.PI) theta += 2.0 * Math.PI
            theta *= sn

            val x = floor(ra * sin(theta) + XO + 0.5).toInt()
            val y = floor(ro - ra * cos(theta) + YO + 0.5).toInt()
            return GridData(x, y)
        }

        private fun getAddress(context: Context, location: Location): Address? {
            val latitude = location.latitude
            val longitude = location.longitude
            return try {
                val addressList =
                    Geocoder(context, Locale.getDefault()).getFromLocation(latitude, longitude, 4)

                if (!addressList.isNullOrEmpty()) {
                    addressList[addressList.size - 1]
                } else {
                    null
                }
            } catch (e: IOException) {
                DLog.writeLogFile(context, message = e.message)
                null
            }
        }

        fun getAddressLine(context: Context, location: Location): String {
            return getAddress(context, location)?.getAddressLine(0)?.replace("대한민국 ","") ?: ""
        }

        @JvmStatic
        fun splitAddressLine(context: Context, location: Location): List<String> {
            return splitAddressLine(getAddress(context, location)?.getAddressLine(0))
        }

        @JvmStatic
        fun splitAddressLine(address: String?): List<String> {
            return address?.split(' ') ?: emptyList()
        }

        @JvmStatic
        fun getCityCode(address: String): String {
            val splitAddress = splitAddressLine(address)
            return if (splitAddress.size >= 2) {
                val result = if (splitAddress[0].contains(Regex(".*도"))) {
                    splitAddress[1]
                } else {
                    splitAddress[0]
                }
                result.substring(0..1)
            } else {
                ""
            }
        }
    }
}