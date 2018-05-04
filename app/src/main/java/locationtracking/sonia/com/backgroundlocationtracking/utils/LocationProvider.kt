package locationtracking.sonia.com.backgroundlocationtracking.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.*
import locationtracking.sonia.com.backgroundlocationtracking.interfaces.LocationListener

/**
 * Created by soniawadji on 04/05/18.
 */
class LocationProvider() {

    private val TAG = "LocationProvider"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private val INTERVAL = 5000L
    private val FASTEST_INTERVAL = 1000L
    var isTrackingEnabled = false
        get() = field


    @SuppressLint("MissingPermission")
    constructor(context: Context, locationListener: LocationListener) : this() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        locationRequest = LocationRequest.create()
                .setInterval(INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    // Update UI with location data
                    locationListener.passLocationData(location)
                }
            }
        }

    }

    fun getFusedLocationClient(): FusedLocationProviderClient {
        return fusedLocationClient
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                null)
        isTrackingEnabled = true
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        isTrackingEnabled = false
    }
}