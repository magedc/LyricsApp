package com.magstore.lyricsapp.application

import com.magstore.lyricsapp.presentation.features.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 *Created by majed on 27/02/2023.

 **/


val viewModelModules = module {

    viewModel { MainViewModel() }
}


/**
 * helper modules
 * */
val helperModels = module {


}