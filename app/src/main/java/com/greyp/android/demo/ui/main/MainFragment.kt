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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.greyp.android.demo.R
import com.greyp.android.demo.common.Destination
import com.greyp.android.demo.databinding.MainFragmentBinding
import com.greyp.android.demo.ui.common.BaseFragment
import com.greyp.android.demo.ui.state.AppState
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.material.slider.*
import com.greyp.android.demo.persistence.IPreferences.Companion.KEY_CATEGORY
import com.greyp.android.demo.persistence.IPreferences.Companion.KEY_RADIUS
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.greyp.android.demo.ui.common.GenericTextWatcher
import com.greyp.android.demo.util.createMissingPermissionsMessage


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
    viewModel.observeAppState().observe(viewLifecycleOwner, { appState ->
      when (appState) {
        is AppState.Offline -> {
          Snackbar.make(
            binding.mainLayout,
            getString(R.string.no_internet_msg),
            Snackbar.LENGTH_SHORT
          ).show()
        }
        is AppState.Ready -> {
          Snackbar.make(binding.mainLayout, getString(R.string.online_msg), Snackbar.LENGTH_SHORT)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.sea_green)).show()
        }
        is AppState.PermissionsMissing -> {
          val permissions = appState.permissionsMissing
          handlePermissionsMissing(permissions)
        }
      }
    })
  }

  private fun handlePermissionsMissing(permissions: List<String>) {
    MaterialDialog(requireContext()).show {
      title(res = R.string.system_message)
      cancelable(false)
      message(text = permissions.createMissingPermissionsMessage(requireContext()))
      positiveButton(res = android.R.string.ok, click = {
        val intent = Intent(
          Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
          Uri.fromParts("package", context.packageName, null)
        )
        startActivity(intent)
        requireActivity().finishAffinity()
        dismiss()
      })
      negativeButton(res = android.R.string.cancel, click = {
        requireActivity().finishAffinity()
        dismiss()
      })
    }
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
        sharedPreferencesManager.saveString(KEY_CATEGORY, category)
        viewModel.fetchPlaces(coordinates)
        dialog.dismiss()
      }

    }
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.fab_list -> viewModel.navigate(Destination.List)
      R.id.fab_map -> viewModel.navigate(Destination.Map)
    }
  }
}