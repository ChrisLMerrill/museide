# MuseIDE
MuseIDE is a visual development environment for the [Muse Test Framework](https://github.com/ChrisLMerrill/muse), built with JavaFX.

# Binary releases

Binary releases of this software are [available for Windows](http://ide4selenium.com/download.html). If you just want to run the software, the binary releases are recommended. That build also includes automatic updates and a UI for installation of projects extensions.

# Building from source

## Requirements

* Java 10
* Muse core libraries installed in the local Maven repository (see the [Muse project](https://github.com/ChrisLMerrill/muse))

## Build and run MuseIDE

This project is divided into multiple sub-projects using a Gradle composite build. You can build and run the entire application from the app folder, which contains the composite build configuration.

To build and run the app, checkout the repository. Then, from the *app* folder, run:

    gradlew installDeps
    gradlew run

The first task builds the dependent projects and installs the libraries into the local Maven repository.

Each of the sub-projects has their own, independent Gradle build file. During development, you can work on the individual projects as needed.

## Run unit tests

Much of the UI has unit tests built with TestFX. Be warned: they pretty much take over the desktop when they run. A headless mode for JavaFX, is available, but tests have not been found reliable in that environment.

As with the Gradle standard, building any project will also run the unit tests

    gradlew build  
