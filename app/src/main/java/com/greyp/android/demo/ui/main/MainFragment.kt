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
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.greyp.android.demo.R
import com.greyp.android.demo.common.Destination
import com.greyp.android.demo.common.Status
import com.greyp.android.demo.databinding.MainFragmentBinding
import com.greyp.android.demo.ui.common.BaseFragment
import com.greyp.android.demo.ui.state.AppState
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.material.slider.*
import com.google.android.material.*
import com.greyp.android.demo.persistence.IPreferences
import com.greyp.android.demo.persistence.IPreferences.Companion.KEY_CATEGORY
import com.greyp.android.demo.persistence.IPreferences.Companion.KEY_RADIUS
import timber.log.Timber
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar


@AndroidEntryPoint
class MainFragment : BaseFragment(), Toolbar.OnMenuItemClickListener, View.OnClickListener {
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
    navController = findNavController()
    appBarConfiguration = AppBarConfiguration(navController.graph)
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

  override fun initBinding(
    inflater: LayoutInflater,
    container: ViewGroup?,
    attachToParent: Boolean
  ) {
    binding = MainFragmentBinding.inflate(inflater, container, attachToParent)
  }

  override fun observeData() {
    viewModel.appState().observe(viewLifecycleOwner, { appState ->
      if (appState is AppState.Offline) {
        Snackbar.make(binding.mainLayout, "No Internet connection", Snackbar.LENGTH_SHORT).show()
      } else if (appState is AppState.Ready) {
        Snackbar.make(binding.mainLayout, "Online", Snackbar.LENGTH_SHORT)
          .setTextColor(ContextCompat.getColor(requireContext(), R.color.sea_green)).show()
      }
    })
  }

  override fun onResume() {
    super.onResume()
    observeData()
  }
  override fun onMenuItemClick(item: MenuItem?): Boolean {
    return when (item?.itemId) {
      R.id.action_search -> {
        val dialog = MaterialDialog(requireContext())
          .cancelable(false)
          .customView(R.layout.view_search_dialog, scrollable = true)

        val customView = dialog.getCustomView()

        val inputField = customView.findViewById<EditText>(R.id.et_input)
        val radiusSlider = customView.findViewById<Slider>(R.id.radius_slider)
        val cancelButton = customView.findViewById<AppCompatButton>(R.id.bt_cancel)
        val submitButton = customView.findViewById<Button>(R.id.bt_submit)

        radiusSlider.setLabelFormatter { value -> String.format("%.0f km", value) }

        radiusSlider.addOnChangeListener { _, value, _ ->
          // Responds to when slider's value is changed
          sharedPreferencesManager.saveFloat(KEY_RADIUS, (value * 1000))//save in meters
        }

        cancelButton.setOnClickListener { dialog.dismiss() }
        submitButton.setOnClickListener {
          val category = inputField.text.toString()
          sharedPreferencesManager.saveString(KEY_CATEGORY, category)
          viewModel.fetchPlaces(coordinates)
          dialog.dismiss()
        }

        dialog.show()

        true
      }
      else -> false
    }
  }

  companion object {
    fun newInstance() = MainFragment()
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.fab_list -> viewModel.navigate(Destination.List)
      R.id.fab_map -> viewModel.navigate(Destination.Map)
    }
  }
}