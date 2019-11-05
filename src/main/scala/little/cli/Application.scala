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

import java.io.{ OutputStream, PrintWriter, StringWriter }

import org.apache.commons.cli._

import scala.util.Try

/**
 * Encapsulates components for providing command line interface.
 *
 * @see [[Cli Cli.application(usage)]], [[Cli Cli.application(usage, options)]]
 */
sealed trait Application {
  /** Gets usage syntax. */
  def usage(): String

  /**
   * Sets usage syntax.
   *
   * @param syntax usage syntax
   */
  def usage(syntax: String): this.type

  /** Gets application options. */
  def options(): Options

  /**
   * Sets application options.
   *
   * @param opts options
   */
  def options(opts: Options): this.type

  /**
   * Sets application options.
   *
   * @param opts options
   */
  def options(opts: Optionable*): this.type

  /**
   * Adds options to existing application options.
   *
   * @param opts options
   */
  def addOptions(opts: Optionable*): this.type

  /**
   * Adds option to existing application options.
   *
   * @param opt option
   */
  def addOption(opt: Option): this.type

  /**
   * Adds option to existing application options.
   *
   * @param opt short option
   * @param description option description
   */
  def addOption(opt: String, description: String): this.type

  /**
   * Adds option to existing application options.
   *
   * @param opt short option
   * @param hasArg specifies whether option takes an argument
   * @param description option description
   */
  def addOption(opt: String, hasArg: Boolean, description: String): this.type

  /**
   * Adds option to existing application options.
   *
   * @param opt short option
   * @param longOpt long option
   * @param hasArg specifies whether option takes an argument
   * @param description option description
   */
  def addOption(opt: String, longOpt: String, hasArg: Boolean, description: String): this.type

  /**
   * Adds option group to existing application options.
   *
   * @param group option group
   */
  def addOptionGroup(group: OptionGroup): this.type

  /**
   * Adds option group to existing application options.
   *
   * @param opts options
   */
  def addOptionGroup(opts: Option*): this.type

  /** Gets header displayed at beginning of help. */
  def header(): String

  /**
   * Sets header displayed at beginning of help.
   *
   * @param text header text
   */
  def header(text: String): this.type

  /** Gets footer displayed at end of help. */
  def footer(): String

  /**
   * Sets footer displayed at end of help.
   *
   * @param text footer text
   */
  def footer(text: String): this.type

  /** Gets formatter used for printing help. */
  def formatter(): HelpFormatter

  /**
   * Sets formatter used for printing help.
   *
   * @param fmt help formatter
   */
  def formatter(fmt: HelpFormatter): this.type

  /** Gets maximum number of characters printed per line in help. */
  def width(): Int

  /**
   * Sets maximum number of characters printed per line in help.
   *
   * @param max number of characters per line
   */
  def width(max: Int): this.type

  /** Gets help. */
  def help(): String

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
   *   instead of failing with `ParseException`
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
      case _    => mergeOptions(new Options(), opts)
    }
    this
  }

  def addOptions(opts: Optionable*): this.type = {
    mergeOptions(_options, opts)
    this
  }

  def addOption(opt: Option): this.type = {
    _options.addOption(opt)
    this
  }

  def addOption(opt: String, description: String): this.type = {
    _options.addOption(opt, description)
    this
  }

  def addOption(opt: String, hasArg: Boolean, description: String): this.type = {
    _options.addOption(opt, hasArg, description)
    this
  }

  def addOption(opt: String, longOpt: String, hasArg: Boolean, description: String): this.type = {
    _options.addOption(opt, longOpt, hasArg, description)
    this
  }

  def addOptionGroup(group: OptionGroup): this.type = {
    _options.addOptionGroup(group)
    this
  }

  def addOptionGroup(opts: Option*): this.type = {
    _options.addOptionGroup(
      opts.foldLeft(new OptionGroup()) { _ addOption _ }
    )
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

  def help(): String = {
    val out = new StringWriter()
    printHelp(new PrintWriter(out, true))
    out.toString
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
    Try { parse(args) }

  def tryParse(args: Array[String], stoppable: Boolean): Try[CommandLine] =
    Try { parse(args, stoppable) }
}
