package locationtracking.sonia.com.backgroundlocationtracking.interfaces

import android.location.Location

/**
 * Created by soniawadji on 04/05/18.
 */
interface LocationListener {
    fun passLocationData(location: Location)
}