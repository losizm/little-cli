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

import org.apache.commons.cli.{ Option => CliOption }

import Cli._
import Implicits._

class CliSpec extends org.scalatest.FlatSpec {
  it should "create Options" in {
    val opts = options(
      option("i", "ignore-case", false, "Perform case insensitive matching"),
      option("l", "files-with-matches", false, "Print file name only"),
      option("m", "max-count", true, "Stop reading file after 'num' matches").argName("num"),
      option("n", "line-number", false, "Include line number of match"),
      option("r", "recursive", false, "Recursively search subdirectories"),
      group(
        option("0", "stdin", false, "Read from standard in"),
        option("f", "file", true, "Read from specified file").argName("name")
      ),
      option("" , "exclude", true, "Exclude filename pattern from search").argName("pattern"),
    )

    assert { opts.getOptions.size == 8 }
    
    testOption(opts.getOption("i"), "ignore-case", false, "Perform case insensitive matching")
    testOption(opts.getOption("l"), "files-with-matches", false, "Print file name only")
    testOption(opts.getOption("m"), "max-count", true, "Stop reading file after 'num' matches", Some("num"))
    testOption(opts.getOption("n"), "line-number", false, "Include line number of match")
    testOption(opts.getOption("r"), "recursive", false, "Recursively search subdirectories")
    testOption(opts.getOption("0"), "stdin", false, "Read from standard in")
    testOption(opts.getOption("f"), "file", true, "Read from specified file", Some("name"))
    testOption(opts.getOption("exclude"), "exclude", true, "Exclude filename pattern from search", Some("pattern"))
  }

  it should "create Application" in {
    val app = application("grep [ options... ] <pattern> [ <fileName>... ]")

    app.options(
      option("i", "ignore-case", false, "Perform case insensitive matching"),
      option("l", "files-with-matches", false, "Print file name only"),
    )

    app.addOption(option("m", "max-count", true, "Stop reading file after 'num' matches").argName("num"))

    app.addOption("n", "line-number", false, "Include line number of match")

    app.addOptions(
      option("r", "recursive", false, "Recursively search subdirectories"),
      option("" , "exclude", true, "Exclude filename pattern from search").argName("pattern")
    )

    app.addOptionGroup(
      option("0", "stdin", false, "Read from standard in"),
      option("f", "file", true, "Read from specified file").argName("name")
    )

    assert { app.usage == "grep [ options... ] <pattern> [ <fileName>... ]" }
    assert { app.options.getOptions.size == 8 }
    
    testOption(app.options.getOption("i"), "ignore-case", false, "Perform case insensitive matching")
    testOption(app.options.getOption("l"), "files-with-matches", false, "Print file name only")
    testOption(app.options.getOption("m"), "max-count", true, "Stop reading file after 'num' matches", Some("num"))
    testOption(app.options.getOption("n"), "line-number", false, "Include line number of match")
    testOption(app.options.getOption("r"), "recursive", false, "Recursively search subdirectories")
    testOption(app.options.getOption("0"), "stdin", false, "Read from standard in")
    testOption(app.options.getOption("f"), "file", true, "Read from specified file", Some("name"))
    testOption(app.options.getOption("exclude"), "exclude", true, "Exclude filename pattern from search", Some("pattern"))

    val args = Array("-ilr", "--exclude", "*.swp", "exception", "src/main/scala")
    val cmd = app.parse(args)

    assert { cmd.hasOption("ignore-case") }
    assert { cmd.hasOption("files-with-matches") }
    assert { cmd.hasOption("recursive") }
    assert { cmd.getOptionValue("exclude") == "*.swp" }

    assert { !cmd.hasOption("line-number") }
    assert { !cmd.hasOption("max-count") }
    assert { !cmd.hasOption("stdin") }
    assert { !cmd.hasOption("file") }

    assert { cmd.hasOption("i", "recursive") == (true, true) }
    assert { cmd.hasOption("recursive", "f") == (true, false) }
    assert { cmd.hasOption("i", "recursive", "f") == (true, true, false) }
    assert { cmd.hasOption("i", "f", "recursive") == (true, false, true) }

    assert { cmd.hasOptions() == Nil }
    assert { cmd.hasOptions("i") == Seq(true) }
    assert { cmd.hasOptions("f") == Seq(false) }
    assert { cmd.hasOptions("i", "recursive") == Seq(true, true) }
    assert { cmd.hasOptions("recursive", "f") == Seq(true, false) }
    assert { cmd.hasOptions("i", "recursive", "f") == Seq(true, true, false) }
    assert { cmd.hasOptions("i", "recursive", "f", "exclude") == Seq(true, true, false, true) }

    assert { cmd.getArgCount == 2 }
    assert { cmd.getArg(0) == "exception" }
    assert { cmd.getArg(1) == "src/main/scala" }

    assert { cmd.getArg(0, "default") == "exception" }
    assert { cmd.getArg(1, "default") == "src/main/scala" }
    assert { cmd.getArg(2, "default") == "default" }
    assert { cmd.getArg(-1, "default") == "default" }
  }

  private def testOption(
    opt: CliOption,
    longOpt: String,
    hasArg: Boolean,
    desc: String,
    argName: Option[String] = None
  ): Unit = {
    assert { opt.getLongOpt == longOpt }
    assert { opt.hasArg == hasArg }
    assert { opt.getDescription == desc }
    argName.foreach { name => opt.getArgName == name }
  }
}
