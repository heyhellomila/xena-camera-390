package com.simplemobiletools.camera.testing

import com.simplemobiletools.camera.activities.MainActivity
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GeotaggingTest {

    // Tests the initial geotag state
    @Test
    fun testInitialGeotagState() {
        val main = MainActivity()
        assertTrue(main.getGeotagState())
    }

    // Tests the initial last location
    @Test
    fun testInitialLastLocation() {
        val main = MainActivity()
        assertNull(main.getLastLocation())
    }
}
