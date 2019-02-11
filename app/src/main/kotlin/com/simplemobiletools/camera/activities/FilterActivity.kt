package com.simplemobiletools.camera.activities

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simplemobiletools.camera.interfaces.EditImageFragmentListener
import com.simplemobiletools.camera.interfaces.FilterListFragmentListener
import com.simplemobiletools.camera.filter.EditImageFragment
import com.simplemobiletools.camera.filter.FilterListFragment
import com.simplemobiletools.camera.R
import com.simplemobiletools.camera.Utils.BitmapUtils
import com.simplemobiletools.camera.adapter.ViewPagerAdapter
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter
import kotlinx.android.synthetic.main.activity_filter.*
import kotlinx.android.synthetic.main.content_filter.*


class FilterActivity : AppCompatActivity(), FilterListFragmentListener, EditImageFragmentListener {

    override fun onFilterSelected(filter: com.zomato.photofilters.imageprocessors.Filter) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBrightnessChanged(brightness: Int) {
      brightnessFinal = brightness
        val myFilter = Filter()
        myFilter.addSubFilter(BrightnessSubFilter(brightness))
        image_preview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)))
    }

    override fun onSaturationChanged(saturation: Float) {
        saturationFinal = saturation
        val myFilter = Filter()
        myFilter.addSubFilter(BrightnessSubFilter(saturation))
        image_preview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)))
    }

    override fun onContrastChanged(constrast: Float) {
        contrastFinal = constrast
        val myFilter = Filter()
        myFilter.addSubFilter(BrightnessSubFilter(constrast))
        image_preview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)))
    }

    override fun onEditStarted() {

    }

    override fun onEditCompleted() {
        val bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true)
        val myFilter = Filter()
        myFilter.addSubFilter(ContrastSubFilter(contrastFinal))
        myFilter.addSubFilter(SaturationSubfilter(saturationFinal))
        myFilter.addSubFilter(BrightnessSubFilter(brightnessFinal))
        finalImage = myFilter.processFilter(bitmap)

    }

    override fun onFilterSelected(filter: FilterListFragment)
    {
        resetControls()
        filteredImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
        image_preview.setImageBitmap(filter.processfilter(filteredImage))
        finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true)

    }

    private fun resetControls() {
        if(editImageFragment !=null)
            editImageFragment.resetControls()

        brightnessFinal = 0
        saturationFinal = 1.0f
        contrastFinal = 1.0f
    }

    internal var originalImage: Bitmap?=null
    internal lateinit var filteredImage:Bitmap
    internal lateinit var finalImage:Bitmap

    internal lateinit var filterListFragment: FilterListFragment
    internal lateinit var editImageFragment: EditImageFragment

    internal var brightnessFinal = 0
    internal var saturationFinal = 1.0f
    internal var contrastFinal = 1.0f


    object Filter {
        val IMAGE_NAME = "flash.jpg"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        //Set Toolbar

        setSupportActionBar(toolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title="XENA Filter"

        loadImage()
        setUpViewPager(viewPager)
        tabs.setupWithViewPager(viewPager)
    }

    private fun setUpViewPager(viewPager: NonSwipeableViewPager?) {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        // add filter list fragment
        filterListFragment = FilterListFragment()
        filterListFragment.setListener(this)

        // add edit image fragment
        editImageFragment = EditImageFragment()
        editImageFragment.setListener(this)

        adapter.addFragment(filterListFragment, "FILTERS")
        adapter.addFragment(editImageFragment, "EDIT")

        viewPager!!.adapter = adapter
    }

    private fun loadImage() {
        originalImage = BitmapUtils.getBitmapFromAssets(this, Filter.IMAGE_NAME, 300, 300)
        filteredImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
        finalImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
    }


}
