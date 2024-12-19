package com.example.storyapp.ui.maps

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.R
import com.example.storyapp.data.model.ListStoryItem
import com.example.storyapp.databinding.ActivityMapsBinding
import com.example.storyapp.utils.Result
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val boundsBuilder = LatLngBounds.Builder()

    private val mapsViewModel: MapsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setMapStyle()
        observeStoryMaps()

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        val dicodingSpace = LatLng(-6.8957643, 107.6338462)
        mMap.addMarker(
            MarkerOptions().position(dicodingSpace).title("Dicoding Space")
                .snippet("Batik Kumeli No.50")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dicodingSpace, 7F))

        mMap.setOnCameraIdleListener {
            val position = mMap.cameraPosition
            Log.d(TAG, "Camera moved to: ${position.target}, Zoom: ${position.zoom}")
        }

        mapsViewModel.getStoriesWithLocation()
    }

    private fun observeStoryMaps() {
        lifecycleScope.launch {
            mapsViewModel.storiesMaps.collectLatest { result ->
                when (result) {
                    Result.Initial -> Unit
                    Result.Loading -> showLoading(true)
                    is Result.Success -> {
                        showLoading(false)
                        displayMarkers(result.data)
                    }

                    is Result.Error -> {
                        showLoading(false)
                        showSnackBar("Error: ${result.error}")
                    }
                }
            }
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun displayMarkers(data: List<ListStoryItem>) {
        data.forEach { data ->
            val lat = data.lat.toString().toDoubleOrNull()
            val lon = data.lon.toString().toDoubleOrNull()

            if (lat != null && lon != null) {
                val position = LatLng(lat, lon)
                mMap.addMarker(
                    MarkerOptions().position(position).title(data.name).snippet(data.description)
                )
                boundsBuilder.include(position)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}