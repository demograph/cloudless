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

/**
 *
 */
class HyParViewActiveViewSpec extends HyParViewBaseSpec {

  behavior of "Active View Management"

  it should "promote a passive node to active with priority if the last active one fails" in {

  }

  it should "promote a passive node to active without priority if an active one fails" in {

  }

  it should "always accept prioritized neighbour requests" in {

  }

  it should "accept non-prioritized neighbour requests when its active view is not full" in {

  }

  it should "reject neighbour requests if the active view is full and no priority is given to the request" in {

  }

  it should "a rejected neighbour request should result in demotion of active to passive node, and a re-attempt to promote _another_ node" in {

  }
}
