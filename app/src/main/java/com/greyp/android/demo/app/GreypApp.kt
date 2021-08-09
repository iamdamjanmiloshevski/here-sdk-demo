/*
 * MIT License
 *
 * Copyright (c) 2021 Greyp Bikes d.o.o.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.greyp.android.demo.app

import android.app.Application
import com.greyp.android.demo.BuildConfig
import com.greyp.android.demo.persistence.IPreferences.Companion.KEY_CATEGORY
import com.greyp.android.demo.persistence.IPreferences.Companion.KEY_RADIUS
import com.greyp.android.demo.persistence.SharedPreferencesManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
Author: Damjan Miloshevski
Created on: 5.8.21
 */

@HiltAndroidApp
class GreypApp : Application() {
  @Inject
  lateinit var sharedPreferencesManager: SharedPreferencesManager

  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    val category = sharedPreferencesManager.getString(KEY_CATEGORY)
    val radius = sharedPreferencesManager.getFloat(KEY_RADIUS)
    if (category == "") sharedPreferencesManager.saveString(KEY_CATEGORY, "restaurant")
    if (radius == 0f) sharedPreferencesManager.saveFloat(KEY_RADIUS, 3000f)
  }

}