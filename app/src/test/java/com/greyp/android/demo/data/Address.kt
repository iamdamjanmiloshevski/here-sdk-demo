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

package com.greyp.android.demo.data

/**
Author: Damjan Miloshevski
Created on: 10.8.21
 */

/**
 * Utility class used for testing purposes only because the original one is obfuscated and final
 * and cannot be mocked
 *
 * @property city
 * @property countryCode
 * @property country
 * @property district
 * @property subdistrict
 * @property houseNumOrName
 * @property postalCode
 * @property stateName
 * @property state
 * @property countyName
 * @property county
 * @property streetName
 * @property street
 * @property block
 * @property subBlock
 * @property addressText
 */
 open class Address(
  val city: String? = null,
  val countryCode: String? = null,
  val country: String? = null,
  val district: String? = null,
  val subdistrict: String? = null,
  val houseNumOrName: String? = null,
  val postalCode: String? = null,
  val stateName: String? = null,
  val state: String? = null,
  val countyName: String? = null,
  val county: String? = null,
  val streetName: String? = null,
  val street: String? = null,
  val block: String? = null,
  val subBlock: String? = null,
  val addressText: String? = null,
)
