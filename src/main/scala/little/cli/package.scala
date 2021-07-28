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
package little

/**
 * The Scala library that provides extension methods to
 * [[https://commons.apache.org/proper/commons-cli/index.html Apache Commons CLI]].
 *
 * ### How It Works
 *
 * Here's an example showing the library in action.
 *
 * {{{
 * import little.cli.Cli.{ application, option }
 * import little.cli.Implicits.{ *, given }
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
package cli

import org.apache.commons.cli.*

/** `Option` and `OptionGroup` can added to `Options`. */
type Optionable = Option | OptionGroup

private[cli] def mergeOptions(options: Options, opts: Seq[Optionable]): Options =
  opts.foldLeft(options) { (_, opt) =>
    opt match
      case opt: Option      => options.addOption(opt)
      case opt: OptionGroup => options.addOptionGroup(opt)
  }
