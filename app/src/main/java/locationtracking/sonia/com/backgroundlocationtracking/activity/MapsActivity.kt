package locationtracking.sonia.com.backgroundlocationtracking.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import locationtracking.sonia.com.backgroundlocationtracking.R
import locationtracking.sonia.com.backgroundlocationtracking.utils.Utils
import android.content.pm.PackageManager
import android.graphics.Color
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.View
import kotlinx.android.synthetic.main.activity_maps.*
import locationtracking.sonia.com.backgroundlocationtracking.service.LocationTrackingService
import locationtracking.sonia.com.backgroundlocationtracking.utils.Constants.Companion.CAMERA_ZOOM
import java.util.ArrayList
import android.content.IntentFilter
import android.content.BroadcastReceiver
import android.content.Context
import android.location.LocationManager
import android.support.v4.content.LocalBroadcastManager
import com.google.android.gms.maps.model.*
import locationtracking.sonia.com.backgroundlocationtracking.utils.Constants.Companion.ACTION_LOCATION_BROADCAST
import locationtracking.sonia.com.backgroundlocationtracking.utils.Constants.Companion.FINAL_LOCATION
import locationtracking.sonia.com.backgroundlocationtracking.utils.Constants.Companion.INTENT_LATITUDE
import locationtracking.sonia.com.backgroundlocationtracking.utils.Constants.Companion.INTENT_LONGITUDE
import locationtracking.sonia.com.backgroundlocationtracking.utils.Constants.Companion.POLYLINE_WIDTH
import locationtracking.sonia.com.backgroundlocationtracking.utils.Constants.Companion.USER_LOCATION


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val TAG = "MapsActivity"
    private lateinit var mMap: GoogleMap
    private var points: ArrayList<LatLng> = ArrayList()
    private var isShiftStarted = false
    private var userLocation: Location = Location("UserLocation")

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        /**
         * Start the service only to initially fetch the user's location to be displayed on the map!
         * */
        startLocationTrackingService()

        startShiftBtn.setOnClickListener {

            /**
             * Start the tracking
             * Clear the map if required
             * Clear all the location points
             * */

            totalShiftTimeCard.visibility = View.INVISIBLE

            points.clear()
            mMap.clear()

            val userLocation = LatLng(userLocation.latitude, userLocation.longitude)
            mMap.addMarker(MarkerOptions().position(userLocation).title(USER_LOCATION))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, CAMERA_ZOOM))

            startLocationTrackingService()

            startShiftBg.setImageDrawable(resources.getDrawable(R.drawable.stop_shift_btn_bg))
            swipeText.setText(R.string.swipeEndText)
            endShiftBtn.visibility = View.VISIBLE
            startShiftBtn.visibility = View.GONE

        }

        endShiftBtn.setOnClickListener {

            /**
             * Stop the tracking
             * */

            totalShiftTimeCard.visibility = View.VISIBLE

            stopLocationTrackingService()

            showFinalRoute()

            startShiftBg.setImageDrawable(resources.getDrawable(R.drawable.start_shift_btn_bg))
            swipeText.setText(R.string.swipeStartText)
            endShiftBtn.visibility = View.GONE
            startShiftBtn.visibility = View.VISIBLE
        }


        LocalBroadcastManager.getInstance(this).registerReceiver(
                object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {
                        val latitude = intent.getDoubleExtra(INTENT_LATITUDE, 0.0)
                        val longitude = intent.getDoubleExtra(INTENT_LONGITUDE, 0.0)

                        Utils.logd(TAG, "passLocationData $latitude $longitude")

                        /**
                         * This is done only initially to display user's location on the map
                         * and then stop the service
                         * */
                        if (userLocation.latitude == 0.0 && userLocation.longitude == 0.0) {

                            mMap.clear()

                            val userLocation = LatLng(latitude, longitude)
                            mMap.addMarker(MarkerOptions().position(userLocation).title(USER_LOCATION))
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, CAMERA_ZOOM))

                            stopLocationTrackingService()
                        }

                        val tempLocation = Location(LocationManager.GPS_PROVIDER)
                        tempLocation.latitude = latitude
                        tempLocation.longitude = longitude
                        userLocation = tempLocation //setting this as user location

                        points.add(LatLng(latitude, longitude))
                        drawUserPath()
                    }
                }, IntentFilter(ACTION_LOCATION_BROADCAST))
    }

    private fun showFinalRoute() {

        val finalLocation = points[points.size - 1]
        mMap.addMarker(MarkerOptions().position(finalLocation).title(FINAL_LOCATION)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))

        val builder = LatLngBounds.Builder()
        for (point in points) {
            builder.include(point)
        }

        val bounds: LatLngBounds = builder.build()

        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        val padding = (width * 0.10).toInt() // offset from edges of the map 10% of screen

        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
        mMap.moveCamera(cameraUpdate)
        mMap.animateCamera(cameraUpdate)
    }

    private fun stopLocationTrackingService() {
        val intent = Intent(this@MapsActivity, LocationTrackingService::class.java)
        stopService(intent)
    }

    private fun startLocationTrackingService() {
        val intent = Intent(this@MapsActivity, LocationTrackingService::class.java)
        startService(intent)
    }

    private fun drawUserPath() {
        mMap.clear()

        if (points.size > 0) {
            val userLocation = points[0]
            mMap.addMarker(MarkerOptions().position(userLocation).title(USER_LOCATION))
        }

        val polyLineOptions = PolylineOptions().width(POLYLINE_WIDTH).color(Color.BLUE).geodesic(true)

        for (i in points.indices) {
            val point: LatLng = points[i]
            polyLineOptions.add(point)
        }

        mMap.addPolyline(polyLineOptions)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(points[points.size - 1], CAMERA_ZOOM))

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
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
