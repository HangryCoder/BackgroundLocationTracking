package locationtracking.sonia.com.backgroundlocationtracking.utils

/**
 * Created by soniawadji on 05/05/18.
 */
class Constants {

    companion object {

        const val DEBUG = true

        const val INTERVAL = 5000L

        const val FASTEST_UPDATE_INTERVAL = 1000L

        const val CAMERA_ZOOM = 15f

        const val POLYLINE_WIDTH = 10f

        const val ACTION_LOCATION_BROADCAST = "LocationBroadcast"

        const val INTENT_LATITUDE = "latitude"

        const val INTENT_LONGITUDE = "longitude"

        const val USER_LOCATION = "User Location"

        const val FINAL_LOCATION = "Final Location"

        const val PERMISSION_REQUEST_CODE: Int = 100

    }
}