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

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.greyp.android.demo.common.Resource
import com.greyp.android.demo.ui.state.AppState
import com.greyp.android.demo.ui.state.FloatingActionButtonState
import com.greyp.android.demo.usecases.POISearchUseCase
import com.here.sdk.search.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GreypAppViewModel @Inject constructor(private val poiUseCase: POISearchUseCase) :
  ViewModel(),POISearchUseCase.POISearchUseCaseCallback {
  protected val appStateObserver = MutableLiveData<AppState>()
  protected val placesObserver = MutableLiveData<Resource<List<Place>>>()
  protected val lastKnownLocationObserver = MutableLiveData<Resource<Location>>()
  protected val floatingActionButtonState = MutableLiveData<FloatingActionButtonState>()

  init {
    poiUseCase.setCallback(this)
  }

  fun emitAppState(appState: AppState) {
    appStateObserver.value = appState
  }

  fun emitFloatingButtonState(state: FloatingActionButtonState) {
    floatingActionButtonState.value = state
  }

  fun fetchPlaces(){
    poiUseCase.searchForPlacesInGeoCircle()
  }

  fun setLastKnownLocation(location: Location) {
    lastKnownLocationObserver.value = Resource.success(null, location)
  }

  fun observeLastKnownLocation() = lastKnownLocationObserver
  fun observeForPlaces() = placesObserver
  fun observeAppState() = appStateObserver
  fun observeFloatingActionButtonState() = floatingActionButtonState

  override fun onSuccess(places: List<Place>) {
    placesObserver.postValue(Resource.success(null, places))
  }

  override fun onError(e: Throwable) {
    placesObserver.postValue(Resource.error(e.localizedMessage, null))
  }
}