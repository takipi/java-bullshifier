# About

![alt 'Java Bullishifier'](http://blog.takipi.com/wp-content/uploads/2016/11/bullishifier.png)

An open source project by [OverOps](https://www.overops.com).

[Visit the site to download a sample result](https://takipi.github.io/java-bullshifier) or [read more on the blog](http://blog.takipi.com/java-bullshifier-generate-massive-random-code-bases)

## Requirements
* Groovy installed
* Gradle installed
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


## Docker Quick start
In simple cases the following command will allow for local execution of the image. If running backend and collector also in docker see "advance" networking section.

```console
docker run -d --rm -e TAKIPI_COLLECTOR_HOST=<collector_hostname>  -e TAKIPI_COLLECTOR_PORT=6060 -e TAKIPI_APPLICATION_NAME=java-bullshifier -e TAKIPI_DEPLOYMENT_NAME=deployment1 -e TAKIPI_SERVER_NAME=DEV -e COLOR=white/yellow/red/black overops-java-bullshifier:latest
```
Note: Depending if you built or pulled the image your Image name may vary! 

## Docker: Advance Networking
If running multiple components in separate docker images (i.e. Backend and collector), it is recommend running with a docker network to allow for communications:

If not already created, create a docker network and start the image on the network:

```console
docker network create --driver bridge overops

docker run -d --rm --network overops -e TAKIPI_COLLECTOR_HOST=<collector_hostname>  -e TAKIPI_COLLECTOR_PORT=6060 -e TAKIPI_APPLICATION_NAME=java-bullshifier -e TAKIPI_DEPLOYMENT_NAME=deployment1 -e TAKIPI_SERVER_NAME=DEV -e COLOR=<white/yellow/red/black> overops-java-bullshifier:latest
```
Note: This assumes you have a collector running in docker with `--network overops --name overops-collector`

The following table lists the configurable ENVS of the Java Bullshifier using `-e`:

| Parameter                                    | Description                                                                                  | Default                           |
| -------------------------------------------- | -------------------------------------------------------------------------------------------- | ----------------------------------|
| `TAKIPI_COLLECTOR_HOST`                      | Collector hostname or K8s Service Name                                                       | `collector`                             |
| `TAKIPI_COLLECTOR_PORT`                      | Collector port                                                                               | `6060`                            |
| `COLOR`                              | The plan of the Bulshifier - affect on how intense will be the load on the application - (Options: white/yellow/red/black) .     | `white`                              |
| `RUNNING_DURATION_HOURS`           | The number of hours java-bullshifier app should be running                                   | `0`                               |
| `RUNNING_DURATION_MINUTES`         | The number of minutes java-bullshifier app should be running                                 | `5`                               |
| `INERVAL_MILLIS`         |  Interval between events (millis)                                 | `300`                               |
