package com.simplemobiletools.camera.testing

import com.simplemobiletools.camera.activities.MainActivity
import org.junit.Test

class GeotaggingTest {

    // Tests the initial geotag state
    @Test
    fun testInitalGeotagState() {
        val main = MainActivity()
        assert(main.getGeotagState() == false)
    }

    // Tests the initial last location
    @Test
    fun testInitalLastLocation() {
        val main = MainActivity()
        assert(main.getLastLocation() == null)
    }

}
