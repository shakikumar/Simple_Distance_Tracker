package com.example.distance_tracker

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

/**
 * Manages fetching the end location and calculating the distance from a start location.
 */
class EndPointManager(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient
) {

    // Public read-only property with private set to store the end location
    var endLocation: Location? = null
        private set

    /**
     * Fetches fresh current location, stores it as endLocation, and calculates distance.
     * Uses getCurrentLocation to ensure the coordinates are not stale.
     */
    @SuppressLint("MissingPermission")
    fun setEndPoint(
        startLocation: Location?,
        onResult: (endLoc: Location, distanceMeters: Float) -> Unit
    ) {
        // 1. Validate that a start point exists before proceeding
        if (startLocation == null) {
            Toast.makeText(context, "Please set Start Point first", Toast.LENGTH_SHORT).show()
            return
        }

        // Show feedback that fetching is in progress
        Toast.makeText(context, "Fetching end location...", Toast.LENGTH_SHORT).show()

        // 2. Fetch fresh high-accuracy location
        val cts = CancellationTokenSource()
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    // 3. Store the location in the property
                    endLocation = location

                    // 4. Calculate the distance between start and end points
                    val distance = startLocation.distanceTo(location)

                    // Provide feedback
                    Toast.makeText(context, "End point captured!", Toast.LENGTH_SHORT).show()

                    // 5. Return the results via callback
                    onResult(location, distance)
                } else {
                    Toast.makeText(context, "Unable to get fresh end location. Try again.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Location fetch failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Clears the stored end point.
     */
    fun clearEndPoint() {
        endLocation = null
    }
}
