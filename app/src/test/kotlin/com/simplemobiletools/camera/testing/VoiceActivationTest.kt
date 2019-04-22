package com.simplemobiletools.camera.testing

import android.widget.ImageView
import com.simplemobiletools.camera.R
import com.simplemobiletools.camera.activities.MainActivity
import org.junit.Test

class VoiceActivationTest {

    val main: MainActivity = MainActivity()

    @Test
    fun testToggleVoiceButton() {
        val originalHandleToggleVoiceCallNumber = main.handleToggleVoiceCalled
        val tvButton: ImageView = main.findViewById(R.id.toggle_voice) as ImageView
        tvButton.performClick()
        assert(main.handleToggleVoiceCalled == originalHandleToggleVoiceCallNumber + 1)
    }

    // Check that a picture is taken when keyword is detected
    @Test
    fun testTakePicture() {
        var originalTryTakePictureCallNumber = main.tryTakePictureCalled
        main.onKeywordDetected()
        assert(main.tryTakePictureCalled == originalTryTakePictureCallNumber + 1)
    }


}
