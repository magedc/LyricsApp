package com.magstore.lyricsapp.presentation.base.viewModel

import android.annotation.SuppressLint
import android.content.Intent
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import com.magstore.lyricsapp.presentation.base.activity.BaseActivity


/**
 * Created by taieb.slama@zeta-box.com on 19/12/2022 .
 * Copyright (c) 2022 ZETA-BOX. All rights reserved.
 *
 * Abstract class BaseViewModel class used to be the
 * inherited class for all viewModels in the project
 */
@Suppress("UNCHECKED_CAST")
abstract class BaseViewModel<VDB : ViewDataBinding> :
    LifecycleObserver, ViewModel() {

    private lateinit var viewDataBinding: VDB

    @SuppressLint("StaticFieldLeak")
    var activity: BaseActivity<*>? = null

    fun setDataBinding(viewDataBinding: ViewDataBinding?) {
        if (viewDataBinding != null) {
            this.viewDataBinding = viewDataBinding as VDB
            configDataBinding(viewDataBinding)
        }
    }

    abstract fun configDataBinding(bindingView: VDB)


    open fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {}
    open fun onActivityResultCanceled() {}
    open fun onPermissionsResult(
        isGranted : Boolean
    ) {
    }

    open fun onResume() {}
    open fun onPause() {}

    open fun onPermissionsResultCanceled() {}

    open fun onAttachFragment() {}

    open fun onDialogFragmentDismiss() {}

}