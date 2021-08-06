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
import androidx.navigation.fragment.findNavController
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

/**
Author: Damjan Miloshevski
Created on: 6.8.21
 */
@AndroidEntryPoint
class MapFragment:BaseFragment() {
  private lateinit var binding: MapFragmentBinding
  private lateinit var mapView: MapViewLite
  private var coordinates = GeoCoordinates(41.9970749, 21.4307746)

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    initBinding(inflater, container, false)
    initUI()
    mapView.onCreate(savedInstanceState)
    requestData()
    return binding.root
  }

  private fun loadMapScene() {
    mapView.mapScene.loadScene(
      MapStyle.SATELLITE
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
    mapView = binding.mapView

  }

  override fun observeData() {
    viewModel.appState().observe(viewLifecycleOwner, { appState ->
      when (appState) {
        is AppState.Offline -> {

        }
        is AppState.PermissionsMissing -> {

        }
        AppState.Ready -> {
          loadMapScene()
        }
      }
    })
    viewModel.observeForPlaces().observe(viewLifecycleOwner, { resource ->
      when (resource.status) {
        Status.SUCCESS -> {
          val places = resource.data
          places?.let { placesOfInterest ->
            placesOfInterest.forEach { place ->
              addMarker(place)
            }
          }
        }
        Status.ERROR -> {

        }
        Status.LOADING -> {

        }
      }
    })
    viewModel.observeNavigation().observe(viewLifecycleOwner,{destination ->
        if(destination is Destination.List) {
          val action =   MapFragmentDirections.actionMapFragmentToListFragment()
          findNavController().navigate(action)
        }
    })
  }

  private fun addMarker(place: Place) {
    with(place) {
      try {
        val mapImage =
          MapImageFactory.fromResource(requireContext().resources, R.drawable.ic_marker)
        this.geoCoordinates?.let { coordinates ->
          val mapMarker = MapMarker(coordinates)
          mapMarker.addImage(mapImage, MapMarkerImageStyle())
          mapView.mapScene.addMapMarker(mapMarker)
        }
      } catch (e: Exception) {
        Timber.e("Unable to add marker $e")
      }
    }
  }

  override fun requestData() {
    viewModel.fetchPlaces("restaurant", coordinates)
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