package com.example.distance_tracker

import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var startPointManager: StartPointManager
    private lateinit var endPointManager: EndPointManager

    // UI Elements - Tracker
    private lateinit var layoutTracker: ConstraintLayout
    private lateinit var tvStartLat: TextView
    private lateinit var tvStartLng: TextView
    private lateinit var tvEndLat: TextView
    private lateinit var tvEndLng: TextView
    private lateinit var tvDistanceValue: TextView
    private lateinit var tvDistanceUnit: TextView
    private lateinit var btnSetStart: Button
    private lateinit var btnSetEnd: Button
    private lateinit var btnReset: Button
    private lateinit var btnStartTracking: Button

    // UI Elements - History
    private lateinit var layoutHistory: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        setupEdgeToEdge()
        initManagers()
        initUI()
        setupBottomNavigation()
        setupClickListeners()
        
        // Request permissions on launch
        if (!PermissionHelper.hasLocationPermission(this)) {
            PermissionHelper.requestLocationPermission(this)
        }
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initManagers() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startPointManager = StartPointManager(fusedLocationClient)
        endPointManager = EndPointManager(this, fusedLocationClient)
    }

    private fun initUI() {
        layoutTracker = findViewById(R.id.layout_tracker)
        layoutHistory = findViewById(R.id.layout_history)
        
        tvStartLat = findViewById(R.id.tv_start_lat)
        tvStartLng = findViewById(R.id.tv_start_lng)
        tvEndLat = findViewById(R.id.tv_end_lat)
        tvEndLng = findViewById(R.id.tv_end_lng)
        tvDistanceValue = findViewById(R.id.tv_distance_value)
        tvDistanceUnit = findViewById(R.id.tv_distance_unit)
        
        btnSetStart = findViewById(R.id.btn_set_start)
        btnSetEnd = findViewById(R.id.btn_set_end)
        btnReset = findViewById(R.id.btn_reset)
        btnStartTracking = findViewById(R.id.btn_start_tracking)
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_tracker -> {
                    layoutTracker.visibility = View.VISIBLE
                    layoutHistory.visibility = View.GONE
                    true
                }
                R.id.navigation_history -> {
                    layoutTracker.visibility = View.GONE
                    layoutHistory.visibility = View.VISIBLE
                    true
                }
                else -> false
            }
        }
    }

    private fun setupClickListeners() {
        btnSetStart.setOnClickListener {
            if (PermissionHelper.hasLocationPermission(this)) {
                startPointManager.fetchStartPoint { location ->
                    if (location != null) {
                        updateStartUI(location)
                        Toast.makeText(this, "Start point set!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
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

        btnStartTracking.setOnClickListener {
            layoutTracker.visibility = View.VISIBLE
            layoutHistory.visibility = View.GONE
            findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = R.id.navigation_tracker
        }
    }

    private fun updateStartUI(location: Location) {
        tvStartLat.text = String.format(Locale.US, "%.6f", location.latitude)
        tvStartLng.text = String.format(Locale.US, "%.6f", location.longitude)
    }

    private fun updateEndUI(location: Location) {
        tvEndLat.text = String.format(Locale.US, "%.6f", location.latitude)
        tvEndLng.text = String.format(Locale.US, "%.6f", location.longitude)
    }

    private fun updateDistanceUI(distanceMeters: Float) {
        if (distanceMeters >= 1000) {
            val km = distanceMeters / 1000
            tvDistanceValue.text = String.format(Locale.US, "%.2f", km)
            tvDistanceUnit.text = getString(R.string.unit_km)
        } else {
            tvDistanceValue.text = String.format(Locale.US, "%.0f", distanceMeters)
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
