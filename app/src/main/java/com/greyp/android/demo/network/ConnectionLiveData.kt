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

package com.greyp.android.demo.network

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.*
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
Author: Damjan Miloshevski
Created on: 9.8.21
 */

class ConnectionLiveData(context: Context):LiveData<Boolean>() {
  private lateinit var networkCallback: ConnectivityManager.NetworkCallback
  private val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager


  override fun onActive() {
    networkCallback = createNetworkCallback()
    val networkRequest = NetworkRequest.Builder()
      .addCapability(NET_CAPABILITY_INTERNET)
      .addTransportType(TRANSPORT_WIFI)
      .addTransportType(TRANSPORT_CELLULAR)
      .build()
    cm.registerNetworkCallback(networkRequest, networkCallback)
  }

  override fun onInactive() {
    cm.unregisterNetworkCallback(networkCallback)
  }

  private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {

    override fun onAvailable(network: Network) {
      postValue(true)
    }
    override fun onLost(network: Network) {
      postValue(false)
    }
  }
}