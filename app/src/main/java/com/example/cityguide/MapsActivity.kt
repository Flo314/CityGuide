package com.example.cityguide

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

/**
 *
 */
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    // Request location permission
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


    /* Checks if the app has been granted the ACCESS_FINE_LOCATION permission.
     If it hasn’t, then request it from the user.*/
    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        // *** Getting current location ***

        /* Active la my-locationcouche qui dessine un point bleu clair sur l'emplacement de l'utilisateur.
         Il ajoute également un bouton à la carte qui,
         lorsque vous appuyez dessus, la centre sur l'emplacement de l'utilisateur.*/
        map.isMyLocationEnabled = true

        // Change the card type
        map.mapType = GoogleMap.MAP_TYPE_TERRAIN

        // Donne l'emplacement le plus récent disponible.
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            // Si vous avez pu récupérer l'emplacement le plus récent,
            // déplacez la caméra vers l'emplacement actuel de l'utilisateur.
            if(location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLng)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,15F))
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        //activate the zoom controls on the map
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)

        setUpMap()
        setMapLongClick(map)
        setPoiClick(map)
    }

    // The Android Maps API lets you use a marker object,
    // which is an icon that can be placed at a particular point on the map’s surface.
    private fun placeMarkerOnMap(location: LatLng) {

        val markerOptions = MarkerOptions().position(location).title(getString(R.string.dropped_pin))

        // change color marker
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(
            BitmapDescriptorFactory.HUE_BLUE))


        map.addMarker(markerOptions)
    }

    // Allow users to add a marker with a long click
    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { LatLng ->

            map.addMarker(MarkerOptions()
                .position(LatLng))

        }
    }

    // Add a Points of Interest Auditor
    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            poiMarker.showInfoWindow()
        }
    }

    override fun onMarkerClick(p0: Marker?) = false


}
