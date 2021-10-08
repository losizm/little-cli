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

/** Maps value to `Int`. */
given intValueMapper: ValueMapper[Int] with
  def map(value: String): Int = value.toInt

/** Maps value to `Long`. */
given longValueMapper: ValueMapper[Long] with
  def map(value: String): Long = value.toLong

/** Maps value to `File`. */
given fileValueMapper: ValueMapper[File] with
  def map(value: String): File = File(value)

/** Maps value to `Path`. */
given pathValueMapper: ValueMapper[Path] with
  def map(value: String): Path = Paths.get(value)
