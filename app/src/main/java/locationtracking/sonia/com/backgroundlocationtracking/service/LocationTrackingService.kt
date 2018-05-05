package locationtracking.sonia.com.backgroundlocationtracking.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.android.gms.location.*
import locationtracking.sonia.com.backgroundlocationtracking.utils.Constants
import locationtracking.sonia.com.backgroundlocationtracking.utils.Utils
import android.support.v4.content.LocalBroadcastManager
import locationtracking.sonia.com.backgroundlocationtracking.utils.Constants.Companion.ACTION_LOCATION_BROADCAST
import locationtracking.sonia.com.backgroundlocationtracking.utils.Constants.Companion.INTENT_LATITUDE
import locationtracking.sonia.com.backgroundlocationtracking.utils.Constants.Companion.INTENT_LONGITUDE


/**
 * Created by soniawadji on 05/05/18.
 */
class LocationTrackingService : Service() {

    private val TAG = "LocationTrackingService"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    var isTrackingEnabled = false


    override fun onCreate() {
        super.onCreate()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        createLocationRequest()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    // Update UI with location data

                    val intent = Intent(ACTION_LOCATION_BROADCAST)
                    intent.putExtra(INTENT_LATITUDE, location.latitude)
                    intent.putExtra(INTENT_LONGITUDE, location.longitude)
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                }
            }
        }

        startLocationUpdates()

        return START_STICKY
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create()
                .setInterval(Constants.INTERVAL)
                .setFastestInterval(Constants.FASTEST_UPDATE_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                null)
        isTrackingEnabled = true
        Utils.logd(TAG, "Start Location Updates")
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        isTrackingEnabled = false
        Utils.logd(TAG, "Stop Location Updates")
    }

    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

}