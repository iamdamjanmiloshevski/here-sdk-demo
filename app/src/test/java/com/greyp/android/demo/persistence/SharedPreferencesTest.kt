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
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import com.greyp.android.demo.BooleanToStringException
import junit.framework.Assert.assertEquals
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.lang.Exception

/**
Author: Damjan Miloshevski
Created on: 18.8.21
 */

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.Q])
class SharedPreferencesTest {
  private lateinit var context:Context
  private lateinit var  sharedPreferences:SharedPreferencesManager

  @Before
  fun setUp() {
    context = InstrumentationRegistry.getInstrumentation().context
    sharedPreferences = SharedPreferencesManager.getInstance(context)
  }

  @Test(expected = Exception::class)
  fun saveString_isDigitsAndChars_throwsException() {
    sharedPreferences.saveString(IPreferences.KEY_CATEGORY,"exampl3WithD1gits")
    Assert.fail("Value must be chars only")
  }

  @Test(expected = Exception::class)
  fun saveString_isDigitsAndSpecialChars_throwsException() {
    sharedPreferences.saveString(IPreferences.KEY_CATEGORY,"#exampl3WithD1gits!")
    Assert.fail("Value must be chars only")
  }

  @Test
  fun saveString_isCharString_returnTrue() {
    sharedPreferences.saveString(IPreferences.KEY_CATEGORY,"example")
    assertEquals("example",sharedPreferences.getString(IPreferences.KEY_CATEGORY))
  }

  @Test(expected = BooleanToStringException::class)
  fun saveString_isBooleanStrict_throwsException() {
    sharedPreferences.saveString(IPreferences.KEY_CATEGORY,"true")
   Assert.fail("Value cannot be boolean")
  }

  @Test(expected = BooleanToStringException::class)
  fun saveString_isBooleanIgnoreCase_throwsException() {
    sharedPreferences.saveString(IPreferences.KEY_CATEGORY,"tRue")
    Assert.fail("Value cannot be boolean")
  }
}