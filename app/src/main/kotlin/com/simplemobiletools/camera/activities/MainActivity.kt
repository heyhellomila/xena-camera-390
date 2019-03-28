package com.simplemobiletools.camera.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.* // ktlint-disable no-wildcard-imports
import android.widget.TextView
import android.widget.RelativeLayout
import android.location.Location
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.github.stephenvinouze.core.interfaces.RecognitionCallback
import com.github.stephenvinouze.core.managers.KontinuousRecognitionManager
import com.github.stephenvinouze.core.models.RecognitionStatus
import com.simplemobiletools.camera.BuildConfig
import com.simplemobiletools.camera.R
import com.simplemobiletools.camera.extensions.config
import com.simplemobiletools.camera.extensions.navBarHeight
import com.simplemobiletools.camera.helpers.* // ktlint-disable no-wildcard-imports
import com.simplemobiletools.camera.implementations.MyCameraImpl
import com.simplemobiletools.camera.interfaces.MyPreview
import com.simplemobiletools.camera.views.CameraPreview
import com.simplemobiletools.camera.views.FocusCircleView
import com.simplemobiletools.commons.extensions.* // ktlint-disable no-wildcard-imports
import com.simplemobiletools.commons.helpers.* // ktlint-disable no-wildcard-imports
import com.simplemobiletools.commons.models.Release
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream

import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient

class MainActivity : SimpleActivity(), PhotoProcessor.MediaSavedListener, RecognitionCallback {
    private val FADE_DELAY = 5000L
    lateinit var mTimerHandler: Handler
    private lateinit var mOrientationEventListener: OrientationEventListener
    private lateinit var mFocusCircleView: FocusCircleView
    private lateinit var mFadeHandler: Handler
    private lateinit var mCameraImpl: MyCameraImpl
    private lateinit var recognitionManager: KontinuousRecognitionManager // Using open source voice recognition from Stephen Vinouze

    private var mPreview: MyPreview? = null
    private var mPreviewUri: Uri? = null
    private var mIsInPhotoMode = false
    private var mIsCameraAvailable = false
    private var mIsVideoCaptureIntent = false
    private var mIsHardwareShutterHandled = false
    private var mCurrVideoRecTimer = 0
    var mToggleVoice = true
    var mToggleGeotag = true
    private var MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    protected var mLastLocation: Location? = null
    private lateinit var locationTv: TextView
    var mLastHandledOrientation = 0
    private var mfilterBitmap: Bitmap? = null

    companion object {
        /**
         * Put any keyword that will trigger the speech recognition
         */
        private const val ACTIVATION_KEYWORD = "Camera Activate"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        useDynamicTheme = false
        super.onCreate(savedInstanceState)
        appLaunched(BuildConfig.APPLICATION_ID)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        initVariables()
        tryInitCamera()
        supportActionBar?.hide()
        checkWhatsNewDialog()
        setupOrientationEventListener()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationTv = findViewById(R.id.location);
    }

    override fun onResume() {
        super.onResume()
        if (hasStorageAndCameraPermissions()) {
            mPreview?.onResumed()
            resumeCameraItems()
            setupPreviewImage(mIsInPhotoMode)
            scheduleFadeOut()
            mFocusCircleView.setStrokeColor(getAdjustedPrimaryColor())

            if (mIsVideoCaptureIntent && mIsInPhotoMode) {
                handleTogglePhotoVideo()
                checkButtons()
            }
            toggleBottomButtons(false)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (hasStorageAndCameraPermissions()) {
            mOrientationEventListener.enable()
        }
    }

    override fun onPause() {
        super.onPause()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (!hasStorageAndCameraPermissions() || isAskingPermissions) {
            return
        }

        mFadeHandler.removeCallbacksAndMessages(null)

        hideTimer()
        mOrientationEventListener.disable()

        if (mPreview?.getCameraState() == STATE_PICTURE_TAKEN) {
            toast(R.string.photo_not_saved)
        }
        mPreview?.onPaused()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::recognitionManager.isInitialized) {
            recognitionManager.destroyRecognizer()
        }
        mPreview = null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun initVariables() {
        mIsInPhotoMode = config.initPhotoMode
        mIsCameraAvailable = false
        mIsVideoCaptureIntent = false
        mIsHardwareShutterHandled = false
        mCurrVideoRecTimer = 0
        mLastHandledOrientation = 0
        mCameraImpl = MyCameraImpl(applicationContext)

        if (config.alwaysOpenBackCamera) {
            config.lastUsedCamera = mCameraImpl.getBackCameraId().toString()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_CAMERA && !mIsHardwareShutterHandled) {
            mIsHardwareShutterHandled = true
            shutterPressed()
            true
        } else if (!mIsHardwareShutterHandled && config.volumeButtonsAsShutter && (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
            mIsHardwareShutterHandled = true
            shutterPressed()
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_CAMERA || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            mIsHardwareShutterHandled = false
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun hideIntentButtons() {
        toggle_photo_video.beGone()
        settings.beGone()
        last_photo_video_preview.beGone()
        filterToggle.beGone()
        toggle_voice.beGone()
    }

    private fun tryInitCamera() {
        handlePermission(PERMISSION_CAMERA) {
            if (it) {
                handlePermission(PERMISSION_WRITE_STORAGE) {
                    if (it) {
                        initializeCamera()
                    } else {
                        toast(R.string.no_storage_permissions)
                        finish()
                    }
                }
            } else {
                toast(R.string.no_camera_permissions)
                finish()
            }
        }
    }

    private fun isImageCaptureIntent() = intent?.action == MediaStore.ACTION_IMAGE_CAPTURE || intent?.action == MediaStore.ACTION_IMAGE_CAPTURE_SECURE

    private fun checkImageCaptureIntent() {
        if (isImageCaptureIntent()) {
            hideIntentButtons()
            val output = intent.extras?.get(MediaStore.EXTRA_OUTPUT)
            if (output != null && output is Uri) {
                mPreview?.setTargetUri(output)

                // convert the photo to a bitMap and assign it to the activity attribute mfilterBitmap
                mfilterBitmap = MediaStore.Images.Media.getBitmap(applicationContext.getContentResolver(), output /*MediaStore.Images.Media.EXTERNAL_CONTENT_URI*/)

                /*
                This above assignment of mfilterBitmap is where I am having problems.

                output and MediaStore.Images.Media.EXTERNAL_CONTENT_URI are incomplete paths and only point to the folder where the photo got saved and not the actual image.
                Unfortunately since the filename assigned to the image is autogenerated by setTargetUri, there is no way to extract it from the method.
                There is also no simple way of determining the correct Uri that doesn't require creating another few classes and modifying them to our setup.

                The other possible solution is that mPreview can be converted it a bitMap directly. I only thought of this after all the struggles with the uri mess.
                I have tried to look into it but so far I haven't found anything that allows for that conversion.
                That could work as a good starting point as it seems trying to determine the autogenerated uri is like implementing a whole other feature.
                */
            }
        }
    }

    private fun checkVideoCaptureIntent() {
        if (intent?.action == MediaStore.ACTION_VIDEO_CAPTURE) {
            mIsVideoCaptureIntent = true
            mIsInPhotoMode = false
            hideIntentButtons()
            shutter.setImageResource(R.drawable.ic_video_rec)
        }
    }

    private fun initializeCamera() {
        setContentView(R.layout.activity_main)
        initButtons()

        (btn_holder.layoutParams as RelativeLayout.LayoutParams).setMargins(0, 0, 0, (navBarHeight + resources.getDimension(R.dimen.activity_margin)).toInt())

        checkVideoCaptureIntent()
        mPreview = CameraPreview(this, camera_texture_view, mIsInPhotoMode)
        view_holder.addView(mPreview as ViewGroup)
        checkImageCaptureIntent()
        mPreview?.setIsImageCaptureIntent(isImageCaptureIntent())

        val imageDrawable = if (config.lastUsedCamera == mCameraImpl.getBackCameraId().toString()) R.drawable.ic_camera_front else R.drawable.ic_camera_rear
        toggle_camera.setImageResource(imageDrawable)

        mFocusCircleView = FocusCircleView(applicationContext)
        view_holder.addView(mFocusCircleView)

        mTimerHandler = Handler()
        mFadeHandler = Handler()
        setupPreviewImage(true)

        val initialFlashlightState = if (config.turnFlashOffAtStartup) FLASH_OFF else config.flashlightState
        mPreview!!.setFlashlightState(initialFlashlightState)
        updateFlashlightState(initialFlashlightState)
    }

    private fun initButtons() {
        toggle_camera.setOnClickListener { toggleCamera() }
        last_photo_video_preview.setOnClickListener { showLastMediaPreview() }
        toggle_flash.setOnClickListener { toggleFlash() }
        shutter.setOnClickListener { shutterPressed() }
        settings.setOnClickListener { launchSettings() }
        toggle_photo_video.setOnClickListener { handleTogglePhotoVideo() }
        filterToggle.setOnClickListener { openFilterOptions() }
        change_resolution.setOnClickListener { mPreview?.showChangeResolutionDialog() }
        toggle_voice.setOnClickListener { handleToggleVoice() }
        toggle_geotag.setOnClickListener { handleToggleGeotag() }
    }

    private fun handleToggleGeotag() {
        // Check permissions. If not granted, then request them. Otherwise, toggleGeotag().
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION)
        } else {
            toggleGeotag()
        }
    }

    private fun toggleGeotag() {
        if (mToggleGeotag) {
            // If permissions have not been granted, call handleToggleGeotag to request permissions.
            // Otherwise, active geotagging.
            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                handleToggleGeotag()
            }
            mToggleGeotag = false
            toggle_geotag.setImageResource(R.drawable.geotag_active)
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener {
                location: Location? ->
                    mLastLocation = location
                    locationTv.setText("Latitude : " + location!!.latitude + "\nLongitude : " + location!!.longitude);
            }
        } else {
            // Turn off geotag feature.
            locationTv.setText("");
            mToggleGeotag = true
            toggle_geotag.setImageResource(R.drawable.geotag)
            fadeInButtons()
        }
    }

    // This method will handle voice activation by first requesting microphone permissions.
    private fun handleToggleVoice() {
        handlePermission(PERMISSION_RECORD_AUDIO) {
            if (it) {
                toggleVoice()
            } else {
                toast(R.string.no_audio_permissions)
            }
        }
    }

    private fun toggleVoice() { // this function will toggle the voice activation function
        if (mToggleVoice) { // if the voice recognition is not active, then turn it on
            recognitionManager = KontinuousRecognitionManager(this, activationKeyword = ACTIVATION_KEYWORD, callback = this)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                recognitionManager.startRecognition()
            }
            mToggleVoice = false
            toggle_voice.setImageResource(R.drawable.microphone_active)
            shutter.setImageResource(R.drawable.microphonebottom)
        } else { // if the voice recognition is active, then turn it off
            recognitionManager.cancelRecognition()
            mToggleVoice = true
            toggle_voice.setImageResource(R.drawable.microphone)
            shutter.setImageResource(R.drawable.ic_shutter)
            fadeInButtons()
        }
    }

    private fun openFilterOptions() {
        var intent = Intent(applicationContext, EffectsFilterActivity::class.java)

        // if a photo has been taken, convert the bitmap into a byteArray
        if (mfilterBitmap != null) {
            val bStream = ByteArrayOutputStream()
            val bitmapCopy = mfilterBitmap
            bitmapCopy!!.compress(Bitmap.CompressFormat.JPEG, 100, bStream)
            val byteArray = bStream.toByteArray()
            // add the photo byteArray to the intent
            intent.putExtra("photoBitmap", byteArray)
        }
        // start EffectsFilterActivity
        startActivity(intent)
    }

    private fun toggleCamera() {
        if (checkCameraAvailable()) {
            mPreview!!.toggleFrontBackCamera()
        }
    }

    private fun showLastMediaPreview() {
        if (mPreviewUri != null) {
            val path = applicationContext.getRealPathFromURI(mPreviewUri!!) ?: mPreviewUri!!.toString()
            openPathIntent(path, false, BuildConfig.APPLICATION_ID)
        }
    }

    private fun toggleFlash() {
        if (checkCameraAvailable()) {
            mPreview?.toggleFlashlight()
        }
    }

    fun updateFlashlightState(state: Int) {
        config.flashlightState = state
        val flashDrawable = when (state) {
            FLASH_OFF -> R.drawable.ic_flash_off
            FLASH_ON -> R.drawable.ic_flash_on
            else -> R.drawable.ic_flash_auto
        }
        toggle_flash.setImageResource(flashDrawable)
    }

    fun updateCameraIcon(isUsingFrontCamera: Boolean) {
        toggle_camera.setImageResource(if (isUsingFrontCamera) R.drawable.ic_camera_rear else R.drawable.ic_camera_front)
    }

    private fun shutterPressed() {
        if (checkCameraAvailable()) {
            handleShutter()
        }
    }

    private fun handleShutter() {
        if (mIsInPhotoMode) {
            toggleBottomButtons(true)
            mPreview?.tryTakePicture()
        } else {
            mPreview?.toggleRecording()
        }
    }

    fun toggleBottomButtons(hide: Boolean) {
        runOnUiThread {
            val alpha = if (hide) 0f else 1f
            shutter.animate().alpha(alpha).start()
            toggle_camera.animate().alpha(alpha).start()
            toggle_flash.animate().alpha(alpha).start()

            shutter.isClickable = !hide
            toggle_camera.isClickable = !hide
            toggle_flash.isClickable = !hide
        }
    }

    private fun launchSettings() {
        if (settings.alpha == 1f) {
            val intent = Intent(applicationContext, SettingsActivity::class.java)
            startActivity(intent)
        } else {
            fadeInButtons()
        }
    }

    private fun handleTogglePhotoVideo() {
        handlePermission(PERMISSION_RECORD_AUDIO) {
            if (it) {
                togglePhotoVideo()
            } else {
                toast(R.string.no_audio_permissions)
                if (mIsVideoCaptureIntent) {
                    finish()
                }
            }
        }
    }

    private fun togglePhotoVideo() {
        if (!checkCameraAvailable()) {
            return
        }

        if (mIsVideoCaptureIntent) {
            mPreview?.tryInitVideoMode()
        }

        mPreview?.setFlashlightState(FLASH_OFF)
        hideTimer()
        mIsInPhotoMode = !mIsInPhotoMode
        config.initPhotoMode = mIsInPhotoMode
        showToggleCameraIfNeeded()
        checkButtons()
        toggleBottomButtons(false)
    }

    private fun checkButtons() {
        if (mIsInPhotoMode) {
            initPhotoMode()
        } else {
            tryInitVideoMode()
        }
    }

    private fun initPhotoMode() {
        toggle_photo_video.setImageResource(R.drawable.ic_video)
        shutter.setImageResource(R.drawable.ic_shutter)
        mPreview?.initPhotoMode()
        setupPreviewImage(true)
    }

    private fun tryInitVideoMode() {
        if (mPreview?.initVideoMode() == true) {
            initVideoButtons()
        } else {
            if (!mIsVideoCaptureIntent) {
                toast(R.string.video_mode_error)
            }
        }
    }

    private fun initVideoButtons() {
        toggle_photo_video.setImageResource(R.drawable.ic_camera)
        showToggleCameraIfNeeded()
        shutter.setImageResource(R.drawable.ic_video_rec)
        setupPreviewImage(false)
        mPreview?.checkFlashlight()
    }

    private fun setupPreviewImage(isPhoto: Boolean) {
        val uri = if (isPhoto) MediaStore.Images.Media.EXTERNAL_CONTENT_URI else MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val lastMediaId = getLatestMediaId(uri)
        if (lastMediaId == 0L) {
            return
        }

        mPreviewUri = Uri.withAppendedPath(uri, lastMediaId.toString())

        runOnUiThread {
            if (!isDestroyed) {
                val options = RequestOptions()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)

                Glide.with(this)
                        .load(mPreviewUri)
                        .apply(options)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(last_photo_video_preview)
            }
        }
    }

    private fun scheduleFadeOut() {
        if (!config.keepSettingsVisible) {
            mFadeHandler.postDelayed({
                fadeOutButtons()
            }, FADE_DELAY)
        }
    }

    private fun fadeOutButtons() {
        fadeAnim(settings, .5f)
        fadeAnim(toggle_photo_video, .0f)
        fadeAnim(change_resolution, .0f)
        fadeAnim(last_photo_video_preview, .0f)
        fadeAnim(filterToggle, .0f)
        fadeAnim(toggle_voice, .0f)
        fadeAnim(toggle_geotag, .0f)
    }

    private fun fadeInButtons() {
        fadeAnim(settings, 1f)
        fadeAnim(toggle_photo_video, 1f)
        fadeAnim(change_resolution, 1f)
        fadeAnim(last_photo_video_preview, 1f)
        fadeAnim(filterToggle, 1f)
        fadeAnim(toggle_voice, 1f)
        fadeAnim(toggle_geotag, 1f)
        scheduleFadeOut()
    }

    private fun fadeAnim(view: View, value: Float) {
        view.animate().alpha(value).start()
        view.isClickable = value != .0f
    }

    private fun hideNavigationBarIcons() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
    }

    private fun showTimer() {
        video_rec_curr_timer.beVisible()
        setupTimer()
    }

    private fun hideTimer() {
        video_rec_curr_timer.text = 0.getFormattedDuration()
        video_rec_curr_timer.beGone()
        mCurrVideoRecTimer = 0
        mTimerHandler.removeCallbacksAndMessages(null)
    }

    private fun setupTimer() {
        runOnUiThread(object : Runnable {
            override fun run() {
                video_rec_curr_timer.text = mCurrVideoRecTimer++.getFormattedDuration()
                mTimerHandler.postDelayed(this, 1000L)
            }
        })
    }

    private fun resumeCameraItems() {
        showToggleCameraIfNeeded()
        hideNavigationBarIcons()

        if (!mIsInPhotoMode) {
            initVideoButtons()
        }
    }

    private fun showToggleCameraIfNeeded() {
        toggle_camera?.beInvisibleIf(mCameraImpl.getCountOfCameras() <= 1)
    }

    private fun hasStorageAndCameraPermissions() = hasPermission(PERMISSION_WRITE_STORAGE) && hasPermission(PERMISSION_CAMERA)

    private fun setupOrientationEventListener() {
        mOrientationEventListener = object : OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            override fun onOrientationChanged(orientation: Int) {
                if (isDestroyed) {
                    mOrientationEventListener.disable()
                    return
                }

                val currOrient = when (orientation) {
                    in 75..134 -> ORIENT_LANDSCAPE_RIGHT
                    in 225..289 -> ORIENT_LANDSCAPE_LEFT
                    else -> ORIENT_PORTRAIT
                }

                if (currOrient != mLastHandledOrientation) {
                    val degrees = when (currOrient) {
                        ORIENT_LANDSCAPE_LEFT -> 90
                        ORIENT_LANDSCAPE_RIGHT -> -90
                        else -> 0
                    }

                    animateViews(degrees)
                    mLastHandledOrientation = currOrient
                }
            }
        }
    }

    private fun animateViews(degrees: Int) {
        val views = arrayOf<View>(toggle_camera, toggle_flash, toggle_photo_video, change_resolution, shutter, settings, last_photo_video_preview)
        for (view in views) {
            rotate(view, degrees)
        }
    }

    private fun rotate(view: View, degrees: Int) = view.animate().rotation(degrees.toFloat()).start()

    private fun checkCameraAvailable(): Boolean {
        if (!mIsCameraAvailable) {
            toast(R.string.camera_unavailable)
        }
        return mIsCameraAvailable
    }

    fun setFlashAvailable(available: Boolean) {
        if (available) {
            toggle_flash.beVisible()
        } else {
            toggle_flash.beInvisible()
            toggle_flash.setImageResource(R.drawable.ic_flash_off)
            mPreview?.setFlashlightState(FLASH_OFF)
        }
    }

    fun setIsCameraAvailable(available: Boolean) {
        mIsCameraAvailable = available
    }

    fun setRecordingState(isRecording: Boolean) {
        runOnUiThread {
            if (isRecording) {
                shutter.setImageResource(R.drawable.ic_video_stop)
                toggle_camera.beInvisible()
                showTimer()
            } else {
                shutter.setImageResource(R.drawable.ic_video_rec)
                showToggleCameraIfNeeded()
                hideTimer()
            }
        }
    }

    fun videoSaved(uri: Uri) {
        setupPreviewImage(false)
        if (mIsVideoCaptureIntent) {
            Intent().apply {
                data = uri
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                setResult(Activity.RESULT_OK, this)
            }
            finish()
        }
    }

    fun drawFocusCircle(x: Float, y: Float) = mFocusCircleView.drawFocusCircle(x, y)

    override fun mediaSaved(path: String) {
        rescanPaths(arrayListOf(path)) {
            setupPreviewImage(true)
            Intent(BROADCAST_REFRESH_MEDIA).apply {
                putExtra(REFRESH_PATH, path)
                `package` = "com.simplemobiletools.gallery"
                sendBroadcast(this)
            }
        }

        if (isImageCaptureIntent()) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun checkWhatsNewDialog() {
        arrayListOf<Release>().apply {
            add(Release(33, R.string.release_33))
            add(Release(35, R.string.release_35))
            add(Release(39, R.string.release_39))
            add(Release(44, R.string.release_44))
            add(Release(46, R.string.release_46))
            add(Release(52, R.string.release_52))
            checkWhatsNew(this, BuildConfig.VERSION_CODE)
        }
    }

    override fun onPrepared(status: RecognitionStatus) {
        when (status) {
            RecognitionStatus.SUCCESS -> {
                Log.i("Recognition", "onPrepared: Success")
                // textView.text = "Recognition ready"
            }
        }
    }

    override fun onBeginningOfSpeech() {
        Log.i("Recognition", "onBeginningOfSpeech")
    }

    override fun onKeywordDetected() {
        mPreview?.tryTakePicture()
        Log.i("Recognition", "keyword detected !!!")
        // textView.text = "Keyword detected"
        System.out.println("Keyword detected")
    }

    override fun onReadyForSpeech(params: Bundle) {
        Log.i("Recognition", "onReadyForSpeech")
    }

    override fun onBufferReceived(buffer: ByteArray) {
        Log.i("Recognition", "onBufferReceived: $buffer")
    }

    override fun onRmsChanged(rmsdB: Float) {
    }

    override fun onPartialResults(results: List<String>) {}

    override fun onResults(results: List<String>, scores: FloatArray?) {
        val text = results.joinToString(separator = "\n")
        Log.i("Recognition", "onResults : $text")
        // textView.text = text
        System.out.println(text)
    }

    override fun onError(errorCode: Int) {
    }

    override fun onEvent(eventType: Int, params: Bundle) {
        Log.i("Recognition", "onEvent")
    }

    override fun onEndOfSpeech() {
        Log.i("Recognition", "onEndOfSpeech")
    }
}
