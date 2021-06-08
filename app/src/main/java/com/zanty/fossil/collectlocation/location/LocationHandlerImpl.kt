package com.zanty.fossil.collectlocation.location

import android.annotation.SuppressLint
import android.app.Activity
import android.content.IntentSender
import android.location.Location
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

private const val REQUEST_CHECK_GPS_ENABLE = 33
private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34

internal class LocationHandlerImpl constructor(
    private val activity: AppCompatActivity
) : LocationHandler, LifecycleObserver {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(activity)
    }
    override val locationRequest: LocationRequest by lazy {
        LocationRequest.create()
            .apply {
                smallestDisplacement = 0f
                interval = 5000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
    }
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.run(mCurrentLocation::postValue)
        }
    }

    private val mCurrentLocation = MutableLiveData<Location>()
    override val currentLocationLive: LiveData<Location> get() = mCurrentLocation

    init {
        activity.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun clean() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    override fun requestLastLocation() {
        if (!activity.isLocationPermissionGranted)
            return

        fusedLocationClient.removeLocationUpdates(locationCallback)
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    @SuppressLint("MissingPermission")
    override fun getLastLocation() {
        if (!activity.isLocationPermissionGranted)
            return

        fusedLocationClient.lastLocation.addOnSuccessListener {
            it?.run(mCurrentLocation::postValue)
        }
    }

    private val mLocationPermissionStateLive = MutableLiveData(LocationPermissionState.UNAVAILABLE)
    override val locationPermissionStateLive: LiveData<LocationPermissionState>
        get() = mLocationPermissionStateLive

    override val locationPermissionGrantedLive = locationPermissionStateLive
        .map { it == LocationPermissionState.ALLOW }

    private fun updateLocationPermission(isAllow: Boolean) {
        val state = when {
            isAllow -> LocationPermissionState.ALLOW

            locationPermissionStateLive.value == null -> LocationPermissionState.UNAVAILABLE

            else -> mLocationPermissionStateLive.value
        }
        if (state != mLocationPermissionStateLive.value)
            mLocationPermissionStateLive.postValue(state)
    }

    override fun checkPermissions(): Unit = with(activity) {
        updateLocationPermission(isLocationPermissionGranted)
    }

    override fun requestLocationPermissions() = with(activity) {
        val isAllowPermission = isLocationPermissionGranted
        // request permission when deny
        if (!isAllowPermission) {
            requestLocationPermission(REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun handleLocationPermissionResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            val isAllow = grantResults.allowLocationPermissionResult()
            val state = if (isAllow) LocationPermissionState.ALLOW
            else LocationPermissionState.DENY
            if (state != mLocationPermissionStateLive.value)
                mLocationPermissionStateLive.postValue(state)
        }
    }

    private val mOnGPSLive = MutableLiveData(false)
    override val onGPSLive: LiveData<Boolean> get() = mOnGPSLive

    /**
     * Using check GPS is turned on
     * Show request dialog to turn GPS on when it is turned off
     */
    override fun checkGPSAndRequest(): Unit = with(activity) {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        LocationServices.getSettingsClient(this)
            .checkLocationSettings(builder.build())
            .addOnSuccessListener { mOnGPSLive.postValue(true) }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        exception.startResolutionForResult(this, REQUEST_CHECK_GPS_ENABLE)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
                mOnGPSLive.postValue(false)
            }
    }

    /**
     * Using handle turn GPS on result
     * Call at onActivityResult in this activity
     */
    override fun handleTurnGPSOnResult(requestCode: Int, resultCode: Int) {
        if (requestCode == REQUEST_CHECK_GPS_ENABLE && resultCode == Activity.RESULT_OK) {
            mOnGPSLive.postValue(true)
        }
    }

}
