package com.greyp.android.demo.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.greyp.android.demo.R
import com.greyp.android.demo.databinding.MainFragmentBinding
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.mapviewlite.MapScene
import com.here.sdk.mapviewlite.MapStyle
import com.here.sdk.mapviewlite.MapViewLite
import timber.log.Timber

class MainFragment : Fragment() {

  companion object {
    fun newInstance() = MainFragment()
  }

  private lateinit var viewModel: MainViewModel
  private lateinit var binding: MainFragmentBinding
  private lateinit var mapView:MapViewLite

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = MainFragmentBinding.inflate(inflater, container, false)
    mapView = binding.mapView
    mapView.onCreate(savedInstanceState)
    loadMapScene()
    return binding.root
  }

  private fun loadMapScene() {
    mapView.mapScene.loadScene(MapStyle.NORMAL_DAY
    ) { errorCode ->
      errorCode?.let {
        mapView.camera?.apply {
          target = GeoCoordinates(52.530932, 13.384915)
          zoomLevel = 14.toDouble()
        }
      } ?: Timber.e("onLoadScene failed: ${errorCode.toString()}")
    }
  }
   override fun onPause() {
    super.onPause()
    mapView.onPause()
  }

   override fun onResume() {
    super.onResume()
    mapView.onResume()
  }

   override fun onDestroy() {
    super.onDestroy()
    mapView.onDestroy()
  }

}