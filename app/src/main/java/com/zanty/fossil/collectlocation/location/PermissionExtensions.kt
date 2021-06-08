package com.zanty.fossil.collectlocation.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

// Base
fun Context.shouldRequestPermission(permission: String) =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED

fun Context.permissionGranted(permission: String) =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Activity.requestPermission(permission: Array<String>, requestCode: Int) =
    ActivityCompat.requestPermissions(this, permission, requestCode)

// Location
val Context.isLocationPermissionGranted: Boolean
    get() = permissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)

val Context.isLocationPermissionDenied: Boolean
    get() = shouldRequestPermission(Manifest.permission.ACCESS_FINE_LOCATION)

fun Activity.requestLocationPermission(requestCode: Int) =
    requestPermission(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        else arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        requestCode
    )

// Handle result
fun IntArray.allowLocationPermissionResult() = getOrNull(0) == PackageManager.PERMISSION_GRANTED
