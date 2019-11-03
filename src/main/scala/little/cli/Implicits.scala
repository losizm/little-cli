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
import java.nio.file.{ Path, Paths }

import org.apache.commons.cli._

/** Provides extension methods to [[https://commons.apache.org/proper/commons-cli/index.html Apache Commons CLI]]. */
object Implicits {
  /** Converts `Option` to `Optionable`. */
  implicit val optionToOptionable: Option => Optionable = Right.apply

  /** Converts `OptionGroup` to `Optionable`. */
  implicit val optionGroupToOptionable: OptionGroup => Optionable = Left.apply

  /** Maps value to `Int`. */
  implicit object IntValueMapper extends ValueMapper[Int] {
    def map(value: String): Int = value.toInt
  }

  /** Maps value to `Long`. */
  implicit object LongValueMapper extends ValueMapper[Long] {
    def map(value: String): Long = value.toLong
  }

  /** Maps value to `File`. */
  implicit object FileValueMapper extends ValueMapper[File] {
    def map(value: String): File = new File(value)
  }

  /** Maps value to `Path`. */
  implicit object PathValueMapper extends ValueMapper[Path] {
    def map(value: String): Path = Paths.get(value)
  }

  /** Adds extension methods to `org.apache.commons.cli.Option`. */
  implicit class OptionType(private val option: Option) extends AnyVal {
    /**
     * Sets number of arguments and returns modified option.
     *
     * @param count number of arguments
     */
    def args(count: Int): Option = {
      option.setArgs(count)
      option
    }

    /**
     * Sets argument name and returns modified option.
     *
     * @param name argument name
     */
    def argName(name: String): Option = {
      option.setArgName(name)
      option
    }

    /**
     * Sets description and returns modified option.
     *
     * @param desc description
     */
    def description(desc: String): Option = {
      option.setDescription(desc)
      option
    }

    /**
     * Sets long name of option and returns modified option.
     *
     * @param name long name of option
     */
    def longOpt(name: String): Option = {
      option.setLongOpt(name)
      option
    }

    /**
     * Sets whether option has an optional argument and returns modified option.
     *
     * @param optional specifies whether argument is optional
     */
    def optionalArg(optional: Boolean): Option = {
      option.setOptionalArg(optional)
      option
    }

    /**
     * Sets whether option is required and returns modified option.
     *
     * @param mandatory specifies whether option is required
     */
    def required(mandatory: Boolean = true): Option = {
      option.setRequired(mandatory)
      option
    }

    /**
     * Sets option type and returns modified option.
     *
     * @param optType option type
     */
    def optionType(optType: Class[_]): Option = {
      option.setType(optType)
      option
    }

    /**
     * Sets value separator and returns modified option.
     *
     * @param sep value separator
     */
    def valueSeparator(sep: Char): Option = {
      option.setValueSeparator(sep)
      option
    }

    /**
     * Maps option value to type T.
     *
     * @param mapper value mapper
     *
     * @throws NoSuchElementException if option value not present
     */
    def mapValue[T](implicit mapper: ValueMapper[T]): T =
      option.getValue match {
        case null  => throw new NoSuchElementException(s"option value not present: ${option.getOpt}")
        case value => mapper.map(value)
      }

    /**
     * Maps option values to Seq[T].
     *
     * @param mapper value mapper
     */
    def mapValues[T](implicit mapper: ValueMapper[T]): Seq[T] =
      option.getValues match {
        case null   => Nil
        case values => values.toSeq.map(mapper.map)
      }
  }

  /** Adds extension methods to `org.apache.commons.cli.OptionGroup`. */
  implicit class OptionGroupType(private val group: OptionGroup) extends AnyVal {
    /**
     * Adds option to group and returns modified group.
     *
     * @param opt option
     */
    def +=(opt: Option): OptionGroup = group.addOption(opt)

    /**
     * Sets whether group is required and returns modified group.
     *
     * @param mandatory specifies whether group is required
     */
    def required(mandatory: Boolean = true): OptionGroup = {
      group.setRequired(mandatory)
      group
    }
  }

  /** Adds extension methods to `org.apache.commons.cli.Options`. */
  implicit class OptionsType(private val options: Options) extends AnyVal {
    /**
     * Adds option and returns modified options.
     *
     * @param opt option
     */
    def +=(opt: Option): Options = options.addOption(opt)

    /**
     * Adds option group and returns modified options.
     *
     * @param group option group
     */
    def +=(group: OptionGroup): Options = options.addOptionGroup(group)

    /**
     * Adds more options and returns modified options.
     *
     * @param opts more options
     */
    def addOptions(opts: Optionable*): Options =
      opts.foldLeft(options) { (os, opt) => opt.fold(os.addOptionGroup, os.addOption) }
  }

  /** Adds extension methods to `org.apache.commons.cli.CommandLine`. */
  implicit class CommandLineType(private val command: CommandLine) extends AnyVal {
    /** Gets argument count. */
    def getArgCount(): Int = command.getArgList.size

    /**
     * Gets argument at specified index.
     *
     * @param index argument index
     */
    def getArg(index: Int): String = command.getArgList.get(index)

    /**
     * Maps argument at specified index to type T.
     *
     * @param index argument index
     * @param mapper value mapper
     */
    def mapArg[T](index: Int)(implicit mapper: ValueMapper[T]): T =
      mapper.map { getArg(index) }

    /**
     * Maps argument at specified index to type T or gets default if argument
     * not present.
     *
     * @param index argument index
     * @param default default value
     * @param mapper value mapper
     */
    def mapArg[T](index: Int, default: => T)(implicit mapper: ValueMapper[T]): T =
      (index >= 0 && index < getArgCount) match {
        case true  => mapper.map { getArg(index) }
        case false => default
      }

    /**
     * Maps arguments to Seq[T].
     *
     * @param mapper value mapper
     */
    def mapArgs[T](implicit mapper: ValueMapper[T]): Seq[T] =
      command.getArgs.toSeq.map(mapper.map)

    /**
     * Maps option value to type T.
     *
     * @param opt option
     * @param mapper value mapper
     *
     * @throws NoSuchElementException if option value not present
     */
    def mapOptionValue[T](opt: String)(implicit mapper: ValueMapper[T]): T =
      command.getOptionValue(opt) match {
        case null  => throw new NoSuchElementException(s"option value not present: $opt")
        case value => mapper.map(value)
      }

    /**
     * Maps option value to type T or gets default if option value not present.
     *
     * @param opt option
     * @param default default value
     * @param mapper value mapper
     */
    def mapOptionValue[T](opt: String, default: => T)(implicit mapper: ValueMapper[T]): T =
      command.getOptionValue(opt) match {
        case null  => default
        case value => mapper.map(value)
      }

    /**
     * Maps option values to Seq[T].
     *
     * @param opt option
     * @param mapper value mapper
     */
    def mapOptionValues[T](opt: String)(implicit mapper: ValueMapper[T]): Seq[T] =
      command.getOptionValues(opt) match {
        case null   => Nil
        case values => values.toSeq.map(mapper.map)
      }

    /**
     * Creates tuple corresponding to supplied options indicating whether each
     * option is set or not.
     *
     * @param opt1 first option
     * @param opt2 second option
     */
    def hasOption(opt1: String, opt2: String): (Boolean, Boolean) =
      (command.hasOption(opt1), command.hasOption(opt2))

    /**
     * Creates tuple corresponding to supplied options indicating whether each
     * option is set or not.
     *
     * @param opt1 first option
     * @param opt2 second option
     * @param opt3 third option
     */
    def hasOption(opt1: String, opt2: String, opt3: String): (Boolean, Boolean, Boolean) =
      (command.hasOption(opt1), command.hasOption(opt2), command.hasOption(opt3))

    /**
     * Creates sequence corresponding to supplied options indicating whether
     * each option is set or not.
     *
     * @param opts options
     */
    def hasOptions(opts: String*): Seq[Boolean] =
      opts.map(command.hasOption)
  }
}
