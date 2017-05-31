# spinacht

This is Micah Nacht and Nick Spinale's final project for CS324 (Data Mining).

Our paper can be found at `./spinacht.pdf`.

## Structure

As usual, our main source is in `src/main`, and tests are in `src/test`.
`data` contains data sets for interactive tests and analysis.
Some data sets are discussed in our paper.

As for the source itself, our actual work is in the Java package `spinacht` (`src/main/java/spinacht`).
This package contains:

*   Implementations of DBSCAN/SUBCLU and their various support classes and
    functions (`spinacht.data`, `spinacht.index`, `spinacht.dbscan`, and `spinacht.subclu`).
*   A desktop application for interactively clustering using SUBCLU in two dimensions (`spinacht.visualizer`).
*   Programs for applying our implementation to specific data sets (`spinacht.analysis`).
    There were written for us to analyze our implementations along with specific data sets, and are not meant to be user-facing.

## Usage

We used gradle to manage dependencies and build our project.
It is very easy to use, and is itself contained in this repository.

To build our project and run its tests, run `./gradlew build`.

To run an arbitrary class within our project (with runtime dependencies in scope), run `./run CLASS ARGS...` or `python3 run.py CLASS ARGS`.

If you are assessing this project, you will probably just want to run the visualizer.
This can be done with `./gradlew vis`.
Alternatively, `./gradlew visJar` creates `./build/libs/spinacht-visualizer.jar` which contains all of its dependencies, and runs the visualizer with `java -jar spinacht-visualizer.jar`.
For your convenience, we have pre-built this jar, and put it in `./pre-built`.

If any of these instructions don't work, please let us know and we will figure out what's going on.

## Using the Visualizer

The left half of the application shows a mutable 2-dimensional spacial database, along with its projections in each of the two dimensions.
The right half contains controls and parameters.

The first row of buttons allows you to clear the database, save the database to a file, load a database from a file, or take a snapshot (.png) of whatever is being displayed on the left half.

The second row of buttons are toggles:

*   `Cluster` toggles whether the database is shown as is or clustered (using SUBCLU).
*   `Erase` toggles whether clicking on the canvas showing 2-space adds or removes points.
*   `Hide Neighborhoods` toggles whether clustered points' epsilon neighborhoods are shown.

The rest of the right half allows you to control the parameters of DBSCAN.
*minPts* can be controlled directly, and *epsilon* can be controlled using the slider at the bottom.

Try clicking on the big canvas.
You'll see points appear, along with their projections in each dimension (to the left and bottom).
Now, toggle `Cluster` on, and see what happens.
Noise points become gray, and clustered points are colored, and their epsilon neighborhoods are shown.
The singleton subspaces are also clustered.

Try playing with the parameters.
You'll see what happens in each subspace as you change the definition of density, and observe the *A-priori* principle in action as clusters in 2-space always project into 1-spaces, but not vice versa.

Note that there is no relationship between colors across subspaces.
Colors are used only to distinguish clusters in a given subspace.
So, a green cluster in the *y*-subspace is not necessarily related to a green cluster in 2-space.

Again, if you have any questions about this application, do not hesitate to ask us about it.
