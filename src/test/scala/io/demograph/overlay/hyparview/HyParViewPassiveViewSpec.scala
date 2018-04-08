/*
 * Copyright 2017 Merlijn Boogerd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.demograph.overlay.hyparview

import scala.concurrent.duration._

/**
 *
 */
class HyParViewPassiveViewSpec extends HyParViewBaseSpec {

  behavior of "Passive View Maintenance"

  it should "periodically initiate a shuffle" in {

  }

  it should "include peers from both passive and active views (up to configured bounds)" in {

  }

  it should "have the Shuffle continue a random walk (TTL decremented) if TTL > 0 and |activeView| > 1" in {

  }

  it should "handle a Shuffle request if its TTL hits zero (after decrementing)" in {

  }

  it should "handle a Shuffle request if |activeView| <= 1" in {

  }

  it should "integrate the exchangeSet included in a ShuffleReply" in {

  }
}
