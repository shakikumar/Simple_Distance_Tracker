package com.example.distance_tracker

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class StartPointManager(private val fusedLocationClient: FusedLocationProviderClient) {
    
    var startLocation: Location? = null
        private set

    /**
     * Fetches the current high-accuracy location and stores it as the start point.
     * Uses getCurrentLocation to ensure a fresh result instead of a stale cached one.
     */
    @SuppressLint("MissingPermission")
    fun fetchStartPoint(callback: (Location?) -> Unit) {
        val cts = CancellationTokenSource()
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
            .addOnSuccessListener { location ->
                startLocation = location
                callback(location)
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    /**
     * Clears the stored start point.
     */
    fun clearStartPoint() {
        startLocation = null
    }
}
