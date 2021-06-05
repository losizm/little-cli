# little-cli

The Scala library that provides extension methods to [Apache Commons CLI](https://commons.apache.org/proper/commons-cli/index.html).

[![Maven Central](https://img.shields.io/maven-central/v/com.github.losizm/little-cli_2.13.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.losizm%22%20AND%20a:%22little-cli_2.13%22)

## Getting Started
To use **little-cli**, start by adding it to your project:

```scala
libraryDependencies += "com.github.losizm" %% "little-cli" % "0.8.0"
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

// Create application with supplied usage and options
val app = application("grep [ options ... ] <pattern> [ <fileName> ... ]",
  option("i", "ignore-case", false, "Perform case insensitive matching"),
  option("l", "files-with-matches", false, "Print file name only"),
  option("m", "max-count", true, "Stop reading file after 'num' matches").argName("num"),
  option("n", "line-number", false, "Include line number of match"),
  option("r", "recursive", false, "Recursively search subdirectories"),
  option("x", "exclude", true, "Exclude filename pattern from search").argName("pattern")
)

val args = Array("-ilr", "--exclude", "*.swp", "exception", "src/main/scala")

// Parse arguments
val cmd = app.parse(args)

cmd.hasOption("help") match {
  case true =>
    // Print help to System.out
    app.printHelp()
  case false =>
    // Get command arguments and pretend to do something
    val pattern  = cmd.getArg(0)
    val fileName = cmd.getArg(1)
    println(s"Searching for files with '$pattern' in $fileName directory...")
}
```

### Mapping Option Values and Arguments

Option values and arguments can be mapped to types other than `String` by adding
an implicit `ValueMapper[T]` to scope. There are default implementations for
`Int`, `Long`, `File`, and `Path` defined in `Implicits`. Feel free to define
your own for other types.

```scala
import java.io.File
import little.cli.Cli.{ application, option }
import little.cli.Implicits._
import little.cli.ValueMapper

case class KeepAlive(idleTimeout: Int, maxRequests: Int)

// Define how to map value to KeepAlive
implicit val keepAliveMapper: ValueMapper[KeepAlive] =
  _.split(":") match {
    case Array(timeout, max) => KeepAlive(timeout.toInt, max.toInt)
  }

val app = application("start-server [ options ... ] port",
  option("d", "directory", true, "Location of public files directory"),
  option("k", "keep-alive", true, "Allow persistent connections").argName("timeout:max")
)

val cmd = app.parse("-d ./public_html --keep-alive 5:10 8080".split("\\s+"))

// Map keep-alive option
val keepAlive = cmd.mapOptionValue[KeepAlive]("keep-alive")

// Map directory option
val directory = cmd.mapOptionValue[File]("directory")

// Map port argument
val port = cmd.mapArg[Int](0)
```

### Checking Multiple Options

At times, you may need to check multiple options, possibly from a group of
mutually exclusive options, to determine what action to take. For those
occasions, you have the overloaded `hasOption` extension method to
`CommandLine`.

```scala
import java.io.{ ByteArrayInputStream, FileInputStream }
import little.cli.Cli.{ application, group, option }
import little.cli.Implicits._

val app = application("post-request [ options ... ] url",
  option("H", true, "Add header").argName("header"),
  // Define group of mutually exclusive options
  group(
    option("d", true, "Use supplied inline data").argName("data"),
    option("f", true, "Use data from supplied file").argName("file"),
    option("s", false, "Use data from stdin (default)")
  )
)

val cmd = app.parse("-f ./message.json http://localhost:8080/messages".split("\\s+"))

// Check option group and assign input stream
val in = cmd.hasOption("d", "f", "s") match {
  case (true, _, _) => new ByteArrayInputStream(cmd.getOptionValue("d").getBytes)
  case (_, true, _) => new FileInputStream(cmd.getOptionValue("f"))
  case (_, _, any ) => System.in
}

val url = cmd.getArg(0)
```

If you need to check more than 3 options, use `hasOptions` whose behavior is
similiar, except it returns `Seq[Boolean]` instead of a tuple.

## API Documentation

See [scaladoc](https://losizm.github.io/little-cli/latest/api/little/cli/index.html)
for additional details.

## License
**little-cli** is licensed under the Apache License, Version 2. See [LICENSE](LICENSE)
for more information.
