package com.step.videoplayerrecyclerviewapplication

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.Util
import com.step.videoplayerrecyclerviewapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), ExoSettingDialog.OnSettingSelected {

    private var player: ExoPlayer? = null
    lateinit var binding: ActivityMainBinding
    private var playWhenReady = true
    private var currentItem = 0
    private var playbackPosition = 0L
    private val playbackStateListener: Player.Listener = playbackStateListener()
    private var controlView: CoordinatorLayout? = null
    private var mFullScreenDialog: Dialog? = null
    private var mExoPlayerFullscreen = false

    //    var url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
//    var url = "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
    var url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

    val TAG = MainActivity::class.java.simpleName


    private var startAutoPlay = false
    private var startWindow = 0
    private var startPosition: Long = 0

    private val KEY_POSITION = "position"
    private val KEY_AUTO_PLAY = "auto_play"
    private val KEY_WINDOW = "window"

    private var VIDEO_QUALITY = 0
    private var PLAY_BACK_SPEED = 1f

    var speed = arrayOf<String>()

    lateinit var mFullScreenIcon: ImageView

    private var trackSelector: DefaultTrackSelector? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        speed = resources.getStringArray(R.array.play_back_array)
        qualityArray = resources.getStringArray(R.array.quality_array)


        if (savedInstanceState != null) {

            startAutoPlay =
                savedInstanceState.getBoolean(KEY_AUTO_PLAY)
            startWindow =
                savedInstanceState.getInt(KEY_WINDOW)
            startPosition = savedInstanceState.getLong(KEY_POSITION)

            VIDEO_QUALITY = savedInstanceState.getInt("VIDEO_QUALITY")
            PLAY_BACK_SPEED = savedInstanceState.getFloat("PLAY_BACK_SPEED")
            Log.d(TAG, "onCreate: ")

        } else {
            clearStartPosition()

        }


    }

    private fun clearStartPosition() {
        startAutoPlay = true
        startWindow = C.INDEX_UNSET
        startPosition = C.TIME_UNSET
    }

    private fun updateStartPosition() {
        if (player != null) {
            startAutoPlay = player!!.playWhenReady
            startWindow = player!!.currentWindowIndex
            startPosition = Math.max(0, player!!.contentPosition)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)


        updateStartPosition()

        outState.putBoolean(
            KEY_AUTO_PLAY,
            startAutoPlay
        )
        outState.putInt(KEY_WINDOW, startWindow)
        outState.putLong(KEY_POSITION, startPosition)
        outState.putInt("VIDEO_QUALITY", VIDEO_QUALITY)
        outState.putFloat("PLAY_BACK_SPEED", PLAY_BACK_SPEED)
    }


    private fun initializePlayer() {

//        val trackSelector = DefaultTrackSelector(this).apply {
//            setParameters(buildUponParameters().setMaxVideoSizeSd())
//        }
//
//        player = ExoPlayer.Builder(this)
//            .setTrackSelector(trackSelector)
//            .build()
//            .also { exoPlayer ->
//                binding.videoExoplayer.player = exoPlayer
//
//            }
//
//        binding.videoExoplayer.player?.let { exoPlayer ->
//
//            val mediaItem = MediaItem.fromUri(url)
//
//            exoPlayer.setMediaItem(mediaItem)
//
//            exoPlayer.playWhenReady = playWhenReady
//            exoPlayer.seekTo(currentItem, playbackPosition)
//            exoPlayer.addListener(playbackStateListener)
//            exoPlayer.prepare()
//
//
//        }

//        trackSelector = DefaultTrackSelector(this)

        trackSelector = DefaultTrackSelector(/* context= */this, AdaptiveTrackSelection.Factory())


        player = ExoPlayer.Builder(this)
            .setTrackSelector(trackSelector!!)
            .build()
            .also { exoPlayer ->
                binding.videoExoplayer.player = exoPlayer

                val mediaItem = MediaItem.fromUri(url)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentItem, playbackPosition)
                exoPlayer.addListener(playbackStateListener)
                exoPlayer.prepare()



                initCustomController()
                initQualityDialog()
                initFullscreenDialog()

                val haveStartPosition = startWindow != C.INDEX_UNSET
                if (haveStartPosition) {
                    player!!.seekTo(startWindow, startPosition)
                }



                try {

                    VIDEO_QUALITY = getString(R.string.low_quality).toInt()
                    setVideoQuality(VIDEO_QUALITY)
                    setPlayBackSpeed(PLAY_BACK_SPEED)

                } catch (e: java.lang.Exception) {

                    Log.d(TAG, "initializePlayer: ")

                }

            }

    }


    var exoSettingDialog: ExoSettingDialog? = null
    var qualityArray = arrayOf<String>()


    private fun initQualityDialog() {
        exoSettingDialog = ExoSettingDialog(
            this,
            qualityArray,
            VIDEO_QUALITY,
            speed,
            PLAY_BACK_SPEED,
            this
        )
    }

    private fun initFullscreenDialog() {
        mFullScreenDialog =
            object : Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
                override fun onBackPressed() {
                    if (mExoPlayerFullscreen) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        closeFullscreenDialog()
                    }
                }
            }
    }

    private fun closeFullscreenDialog() {
        supportActionBar!!.show()
        (binding.videoExoplayer.getParent() as ViewGroup).removeView(binding.videoExoplayer)
        (findViewById<View>(R.id.root) as FrameLayout).addView(binding.videoExoplayer)
        mExoPlayerFullscreen = false
        mFullScreenDialog!!.dismiss()
        mFullScreenIcon.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_arrow_expand
            )
        )
    }

    private fun initCustomController() {


        controlView =
            binding.videoExoplayer.findViewById<CoordinatorLayout>(R.id.custom_controller)

        val exoGoBack: LinearLayout = controlView!!.findViewById<LinearLayout>(R.id.exo_exit)
        val retryButton = controlView!!.findViewById<ImageButton>(R.id.retry)
        val rewind = controlView!!.findViewById<ImageView>(R.id.exo_rewind)
        val forward = controlView!!.findViewById<ImageView>(R.id.exo_fastforword)


        val exoSettingBtn: ImageView = controlView!!.findViewById<ImageView>(R.id.setting_imageview)
        exoSettingBtn.setOnClickListener { exoSettingDialog?.show() }

        forward.setOnClickListener(View.OnClickListener {
            if (player != null) {
                val current = player!!.currentPosition
                val duration = player!!.duration
                if (current < duration) {
                    player!!.seekTo(player!!.contentPosition + 15000)
                }
            }
        })
        rewind.setOnClickListener(View.OnClickListener {
            if (player != null) {
                val current = player!!.currentPosition
                if (current > 0) {
                    player!!.seekTo(player!!.contentPosition - 15000)
                } else {
                    player!!.seekTo(0)
                }
            }
        })
        val centerControl = controlView!!.findViewById<LinearLayout>(R.id.center_control)
        mFullScreenIcon = controlView!!.findViewById<ImageView>(R.id.exo_fullscreen_icon)
        exoGoBack.setOnClickListener { finish() }
        retryButton.setOnClickListener(View.OnClickListener {
            centerControl.setVisibility(View.VISIBLE)
            retryButton.setVisibility(View.GONE)
            player!!.seekTo(0)
            player!!.playWhenReady = true
        })
        initFullscreenButton()
    }

    private fun initFullscreenButton() {
        val mFullScreenButton: RelativeLayout =
            controlView!!.findViewById<RelativeLayout>(R.id.exo_fullscreen_button)
        mFullScreenButton.setOnClickListener {
            if (!mExoPlayerFullscreen) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                openFullscreenDialog()
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                closeFullscreenDialog()
            }
        }
    }

    private fun openFullscreenDialog() {
        supportActionBar!!.hide()
        (binding.videoExoplayer.getParent() as ViewGroup).removeView(binding.videoExoplayer)
        mFullScreenDialog!!.addContentView(
            binding.videoExoplayer,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        mFullScreenIcon.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_arrow_collapse
            )
        )
        mExoPlayerFullscreen = true
        mFullScreenDialog!!.show()
    }

    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUi()
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer()
        }
    }

    public override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }


    public override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun playbackStateListener() = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Log.d(TAG, "changed state to $stateString")
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
        }
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            currentItem = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.removeListener(playbackStateListener)
            exoPlayer.release()

        }
        player = null
    }

    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.videoExoplayer).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onQualitySelectedListener(quality: String) {

        try {
            VIDEO_QUALITY = quality.toInt()

            setVideoQuality(quality.toInt())
        } catch (e: Exception) {
            Log.d(TAG, "onQualitySelectedListener: ")
        }
    }

    private fun setVideoQuality(quality: Int) {

        val parameters = trackSelector!!.buildUponParameters()
            .setForceHighestSupportedBitrate(true)

        if (quality > 0) {
            parameters.setMaxVideoBitrate(quality)
        } else {
            val q = getString(R.string.low_quality).toInt()
            parameters.setMaxVideoBitrate(q)
        }

        trackSelector!!.parameters = parameters.build()
    }

    override fun onSpeedSelectedListener(speed: Float) {

        try {
            PLAY_BACK_SPEED = speed
            setPlayBackSpeed(speed)
        } catch (e: Exception) {
            Log.d(TAG, "onSpeedSelectedListener: ")
        }

    }

    private fun setPlayBackSpeed(speed: Float) {
        val param = PlaybackParameters(speed)
        player!!.playbackParameters = param
    }


}