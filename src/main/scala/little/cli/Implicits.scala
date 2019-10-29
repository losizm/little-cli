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

/** Provides extension methods to Apache Commons CLI. */
object Implicits {
  /** Converts `OptionGroup` to `Optionable`. */
  implicit val optionGroupToOptionable: OptionGroup => Optionable = Left.apply

  /** Converts `Option` to `Optionable`. */
  implicit val optionToOptionable: Option => Optionable = Right.apply

  /** Adds extends methods to `org.apache.commons.cli.Option`. */
  implicit class OptionType(private val option: Option) extends AnyVal {
    /**
     * Sets number of arguments and returns modified option.
     *
     * @param value number of arguments
     */
    def args(value: Int): Option = {
      option.setArgs(value)
      option
    }

    /**
     * Sets argument name and returns modified option.
     *
     * @param value argument name
     */
    def argName(value: String): Option = {
      option.setArgName(value)
      option
    }

    /**
     * Sets description and returns modified option.
     *
     * @param value description
     */
    def description(value: String): Option = {
      option.setDescription(value)
      option
    }

    /**
     * Sets long name of option and returns modified option.
     *
     * @param value long name of option
     */
    def longOpt(value: String): Option = {
      option.setLongOpt(value)
      option
    }

    /**
     * Sets whether option has an optional argument and returns modified option.
     *
     * @param value specifies whether argument is optional
     */
    def optionalArg(value: Boolean): Option = {
      option.setOptionalArg(value)
      option
    }

    /**
     * Sets whether option is required and returns modified option.
     *
     * @param value specifies whether option is required
     */
    def required(value: Boolean = true): Option = {
      option.setRequired(value)
      option
    }

    /**
     * Sets option type and returns modified option.
     *
     * @param value option type
     */
    def optionType(value: Class[_]): Option = {
      option.setType(value)
      option
    }

    /**
     * Sets value separator and returns modified option.
     *
     * @param value value separator
     */
    def valueSeparator(value: Char): Option = {
      option.setValueSeparator(value)
      option
    }
  }

  /** Adds extends methods to `org.apache.commons.cli.Options`. */
  implicit class OptionsType(private val options: Options) extends AnyVal {
    /**
     * Adds option to options and returns modified options.
     *
     * @param opt option to add
     */
    def +=(opt: Option): Options = options.addOption(opt)

    /**
     * Adds option group to options and returns modified options.
     *
     * @param group option group to add
     */
    def +=(group: OptionGroup): Options = options.addOptionGroup(group)
  }
}
