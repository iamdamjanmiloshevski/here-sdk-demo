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

package com.greyp.android.demo.data.persistence

/**
Author: Damjan Miloshevski
Created on: 9.8.21
 */

interface IPreferences {
  companion object {
    const val KEY_RADIUS = "KEY_RADIUS"
    const val KEY_CATEGORY = "KEY_CATEGORY"
    const val KEY_LATITUDE = "KEY_LATITUDE"
    const val KEY_LONGITUDE = "KEY_LONGITUDE"
    const val PREFS_NAME = "demo_app_prefs"
  }
  fun saveString(key:String,value:String)
  fun saveInt(key:String,value:Int)
  fun saveFloat(key: String,value:Float)
  fun saveBoolean(key:String,value: Boolean)
  fun getString(key: String):String
  fun getInt(key:String):Int
  fun getFloat(key:String):Float
  fun getBoolean(key:String):Boolean
}