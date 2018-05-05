package locationtracking.sonia.com.backgroundlocationtracking.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import locationtracking.sonia.com.backgroundlocationtracking.utils.Constants.Companion.DEBUG

/**
 * Created by soniawadji on 04/05/18.
 */
class Utils {

    companion object {

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