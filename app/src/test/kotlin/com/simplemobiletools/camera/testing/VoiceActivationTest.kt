package com.simplemobiletools.camera.testing

import com.simplemobiletools.camera.activities.MainActivity
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify


class VoiceActivationTest {

/*
    // Check that Voice Activation Handler is run when the toggle button is pressed
    @Test
    fun testHandleToggleVoice() {
        val main = MainActivity::class.java
        var originalHandleCallNumber = main.handleToggleVoiceCalled
        // var originalToggleCallNumber = main.toggleVoiceCalled
        ((Button) R.id.toggle_voice).performClick()
        assert(main.handleToggleVoiceCalled == originalHandleCallNumber+1)
        //assert(main.toggleVoiceCalled == originalToggleCallNumber+1)
    }
*/


    // Check that a picture is taken
    @Test
    fun testTakePicture() {
        val main = MainActivity::class.java
        var originalTryTakePictureCallNumber = main.tryTakePictureCalled
        main.onKeywordDetected()
        assert(main.tryTakePictureCalled == originalTryTakePictureCallNumber+1)
    }

}
