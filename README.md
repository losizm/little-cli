# little-cli

The Scala library that provides extension methods to [Apache Commons CLI](https://commons.apache.org/proper/commons-cli/index.html).

[![Maven Central](https://img.shields.io/maven-central/v/com.github.losizm/little-cli_2.12.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.losizm%22%20AND%20a:%22little-cli_2.12%22)

## Getting Started
To use **little-cli**, start by adding it to your project:

```scala
libraryDependencies += "com.github.losizm" %% "little-cli" % "0.4.0"
```

There's a runtime dependency to [Apache Commons CLI](https://commons.apache.org/proper/commons-cli/index.html),
so you'll need to add that too.

```scala
libraryDependencies += "commons-cli" % "commons-cli" % "1.4"
```

## How It Works

Here's an example showing the library in action.

```scala
import little.cli.Cli.{ application, option }
import little.cli.Implicits._

// Create application with specified usage
val app = application("grep [ options ... ] <pattern> [ <fileName> ... ]")

// Add options to application
app.options(
  option("i", "ignore-case", false, "Perform case insensitive matching"),
  option("l", "files-with-matches", false, "Print file name only"),
  option("m", "max-count", true, "Stop reading file after 'num' matches").argName("num"),
  option("n", "line-number", false, "Include line number of match"),
  option("r", "recursive", false, "Recursively search subdirectories"),
  option("x", "exclude", true, "Exclude filename pattern from search").argName("pattern"),
)

// Create some arguments to play with
val args = Array("-ilr", "--exclude", "*.swp", "exception", "src/main/scala")

// Parse arguments
val cmd = app.parse(args)

cmd.hasOption("help") match {
  case true  => app.printHelp() // Print help if requested
  case false =>
    // Interpret command and pretend to do something
    val pattern  = cmd.getArg(0)
    val fileName = cmd.getArg(1)

    println(s"Searching for files with '$pattern' in $fileName directory...")
}
```

### Mapping Option and Argument Values

Option and argument values can be mapped to types other than `String` by
adding a `ValueMapper[T]` to implicit scope. There are default implementations
for `Int`, `Long`, and `File` defined in `Implicits`. Feel free to define your
own for other types.

```scala
import little.cli.Cli.{ application, option }
import little.cli.Implicits._
import little.cli.ValueMapper

case class KeepAlive(idleTimeout: Int, maxRequests: Int)

// Define how to map value to KeepAlive
implicit val keepAliveMapper: ValueMapper[KeepAlive] =
  _.split(":") match {
    case Array(timeout, max) => KeepAlive(timeout.toInt, max.toInt)
  }

val app = application("start-server [ options ... ] port")
  .options(
    option("d", "directory", true, "Location of public files directory"),
    option("k", "keep-alive", true, "Allow persistent connections")
      .argName("timeout:max")
  )

val cmd = app.parse("-d ./public_html --keep-alive 5:10 8080".split("\\s+"))

// Map keep-alive option
val keepAlive = cmd.mapOptionValue[KeepAlive]("keep-alive")

// Map directory option
val directory = cmd.mapOptionValue[java.io.File]("directory")

// Map port argument
val port = cmd.mapArg[Int](0)
```

## API Documentation

See [scaladoc](https://losizm.github.io/little-cli/latest/api/little/cli/index.html)
for additional details.

## License
**little-cli** is licensed under the Apache License, Version 2. See LICENSE file
for more information.
