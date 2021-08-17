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

package com.greyp.android.demo.usecases

import com.greyp.android.demo.persistence.IPreferences
import com.greyp.android.demo.persistence.SharedPreferencesManager
import com.greyp.android.demo.repository.Repository
import com.greyp.android.demo.repository.RepositoryImpl
import com.greyp.android.demo.repository.SearchResultsCallback
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.search.Place
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
Author: Damjan Miloshevski
Created on: 17.8.21
 */

class POISearchUseCase @Inject constructor(
  val sharedPreferencesManager: SharedPreferencesManager,
  private val repository: Repository
) {
  private var callback: POISearchUseCaseCallback? = null
  fun searchForPlacesInGeoCircle() {
    val category = sharedPreferencesManager.getString(IPreferences.KEY_CATEGORY)
    val radius = sharedPreferencesManager.getFloat(IPreferences.KEY_RADIUS).toDouble()
    val latitude = sharedPreferencesManager.getFloat(IPreferences.KEY_LATITUDE).toDouble()
    val longitude = sharedPreferencesManager.getFloat(IPreferences.KEY_LONGITUDE).toDouble()
    repository.searchForPlacesInGeoCircle(
      GeoCoordinates(latitude,longitude),
      category = category,
      radius = radius,
      callback = object: SearchResultsCallback {
        override fun onSuccess(resultsFlow: Flow<List<Place>>) {
          GlobalScope.launch(Dispatchers.IO) {
            resultsFlow.catch { exception ->
              callback?.onError(exception)
            }.collect { places ->
              callback?.onSuccess(places)
            }
          }
        }

        override fun onError(e: Throwable) {
          callback?.onError(e)
        }
      })
  }

  fun setCallback(callback: POISearchUseCaseCallback) {
    this.callback = callback
  }

  interface POISearchUseCaseCallback {
    fun onSuccess(places: List<Place>)
    fun onError(e: Throwable)
  }
}