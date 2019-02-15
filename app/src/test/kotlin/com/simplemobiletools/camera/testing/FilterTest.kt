package com.simplemobiletools.camera.testing

import android.graphics.Bitmap
import com.simplemobiletools.camera.activities.FilterActivity
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.imageprocessors.SubFilter
import org.junit.Before
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class FilterTest {

    var filterActivity = FilterActivity()

    @Mock
    lateinit var image : Bitmap

    @Mock
    lateinit var filter : Filter

    @Mock
    lateinit var subFilter : SubFilter

    @Mock
    lateinit var contrastSubFilter : SubFilter

    @Before
    fun initiateFilter() {
        `when`(filter.processFilter(image)).thenReturn(image)
        `when`(image.copy(Bitmap.Config.ARGB_8888, true)).thenReturn(image)
        `when`(subFilter.process(ArgumentMatchers.any())).thenReturn(image)
        `when`(contrastSubFilter.process(ArgumentMatchers.any())).thenReturn(image)
    }

    @Test
    fun testOnEditCompleted(){
        filterActivity.filteredImage = image
        filterActivity.onEditCompleted()
        assertEquals(1,1)
    }


}
