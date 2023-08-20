package com.magstore.lyricsapp.presentation.features

import android.media.MediaPlayer
import com.magstore.lrc.ILrcView
import com.magstore.lyricsapp.R
import com.magstore.lyricsapp.presentation.base.viewModel.BaseViewModel
import com.magstore.lyricsapp.databinding.ActivityMainBinding
import java.io.IOException
import java.util.*

class MainViewModel: BaseViewModel<ActivityMainBinding>() {

    lateinit var mp: MediaPlayer
    private var mTimer: Timer? = null
    private val mPalyTimerDuration = 1000

    override fun configDataBinding(bindingView: ActivityMainBinding) {
        bindingView.viewModel = this
        mp = MediaPlayer()
    }

    fun beginLrcPlay(mLrcView:ILrcView) {
        var beginTime: Long = -1
        try {
            mp = MediaPlayer.create(activity, R.raw.alerta)
            mp.setOnPreparedListener { mp ->
                mp.start()
                if (mTimer == null) {
                    mTimer = Timer()
                    val mTask = object : TimerTask() {
                        override fun run() {
                            try {
                                if (beginTime == -1L) {
                                    beginTime = System.currentTimeMillis()
                                }
                                val timePassed = mp.currentPosition.toLong()
                                activity!!.runOnUiThread {
                                    mLrcView.seekLrcToTime(
                                        timePassed
                                    )
                                }
                            } catch (e: Exception) {
                                e.message
                            }
                        }

                    }
                    mTimer?.scheduleAtFixedRate(mTask, 0, mPalyTimerDuration.toLong())
                }
            }
            mp.setOnCompletionListener { stopLrcPlay() }
            mp.prepare()
            mp.start()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopLrcPlay() {
        if (mTimer != null) {
            mTimer!!.cancel()
            mTimer = null
        }
    }
}