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

import java.io.{ PrintWriter, OutputStream }

import org.apache.commons.cli._


/** Bundles up components for providing command line interface. */
sealed trait Application {
  /** Gets command syntax of usage. */
  def usage(): String

  /** Sets command syntax of usage. */
  def usage(syntax: String): this.type

  /** Gets command options. */
  def options(): Options

  /** Sets command options. */
  def options(opts: Options): this.type

  /** Sets command options. */
  def options(opts: Optionable*): this.type

  /** Gets header text displayed at beginning of help information. */
  def header(): String

  /** Sets header text displayed at beginning of help information. */
  def header(text: String): this.type

  /** Sets footer text displayed at end of help information. */
  def footer(): String

  /** Sets footer text displayed at end of help information. */
  def footer(text: String): this.type

  /** Gets formatter used for printing help information. */
  def formatter(): HelpFormatter

  /** Sets formatter used for printing help information. */
  def formatter(fmt: HelpFormatter): this.type

  /** Gets maximum width of lines in help information. */
  def width(): Int

  /** Sets maximum width lines in help information. */
  def width(num: Int): this.type

  /** Prints help to `Sytem.out`. */
  def printHelp(): Unit

  /** Prints help to supplied output stream. */
  def printHelp(out: OutputStream): Unit

  /** Prints help to supplied writer. */
  def printHelp(out: PrintWriter): Unit

  /**
   * Parses supplied arguments to generate command.
   *
   * @param arg arguments
   */
  def parse(args: Array[String]): CommandLine

  /**
   * Parses supplied arguments to generate command.
   *
   * @param args arguments
   * @param stoppable specifies whether to stop at first unrecognized argument
   *   instead of throwing `ParseException`
   */
  def parse(args: Array[String], stoppable: Boolean): CommandLine
}

private class ApplicationImpl extends Application {
  private var _usage: String = ""
  private var _options: Options = new Options()
  private var _header: String = ""
  private var _footer: String = ""
  private var _formatter: HelpFormatter = new HelpFormatter()

  private lazy val parser = new DefaultParser()

  def usage: String = _usage
  def options: Options = _options
  def header: String = _header
  def footer: String = _footer
  def formatter: HelpFormatter = _formatter
  def width: Int = _formatter.getWidth

  def usage(syntax: String): this.type = {
    if (syntax == null || syntax.isEmpty)
      throw new IllegalArgumentException("Invalid usage: null or empty")
    _usage = syntax
    this
  }

  def options(opts: Options): this.type = {
    _options = opts match {
      case null => new Options()
      case _    => opts
    }
    this
  }

  def options(opts: Optionable*): this.type = {
    _options = opts match {
      case null => new Options()
      case _    => Cli.options(opts : _*)
    }
    this
  }

  def header(text: String): this.type = {
    _header = text match {
      case null => ""
      case _    => text
    }
    this
  }

  def footer(text: String): this.type = {
    _footer = text match {
      case null => ""
      case _    => text
    }
    this
  }

  def formatter(fmt: HelpFormatter): this.type = {
    _formatter = fmt match {
      case null => new HelpFormatter()
      case _    => fmt
    }
    this
  }

  def width(num: Int): this.type = {
    _formatter.setWidth(num.max(40))
    this
  }

  def printHelp(): Unit =
    formatter.printHelp(usage, header, options, footer)

  def printHelp(out: OutputStream): Unit =
    printHelp(new PrintWriter(out, true))

  def printHelp(out: PrintWriter): Unit = {
    formatter.printUsage(out, formatter.getWidth, usage)
    formatter.printWrapped(out, formatter.getWidth, header)
    formatter.printOptions(out, formatter.getWidth, options, formatter.getLeftPadding, formatter.getDescPadding)
    formatter.printWrapped(out, formatter.getWidth, footer)
    out.flush()
  }

  def parse(args: Array[String]): CommandLine =
    parser.parse(options, args)

  def parse(args: Array[String], stoppable: Boolean): CommandLine =
    parser.parse(options, args, stoppable)
}
