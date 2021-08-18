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

package com.greyp.android.demo.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.greyp.android.demo.domain.common.Status
import com.greyp.android.demo.databinding.MapFragmentBinding
import com.greyp.android.demo.ui.common.BaseFragment
import com.greyp.android.demo.util.showSimpleMessageDialog
import com.here.sdk.mapviewlite.*
import com.here.sdk.search.Place
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import com.here.sdk.mapviewlite.MapCircle
import com.here.sdk.mapviewlite.MapCircleStyle
import com.here.sdk.core.GeoCircle
import com.here.sdk.mapviewlite.MapMarkerImageStyle
import com.here.sdk.mapviewlite.MapMarker
import com.greyp.android.demo.R
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.mapviewlite.MapImageFactory



/**
Author: Damjan Miloshevski
Created on: 6.8.21
 */
@AndroidEntryPoint
class MapFragment : BaseFragment() {
  private lateinit var binding: MapFragmentBinding
  private lateinit var mapView: MapViewLite
  private val markers = mutableListOf<MapMarker>()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    initBinding(inflater, container, false)
    initUI()
    mapView.onCreate(savedInstanceState)
    return binding.root
  }

  private fun loadMapScene(coordinates: GeoCoordinates) {
    mapView.mapScene.loadScene(
      MapStyle.NORMAL_DAY
    ) { errorCode ->
      if (errorCode == null) {
        mapView.camera?.apply {
          target = coordinates
          zoomLevel = DEFAULT_ZOOM_LEVEL
        }
      } else {
        Timber.e("onLoadScene failed: $errorCode")
      }
    }
  }


  override fun initUI() {
    navController = findNavController()
    mapView = binding.mapView
  }

  override fun observeData() {
  viewModel.observeLastKnownLocation().observe(viewLifecycleOwner,{resource ->
      if(resource.status == Status.SUCCESS){
        val location = resource.data
        location?.let { lastKnownLocation ->
          val coordinates = GeoCoordinates(lastKnownLocation.latitude,lastKnownLocation.longitude)
          loadMapScene(coordinates)
          viewModel.fetchPlaces()
        }
      }
  })
    viewModel.observeForPlaces().observe(viewLifecycleOwner, { resource ->
      if (resource.status == Status.SUCCESS) {
        clearMarkers()
        val places = resource.data
        places?.let { placesOfInterest ->
          placesOfInterest.forEach { place ->
            addMarker(place)
          }
          markers.forEach {
            mapView.mapScene.addMapMarker(it)
          }
          placesOfInterest[0].geoCoordinates?.let {
            mapView.camera.target = it
            mapView.camera.zoomLevel = DEFAULT_ZOOM_LEVEL
          }
        }
      } else if (resource.status == Status.ERROR) {
        val errorMessage = resource.message
        requireContext().showSimpleMessageDialog(text = errorMessage)
      }
    })
  }

  private fun addCircle(place: Place) {
    with(place) {
      try {
        this.geoCoordinates?.let { coordinates ->
          val radiusInMeters = 10f
          val geoCircle = GeoCircle(
            coordinates,
            radiusInMeters.toDouble()
          )
          val mapCircleStyle = MapCircleStyle()
          mapCircleStyle.setFillColor(0x00908AA0, PixelFormat.RGBA_8888)
          val mapCircle = MapCircle(geoCircle, mapCircleStyle)
        }
      } catch (e: Exception) {
        Timber.e("Unable to add marker $e")
      }
    }
  }

  private fun clearMarkers() {
    if (markers.isNotEmpty()) markers.forEach {
      mapView.mapScene.removeMapMarker(it)
    }
    markers.clear()
  }

  private fun addMarker(place: Place) {
    /**
     * Per HERE SDK's docs:
     * The HERE SDK for Android supports PNG resources with or without transparency (alpha channel)
     * - as well as all other common bitmap resources that are natively supported by Android.
     * Vector graphics are not yet supported - even when converted from SVG to an XML representation within Android Studio.
     * ----
     * If markers throw IllegalStateException just remove the .xml (SVG) resource from that resource's folder
     * because that's what fails the app
     *
     */
    with(place) {
      try {
        this.geoCoordinates?.let { coordinates ->
          val mapImage =
            MapImageFactory.fromResource(requireContext().resources, R.drawable.ic_marker)
          val mapMarker = MapMarker(coordinates)
          mapMarker.addImage(mapImage, MapMarkerImageStyle())
          markers.add(mapMarker)
        }
      } catch (e: Exception) {
        Timber.e("Unable to add marker $e")
      }
    }
  }

  override fun initBinding(
    inflater: LayoutInflater,
    container: ViewGroup?,
    attachToParent: Boolean
  ) {
    binding = MapFragmentBinding.inflate(inflater, container, false)
  }

  override fun onPause() {
    super.onPause()
    mapView.onPause()
  }

  override fun onResume() {
    super.onResume()
    observeData()
    mapView.onResume()
  }

  override fun onDestroy() {
    super.onDestroy()
    mapView.onDestroy()
  }
  companion object{
    const val DEFAULT_ZOOM_LEVEL = 15.toDouble()
  }
}