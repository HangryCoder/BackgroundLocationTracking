package locationtracking.sonia.com.backgroundlocationtracking.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import locationtracking.sonia.com.backgroundlocationtracking.utils.Constants.Companion.DEBUG
import java.util.*


/**
 * Created by soniawadji on 04/05/18.
 */
class Utils {

    companion object {

        private val TAG = "Utils"

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

        fun calculateShiftTimeDuration(startDate: Date, endDate: Date): String {
            //milliseconds
            var different = endDate.getTime() - startDate.getTime()

            logd(TAG, "startDate : " + startDate)
            logd(TAG, "endDate : " + endDate)
            logd(TAG, "different : " + different)

            val secondsInMilli: Long = 1000
            val minutesInMilli = secondsInMilli * 60
            val hoursInMilli = minutesInMilli * 60
            val daysInMilli = hoursInMilli * 24

            val elapsedDays = different / daysInMilli
            different %= daysInMilli

            val elapsedHours = different / hoursInMilli
            different %= hoursInMilli

            val elapsedMinutes = different / minutesInMilli
            different %= minutesInMilli

            val elapsedSeconds = different / secondsInMilli

            System.out.printf(
                    "%d days, %d hours, %d minutes, %d seconds%n",
                    elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds)

            val totalShiftTime = String.format("%02dh %02dm %02ds", elapsedHours, elapsedMinutes, elapsedSeconds)

            return totalShiftTime
        }
    }
}