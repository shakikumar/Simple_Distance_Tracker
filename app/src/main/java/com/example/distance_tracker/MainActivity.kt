package com.example.distance_tracker

import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.LocationServices
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var startPointManager: StartPointManager
    private lateinit var endPointManager: EndPointManager

    private lateinit var tvStartLat: TextView
    private lateinit var tvStartLng: TextView
    private lateinit var tvEndLat: TextView
    private lateinit var tvEndLng: TextView
    private lateinit var tvDistanceValue: TextView
    private lateinit var tvDistanceUnit: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Managers
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startPointManager = StartPointManager(fusedLocationClient)
        endPointManager = EndPointManager(this, fusedLocationClient)

        // Initialize Views
        tvStartLat = findViewById(R.id.tv_start_lat)
        tvStartLng = findViewById(R.id.tv_start_lng)
        tvEndLat = findViewById(R.id.tv_end_lat)
        tvEndLng = findViewById(R.id.tv_end_lng)
        tvDistanceValue = findViewById(R.id.tv_distance_value)
        tvDistanceUnit = findViewById(R.id.tv_distance_unit)

        val btnSetStart: Button = findViewById(R.id.btn_set_start)
        val btnSetEnd: Button = findViewById(R.id.btn_set_end)
        val btnReset: Button = findViewById(R.id.btn_reset)

        // Request Permission on Launch
        if (!PermissionHelper.hasLocationPermission(this)) {
            PermissionHelper.requestLocationPermission(this)
        }

        btnSetStart.setOnClickListener {
            if (PermissionHelper.hasLocationPermission(this)) {
                startPointManager.fetchStartPoint { location ->
                    if (location != null) {
                        updateStartUI(location)
                    } else {
                        Toast.makeText(this, "Failed to get start location", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                PermissionHelper.requestLocationPermission(this)
            }
        }

        btnSetEnd.setOnClickListener {
            if (PermissionHelper.hasLocationPermission(this)) {
                val startLocation = startPointManager.startLocation
                if (startLocation == null) {
                    Toast.makeText(this, "Please set Start Point first", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                endPointManager.setEndPoint(startLocation) { endLoc, distanceMeters ->
                    updateEndUI(endLoc)
                    updateDistanceUI(distanceMeters)
                }
            } else {
                PermissionHelper.requestLocationPermission(this)
            }
        }

        btnReset.setOnClickListener {
            resetTracking()
        }
    }

    private fun updateStartUI(location: Location) {
        tvStartLat.text = String.format(Locale.getDefault(), "%.6f", location.latitude)
        tvStartLng.text = String.format(Locale.getDefault(), "%.6f", location.longitude)
    }

    private fun updateEndUI(location: Location) {
        tvEndLat.text = String.format(Locale.getDefault(), "%.6f", location.latitude)
        tvEndLng.text = String.format(Locale.getDefault(), "%.6f", location.longitude)
    }

    private fun updateDistanceUI(distanceMeters: Float) {
        if (distanceMeters >= 1000) {
            val distanceKm = distanceMeters / 1000
            tvDistanceValue.text = String.format(Locale.getDefault(), "%.2f", distanceKm)
            tvDistanceUnit.text = getString(R.string.unit_km)
        } else {
            tvDistanceValue.text = String.format(Locale.getDefault(), "%.0f", distanceMeters)
            tvDistanceUnit.text = getString(R.string.unit_m)
        }
    }

    private fun resetTracking() {
        startPointManager.clearStartPoint()
        endPointManager.clearEndPoint()
        tvStartLat.text = getString(R.string.label_default_coords)
        tvStartLng.text = getString(R.string.label_default_coords)
        tvEndLat.text = getString(R.string.label_default_coords)
        tvEndLng.text = getString(R.string.label_default_coords)
        tvDistanceValue.text = getString(R.string.label_default_distance)
        tvDistanceUnit.text = getString(R.string.unit_km)
        Toast.makeText(this, "Tracking reset", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionHelper.onRequestPermissionsResult(requestCode, grantResults)) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
}
