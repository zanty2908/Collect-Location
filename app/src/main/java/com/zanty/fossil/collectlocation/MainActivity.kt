package com.zanty.fossil.collectlocation

import android.animation.ValueAnimator
import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.zanty.fossil.collectlocation.databinding.ActivityMainBinding
import com.zanty.fossil.collectlocation.location.LocationHandler
import com.zanty.fossil.collectlocation.location.isLocationPermissionGranted

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private val mLocationHandler by lazy { LocationHandler.create(this) }

    private val findingAnimation by lazy {
        ValueAnimator.ofFloat(0f, 1f)
            .apply {
                duration = 1000L
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                addUpdateListener {
                    mBinding.ivCenter.alpha = it.animatedValue as Float
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        observerData()
    }

    override fun onStart() {
        super.onStart()
        mLocationHandler.checkPermissions()
        if (isLocationPermissionGranted)
            mLocationHandler.checkGPSAndRequest()

    }

    private fun observerData() = with(mLocationHandler) {
        locationPermissionGrantedLive.observe(this@MainActivity) {
            if (it == true) {
                checkGPSAndRequest()
                requestLastLocation()
                findingAnimation.run {
                    if (isRunning) return@run
                    start()
                }
            } else
                requestLocationPermissions()
        }

        currentLocationLive.observe(this@MainActivity) {
            val currentLocation = it ?: return@observe
            drawViews(currentLocation)
        }
    }

    private var animator: ValueAnimator? = null
    private var bearingAnimator: ValueAnimator? = null

    private fun drawViews(location: Location) = with(mBinding) {
        group.isVisible = true

        val latitude = location.latitude
        val longitude = location.longitude
        tvLatitude.text = latitude.toString()
        tvLongitude.text = longitude.toString()

        bearingAnimator?.run { if (isStarted) cancel() }
        val currentRotate = ivDirection.rotation
        val newRotation = 180 + location.bearing
        bearingAnimator = ValueAnimator.ofFloat(currentRotate, newRotation)
            .apply {
                duration = 1000L
                addUpdateListener {
                    ivDirection.rotation = it.animatedValue as Float
                }
            }
        bearingAnimator?.start()

        animator?.run { if (isStarted) cancel() }
        /**
         * Accuracy has value from 0 to 100
         * Scale of views is 0.4 to 1
         */
        val accuracy = location.accuracy
        val scaleValue = accuracy / 100 + 0.3f
        val expectedValue = if (scaleValue > 1) 1f else scaleValue
        val currentScale = vAccuracy.scaleX
        animator = ValueAnimator.ofFloat(currentScale, expectedValue)
            .apply {
                duration = 1000L
                addUpdateListener {
                    val value = it.animatedValue as Float
                    vAccuracy.scaleX = value
                    vAccuracy.scaleY = value
                }
            }
        animator?.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mLocationHandler.handleLocationPermissionResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mLocationHandler.handleTurnGPSOnResult(requestCode, resultCode)
    }

}
