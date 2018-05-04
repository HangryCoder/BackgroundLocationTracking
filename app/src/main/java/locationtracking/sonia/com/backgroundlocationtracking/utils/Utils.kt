package locationtracking.sonia.com.backgroundlocationtracking.utils

import android.util.Log

/**
 * Created by soniawadji on 04/05/18.
 */
class Utils {

    companion object {

        const val DEBUG = true

        fun logd(TAG: String, message: String) {
            if (DEBUG) {
                Log.e(TAG, message)
            }
        }

    }
}