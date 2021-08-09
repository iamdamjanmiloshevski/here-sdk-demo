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
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import com.greyp.android.demo.R
import com.greyp.android.demo.common.Destination
import com.greyp.android.demo.common.Status
import com.greyp.android.demo.databinding.MainFragmentBinding
import com.greyp.android.demo.databinding.MapFragmentBinding
import com.greyp.android.demo.ui.common.BaseFragment
import com.greyp.android.demo.ui.state.AppState
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.mapviewlite.*
import com.here.sdk.search.Place
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import com.here.sdk.mapviewlite.MapCircle

import com.here.sdk.mapviewlite.MapCircleStyle

import com.here.sdk.core.GeoCircle


/**
Author: Damjan Miloshevski
Created on: 6.8.21
 */
@AndroidEntryPoint
class MapFragment : BaseFragment() {
  private lateinit var binding: MapFragmentBinding
  private lateinit var mapView: MapViewLite
  private val markers = mutableListOf<MapCircle>()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    initBinding(inflater, container, false)
    initUI()
    mapView.onCreate(savedInstanceState)
    return binding.root
  }

  private fun loadMapScene() {
    mapView.mapScene.loadScene(
      MapStyle.NORMAL_DAY
    ) { errorCode ->
      if (errorCode == null) {
        mapView.camera?.apply {
          target = coordinates
          zoomLevel = 15.toDouble()
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
    viewModel.appState().observe(viewLifecycleOwner, { appState ->
      if (appState is AppState.Ready) {
        loadMapScene()
      }
    })
    viewModel.observeForPlaces().observe(viewLifecycleOwner, { resource ->
      when (resource.status) {
        Status.SUCCESS -> {
          clearMarkers()
          val places = resource.data
          places?.let { placesOfInterest ->
            placesOfInterest.forEach { place ->
              addCircle(place)
            }
            markers.forEach {
              mapView.mapScene.addMapCircle(it)
            }
            placesOfInterest[0].geoCoordinates?.let {
              mapView.camera.target = it
              mapView.camera.zoomLevel = 15.toDouble()
            }
          }

        }
        Status.ERROR -> {

        }
        Status.LOADING -> {

        }
      }
    })
    viewModel.observeNavigation().observe(viewLifecycleOwner, { destination ->
      if (destination is Destination.List) {
        val action = MapFragmentDirections.actionMapFragmentToListFragment()
        navController.navigate(action)
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
          markers.add(mapCircle)
        }
      } catch (e: Exception) {
        Timber.e("Unable to add marker $e")
      }
    }
  }

  private fun clearMarkers() {
    if (markers.isNotEmpty()) markers.forEach {
      mapView.mapScene.removeMapCircle(it)
    }
  }

  private fun addMarker(place: Place) {
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
          mapView.mapScene.addMapCircle(mapCircle)
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
}