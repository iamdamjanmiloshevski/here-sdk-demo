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

import android.Manifest
import android.os.Build
import com.greyp.android.demo.data.Address

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Author: Damjan Miloshevski
 * Created on: 9.8.21
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.Q])
class ExtensionsKtTest {

  @Test
  fun beautifyAddress_fromAddressObject_isParsedCorrectly() {
    val addressMock = Address(
      street = "Bulevar Partizanski odredi",
      postalCode = "1114",
      city = "Skopje",
      country = "North Macedonia"
    )
    val result = "Bulevar Partizanski odredi, 1114 Skopje, North Macedonia"
    assertEquals(result, addressMock.beautify())
  }

  /**
   * The logic of creating the string is being tested here, the resources will load for sure,
   * Android SDK has it's own tests to test resources retrieval therefore that part is not
   * required here hence the difference of this method with the one in the production code
   *
   */
  @Test
  fun createMissingPermissionsMessage_returnsCorrectString() {
    val mockPermissions = listOf(
      "android.permission.ACCESS_COARSE_LOCATION",
      "android.permission.ACCESS_FINE_LOCATION"
    )
    val expectedMessage =
      "Our app requires the following permissions:\n\nandroid.permission.ACCESS_COARSE_LOCATION\nandroid.permission.ACCESS_FINE_LOCATION\n\nPlease grant these permissions to proceed."
    assertEquals(expectedMessage, mockPermissions.createMissingPermissionsMessage())
  }
}