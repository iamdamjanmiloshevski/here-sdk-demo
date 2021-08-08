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

package com.greyp.android.demo.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices
import com.greyp.android.demo.ui.common.GreypAppViewModel
import com.greyp.android.demo.util.Constants.LOCATION_UPDATES_FASTEST_INTERVAL
import com.greyp.android.demo.util.Constants.LOCATION_UPDATES_INTERVAL
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
Author: Damjan Miloshevski
Created on: 8.8.21
 */

@AndroidEntryPoint
class LocationService : Service() {
  private lateinit var fusedLocationClient: FusedLocationProviderClient
  private lateinit var locationCallback: LocationCallback

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Timber.i("Location service started")
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    locationCallback = object : LocationCallback() {
      override fun onLocationResult(locationResult: LocationResult) {
        Timber.i("New location detected: (${locationResult.lastLocation.latitude}, ${locationResult.lastLocation.longitude})")
        notifyLastKnownLocation(locationResult.lastLocation)
      }
    }
    startLocationUpdates()
    return START_NOT_STICKY
  }

  private fun notifyLastKnownLocation(lastKnownLocation: Location?) {
    val locationIntent = Intent(KEY_INTENT_FILTER)
    locationIntent.putExtra(KEY_LAST_LOCATION, lastKnownLocation)
    LocalBroadcastManager.getInstance(this).sendBroadcast(locationIntent)
  }

  private fun createLocationRequest(): LocationRequest {
    return LocationRequest.create().apply {
      interval = LOCATION_UPDATES_INTERVAL
      fastestInterval = LOCATION_UPDATES_FASTEST_INTERVAL
      priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    } as LocationRequest
  }

  @SuppressLint("MissingPermission")
  private fun startLocationUpdates() {
    Timber.i("Requesting location updates")
    fusedLocationClient.requestLocationUpdates(
      createLocationRequest(),
      locationCallback,
      Looper.getMainLooper()
    )
  }

  override fun onDestroy() {
    super.onDestroy()
    Timber.i("Location service destroyed")
    fusedLocationClient.removeLocationUpdates(locationCallback)
  }

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }
  companion object{
    //region Service constants
    const val KEY_INTENT_FILTER = "USER_LOCATION"
    const val KEY_LAST_LOCATION = "location"
    //endregion
  }
}