package com.example.distance_tracker

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient

class StartPointManager(private val fusedLocationClient: FusedLocationProviderClient) {
    
    var startLocation: Location? = null
        private set

    /**
     * Fetches the current location and stores it as the start point.
     * Assumes permissions are already granted by PermissionHelper.
     */
    @SuppressLint("MissingPermission")
    fun fetchStartPoint(callback: (Location?) -> Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            startLocation = location
            callback(location)
        }
    }

    /**
     * Clears the stored start point.
     */
    fun clearStartPoint() {
        startLocation = null
    }
}
