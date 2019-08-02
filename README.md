# FogGatewayLib
FogGatewayLib is an Android library that makes integration with IoT devices and Fod/Cloud computing
resources easier using your Android device as a gateway between them.

The goal of the library is to create simple interfaces for developers to easily create new Fog and
IoT applications. The library will take care of Android services and threads management, leaving
the user only the definition of how to collect data and how to send this data to the Fog/Cloud.

Simple implementations of most common operations are also provided within the library
(simple http connections, camera management, Bluetooth (coming soon), Bluetooth LE (coming soon)).

In the future it will also be possible to schedule tasks between multiple Fog/Cloud and the device
itself.

## Architecture
There are three main components:
 * InputHandler: this service gathers and pre-processes input
 * Executor: this service fetches input to the Fog/Cloud and retrieves the results.
 * DataStore: this shared object keeps the collected data.

___

Copyright [2019] The Cloud Computing and Distributed Systems (CLOUDS) Laboratory,
University of Melbourne

Licensed under the Apache License, Version 2.0 (see [LICENSE](LICENSE)).