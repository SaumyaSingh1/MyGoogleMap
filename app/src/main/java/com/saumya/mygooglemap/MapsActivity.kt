package com.saumya.mygooglemap

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import android.support.v4.app.FragmentActivity
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import javax.security.auth.callback.Callback


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    override fun onMapReady(p0: GoogleMap?) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d("TAG", "onMapReady: map is ready")
        mMap = p0
        if (mLocationPermissionsGranted) {
            getDeviceLocation()
            if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            mMap?.setMyLocationEnabled(true)
            mMap?.getUiSettings()?.setMyLocationButtonEnabled(false)
        }
    }


    private val FINE_LOCATION = ACCESS_FINE_LOCATION
    private val COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
    private val LOCATION_PERMISSION_REQUEST_CODE = 1234
    private val DEFAULT_ZOOM = 15f
    private var mLocationPermissionsGranted = false
    private var mMap: GoogleMap? = null
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        getLocationPermission()
    }

    private fun getDeviceLocation() {
        Log.d("TAG", "getDeviceLocation: getting the devices current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        try {
            if (mLocationPermissionsGranted) {
                val location = mFusedLocationProviderClient?.lastLocation

                location?.addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        val currentLocation: Location = it.result!!
                        moveCamera(LatLng(currentLocation.latitude, currentLocation.longitude), DEFAULT_ZOOM)
                    } else {
                        Log.d("TAG", "onComplete: current location is null");

                        Toast.makeText(this, "unable to get current location", Toast.LENGTH_SHORT).show()
                    }
                }

            }

//            location.addOnCompleteListener(new OnCompleteListener() {
//
//                @Override
//
//                public void onComplete(@NonNull Task task) {
//
//                    if(task.isSuccessful()){
//
//                        Log.d(TAG, "onComplete: found location!");
//
//                        Location currentLocation = (Location) task.getResult();
//
//
//
//                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
//
//                        DEFAULT_ZOOM);
//
//
//
//                    }else{
//
//                        Log.d(TAG, "onComplete: current location is null");
//
//                        Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
//
//                    }
//
//                }
//
//            });
        } catch (e: SecurityException) {
            Log.e("TAG", "getDeviceLocation: SecurityException: " + e.message)
        }
    }

    private fun getLocationPermission() {
        Log.d("TAG", "getLocationPermission: getting location permissions")
        val permissions = arrayOf(
            ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (ContextCompat.checkSelfPermission(
                    this.applicationContext,
                    COURSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mLocationPermissionsGranted = true
                initMap()
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE)
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                permissions,
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.d("TAG", "onRequestPermissionsResult: called.")
        mLocationPermissionsGranted = false
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty()) {
                for (i in 0..grantResults.size) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionsGranted = false;
                        Log.d("TAG", "onRequestPermissionsResult: permission failed");
                        return

                    }
                }
            }
            Log.d("TAG", "onRequestPermissionsResult: permission granted")
            mLocationPermissionsGranted = true
            //initialize our map
            initMap()
        }
    }

    private fun moveCamera(latLng: LatLng, zoom: Float) {
        Log.d("TAG", "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude)
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }

    private fun initMap() {
        Log.d("TAG", "initMap: initializing map")
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this@MapsActivity)
    }


}

