# FogGatewayLib
FogGatewayLib is an Android library that makes integration with IoT
devices and Fod/Cloud computing resources easier using your Android
device as a gateway between them.

The goal of the library is to create simple interfaces for developers to
easily create new Fog and IoT applications. The library will take care
of Android service and thread management, leaving the user only the
definition of how to collect data and how to send this data to the
Fog/Cloud (and any other possible step in between).

Simple implementations of most common operations are also provided
within the library (simple HTTP connections, camera management,
Bluetooth (coming soon), Bluetooth LE (coming soon)).

In the future it will also be possible to schedule tasks between
multiple Fog/Cloud and the device itself.

## Architecture
The main components are:
 * `ExecutionManager`: this class coordinates the other components and 
    provides an API for managing them. 
 * `Data`: this is not a proper component but is the base class that 
    every data inside this library must extend. It is characterized by
    an `id` and a `request_id`, the former must be unique between data
    of the same `Store`, the latter is useful for tracking data belonging 
    to the same request.  
 * `Store`: a store is a collection of `Data`. A store provides two 
    operations: `retrieve` for retrieving previously stored data and 
    `store` for storing new data. Every `Store` is uniquely identified by
    a key. There can be multiple `Store`s for the same `Data` type.
 * `Provider`: a provider takes some `Data` in input and provides some 
    other `Data` in output. Every `Provider` is uniquely identified by
    a key. There can be multiple `Provider`s for the same `Store`.
 * `Chooser`: in case of multiple providers for the same `Store`, the
    `Chooser` chooses between them. It must be present in case there 
    exist two or more `Provider`s for the same `Store`.
 * `Trigger`: whenever `Data` is stored in a `Store`, `Trigger`s 
    associated with that store are fired. For example, a `Trigger` could
    start a `Provider` with the recently stored `Data`.

## Project Structure
 * `bluetooth`: module for bluetooth integration.
 * `bluetoothdemo`: demo application for bluetooth using 
    [FogBus](https://github.com/Cloudslab/FogBus):
 * `camera`: module for simple camera integration.
 * `camerademo`: demo application for camera using 
    [EdgeLens](https://github.com/Cloudslab/EdgeLens).
 * `core`: module containing the library core.
 * `service`: module for integrating the library with a Foreground 
    Service.  
    

___

Copyright 2019 The Cloud Computing and Distributed Systems (CLOUDS) 
Laboratory, University of Melbourne

Licensed under the Apache License, Version 2.0 (see [LICENSE](LICENSE)).