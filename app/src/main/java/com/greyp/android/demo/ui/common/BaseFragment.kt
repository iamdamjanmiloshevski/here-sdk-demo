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


import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import com.greyp.android.demo.domain.common.Status
import com.greyp.android.demo.data.persistence.IPreferences
import com.greyp.android.demo.data.persistence.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
Author: Damjan Miloshevski
Created on: 6.8.21Å
 */
@AndroidEntryPoint
abstract class BaseFragment : Fragment(), IBaseFragmentView {
  protected val viewModel: GreypFragmentViewModel by activityViewModels()
  protected lateinit var navController: NavController
  protected var latitude:Double = 0.toDouble()
  protected var longitude:Double = 0.toDouble()


  @Inject
  lateinit var sharedPreferencesManager: SharedPreferencesManager

}