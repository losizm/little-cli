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

import org.apache.commons.cli.*

import scala.util.Try

/**
 * Provides factory methods and other utilities.
 *
 * {{{
 * import little.cli.{ *, given }
 * import Cli.{ application, option }
 *
 * // Create application with supplied usage and options
 * val app = application("grep [ options ... ] <pattern> [ <fileName> ... ]",
 *   option("i", "ignore-case", false, "Perform case insensitive matching"),
 *   option("l", "files-with-matches", false, "Print file name only"),
 *   option("m", "max-count", true, "Stop reading file after 'num' matches").argName("num"),
 *   option("n", "line-number", false, "Include line number of match"),
 *   option("r", "recursive", false, "Recursively search subdirectories"),
 *   option("x", "exclude", true, "Exclude filename pattern from search").argName("pattern")
 * )
 *
 * val args = Array("-ilr", "--exclude", "*.swp", "exception", "src/main/scala")
 *
 * // Parse arguments
 * val cmd = app.parse(args)
 *
 * cmd.hasOption("help") match
 *   case true  =>
 *     // Print help to System.out
 *     app.printHelp()
 *   case false =>
 *     // Get command arguments and pretend to do something
 *     val pattern  = cmd.getArg(0)
 *     val fileName = cmd.getArg(1)
 *     println(s"Searching for files with '\$pattern' in \$fileName directory...")
 * }}}
 */
object Cli:
  /**
   * Creates Application with supplied usage.
   *
   * @param usage usage syntax
   */
  def application(usage: String): Application =
    ApplicationImpl().usage(usage)

  /**
   * Creates Application with supplied usage and options.
   *
   * @param usage usage syntax
   * @param options application options
   */
  def application(usage: String, options: Options): Application =
    ApplicationImpl().usage(usage).options(options)

  /**
   * Creates Application with supplied usage and options.
   *
   * @param usage usage syntax
   * @param options application options
   */
  def application(usage: String, options: Optionable*): Application =
    ApplicationImpl().usage(usage).addOptions(options : _*)

  /**
   * Creates new options with supplied options.
   *
   * @param opts options
   */
  def options(opts: Optionable*): Options =
    mergeOptions(Options(), opts)

  /**
   * Creates new option with supplied short option and description.
   *
   * @param opt short option
   * @param description option description
   */
  def option(opt: String, description: String): Option =
    Option(opt, description)

  /**
   * Creates new option with supplied attributes.
   *
   * @param opt short option
   * @param hasArg specifies whether option takes an argument
   * @param description option description
   */
  def option(opt: String, hasArg: Boolean, description: String): Option =
    Option(opt, hasArg, description)

  /**
   * Creates new option with supplied attributes.
   *
   * @param opt short option
   * @param longOpt long option
   * @param hasArg specifies whether option takes an argument
   * @param description option description
   */
  def option(opt: String, longOpt: String, hasArg: Boolean, description: String): Option =
    Option(opt, longOpt, hasArg, description)

  /**
   * Creates new options group with supplied options.
   *
   * @param opts options
   */
  def group(opts: Option*): OptionGroup =
    opts.foldLeft(OptionGroup())(_ addOption _)

  /**
   * Parses arguments according to specified options.
   *
   * @param opts options
   * @param args arguments
   */
  def parse(opts: Options, args: Array[String]): CommandLine =
    DefaultParser().parse(opts, args)

  /**
   * Parses arguments according to specified options.
   *
   * @param opts options
   * @param args arguments
   * @param stoppable specifies whether to stop at first unrecognized argument
   *   instead of throwing `ParseException`
   */
  def parse(opts: Options, args: Array[String], stoppable: Boolean): CommandLine =
    DefaultParser().parse(opts, args, stoppable)

  /**
   * Tries to parse arguments according to specified options.
   *
   * @param opts options
   * @param args arguments
   */
  def tryParse(opts: Options, args: Array[String]): Try[CommandLine] =
    Try(parse(opts, args))

  /**
   * Tries to parse arguments according to specified options.
   *
   * @param opts options
   * @param args arguments
   * @param stoppable specifies whether to stop at first unrecognized argument
   *   instead of failing with `ParseException`
   */
  def tryParse(opts: Options, args: Array[String], stoppable: Boolean): Try[CommandLine] =
    Try(parse(opts, args, stoppable))

  /**
   * Prints help to `Sytem.out` for `options` with specified `usage` syntax.
   *
   * @param usage usage syntax
   * @param options command options
   */
  def printHelp(usage: String, options: Options): Unit =
    application(usage, options).printHelp()

  /**
   * Prints help to `Sytem.out` for `options` with specified `usage` syntax.
   *
   * @param usage usage syntax
   * @param header text to print before options section
   * @param options command options
   * @param footer text to print after options section
   */
  def printHelp(usage: String, header: String, options: Options, footer: String): Unit =
    application(usage, options).header(header).footer(footer).printHelp()

  /**
   * Prints help to `Sytem.out` for `options` with specified `usage` syntax.
   *
   * @param width maximum number of characters to print per line
   * @param usage usage syntax
   * @param options command options
   */
  def printHelp(width: Int, usage: String, options: Options): Unit =
    application(usage, options).width(width).printHelp()

  /**
   * Prints help to `Sytem.out` for `options` with specified `usage` syntax.
   *
   * @param width maximum number of characters to print per line
   * @param usage usage syntax
   * @param header text to print before options section
   * @param options command options
   * @param footer text to print after options section
   */
  def printHelp(width: Int, usage: String, header: String, options: Options, footer: String): Unit =
    application(usage, options).width(width).header(header).footer(footer).printHelp()
