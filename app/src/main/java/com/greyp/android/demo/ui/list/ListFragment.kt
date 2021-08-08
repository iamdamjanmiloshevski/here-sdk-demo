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

package com.greyp.android.demo.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.greyp.android.demo.R
import com.greyp.android.demo.common.Destination
import com.greyp.android.demo.common.Status
import com.greyp.android.demo.databinding.FragmentListBinding
import com.greyp.android.demo.ui.adapters.PlacesRecyclerViewAdapter
import com.greyp.android.demo.ui.common.BaseFragment
import com.greyp.android.demo.ui.map.MapFragmentDirections
import com.here.sdk.core.GeoCoordinates
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
Author: Damjan Miloshevski
Created on: 6.8.21
 */

@AndroidEntryPoint
class ListFragment : BaseFragment() {
  private lateinit var binding: FragmentListBinding
  private var placesAdapter: PlacesRecyclerViewAdapter = PlacesRecyclerViewAdapter()
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    initBinding(inflater, container, false)
    initUI()
    return binding.root
  }

  override fun initUI() {
    navController = findNavController()
    binding.rvItems.apply {
      layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
      adapter = placesAdapter
    }
    placesAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
      override fun onChanged() {
        if (placesAdapter.itemCount > 0) {
          //todo
        }
      }
    })
  }

  override fun observeData() {
    viewModel.observeNavigation().observe(viewLifecycleOwner, { destination ->
      if (navController.currentDestination?.id == R.id.ListFragment) {
        // added this to avoid crash on first app launch because of a button press simultaneously
        //for more info please refer to https://stackoverflow.com/questions/54689361/avoiding-android-navigation-illegalargumentexception-in-navcontroller
        if (destination is Destination.Map) {
          val action = ListFragmentDirections.actionListFragmentToMapFragment()
          navController.navigate(action)
        }
      }
    })
    viewModel.observeForPlaces().observe(viewLifecycleOwner, { resource ->
      when (resource.status) {
        Status.SUCCESS -> {
          val places = resource.data
          places?.let { placesOfInterest ->
            placesAdapter.setData(placesOfInterest)
          }
        }
        Status.ERROR -> {

        }
        Status.LOADING -> {

        }
      }
    })
    viewModel.observeLastKnownLocation().observe(viewLifecycleOwner, { resource ->
      when (resource.status) {
        Status.SUCCESS -> {
          val location = resource.data
          location?.let {
            coordinates = GeoCoordinates(it.latitude, it.longitude)
            viewModel.fetchPlaces("restaurant", coordinates)
          }

        }
        Status.ERROR -> {
        }
        Status.LOADING -> {
        }
      }

    })
  }


  override fun onResume() {
    super.onResume()
    observeData()
  }

  override fun initBinding(
    inflater: LayoutInflater,
    container: ViewGroup?,
    attachToParent: Boolean
  ) {
    binding = FragmentListBinding.inflate(inflater, container, attachToParent)
  }
}