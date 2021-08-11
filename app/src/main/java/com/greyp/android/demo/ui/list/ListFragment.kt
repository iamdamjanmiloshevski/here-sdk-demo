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
import com.greyp.android.demo.ui.state.AppState
import dagger.hilt.android.AndroidEntryPoint

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
    showError(false)
    showProgress(false)
    binding.rvItems.apply {
      layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
      adapter = placesAdapter
    }
    placesAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
      override fun onChanged() {
        if (placesAdapter.itemCount == 0) {
          binding.errorView.visibility = View.VISIBLE
          showProgress(false)
        }
      }
    })
  }

  override fun observeData() {
    viewModel.observeNavigation().observe(viewLifecycleOwner, { destination ->
      if (navController.currentDestination?.id == R.id.ListFragment) {
        if (destination is Destination.Map) {
          val action = ListFragmentDirections.actionListFragmentToMapFragment()
          navController.navigate(action)
        }
      }
    })
    viewModel.observeForPlaces().observe(viewLifecycleOwner, { resource ->
      when (resource.status) {
        Status.SUCCESS -> {
          showProgress(false)
          showError(false)
          val places = resource.data
          places?.let { placesOfInterest ->
            placesAdapter.setData(placesOfInterest)
          }
        }
        Status.ERROR -> {
          showProgress(false)
          showError(true)
          binding.errorView.setErrorMessage(errorMessage = resource.message)
        }
        Status.LOADING -> {
          showProgress(true)
          binding.errorView.visibility = View.GONE
        }
      }
    })
    viewModel.observeAppState().observe(viewLifecycleOwner, { appState ->
      if (appState is AppState.PermissionsMissing) {
        showError(false)
        showProgress(false)
      }
    })
  }

  private fun showError(show: Boolean) {
    if (show) {
      binding.errorView.visibility = View.VISIBLE
      binding.rvItems.visibility = View.GONE
    } else {
      binding.errorView.visibility = View.GONE
      binding.rvItems.visibility = View.VISIBLE
    }
  }

  private fun showProgress(show: Boolean) {
    if (show) {
      binding.progressBar.visibility = View.VISIBLE
      binding.rvItems.visibility = View.GONE
    } else {
      binding.progressBar.visibility = View.GONE
      binding.rvItems.visibility = View.VISIBLE
    }
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