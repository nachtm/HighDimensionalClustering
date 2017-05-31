# spinacht

This is Micah Nacht and Nick Spinale's final project for CS324 (Data Mining).

Our paper can be found at `./spinacht.pdf`.

## Structure

Our actual work is in the package `spinacht`.
This package contains implementations of DBSCAN/SUBCLU, a desktop application for interactively clustering in two dimensions, and various utility classes.
Instructions for running and using visualizer (`spinacht.visualizer.Visualizer`) can be found below.

The package `demo` contains code for applications of our implementation to specific data sets.
Most if its glue for allowing our implementation to interface with the formats of these data sets.
It is not meant to be used for assessment purposes directly, but some of the results of this code are in our paper.

## Usage

We used gradle to manage dependencies and build our project.
It is very easy to use, and is itself contained in this repository.

To build our project and run its tests, run `./gradlew build`.

To run an arbitrary class within our project (with dependencies in scope), run `./run CLASS ARGS...` or `python3 run.py CLASS ARGS`.

If you are assessing this project, you will probably just want to run the visualizer.
This can be done with `./gradlew vis`.
Alternatively, `./gradlew visJar` creates `./build/libs/spinacht-visualizer.jar` which contains all of its dependencies, and runs the visualizer with `java -jar spinacht-visualizer.jar`.
For your convenience, we have pre-build this jar, and put it in `./pre-built`.

## Using the Visualizer
