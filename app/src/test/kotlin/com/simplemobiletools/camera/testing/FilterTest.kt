package com.simplemobiletools.camera.testing

import android.graphics.Bitmap
import com.simplemobiletools.camera.activities.FilterActivity
import com.zomato.photofilters.imageprocessors.Filter
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class FilterTest {

    @Spy
    lateinit var filterActivity : FilterActivity

    @Mock
    lateinit var image : Bitmap

    @Mock
    lateinit var filter : Filter

    @Before
    fun initiateFilter() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testOnEditCompleted(){

        // initialisation for this test
        filterActivity.filteredImage = image
        `when`(filter.processFilter(image)).thenReturn(image)
        `when`(image.copy(Bitmap.Config.ARGB_8888, true)).thenReturn(image)
        doReturn(filter).`when`(filterActivity).createFilter()
        `when`(filter.processFilter(ArgumentMatchers.any())).thenReturn(image)

        // Test
        filterActivity.onEditCompleted()

        // Assertions and/or verifications
        verify(filterActivity).onEditCompleted()
    }

}
