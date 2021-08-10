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

package com.greyp.android.demo.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.greyp.android.demo.R
import com.greyp.android.demo.databinding.ViewErrorBinding

/**
Author: Damjan Miloshevski
Created on: 10.8.21
 */

class ErrorView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
  private var binding: ViewErrorBinding
  private val inflater = LayoutInflater.from(context)
  private var errorMessageResource: Int = 0

  init {
    binding = ViewErrorBinding.inflate(inflater, this, true)
    extractArguments(context, attrs)
  }

  private fun extractArguments(context: Context, attrs: AttributeSet) {
    attrs.apply {
      val styledAttributes = context.obtainStyledAttributes(this, R.styleable.ErrorView)
      errorMessageResource = styledAttributes.getResourceId(
        R.styleable.ErrorView_errorMessage,
        R.string.no_items_msg
      )
      binding.tvMessage.text = context.getText(errorMessageResource)
      styledAttributes.recycle()
    }
  }

  fun setErrorMessage(@StringRes errorMessageResource: Int? = null, errorMessage: String? = null) {
    when {
      errorMessageResource != null -> binding.tvMessage.text = context.getText(errorMessageResource)
      errorMessage != null -> binding.tvMessage.text = errorMessage
      else -> throw NullPointerException("ErrorView errorMessage must not be null!")
    }
    invalidate()
    requestLayout()
  }
}