package com.simplemobiletools.camera.testing

import com.simplemobiletools.camera.activities.MainActivity
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify


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
        // var originalToggleCallNumber = main.toggleVoiceCalled
        // R.id.toggle_voice.performClick()
        assert(main!!.handleToggleVoiceCalled == originalHandleCallNumber+1)
        //assert(main.toggleVoiceCalled == originalToggleCallNumber+1)
    }



    // Check that a picture is taken
    @Test
    fun testTakePicture() {
        var originalTryTakePictureCallNumber = main!!.tryTakePictureCalled
        main!!.onKeywordDetected()
        assert(main!!.tryTakePictureCalled == originalTryTakePictureCallNumber+1)
    }

}
