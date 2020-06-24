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

package io.demograph.remoting

import org.scalatest.{ FlatSpec, Matchers }
import scalapb.{ GeneratedMessage, GeneratedMessageCompanion, Message }
import test.SearchRequest

class ProtobufExperiment extends FlatSpec with Matchers {

  type PBMessage[A] = GeneratedMessage with Message[A]
  type PBSerializer[A <: PBMessage[A]] = GeneratedMessageCompanion[A]

  trait Serializable[A] {
    type B <: GeneratedMessage with Message[B]
    val gmc: GeneratedMessageCompanion[B]
    def toB(a: A): B
    def toA(b: B): A
  }
  object Serializable {
    def apply[A: Serializable]: Serializable[A] = implicitly[Serializable[A]]
    def fromProtobuf[A, B1 <: GeneratedMessage with Message[B1]: PBSerializer](f: A => B1)(g: B1 => A): Serializable[A] = {
      new Serializable[A] {
        override type B = B1
        override val gmc: GeneratedMessageCompanion[B1] = implicitly[GeneratedMessageCompanion[B1]]
        override def toB(a: A): B = f(a)
        override def toA(b: B): A = g(b)
      }
    }
  }

  it should "work using typeclasses" in {
    def toBinary[A <: PBMessage[A]: PBSerializer](a: A): Array[Byte] =
      implicitly[GeneratedMessageCompanion[A]].toByteArray(a)

    def fromBinary[A <: PBMessage[A]: PBSerializer](bytes: Array[Byte]): A =
      implicitly[GeneratedMessageCompanion[A]].parseFrom(bytes)

    val sr = SearchRequest("query", pageNumber = 1, resultPerPage = 50)
    fromBinary[SearchRequest](toBinary(sr)) shouldBe sr
  }

  it should "work using only a typeclass" in {
    case class SearchRequestModel(searchUrl: String, page: Int, elementCount: Int)

    implicit val pba: Serializable[SearchRequestModel] = Serializable.fromProtobuf[SearchRequestModel, SearchRequest](a =>
      SearchRequest(a.searchUrl, a.page, a.elementCount))(b => SearchRequestModel(b.query, b.pageNumber, b.resultPerPage))

    def toBinary[A: Serializable](a: A): Array[Byte] =
      Serializable[A].toB(a).toByteArray

    def fromBinary[A: Serializable](bytes: Array[Byte]): A = {
      val deserializer = Serializable[A]
      deserializer.toA(deserializer.gmc.parseFrom(bytes))
    }

    val sr = SearchRequestModel("query", 1, 50)
    fromBinary[SearchRequestModel](toBinary(sr)) shouldBe sr
  }

}
