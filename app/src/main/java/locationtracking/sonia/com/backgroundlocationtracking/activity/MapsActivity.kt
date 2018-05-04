package locationtracking.sonia.com.backgroundlocationtracking.activity

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import locationtracking.sonia.com.backgroundlocationtracking.R
import locationtracking.sonia.com.backgroundlocationtracking.interfaces.LocationListener
import locationtracking.sonia.com.backgroundlocationtracking.utils.LocationProvider
import locationtracking.sonia.com.backgroundlocationtracking.utils.Utils
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.DialogInterface
import android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.ArrayList


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    private val TAG = "MapsActivity"
    private lateinit var mMap: GoogleMap
    private lateinit var locationProvider: LocationProvider
    private val CAMERA_ZOOM = 15f
    private var points: ArrayList<LatLng> = ArrayList()
    private var isShiftStarted = false

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationProvider = LocationProvider(this, this)

        startShiftBtn.setOnClickListener {
            if (!locationProvider.isTrackingEnabled) {

            }
        }

        stopShiftBtn.setOnClickListener {
            if (locationProvider.isTrackingEnabled) {

            }
        }
    }

    override fun passLocationData(location: Location) {
        Utils.logd(TAG, "passLocationData ${location.latitude} " + location.longitude)

        // mMap.clear()
        // val currentLocation = LatLng(location.latitude, location.longitude)
        // mMap.addMarker(MarkerOptions().position(currentLocation).title("Current Location"))
        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, CAMERA_ZOOM))
    }

    override fun onResume() {
        super.onResume()
        locationProvider.startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        locationProvider.stopLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //mMap.isMyLocationEnabled = true

        locationProvider.getFusedLocationClient().lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val userLocation = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(userLocation).title("User Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, CAMERA_ZOOM))
            }
        }
    }

    /**
     * Do this later...
     * Permission Dialog
     * */
    val MY_PERMISSIONS_REQUEST_LOCATION = 99

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", { dialogInterface, i ->
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(this@MapsActivity,
                                    arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                                    MY_PERMISSIONS_REQUEST_LOCATION)
                        })
                        .create()
                        .show()


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        MY_PERMISSIONS_REQUEST_LOCATION)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        /* if (mGoogleApiClient == null) {
                             buildGoogleApiClient()
                         }
                         mGoogleMap.setMyLocationEnabled(true)*/
                    }

                } else {

                    Utils.customToast(this, resources.getString(R.string.permission_denied))
                }
                return
            }
        }
    }
}
