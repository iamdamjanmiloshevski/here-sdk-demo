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

package com.greyp.android.demo.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.greyp.android.demo.R
import com.greyp.android.demo.common.Destination
import com.greyp.android.demo.common.Status
import com.greyp.android.demo.databinding.MainFragmentBinding
import com.greyp.android.demo.ui.common.BaseFragment
import com.greyp.android.demo.ui.state.AppState
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainFragment : BaseFragment(), Toolbar.OnMenuItemClickListener,View.OnClickListener {
  private lateinit var binding: MainFragmentBinding
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
    appBarConfiguration = AppBarConfiguration(findNavController().graph)
    appBarConfiguration?.let { appBarConfig ->
      initToolbar(
        binding.toolbar,
        appBarConfig,
        R.menu.menu_main,
        this
      )
    }
    binding.fabMap.setOnClickListener(this)
    binding.fabList.setOnClickListener(this)
  }

  override fun observeData() {
    viewModel.appState().observe(viewLifecycleOwner, { appState ->
      when (appState) {
        is AppState.Offline -> {

        }
        is AppState.PermissionsMissing -> {

        }
        AppState.Ready -> {

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
    binding = MainFragmentBinding.inflate(inflater, container, attachToParent)
  }

  override fun onMenuItemClick(item: MenuItem?): Boolean {
    return true
  }

  companion object {
    fun newInstance() = MainFragment()
  }

  override fun onClick(v: View?) {
    when(v?.id){
      R.id.fab_list -> viewModel.navigate(Destination.List)
      R.id.fab_map -> viewModel.navigate(Destination.Map)
    }
  }
}