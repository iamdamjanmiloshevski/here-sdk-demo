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

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.greyp.android.demo.domain.common.Resource
import com.greyp.android.demo.domain.usecases.POISearchUseCase
import com.here.sdk.search.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
Author: Damjan Miloshevski
Created on: 18.8.21
 */
@HiltViewModel
class GreypFragmentViewModel @Inject constructor(private val poiSearchUseCase: POISearchUseCase):ViewModel() {
  private val placesObserver = MutableLiveData<Resource<List<Place>>>()

  fun fetchPlaces(){
    placesObserver.postValue(Resource.loading(null,null))
    poiSearchUseCase.searchForPlacesInGeoCircle(onSuccess = {places->
      placesObserver.postValue(Resource.success(null,places))
    },onError = {error->
      placesObserver.postValue(Resource.error(error,null))
    })
  }

  fun observeForPlaces() = placesObserver
}