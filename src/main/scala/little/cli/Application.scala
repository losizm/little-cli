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

import scala.util.Try

/**
 * Bundles up components for providing command line interface.
 *
 * @see [[Cli Cli.application(usage)]], [[Cli Cli.application(usage, options)]]
 */
sealed trait Application {
  /** Gets usage syntax. */
  def usage(): String

  /** Sets usage syntax. */
  def usage(syntax: String): this.type

  /** Gets application options. */
  def options(): Options

  /** Sets application options. */
  def options(opts: Options): this.type

  /** Sets application options. */
  def options(opts: Optionable*): this.type

  /** Gets header displayed at beginning of help. */
  def header(): String

  /** Sets header displayed at beginning of help. */
  def header(text: String): this.type

  /** Gets footer displayed at end of help. */
  def footer(): String

  /** Sets footer displayed at end of help. */
  def footer(text: String): this.type

  /** Gets formatter used for printing help. */
  def formatter(): HelpFormatter

  /** Sets formatter used for printing help. */
  def formatter(fmt: HelpFormatter): this.type

  /** Gets maximum number of characters printed per line in help. */
  def width(): Int

  /** Sets maximum number of characters printed per line in help. */
  def width(max: Int): this.type

  /** Prints help to `Sytem.out`. */
  def printHelp(): Unit

  /**
   * Prints help to supplied output stream.
   *
   * @param out output stream to which help is printed
   */
  def printHelp(out: OutputStream): Unit

  /**
   * Prints help to supplied writer.
   *
   * @param out writer to which help is printed
   */
  def printHelp(out: PrintWriter): Unit

  /**
   * Parses supplied arguments according to application options.
   *
   * @param args arguments
   */
  def parse(args: Array[String]): CommandLine

  /**
   * Parses supplied arguments according to application options.
   *
   * @param args arguments
   * @param stoppable specifies whether to stop at first unrecognized argument
   *   instead of throwing `ParseException`
   */
  def parse(args: Array[String], stoppable: Boolean): CommandLine

  /**
   * Tries to parse supplied arguments according to application options.
   *
   * @param args arguments
   */
  def tryParse(args: Array[String]): Try[CommandLine]

  /**
   * Tries to parse supplied arguments according to application options.
   *
   * @param args arguments
   * @param stoppable specifies whether to stop at first unrecognized argument
   *   instead of throwing `ParseException`
   */
  def tryParse(args: Array[String], stoppable: Boolean): Try[CommandLine]
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
    _header = text
    this
  }

  def footer(text: String): this.type = {
    _footer = text
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

  def printHelp(): Unit = printHelp(System.out)

  def printHelp(out: OutputStream): Unit = printHelp(new PrintWriter(out, true))

  def printHelp(out: PrintWriter): Unit = {
    formatter.printUsage(out, formatter.getWidth, usage)
    if (header != null) formatter.printWrapped(out, formatter.getWidth, header)
    formatter.printOptions(out, formatter.getWidth, options, formatter.getLeftPadding, formatter.getDescPadding)
    if (footer != null) formatter.printWrapped(out, formatter.getWidth, footer)
    out.flush()
  }

  def parse(args: Array[String]): CommandLine =
    parser.parse(options, args)

  def parse(args: Array[String], stoppable: Boolean): CommandLine =
    parser.parse(options, args, stoppable)

  def tryParse(args: Array[String]): Try[CommandLine] =
    Try { new DefaultParser().parse(options, args) }

  def tryParse(args: Array[String], stoppable: Boolean): Try[CommandLine] =
    Try { new DefaultParser().parse(options, args, stoppable) }
}
