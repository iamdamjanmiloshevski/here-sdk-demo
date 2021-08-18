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

package com.greyp.android.demo.util

import java.util.concurrent.TimeUnit

/**
Author: Damjan Miloshevski
Created on: 8.8.21
 */

object Constants {
  // region INTERVAL CONSTANTS
  /**
   * Should increase these values or make them dynamic via UI to reduce battery usage
   */
  val LOCATION_UPDATES_INTERVAL = TimeUnit.MINUTES.toMillis(1)
  val LOCATION_UPDATES_FASTEST_INTERVAL = TimeUnit.SECONDS.toMillis(5)
  //endregion

  const val DEFAULT_RADIUS = 20000.toDouble() //default radius in meters
  const val DEFAULT_MAX_ITEMS = 30
}