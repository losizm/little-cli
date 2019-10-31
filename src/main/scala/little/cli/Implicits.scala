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

/** Provides extension methods to [[https://commons.apache.org/proper/commons-cli/index.html Apache Commons CLI]]. */
object Implicits {
  /** Converts `OptionGroup` to `Optionable`. */
  implicit val optionGroupToOptionable: OptionGroup => Optionable = Left.apply

  /** Converts `Option` to `Optionable`. */
  implicit val optionToOptionable: Option => Optionable = Right.apply

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
     * @param value specifies whether group is required
     */
    def required(value: Boolean = true): OptionGroup = {
      group.setRequired(value)
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
    def getArgCount(): Int = command.getArgs.size

    /**
     * Gets argument at specified index.
     *
     * @param index argument index
     */
    def getArg(index: Int): String = command.getArgs().apply(index)
  }
}
