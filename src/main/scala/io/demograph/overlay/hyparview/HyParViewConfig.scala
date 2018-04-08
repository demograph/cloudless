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

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.NonNegative

import scala.concurrent.duration.FiniteDuration

case class HyParViewConfig(
  maxActiveViewSize: Int Refined NonNegative,
  maxPassiveViewSize: Int Refined NonNegative,
  activeRWL: Int Refined NonNegative,
  passiveRWL: Int Refined NonNegative,
  shuffleRWL: Int Refined NonNegative,
  shuffleActive: Int Refined NonNegative,
  shufflePassive: Int Refined NonNegative,
  shuffleInterval: FiniteDuration) {

  assert(shuffleActive.value >= shufflePassive.value, "shuffleActive should be greater than or equal to shufflePassive")
}