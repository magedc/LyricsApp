package com.magstore.lyricsapp.presentation.base.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import com.magstore.lyricsapp.presentation.base.viewModel.BaseViewModel


/**
 * Created by taieb.slama@zeta-box.com on 19/12/2022 .
 * Copyright (c) 2022 ZETA-BOX. All rights reserved.
 *
 * Abstract Base Activity class used to be the inherited class for all activities in the project
 */
abstract class BaseActivity<VM : BaseViewModel<*>>(private val layoutId: Int) :
    AppCompatActivity(), LifecycleOwner {

    var bindingView: ViewDataBinding? = null
    lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = setUpUseCase()
        viewModel.activity = this
        val root = LayoutInflater.from(this).inflate(layoutId, null, false)
        setContentView(root)
        bindingView = DataBindingUtil.bind(root)
        viewModel.setDataBinding(bindingView)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.e("request_permission","BaseActivity -> onRequestPermissionsResult")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onSupportNavigateUp(): Boolean {
        return setSupportNavigateUp()
    }

    abstract fun setSupportNavigateUp(): Boolean

    abstract fun setUpUseCase(): VM

    abstract fun getNavHostId(): Int

}