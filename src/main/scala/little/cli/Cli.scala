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

import org.apache.commons.cli._

/** Provides factory methods. */
object Cli {
  /** Creates new parser. */
  def parser(): CommandLineParser = new DefaultParser()

  /** Creates new options. */
  def options(): Options = new Options()

  /** Creates new options from supplied options. */
  def options(opts: Option*): Options =
    opts.foldLeft(new Options()) { _ addOption _ }

  /**
   * Creates new option with supplied short option and description.
   *
   * @param opt short option
   * @param description option description
   */
  def option(opt: String, description: String): Option =
    new Option(opt, description)

  /**
   * Creates new option with supplied attributes.
   *
   * @param opt short option
   * @param hasArg specifies whether option takes an argument
   * @param description option description
   */
  def option(opt: String, hasArg: Boolean, description: String): Option =
    new Option(opt, hasArg, description)

  /**
   * Creates new option with supplied attributes.
   *
   * @param opt short option
   * @param longOpt long option
   * @param hasArg specifies whether option takes an argument
   * @param description option description
   */
  def option(opt: String, longOpt: String, hasArg: Boolean, description: String): Option =
    new Option(opt, longOpt, hasArg, description)

  /** Creates new option group. */
  def group(): OptionGroup = new OptionGroup()

  /** Creates new option builder. */
  def builder(): Option.Builder = Option.builder()

  /**
   * Creates new option builder using supplied short option.
   *
   * @param opt short option
   */
  def builder(opt: String): Option.Builder = Option.builder(opt)

  /** Creates new help formatter. */
  def formatter(): HelpFormatter = new HelpFormatter()

  /**
   * Parses arguments according to specified options.
   *
   * @param opts options
   * @param args arguments
   */
  def parse(opts: Options, args: Array[String]): CommandLine =
    new DefaultParser().parse(opts, args)

  /**
   * Parses arguments according to specified options.
   *
   * @param opts options
   * @param args arguments
   * @param stoppable if `true`, parsing stops at an unrecognized argument
   *  and the remaining arguments are added to `CommandLine`'s argument list; if
   *  `false`, a `ParseException` is thrown after encountering an unrecognized
   *  argument.
   */
  def parse(opts: Options, args: Array[String], stoppable: Boolean): CommandLine =
    new DefaultParser().parse(opts, args, stoppable)

  /**
   * Prints help to `Sytem.out` for `options` with specified command line
   * `syntax`.
   *
   * @param syntax command line syntax
   * @param options command options
   */
  def printHelp(syntax: String, options: Options): Unit =
    new HelpFormatter().printHelp(syntax, options)

  /**
   * Prints help to `Sytem.out` for `options` with specified command line
   * `syntax`.
   *
   * @param syntax command line syntax
   * @param header text to display before options section
   * @param options command options
   * @param footer text to display after options section
   */
  def printHelp(syntax: String, header: String, options: Options, footer: String): Unit =
    new HelpFormatter().printHelp(syntax, header, options, footer)

  /**
   * Prints help to `Sytem.out` for `options` with specified command line
   * `syntax`.
   *
   * @param width maximum number of characters to display per line
   * @param syntax command line syntax
   * @param options command options
   */
  def printHelp(width: Int, syntax: String, options: Options): Unit =
    new HelpFormatter().printHelp(width, syntax, null, options, null)

  /**
   * Prints help to `Sytem.out` for `options` with specified command line
   * `syntax`.
   *
   * @param width maximum number of characters to display per line
   * @param syntax command line syntax
   * @param header text to display before options section
   * @param options command options
   * @param footer text to display after options section
   */
  def printHelp(width: Int, syntax: String, header: String, options: Options, footer: String): Unit =
    new HelpFormatter().printHelp(width, syntax, header, options, footer)
}
