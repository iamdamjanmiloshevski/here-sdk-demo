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
import androidx.lifecycle.viewModelScope
import com.greyp.android.demo.common.Destination
import com.greyp.android.demo.common.Resource
import com.greyp.android.demo.persistence.IPreferences
import com.greyp.android.demo.persistence.SharedPreferencesManager
import com.greyp.android.demo.repository.IRepository
import com.greyp.android.demo.repository.Repository
import com.greyp.android.demo.ui.state.AppState
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.search.Place
import com.here.sdk.search.SearchError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GreypAppViewModel @Inject constructor(
  private val repository: Repository,
  private val sharedPreferencesManager: SharedPreferencesManager
) : ViewModel() {
  private val appStateObserver = MutableLiveData<AppState>()
  private val placesObserver = MutableLiveData<Resource<List<Place>>>()
  private val navigationObserver = MutableLiveData<Destination>()
  private val lastKnownLocationObserver = MutableLiveData<Resource<Location>>()

  fun emitAppState(appState: AppState) {
    appStateObserver.value = appState
  }

  fun fetchPlaces(
    geoCoordinates: GeoCoordinates
  ) {
    val category = sharedPreferencesManager.getString(IPreferences.KEY_CATEGORY)
    val radius = sharedPreferencesManager.getFloat(IPreferences.KEY_RADIUS)
    placesObserver.value = Resource.loading(null, null)
    repository.searchForPlacesInGeoCircle(
      geoCoordinates,
      radius.toDouble(),
      category,
      successCallback = { placesFlow ->
        viewModelScope.launch {
          placesFlow.catch { exception ->
            placesObserver.value = Resource.error(exception.localizedMessage, null)
          }.collect {
            placesObserver.value = Resource.success(null, it)
          }
        }
      },
      errorCallback = {
        placesObserver.value = Resource.error(it, null)
      })
  }

  fun navigate(destination: Destination) {
    navigationObserver.value = destination
  }


  fun setLastKnownLocation(location: Location) {
    lastKnownLocationObserver.value = Resource.success(null, location)
  }

  fun observeLastKnownLocation() = lastKnownLocationObserver
  fun observeForPlaces() = placesObserver
  fun observeNavigation() = navigationObserver
  fun observeAppState() = appStateObserver
}