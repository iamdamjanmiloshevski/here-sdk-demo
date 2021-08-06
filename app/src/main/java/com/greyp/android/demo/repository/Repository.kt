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

package com.greyp.android.demo.repository

import com.here.sdk.core.GeoCoordinates
import com.here.sdk.core.LanguageCode
import com.here.sdk.search.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
Author: Damjan Miloshevski
Created on: 6.8.21
 */

class Repository @Inject constructor() : IRepository, CoroutineScope {
  private val searchEngine: SearchEngine = SearchEngine()


  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO

  override fun searchByCategory(
    coordinates: GeoCoordinates,
    category: String,
    errorCallback: (String) -> Unit,
    successCallback: (Flow<List<Place>>) -> Unit
  ) {
    val maxItems = 30
    val query = TextQuery(category, coordinates)
    val searchOptions = SearchOptions(LanguageCode.EN_US, maxItems)
    val placesFound = mutableListOf<Place>()
    searchEngine.search(
      query, searchOptions
    ) { error, places ->
      if (error != null) {
        Timber.e("Something went wrong! error: $error")
        errorCallback.invoke(error.name)
      } else {
        placesFound.clear()
        places?.let {
          placesFound.addAll(it)
          successCallback.invoke(flow {

            emit(places)
          }.flowOn(coroutineContext))
        }
      }
    }
  }

}