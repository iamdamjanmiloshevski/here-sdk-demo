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

package com.greyp.android.demo.persistence

import android.content.Context
import android.content.SharedPreferences

/**
Author: Damjan Miloshevski
Created on: 9.8.21
 */

class SharedPreferencesManager(context: Context) : IPreferences {
  private var sharedPreferences: SharedPreferences =
    context.getSharedPreferences("demo_app_prefs", Context.MODE_PRIVATE)
  private var editor: SharedPreferences.Editor = sharedPreferences.edit()

  override fun saveString(key: String, value: String) {
    editor.apply {
      putString(key, value)
      apply()
    }
  }

  override fun saveInt(key: String, value: Int) {
    editor.apply {
      putInt(key, value)
      apply()
    }
  }

  override fun saveFloat(key: String, value: Float) {
    editor.apply {
      putFloat(key, value)
      apply()
    }
  }


  override fun saveBoolean(key: String, value: Boolean) {
    editor.apply {
      putBoolean(key, value)
      apply()
    }
  }

  override fun getString(key: String): String {
    return sharedPreferences.getString(key, "") as String
  }

  override fun getInt(key: String): Int {
    return sharedPreferences.getInt(key, 0)
  }

  override fun getFloat(key: String): Float {
    return sharedPreferences.getFloat(key, 0f)
  }

  override fun getBoolean(key: String): Boolean {
    return sharedPreferences.getBoolean(key, true)
  }

  companion object {
    private var INSTANCE: SharedPreferencesManager? = null
    fun getInstance(context: Context): SharedPreferencesManager {
      if (INSTANCE == null) {
        synchronized(SharedPreferencesManager::class) {
          if (INSTANCE == null) {
            INSTANCE = SharedPreferencesManager(context)
          }
        }
      }
      return INSTANCE as SharedPreferencesManager
    }
  }
}