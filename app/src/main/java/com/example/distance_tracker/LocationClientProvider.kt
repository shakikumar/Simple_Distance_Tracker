package com.example.distance_tracker

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

/**
 * Singleton-style wrapper for the FusedLocationProviderClient.
 * Member 3 and Member 4 call getClient() to obtain the shared instance.
 */
object LocationClientProvider {

    private var client: FusedLocationProviderClient? = null

    /**
     * Call once in MainActivity.onCreate() to initialize.
     * Subsequent calls return the same instance.
     */
    fun init(context: Context): FusedLocationProviderClient {
        if (client == null) {
            client = LocationServices.getFusedLocationProviderClient(context)
        }
        return client!!
    }

    /**
     * Returns the initialized client.
     * Throws if init() was never called.
     */
    fun getClient(): FusedLocationProviderClient {
        return client
            ?: throw IllegalStateException(
                "LocationClientProvider not initialized. Call init(context) in MainActivity.onCreate() first."
            )
    }
}