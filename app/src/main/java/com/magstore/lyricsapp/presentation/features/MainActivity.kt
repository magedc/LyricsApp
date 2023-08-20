package com.magstore.lyricsapp.presentation.features

import com.magstore.lyricsapp.R
import com.magstore.lyricsapp.presentation.base.activity.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<MainViewModel>(R.layout.activity_main) {

    private val mainViewModel: MainViewModel by viewModel()

    override fun setSupportNavigateUp(): Boolean {
        return false
    }

    override fun setUpUseCase(): MainViewModel {
        return mainViewModel
    }

    override fun getNavHostId(): Int {
        return 0
    }

}