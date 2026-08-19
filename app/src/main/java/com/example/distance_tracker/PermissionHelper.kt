package com.group3.distancetracker

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionHelper {

    const val LOCATION_PERMISSION_REQUEST_CODE = 1001

    /**
     * Returns true if ACCESS_FINE_LOCATION has already been granted.
     */
    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Requests ACCESS_FINE_LOCATION at runtime.
     * Call this from your Activity when hasLocationPermission() returns false.
     */
    fun requestLocationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    /**
     * Call this inside Activity.onRequestPermissionsResult() to check the outcome.
     * Returns true if permission was granted, false if denied.
     */
    fun onRequestPermissionsResult(
        requestCode: Int,
        grantResults: IntArray
    ): Boolean {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) return false
        return grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
    }
}