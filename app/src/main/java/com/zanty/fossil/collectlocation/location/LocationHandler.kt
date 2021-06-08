package com.zanty.fossil.collectlocation.location

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import com.google.android.gms.location.LocationRequest

interface LocationHandler {

    companion object {
        fun create(activity: AppCompatActivity): LocationHandler = LocationHandlerImpl(activity)
    }

    val locationRequest: LocationRequest
    val currentLocationLive: LiveData<Location>
    fun requestLastLocation()
    fun getLastLocation()

    // Permission
    /**
     * Has 3 states:
     * 1. true -> location permission is granted
     * 2. false -> location permission is unavailable
     * 3. null -> location permission is deny
     */
    val locationPermissionStateLive: LiveData<LocationPermissionState>
    val locationPermissionGrantedLive: LiveData<Boolean>
    fun checkPermissions()
    fun requestLocationPermissions()
    fun handleLocationPermissionResult(requestCode: Int, grantResults: IntArray)

    // GPS
    val onGPSLive: LiveData<Boolean>
    fun checkGPSAndRequest()
    fun handleTurnGPSOnResult(requestCode: Int, resultCode: Int)

}
