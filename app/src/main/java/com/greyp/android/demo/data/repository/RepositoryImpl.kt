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

import com.here.sdk.core.GeoCircle
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

class RepositoryImpl @Inject constructor() : Repository, CoroutineScope {
  private val searchEngine: SearchEngine = SearchEngine()

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO

  override fun searchForPlacesInGeoCircle(
    coordinates: GeoCoordinates,
    radius: Double,
    category: String,
    maxItems: Int,
    callback: SearchResultsCallback
  ) {
    val geoCircle = GeoCircle(coordinates, radius)
    val query = TextQuery(category, geoCircle)
    val searchOptions = SearchOptions(LanguageCode.EN_US, maxItems)
    val placesFound = mutableListOf<Place>()

    searchEngine.search(
      query, searchOptions
    ) { error, places ->
      if (error != null) {
        Timber.e("Something went wrong! error: $error")
        handleErrors(error, callback)
      } else {
        placesFound.clear()
        places?.let {
          placesFound.addAll(it)
          callback.onSuccess(flow {
            emit(places)
          }.flowOn(coroutineContext))
        }
      }
    }
  }

  private fun handleErrors(
    error: SearchError,
    callback: SearchResultsCallback
  ) {
    when (error) {
      SearchError.NO_RESULTS_FOUND -> callback.onError(Throwable("No results found for the specified criteria"))
      SearchError.AUTHENTICATION_FAILED,
      SearchError.MAX_ITEMS_OUT_OF_RANGE,
      SearchError.POLYLINE_TOO_LONG,
      SearchError.PARSING_ERROR,
      SearchError.HTTP_ERROR,
      SearchError.SERVER_UNREACHABLE,
      SearchError.INVALID_PARAMETER,
      SearchError.FORBIDDEN,
      SearchError.EXCEEDED_USAGE_LIMIT,
      SearchError.OPERATION_FAILED,
      SearchError.OPERATION_CANCELLED,
      SearchError.OPTION_NOT_AVAILABLE,
      SearchError.TIMED_OUT,
      SearchError.QUERY_TOO_LONG,
      SearchError.FILTER_TOO_LONG -> callback.onError(Throwable("Something went wrong. Please try again later or reach out to support"))
      SearchError.OFFLINE -> callback.onError(Throwable("Please go online to fetch results!"))
    }
  }
}
interface SearchResultsCallback {
  fun onSuccess(resultsFlow: Flow<List<Place>>)
  fun onError(e: Throwable)
}