/*
 * Copyright 2021 Carlos Conyers
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
import java.nio.file.{ Path, Paths }

import org.apache.commons.cli.CommandLine

import scala.language.implicitConversions

import Cli.*

class ValueMapperSpec extends org.scalatest.flatspec.AnyFlatSpec:
  private case class User(id: Int, name: String)

  private given ValueMapper[User] =
    _.split(":") match
      case Array(id, name) => User(id.toInt, name)

  it should "map option values and arguments" in {
    val root = User(0, "root")
    val guest = User(1, "guest")

    val app = application("test [ options... ] args...",
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
    assertMapValues(app.parse(split("-f a a"))    , "f", path("a"))
    assertMapValues(app.parse(split("-F a b a b")), "F", path("a"), path("b"))
    assertMapValues(app.parse(split("-u 0:root 0:root")), "u", root)
    assertMapValues(app.parse(split("-U 0:root 1:guest 0:root 1:guest")), "U", root, guest)
  }

  it should "map empty option values and arguments" in {
    val app = application("test [ options... ] args...",
      option("n", "Number option" ).args(1),
      option("N", "Number options").args(2),
      option("f", "File option"   ).args(1),
      option("F", "Files option"  ).args(2),
      option("u", "User option"   ).args(1),
      option("U", "User options"  ).args(2),
      option("x", "Test opiton"   ).args(0)
    )

    val cmd = app.parse(Array("-x"))

    assertThrows[NoSuchElementException] { cmd.mapOptionValue[Int]("n") }
    assertThrows[NoSuchElementException] { cmd.mapOptionValue[Long]("n") }
    assertThrows[NoSuchElementException] { cmd.mapOptionValue[File]("f") }
    assertThrows[NoSuchElementException] { cmd.mapOptionValue[Path]("f") }
    assertThrows[NoSuchElementException] { cmd.mapOptionValue[User]("u") }

    assert { cmd.mapOptionValue("n", 10) == 10 }
    assert { cmd.mapOptionValue("n", 10L) == 10L }
    assert { cmd.mapOptionValue("f", file(".")) == file(".") }
    assert { cmd.mapOptionValue("u", User(1, "nobody")) == User(1, "nobody") }

    assert { cmd.mapOptionValues[Int]("n").isEmpty }
    assert { cmd.mapOptionValues[Long]("n").isEmpty }
    assert { cmd.mapOptionValues[File]("f").isEmpty }
    assert { cmd.mapOptionValues[Path]("f").isEmpty }
    assert { cmd.mapOptionValues[User]("U").isEmpty }

    assertThrows[NoSuchElementException] { cmd.mapOptionValue[Int]("x") }
    assertThrows[NoSuchElementException] { cmd.mapOptionValue[Long]("x") }
    assertThrows[NoSuchElementException] { cmd.mapOptionValue[File]("x") }
    assertThrows[NoSuchElementException] { cmd.mapOptionValue[Path]("x") }
    assertThrows[NoSuchElementException] { cmd.mapOptionValue[User]("x") }

    assert { cmd.mapOptionValues[Int]("x").isEmpty }
    assert { cmd.mapOptionValues[Long]("x").isEmpty }
    assert { cmd.mapOptionValues[File]("x").isEmpty }
    assert { cmd.mapOptionValues[Path]("x").isEmpty }
    assert { cmd.mapOptionValues[User]("x").isEmpty }

    assertThrows[NoSuchElementException] { cmd.getOptions.foreach(_.mapValue[Int]) }
    assertThrows[NoSuchElementException] { cmd.getOptions.foreach(_.mapValue[Long]) }
    assertThrows[NoSuchElementException] { cmd.getOptions.foreach(_.mapValue[File]) }
    assertThrows[NoSuchElementException] { cmd.getOptions.foreach(_.mapValue[Path]) }
    assertThrows[NoSuchElementException] { cmd.getOptions.foreach(_.mapValue[User]) }

    assert { cmd.getOptions.forall(_.mapValues[Int].isEmpty) }
    assert { cmd.getOptions.forall(_.mapValues[Long].isEmpty) }
    assert { cmd.getOptions.forall(_.mapValues[File].isEmpty) }
    assert { cmd.getOptions.forall(_.mapValues[Path].isEmpty) }
    assert { cmd.getOptions.forall(_.mapValues[User].isEmpty) }

    assert { cmd.mapArg[Int](0, 10) == 10 }
    assert { cmd.mapArg[Long](0, 10L) == 10L }
    assert { cmd.mapArg[File](0, file(".")) == file(".") }
    assert { cmd.mapArg[Path](0, path(".")) == path(".") }
    assert { cmd.mapArg[User](0, User(1, "nobody")) == User(1, "nobody") }

    assert { cmd.mapArgs[Int] == Nil }
    assert { cmd.mapArgs[Long] == Nil }
    assert { cmd.mapArgs[File] == Nil }
    assert { cmd.mapArgs[Path] == Nil }
    assert { cmd.mapArgs[User] == Nil }
  }

  private def split(cmdline: String): Array[String] = cmdline.split(" ")

  private def file(name: String) = File(name)

  private def path(name: String) = Paths.get(name)

  private def assertMapValues[T](cmd: CommandLine, opt: String, values: T*)(implicit mapper: ValueMapper[T]): Unit =
    assert { cmd.hasOption(opt) }
    assert { cmd.mapOptionValue[T](opt) == values.head }
    assert { cmd.mapOptionValues[T](opt) == values }

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
        .mapValues[T] == values
    }

    values.zipWithIndex.foreach {
      case (value, index) => assert { cmd.mapArg[T](index) == value }
    }

    assert { cmd.mapArgs[T] == values }
