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
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.slider.Slider
import com.greyp.android.demo.R
import com.greyp.android.demo.databinding.ActivityMainBinding
import com.greyp.android.demo.data.network.ConnectionLiveData
import com.greyp.android.demo.data.persistence.IPreferences
import com.greyp.android.demo.data.persistence.IPreferences.Companion.KEY_RADIUS
import com.greyp.android.demo.exceptions.BooleanToStringException
import com.greyp.android.demo.ui.common.BaseActivity
import com.greyp.android.demo.ui.common.GenericTextWatcher
import com.greyp.android.demo.ui.listeners.OnListScrollChangeListener
import com.greyp.android.demo.ui.state.AppState
import com.greyp.android.demo.ui.state.FloatingActionButtonState
import com.greyp.android.demo.ui.list.ListFragmentDirections
import com.greyp.android.demo.ui.map.MapFragmentDirections
import com.greyp.android.demo.util.createMissingPermissionsMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity(), View.OnClickListener, OnListScrollChangeListener,
  NavController.OnDestinationChangedListener {

  private lateinit var appBarConfiguration: AppBarConfiguration
  private lateinit var binding: ActivityMainBinding
  private lateinit var navController: NavController

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    binding.toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black))
    setSupportActionBar(binding.toolbar)
    initUI()
    requestPermissions()
    verifyUserPermissionsAndProceed()
  }

  private fun verifyUserPermissionsAndProceed() {
    if (ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      requestPermissions()
      return
    } else {
      startLocationService()
    }
  }

  private fun initUI() {
    navController = findNavController(R.id.nav_host_fragment_content_main)
    appBarConfiguration = AppBarConfiguration(navController.graph)
    setupActionBarWithNavController(navController, appBarConfiguration)
    navController.addOnDestinationChangedListener(this)
    connectionLiveData = ConnectionLiveData(this)
    binding.fabNavigation.setOnClickListener(this)
  }

  override fun onSupportNavigateUp(): Boolean {
    val navController = findNavController(R.id.nav_host_fragment_content_main)
    return navController.navigateUp(appBarConfiguration)
        || super.onSupportNavigateUp()
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.fabNavigation -> {
        when (navController.currentDestination?.id) {
          R.id.MapFragment -> {
            val action = MapFragmentDirections.actionMapFragmentToListFragment()
            navController.navigate(action)
          }
          R.id.ListFragment -> {
            val action = ListFragmentDirections.actionListFragmentToMapFragment()
            navController.navigate(action)
          }
        }
      }
    }
  }

  override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
    val searchItem = menu?.findItem(R.id.action_search)
    searchItem?.let {
      it.icon.apply {
        this.mutate()
        this.setTint(ContextCompat.getColor(this@MainActivity, R.color.black))
      }
    }
    return true
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_search -> {
        val dialog = MaterialDialog(this)
          .cancelable(false)
          .customView(R.layout.view_search_dialog, scrollable = true)
        val customView = dialog.getCustomView()
        interactWithDialogView(customView, dialog)
        dialog.show()
        true
      }
      else -> false
    }
  }

  private fun interactWithDialogView(
    customView: View,
    dialog: MaterialDialog
  ) {
    val inputField = customView.findViewById<EditText>(R.id.et_input)
    val radiusSlider = customView.findViewById<Slider>(R.id.radius_slider)
    val cancelButton = customView.findViewById<AppCompatButton>(R.id.bt_cancel)
    val submitButton = customView.findViewById<Button>(R.id.bt_submit)
    var category = ""
    val radius = sharedPreferencesManager.getFloat(KEY_RADIUS)
    radiusSlider.value = radius / 1000 //convert in kilometers
    radiusSlider.setLabelFormatter { value -> String.format("%.0f km", value) }
    radiusSlider.addOnChangeListener { _, value, _ ->
      sharedPreferencesManager.saveFloat(KEY_RADIUS, (value * 1000))//convert in meters
    }
    inputField.addTextChangedListener(GenericTextWatcher(callback = {
      category = it
    }, errorCallback = {
      inputField.error = null
    }))
    cancelButton.setOnClickListener { dialog.dismiss() }
    submitButton.setOnClickListener {
      if (category.isBlank()) {
        inputField.error = getString(R.string.input_field_err_msg)
      } else {
        try {
          sharedPreferencesManager.saveString(IPreferences.KEY_CATEGORY, category)
          fragmentViewModel.fetchPlaces()
          dialog.dismiss()
        } catch (e: BooleanToStringException) {
          inputField.error = getString(R.string.boolean_to_string_err_msg)
        } catch (e: Exception) {
          inputField.error = getString(R.string.characters_error_msg)
        }
      }

    }
  }

  private fun observeData() {
    viewModel.observeAppState().observe(this, { appState ->
      when (appState) {
        is AppState.Offline -> {
          Snackbar.make(
            binding.root,
            getString(R.string.no_internet_msg),
            Snackbar.LENGTH_SHORT
          ).show()
        }
        is AppState.Ready -> {
          Snackbar.make(binding.root, getString(R.string.online_msg), Snackbar.LENGTH_SHORT)
            .setTextColor(ContextCompat.getColor(this, R.color.sea_green)).show()
        }
        is AppState.PermissionsMissing -> {
          val permissions = appState.permissionsMissing
          handlePermissionsMissing(permissions)
        }
      }
    })

    viewModel.observeFloatingActionButtonState().observe(this, { state ->
      when (state) {
        is FloatingActionButtonState.List -> binding.fabNavigation.setImageResource(state.icon)
        is FloatingActionButtonState.Map -> binding.fabNavigation.setImageResource(state.icon)
      }
    })
    listenForNetworkChanges()
  }

  private fun handlePermissionsMissing(permissions: List<String>) {
    MaterialDialog(this).show {
      title(res = R.string.system_message)
      cancelable(false)
      message(text = permissions.createMissingPermissionsMessage(this@MainActivity))
      positiveButton(res = android.R.string.ok, click = {
        val intent = Intent(
          Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
          Uri.fromParts("package", context.packageName, null)
        )
        startActivity(intent)
        finishAffinity()
        dismiss()
      })
      negativeButton(res = android.R.string.cancel, click = {
        finishAffinity()
        dismiss()
      })
    }
  }

  override fun onResume() {
    super.onResume()
    observeData()
  }

  private fun listenForNetworkChanges() {
    connectionLiveData.observe(this, {
      when (it) {
        true -> {
          viewModel.emitAppState(AppState.Ready)
        }
        false -> {
          viewModel.emitAppState(AppState.Offline)
        }
      }
    })
  }

  override fun onScrollChanged(show: Boolean) {
    if (show) binding.fabNavigation.show()
    else binding.fabNavigation.hide()
  }

  override fun onDestinationChanged(
    controller: NavController,
    destination: NavDestination,
    arguments: Bundle?
  ) {
    when (destination.id) {
      R.id.MapFragment -> {
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        viewModel.emitFloatingButtonState(FloatingActionButtonState.Map())
      }
      R.id.ListFragment -> viewModel.emitFloatingButtonState(FloatingActionButtonState.List())
    }
  }
}