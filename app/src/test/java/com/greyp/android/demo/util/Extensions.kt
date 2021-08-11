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

import androidx.navigation.NavController
import com.greyp.android.demo.data.Address

/**
Author: Damjan Miloshevski
Created on: 10.8.21
 */

fun Address.beautify():String{
  return "${this.street}, ${this.postalCode} ${this.city}, ${this.country}"
}
fun List<String>.createMissingPermissionsMessage(): String {
  val stringBuilder = StringBuilder()
  stringBuilder.append("Our app requires the following permissions:").append("\n\n")
  this.forEach {
    stringBuilder.append(it).append("\n")
  }
  stringBuilder.append("\n").append("Please grant these permissions to proceed.")
  return stringBuilder.toString()
}