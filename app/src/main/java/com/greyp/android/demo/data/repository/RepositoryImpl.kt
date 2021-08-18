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

package com.greyp.android.demo.data.repository

import com.greyp.android.demo.data.datasource.NetworkDataSource
import com.here.sdk.core.GeoCircle
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.core.LanguageCode
import com.here.sdk.search.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
Author: Damjan Miloshevski
Created on: 6.8.21
 */

class RepositoryImpl @Inject constructor(private val networkDataSource: NetworkDataSource) :
  Repository,CoroutineScope {
  override fun searchForPlacesInGeoCircle(
    onSuccess: (List<Place>) -> Unit,
    onError: (Throwable) -> Unit
  ) {
    networkDataSource.searchForPlacesInGeoCircle(onSuccess = {placesFlow->
      launch(coroutineContext) {
        placesFlow.catch { e->
          onError.invoke(e)
        }.collect { places->
          onSuccess.invoke(places)
        }
      }
    },onError = {
      onError.invoke(it)
    })
  }

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO


}