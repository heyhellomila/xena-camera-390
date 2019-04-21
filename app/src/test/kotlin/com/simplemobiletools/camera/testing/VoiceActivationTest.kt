package com.simplemobiletools.camera.testing

import com.simplemobiletools.camera.activities.MainActivity
import org.junit.Before
import org.junit.Test

class VoiceActivationTest {
    var main: MainActivity? = null

    @Before
    fun setup() {
        main = MainActivity()
    }

    // Check that Voice Activation Handler is run when the toggle button is pressed
    @Test
    fun testHandleToggleVoice() {
        var originalHandleCallNumber = main!!.handleToggleVoiceCalled
        // Need something here that calls and runs main.handleToggleVoice() -->  toggle_voice.performClick()?
        assert(main!!.handleToggleVoiceCalled == originalHandleCallNumber+1)
    }



    // Check that a picture is taken when keyword is detected
    @Test
    fun testTakePicture() {
        var originalTryTakePictureCallNumber = main!!.tryTakePictureCalled
        main!!.onKeywordDetected()
        assert(main!!.tryTakePictureCalled == originalTryTakePictureCallNumber+1)
    }

}
