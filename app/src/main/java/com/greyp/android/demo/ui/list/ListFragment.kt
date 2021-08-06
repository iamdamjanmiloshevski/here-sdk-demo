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
import com.greyp.android.demo.common.Destination
import com.greyp.android.demo.databinding.FragmentListBinding
import com.greyp.android.demo.ui.common.BaseFragment
import com.greyp.android.demo.ui.map.MapFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

/**
Author: Damjan Miloshevski
Created on: 6.8.21
 */

@AndroidEntryPoint
class ListFragment:BaseFragment() {
  private lateinit var binding:FragmentListBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    initBinding(inflater, container, false)
    initUI()
    return binding.root
  }

  override fun observeData() {
    viewModel.observeNavigation().observe(viewLifecycleOwner,{destination ->
      if(destination is Destination.Map) {
        val action =   ListFragmentDirections.actionListFragmentToMapFragment()
        findNavController().navigate(action)
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
    binding = FragmentListBinding.inflate(inflater,container,attachToParent)
  }
}