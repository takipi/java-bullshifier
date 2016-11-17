# About

An open source project by [OverOps](https://www.overops.com).

![alt 'Java Bullishifier'](http://blog.takipi.com/wp-content/uploads/2016/11/bullishifier.png)

[Visit the site to download a sample result](https://takipi.github.io/java-bullshifier) or [read more on the blog](http://blog.takipi.com/java-bullshifier-generate-massive-random-code-bases)

## Requirements
* Groovy installed
* Java installed


## Installation
* Download, unzip, and you’re ready to go


## Run settings
* `./gradlew run` (default parameters, generates one jar with 10 classes)
* `cd output && gradle fatJar` to build the generated project
* `java -cp output/build/libs/tester.jar helpers.Main` to run it

Or, you can simply run `./scripts/small.sh`, or `./scripts/big.sh`, with preconfigured run settings.


## Flags
* `-Poutput-directory` (relative path to output directory)
* `-Poutput-classes` (number of classes to generate)
* `-Psubprojects` (number of jars to generate)

Keep in mind that generating over 500 classes will take quite some time. Our biggest run had 20,000 classes, but it's better to keep this under 5,000.

## Running sub projects
* `gradle build` (get a WAR file)
* Go to `bin`
* A shell script is created per project, root will run them all


## Advanced config
There are some additional options that give you fine grained control over the generated code, but might mess it up, use at your own risk:
* Low level config: `src/main/groovy/generator/Config.groovy`
* Higher level config is available in the output folder. There are more options to add logging, and fine tune the behavior of the application but it’s experimental at the moment.



If you’d like to learn more, feel free to reach out for a deeper walkthrough (hello@overops.com). Default settings are no logs, and an exception on every 10th frame in the call stack.
