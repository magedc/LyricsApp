package com.magstore.lyricsapp.presentation.helpers.shared

import android.annotation.SuppressLint
import android.util.Log
import androidx.databinding.BindingAdapter
import com.magstore.lrc.ILrcBuilder
import com.magstore.lrc.ILrcView
import com.magstore.lrc.LrcRow
import com.magstore.lrc.LrcView
import com.magstore.lyricsapp.presentation.features.MainViewModel
import com.magstore.lyricsapp.presentation.helpers.utils.readAssetFile

/**
 *Created by majed on 02/03/2023.

 **/

@SuppressLint("NewApi", "WrongConstant")
@BindingAdapter("bind:lrcFile")
fun setLrcFile(mLrcView:LrcView,mainViewModel: MainViewModel) {

    val rows: List<LrcRow> = ILrcBuilder.getInstance().getLrcRows(readAssetFile(mainViewModel.activity!!,"alerta.lrc"))!!
    mLrcView.setLrc(rows)
    mainViewModel.beginLrcPlay(mLrcView)

    mLrcView.setListener(object : ILrcView.LrcViewListener {
        override fun onLrcSeeked(newPosition: Int, row: LrcRow?) {
            mainViewModel.mp.seekTo(row!!.time.toInt())
        }
    })
}