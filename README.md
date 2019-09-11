# FogGatewayLib

FogGatewayLib is an Android library that makes handling data streams in 
Android easy and efficient. 
The library makes it simple to create new data-driven applications 
without having to care about Android service and thread management. 
Everything the user of the library has to do is just defining of how to collect 
data and elaborate data within the framework.
 
The goal of the library is to make the development of Android applications
that act as a gateway between IoT and the Fog/Cloud easier.
This is achieved also by providing the user with simple implementations of 
most common operations out-of-the-box. E.g. simple HTTP and FTP connections, 
camera management, Bluetooth LE, etc. The project has been developed
with modularity in mind so the addition of new features should be 
straightforward.


## Architecture

The project is based on the publisher-subscriber paradigm. 
In particular, a publisher is here called `Provider`, a topic is a 
`Store` and a subscriber is a `Trigger`. Whenever a `Provider` stores new
data to a `Store`, all `Trigger`s associated with the `Store` are 
executed, which, in their turn, can start new `Provider`s for other 
`Store`s (or even the same one).
A more detailed description of the components follows. 


### Components

The main components are:
 * `ExecutionManager`: coordinates the other components and 
    provides an API for managing them. 
 * `Data`: not a proper component but is the base class that 
    every data inside this library must extend. It is characterized by
    an `id` and a `request_id`, the former must be unique between data
    of the same `Store`, the latter is useful for tracking data belonging 
    to the same request.  
 * `Store`: stores `Data` elements and provides two operations: 
    `retrieve` for retrieving previously stored data and 
    `store` for storing new data. Every `Store` is uniquely identified by
    a key. There can be multiple `Store`s for the same `Data` type.
 * `Provider`: takes some `Data` in input and produces some 
    other `Data` in output. Every `Provider` is uniquely identified by
    a key. There can be multiple `Provider`s for the same `Store` but 
    a `Provider` can use only one `Store` at a time (plus the
    `ProgressStore`, used for providing information about its state to 
    the UI or other components). A `Provider` can be started by calling
    its `execute` method either through `ExecutionManager.runProvider` or
    through `ExecutionManager.produceData`. In the latter case, the user
    specifies only which `Data` it wants to be produced (by means of a 
    `Store` key) and a suitable `Provider` will be executed. In case there
    are two or more `Provider`s for the same `Store`, a `Chooser` will 
    be used.
 * `Chooser`: chooses the `Provider` to produce data for a `Store`. 
    It must be present in case there exist two or more `Provider`s for 
    the same `Store`.
 * `Trigger`: whenever `Data` is stored in a `Store`, `Trigger`s 
    associated with that store are fired. For example, a `Trigger` could
    start a `Provider` with the recently stored `Data`.
    

### Threads and synchronization

Please note that there is no built-in synchronization mechanism for a 
`Store` by default. Thus all `retrieve` and (in particular) `store` 
operations should not be executed concurrently.

This is not an issue in Android when using the provided
`AsyncProvider` implementation of a `Provider` since it takes advantage
of the the `onPreExecute` and `onPostExecute` callbacks of the `AsyncTask`
in order to execute thread-unsafe code in the main thread. Furthermore, 
it also has built-in thread-safety mechanisms in its `publishResults` 
and `publishProgress` methods that check the thread they're running in 
before storing the `Data` to the `Store`. 


### Documentation

There are two sources of documentation for this project:
 
 * Javadoc, available for most classes and methods.
 * Examples:
   * `camerademo`
   * `bluetoothdemo`


### Project Structure

The project is divided into modules, each of which is dedicated to a 
specific feature. Where possible, Android-specific code and generic Java 
code have been kept separated in order to make the core of the project
more portable.

 * `aneka`: Android library module for Aneka REST API integration.
 * `aneka-wsdl`: Java module for Aneka REST API WSDL bindings.
 * `bluetooth`: Android module for bluetooth LE integration.
 * `bluetoothdemo`: demo application for bluetooth module using 
                    [FogBus](https://github.com/Cloudslab/FogBus).
 * `camera`: Android module for simple camera integration.
 * `camerademo`: demo application for camera module using 
                 [EdgeLens](https://github.com/Cloudslab/EdgeLens).
 * `core`: Android module containing the Android-specific part of the 
           library core.
 * `core-java`: Java module containing the library core.
 * `fogbus`: Android module containing a simple integration with
             [FogBus](https://github.com/Cloudslab/FogBus).
 * `service`: Android module for integrating the library with a Foreground 
              Service.   
 * `utils`: Android module for Android-specific utility classes 
            (NotificationUtils, Timer)
 * `utils-java`: Java module for utility classes such as MultiMap,
                 SimpleFTPClient, SimpleHttpConnection, etc.


## Building the project

The project has been created using [Android Studio](https://developer.android.com/studio),
which uses [Gradle](https://gradle.org/) to build the project. Therefore, 
you can either import it in Android Studio or build it from command line
using the Gradle wrapper.


### Importing in Android Studio

Either clone this repository with `git` and import it in Android Studio
or directly import the project from GitHub with Android Studio.


### Building from command line

To build from command line you can do (Unix syntax follows, for Windows 
replace `./gradlew` with `gradlew.bat`):
```bash
# Build everything
./gradlew assemble

# Build module 'mymodule'
./gradlew :mymodule:assemble

# Build and test everything
./gradlew build

# Build and test module 'mymodule'
./gradlew :mymodule:build
```
These commands build the release variant. In order to build the debug variant, 
just append `Debug` to the command name (i.e. `assembleDebug`, `buildDebug`;
debug variant is not available for Java modules).

Outputs of the building process can be found in (assuming you're building 
`mymodule` with variant `variant`):
 * `mymodule/build/outputs/apk/mymmodule-variant.apk` for Android application
    modules.
 * `mymodule/build/outputs/aar/mymmodule-variant.aar` for Android library
    modules.
 * `mymodule/build/libs/mymmodule.jar` for Java modules.


## Using the library

At the moment the library is not distributed through Maven Central or 
an equivalent platform. Therefore you can either clone the repository and
import the modules you need in Android Studio or you can download the 
`*.jar` or `*.aar` files from the latest release in the Release page and 
use them in your project.


___

Copyright 2019 The Cloud Computing and Distributed Systems (CLOUDS) 
Laboratory, University of Melbourne

Licensed under the Apache License, Version 2.0 (see [LICENSE](LICENSE)).