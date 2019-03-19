package com.simplemobiletools.camera.testing

import com.simplemobiletools.camera.activities.EffectsFilterActivity
import org.junit.Test
import org.mockito.Mockito.`when` as _when

// Useful link to mockito using kotlin
// https://antonioleiva.com/unit-tests-android-kotlin/

class FilterTest {

    // Tests setting an effect.
    @Test
    fun testSetCurrentEffect() {
        val filterActivity = EffectsFilterActivity()
        filterActivity.setCurrentEffect(1)
        assert(filterActivity.mCurrentEffect == 1)
    }
}
