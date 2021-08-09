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

package com.greyp.android.demo.ui.common

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.greyp.android.demo.persistence.SharedPreferencesManager
import com.greyp.android.demo.services.LocationService
import com.greyp.android.demo.services.LocationService.Companion.KEY_INTENT_FILTER
import com.greyp.android.demo.services.LocationService.Companion.KEY_LAST_LOCATION
import com.greyp.android.demo.ui.state.AppState
import timber.log.Timber
import javax.inject.Inject

/**
Author: Damjan Miloshevskia
Created on: 5.8.21
 */

abstract class BaseActivity : AppCompatActivity() {
  protected val viewModel: GreypAppViewModel by viewModels()
  private val notGrantedPermissions = mutableListOf<String>()
  @Inject
  lateinit var sharedPreferencesManager: SharedPreferencesManager
  private val locationListener: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      intent?.let { locationIntent ->
        Timber.i("Location received")
        val location = locationIntent.getParcelableExtra(KEY_LAST_LOCATION) as Location?
        location?.let {
          viewModel.setLastKnownLocation(it)
        }
      }
    }
  }

  protected fun startLocationService() {
    val locationServiceIntent = Intent(this, LocationService::class.java)
    startService(locationServiceIntent)
  }
  protected fun requestPermissions() {
      permissionsLauncher.launch(
        arrayOf(
          Manifest.permission.ACCESS_COARSE_LOCATION,
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
      )
  }


  private val permissionsLauncher =
    registerForActivityResult(
      ActivityResultContracts.RequestMultiplePermissions()
    )
    { permissions ->
      // Handle Permission granted/rejected
      permissions.entries.forEach {
        val permissionName = it.key
        val isGranted = it.value
        if (!isGranted) notGrantedPermissions.add(permissionName)
        if (notGrantedPermissions.isNotEmpty()) viewModel.emitAppState(
          AppState.PermissionsMissing(
            notGrantedPermissions
          )
        ) else {
          startLocationService()
          viewModel.emitAppState(AppState.Ready)
        }
      }
    }
  override fun onStart() {
    super.onStart()
    LocalBroadcastManager.getInstance(this)
      .registerReceiver(locationListener, IntentFilter(KEY_INTENT_FILTER))
  }

  override fun onDestroy() {
    super.onDestroy()
    LocalBroadcastManager.getInstance(this).unregisterReceiver(locationListener)
  }


}