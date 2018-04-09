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

package io.demograph.overlay

import akka.actor.ActorSystem
import akka.testkit.TestProbe
import io.reactors.protocol._
import io.reactors.{ Arrayable, _ }
import org.scalatest.concurrent.Futures

import scala.concurrent.{ Future, Promise }
import scala.util.{ Failure, Success }

trait ReactorOps extends Futures {

  implicit val as: ActorSystem

  implicit class ServerExtOps[T, @specialized(Int, Long, Double) S: Arrayable](val server: Server[T, S]) {
    def ??(t: T)(implicit rs: ReactorSystem): Future[S] = {
      val (c, f) = channelHead[S]
      server ! ((t, c))
      f
    }
  }

  def channelHead[S: Arrayable](implicit rs: ReactorSystem): (Channel[S], Future[S]) = {
    val p = Promise[S]
    val c: Channel[S] = rs.spawnLocal[S] { self =>
      self.main.events.onEvent { t =>
        p.tryComplete(Success(t))
        self.main.seal()
      }
      self.main.events.onExcept {
        case t =>
          p.tryComplete(Failure(t))
          self.main.seal()
      }
    }
    (c, p.future)
  }

  def channelProbe[S: Arrayable](implicit rs: ReactorSystem): (Channel[S], TestProbe) = {
    val probe = TestProbe()
    val c: Channel[S] = rs.spawnLocal[S] { self =>
      self.main.events.onEvent { t =>
        probe.ref ! t
      }
      self.main.events.onExcept {
        case t =>
          probe.ref ! t
          self.main.seal()
      }
    }
    (c, probe)
  }
}
