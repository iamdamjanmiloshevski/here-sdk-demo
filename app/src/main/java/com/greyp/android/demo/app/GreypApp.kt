package com.greyp.android.demo.app

import android.app.Application
import com.greyp.android.demo.BuildConfig
import timber.log.Timber

/*
    Author: Damjan Miloshevski 
    Created on 5.8.21 13:41
    Project: Demo
    © 2Play Tech  2021. All rights reserved
*/
class GreypApp : Application() {

  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
  }
}