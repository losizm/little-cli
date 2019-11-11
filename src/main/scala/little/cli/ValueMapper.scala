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

/**
 * Provides utility for mapping option values and arguments.
 *
 * === How It Works ===
 *
 * Add implicit mappers to scope to enable maps to specific value types.
 *
 * {{{
 * import little.cli.Cli.{ application, option }
 * import little.cli.Implicits._
 * import little.cli.ValueMapper
 *
 * case class KeepAlive(idleTimeout: Int, maxRequests: Int)
 *
 * // Define how to map value to KeepAlive
 * implicit val keepAliveMapper: ValueMapper[KeepAlive] =
 *   _.split(":") match {
 *     case Array(timeout, max) => KeepAlive(timeout.toInt, max.toInt)
 *   }
 *
 * val app = application("start-server [ options ... ] port",
 *   option("d", "directory", true, "Location of public files directory"),
 *   option("k", "keep-alive", true, "Allow persistent connections").argName("timeout:max")
 * )
 *
 * val cmd = app.parse("-d ./public_html --keep-alive 5:10 8080".split("\\s+"))
 *
 * // Map keep-alive option
 * val keepAlive = cmd.mapOptionValue[KeepAlive]("keep-alive")
 *
 * // Map directory option via FileValueMapper in Implicits
 * val directory = cmd.mapOptionValue[java.io.File]("directory")
 *
 * // Map port argument via IntValueMapper in Implicits
 * val port = cmd.mapArg[Int](0)
 * }}}
 *
 * @see [[Implicits.OptionType OptionType.mapValue()]],
 *  [[Implicits.CommandLineType CommanLineType.mapOptionValue()]],
 *  [[Implicits.CommandLineType CommanLineType.mapArg()]]
 */
trait ValueMapper[T] {
  /**
   * Maps value to type T.
   *
   * @param value input value
   */
  def map(value: String): T
}
