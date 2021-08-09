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


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.greyp.android.demo.R
import com.greyp.android.demo.common.Status
import com.greyp.android.demo.persistence.SharedPreferencesManager
import com.here.sdk.core.GeoCoordinates
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
Author: Damjan Miloshevski
Created on: 6.8.21Å
 */
@AndroidEntryPoint
abstract class BaseFragment():Fragment(),IBaseFragmentView{
  protected val viewModel: GreypAppViewModel by activityViewModels()
  protected var appBarConfiguration:AppBarConfiguration? = null
  protected lateinit var navController:NavController
  protected var coordinates = GeoCoordinates(45.81332,15.97733)
  @Inject
  lateinit var sharedPreferencesManager: SharedPreferencesManager

  override fun initToolbar(
    toolbar: Toolbar,
    appBarConfiguration: AppBarConfiguration,
    menuRes: Int,
    menuItemClickListener: Toolbar.OnMenuItemClickListener
  ) {
    toolbar.setupWithNavController(findNavController(),appBarConfiguration)
    toolbar.inflateMenu(menuRes)
    toolbar.setOnMenuItemClickListener(menuItemClickListener)
  }

  override fun onResume() {
    super.onResume()
    viewModel.observeLastKnownLocation().observe(viewLifecycleOwner, { resource ->
      when (resource.status) {
        Status.SUCCESS -> {
          val location = resource.data
          location?.let {
            coordinates = GeoCoordinates(it.latitude,it.longitude)
            viewModel.fetchPlaces(coordinates)
          }

        }
        Status.ERROR -> {
        }
        Status.LOADING -> {
        }
      }

    })
  }
}