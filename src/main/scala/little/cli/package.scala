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
package little

import org.apache.commons.cli._

/**
 * The Scala library that provides extension methods to
 * [[https://commons.apache.org/proper/commons-cli/index.html Apache Commons CLI]].
 *
 * === How It Works ===
 *
 * Here's an example showing the library in action.
 *
 * {{{
 * import little.cli.Cli
 * import little.cli.Implicits._
 *
 * // Import this for code aesthetics
 * import Cli.option
 *
 * // Create application with specified usage
 * val app = Cli.app("grep [ options ... ] <pattern> [ <fileName> ... ]")
 *
 * // Add options to application
 * app.options(
 *   option("i", "ignore-case", false, "Perform case insensitive matching"),
 *   option("l", "files-with-matches", false, "Print file name only"),
 *   option("m", "max-count", true, "Stop reading file after 'num' matches").argName("num"),
 *   option("n", "line-number", false, "Include line number of match"),
 *   option("r", "recursive", false, "Recursively search subdirectories"),
 *   option("x", "exclude", true, "Exclude filename pattern from search").argName("pattern"),
 * )
 *
 * // Create some arguments to play with
 * val args = Array("-ilr", "--exclude", "*.swp", "exception", "src/main/scala")
 *
 * // Parse arguments
 * val command = app.parse(args)
 *
 * command.hasOption("help") match {
 *   case true  => app.printHelp() // Print help if requested
 *   case false =>
 *     // Interpret command and pretend to do something
 *     val pattern  = command.getArgList.get(0)
 *     val fileName = command.getArgList.get(1)
 *
 *     println(s"Searching for files with '\$pattern' in \$fileName directory...")
 * }
 * }}}
 */
package object cli {
  /** Either `OptionGroup` or `Option` can added to `Options`. */
  type Optionable = Either[OptionGroup, Option]
}
