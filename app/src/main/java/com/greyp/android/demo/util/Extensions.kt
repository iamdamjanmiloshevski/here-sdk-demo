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

import android.app.Dialog
import android.content.Context
import androidx.annotation.StringRes
import com.afollestad.materialdialogs.MaterialDialog
import com.greyp.android.demo.R
import com.here.sdk.search.Address
import java.lang.Exception

/**
Author: Damjan Miloshevski
Created on: 9.8.21
 */

fun Address.beautify(): String {
  return "${this.street}, ${this.postalCode} ${this.city}, ${this.country}"
}

fun Context.showSimpleMessageDialog(
  @StringRes messageRes: Int? = null,
  text: String? = null
) {
  MaterialDialog(this).show {
    title(R.string.system_message)
    cancelable(false)
    when {
      messageRes != null -> message(messageRes)
      text != null -> message(text = text)
      else -> throw NullPointerException(
        "Dialog message must not be null!"
      )
    }
    positiveButton(res = android.R.string.ok, click = {
      dismiss()
    })
  }
}