package com.simplemobiletools.camera.testing

import com.simplemobiletools.camera.activities.MainActivity
import org.junit.Before
import org.junit.Test

class VoiceActivationTest {

    // Check that a picture is taken when keyword is detected
    @Test
    fun testTakePicture() {
        val main = MainActivity()
        var originalTryTakePictureCallNumber = main.tryTakePictureCalled
        main.onKeywordDetected()
        assert(main.tryTakePictureCalled == originalTryTakePictureCallNumber+1)
    }

}
