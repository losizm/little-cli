
/*
 * Copyright 2019 Carlos Conyers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package little.cli

import java.io.File
import org.apache.commons.cli.CommandLine
import scala.reflect.ClassTag

import Cli._
import Implicits._

class ValueMapperSpec extends org.scalatest.FlatSpec {
  private case class User(id: Int, name: String)

  private implicit val userValueMapper: ValueMapper[User] =
    _.split(":") match {
      case Array(id, name) => User(id.toInt, name)
    }

  it should "map option and argument values" in {
    val root = User(0, "root")
    val guest = User(1, "guest")

    val app = application("test [ options... ] args...")
      .options(
        option("n", "Number option" ).args(1),
        option("N", "Number options").args(2),
        option("f", "File option"   ).args(1),
        option("F", "Files option"  ).args(2),
        option("u", "User option"   ).args(1),
        option("U", "User options"  ).args(2)
      )

    assertMapValues(app.parse(split("-n 1 1"))    , "n", 1)
    assertMapValues(app.parse(split("-N 1 2 1 2")), "N", 1, 2)
    assertMapValues(app.parse(split("-n 1 1"))    , "n", 1L)
    assertMapValues(app.parse(split("-N 1 2 1 2")), "N", 1L, 2L)
    assertMapValues(app.parse(split("-f a a"))    , "f", file("a"))
    assertMapValues(app.parse(split("-F a b a b")), "F", file("a"), file("b"))
    assertMapValues(app.parse(split("-u 0:root 0:root")), "u", root)
    assertMapValues(app.parse(split("-U 0:root 1:guest 0:root 1:guest")), "U", root, guest)
  }

  it should "map empty option and argument values" in {
    val app = application("test [ options... ] args...")
      .options(
        option("n", "Number option" ).args(1),
        option("N", "Number options").args(2),
        option("f", "File option"   ).args(1),
        option("F", "Files option"  ).args(2),
        option("u", "User option"   ).args(1),
        option("U", "User options"  ).args(2),
        option("x", "Test opiton"   ).args(0)
      )

    val cmd = app.parse(Array("-x"))

    assertThrows[NullPointerException] { cmd.mapOptionValue[Int]("n") }
    assertThrows[NullPointerException] { cmd.mapOptionValue[Long]("n") }
    assertThrows[NullPointerException] { cmd.mapOptionValue[File]("f") }
    assertThrows[NullPointerException] { cmd.mapOptionValue[User]("u") }

    assertThrows[NullPointerException] { cmd.mapOptionValues[Int]("n") }
    assertThrows[NullPointerException] { cmd.mapOptionValues[Long]("n") }
    assertThrows[NullPointerException] { cmd.mapOptionValues[File]("f") }
    assertThrows[NullPointerException] { cmd.mapOptionValues[User]("U") }

    assertThrows[NullPointerException] { cmd.mapOptionValue[Int]("x") }
    assertThrows[NullPointerException] { cmd.mapOptionValue[Long]("x") }
    assertThrows[NullPointerException] { cmd.mapOptionValue[File]("x") }
    assertThrows[NullPointerException] { cmd.mapOptionValue[User]("x") }

    assertThrows[NullPointerException] { cmd.mapOptionValues[Int]("x") }
    assertThrows[NullPointerException] { cmd.mapOptionValues[Long]("x") }
    assertThrows[NullPointerException] { cmd.mapOptionValues[File]("x") }
    assertThrows[NullPointerException] { cmd.mapOptionValues[User]("x") }

    assertThrows[NullPointerException] { cmd.getOptions.foreach(_.mapValue[Int]) }
    assertThrows[NullPointerException] { cmd.getOptions.foreach(_.mapValue[Long]) }
    assertThrows[NullPointerException] { cmd.getOptions.foreach(_.mapValue[File]) }
    assertThrows[NullPointerException] { cmd.getOptions.foreach(_.mapValue[User]) }

    assertThrows[NullPointerException] { cmd.getOptions.foreach(_.mapValues[Int]) }
    assertThrows[NullPointerException] { cmd.getOptions.foreach(_.mapValues[Long]) }
    assertThrows[NullPointerException] { cmd.getOptions.foreach(_.mapValues[File]) }
    assertThrows[NullPointerException] { cmd.getOptions.foreach(_.mapValues[User]) }

    assert { cmd.mapArgs[Int].toSeq == Nil }
    assert { cmd.mapArgs[Long].toSeq == Nil }
    assert { cmd.mapArgs[File].toSeq == Nil }
    assert { cmd.mapArgs[User].toSeq == Nil }
  }

  private def split(cmdline: String): Array[String] = cmdline.split(" ")

  private def file(name: String) = new File(name)

  private def assertMapValues[T](cmd: CommandLine, opt: String, values: T*)(implicit mapper: ValueMapper[T], tag: ClassTag[T]): Unit = {
    assert { cmd.hasOption(opt) }
    assert { cmd.mapOptionValue[T](opt) == values.head }
    assert { cmd.mapOptionValues[T](opt).toSeq == values }

    assert {
      cmd.getOptions
        .find { _.getOpt == opt }
        .get
        .mapValue[T] == values.head
    }

    assert {
      cmd.getOptions
        .find { _.getOpt == opt }
        .get
        .mapValues[T].toSeq == values
    }

    values.zipWithIndex.foreach {
      case (value, index) => assert { cmd.mapArg[T](index) == value }
    }

    assert { cmd.mapArgs[T].toSeq == values }
  }
}
