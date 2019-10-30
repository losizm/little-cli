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

import java.io.PrintWriter

import org.apache.commons.cli._

/** Provides factory methods and other utilities. */
object Cli {
  /** Creates new parser. */
  def parser(): CommandLineParser = new DefaultParser()

  /** Creates new options. */
  def options(): Options = new Options()

  /**
   * Creates new options with supplied options.
   *
   * @param opts options
   */
  def options(opts: Optionable*): Options =
    opts.foldLeft(new Options()) { (opts, opt) =>
      opt.fold(left => opts addOptionGroup left, right => opts addOption right)
    }

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

  /** Creates new options group with supplied options. */
  def group(opts: Option*): OptionGroup =
    opts.foldLeft(new OptionGroup()) { _ addOption _ }

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
   * @param stoppable specifies whether to stop at first unrecognized argument
   *   instead of throwing `ParseException`
   */
  def parse(opts: Options, args: Array[String], stoppable: Boolean): CommandLine =
    new DefaultParser().parse(opts, args, stoppable)

  /**
   * Prints help to `Sytem.out` for `options` with specified command `usage`.
   *
   * @param usage command usage
   * @param options command options
   */
  def printHelp(usage: String, options: Options): Unit =
    new HelpFormatter().printHelp(usage, options)

  /**
   * Prints help to `out` for `options` with specified command `usage`.
   *
   * @param out output destination
   * @param usage command usage
   * @param options command options
   */
  def printHelp(out: PrintWriter, usage: String, options: Options): Unit = {
    val formatter = new HelpFormatter()
    formatter.printUsage(out, formatter.getWidth, usage)
    formatter.printOptions(out, formatter.getWidth, options, formatter.getLeftPadding, formatter.getDescPadding)
    out.flush()
  }

  /**
   * Prints help to `Sytem.out` for `options` with specified command `usage`.
   *
   * @param usage command usage
   * @param header text to display before options section
   * @param options command options
   * @param footer text to display after options section
   */
  def printHelp(usage: String, header: String, options: Options, footer: String): Unit =
    new HelpFormatter().printHelp(usage, header, options, footer)

  /**
   * Prints help to `out` for `options` with specified command `usage`.
   *
   * @param out output destination
   * @param usage command usage
   * @param header text to display before options section
   * @param options command options
   * @param footer text to display after options section
   */
  def printHelp(out: PrintWriter, usage: String, header: String, options: Options, footer: String): Unit = {
    val formatter = new HelpFormatter()
    formatter.printUsage(out, formatter.getWidth, usage)
    formatter.printWrapped(out, formatter.getWidth, header)
    formatter.printOptions(out, formatter.getWidth, options, formatter.getLeftPadding, formatter.getDescPadding)
    formatter.printWrapped(out, formatter.getWidth, footer)
    out.flush()
  }

  /**
   * Prints help to `Sytem.out` for `options` with specified command `usage`.
   *
   * @param width maximum number of characters to display per line
   * @param usage command usage
   * @param options command options
   */
  def printHelp(width: Int, usage: String, options: Options): Unit =
    new HelpFormatter().printHelp(width, usage, null, options, null)

  /**
   * Prints help to `out` for `options` with specified command `usage`.
   *
   * @param out output destination
   * @param width maximum number of characters to display per line
   * @param usage command usage
   * @param options command options
   */
  def printHelp(out: PrintWriter, width: Int, usage: String, options: Options): Unit = {
    val formatter = new HelpFormatter()
    formatter.printUsage(out, width, usage)
    formatter.printOptions(out, width, options, formatter.getLeftPadding, formatter.getDescPadding)
    out.flush()
  }

  /**
   * Prints help to `Sytem.out` for `options` with specified command `usage`.
   *
   * @param width maximum number of characters to display per line
   * @param usage command usage
   * @param header text to display before options section
   * @param options command options
   * @param footer text to display after options section
   */
  def printHelp(width: Int, usage: String, header: String, options: Options, footer: String): Unit =
    new HelpFormatter().printHelp(width, usage, header, options, footer)

  /**
   * Prints help to `out` for `options` with specified command `usage`.
   *
   * @param out output destination
   * @param width maximum number of characters to display per line
   * @param usage command usage
   * @param header text to display before options section
   * @param options command options
   * @param footer text to display after options section
   */
  def printHelp(out: PrintWriter, width: Int, usage: String, header: String, options: Options, footer: String): Unit = {
    val formatter = new HelpFormatter()
    formatter.printUsage(out, width, usage)
    formatter.printWrapped(out, width, header)
    formatter.printOptions(out, width, options, formatter.getLeftPadding, formatter.getDescPadding)
    formatter.printWrapped(out, width, footer)
    out.flush()
  }
}
