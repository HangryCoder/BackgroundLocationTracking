package locationtracking.sonia.com.backgroundlocationtracking.utils

import android.content.Context
import android.util.Log
import android.widget.Toast

/**
 * Created by soniawadji on 04/05/18.
 */
class Utils {

    companion object {

        const val DEBUG = true

        fun logd(TAG: String, message: String) {
            if (DEBUG) {
                Log.d(TAG, message)
            }
        }


        fun customToast(context: Context, message: String) {
            if (DEBUG) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }
    }
}