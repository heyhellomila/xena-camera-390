package com.simplemobiletools.camera.testing

import com.simplemobiletools.camera.activities.MainActivity
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
// import org.mockito.Mockito.`when` as _when

// Useful link to mockito using kotlin
// https://antonioleiva.com/unit-tests-android-kotlin/

class AppTest {

    @Test
    fun testJunit() {
        val main = mock(MainActivity::class.java)
        main.setIsCameraAvailable(false)
        verify(main).setIsCameraAvailable(false)
    }
}
