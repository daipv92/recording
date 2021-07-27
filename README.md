# ModuloTech Test
Create a small smart home application, able to steer devices such as lights, roller shutters, or heaters.

## Features

- [x] On the home page, list the devices, and make them filterable by product type
- [x] List the devices of one or more selected device types
- [x] Allow deleting any devices
- [x] Create a steering page for each device type
- [x] Lights: Mode ON/OFF and intensity management (0 - 100)
- [x] Roller shutters: Set position using a vertical slider (0 - 100)
- [x] Heaters: Mode ON/OFF and set the temperature with a step of 0.5 degrees (min: 7°, max 28°)

## Requirements

- Android >=21

## Build instructions

To clone the Git repository to your local machine: 
```
$ git clone https://bitbucket.org/vmodev/stl4-android.git
```
From Android Studio: click Run 'app' to launch

To build APK from command: refer to https://developer.android.com/studio/build/building-cmdline

## Technical Points

- Language: Kotlin (Android)
- Used libraries: Dagger Hint, Jetpack navigation, architecture Components, coroutines
- Pattern: MVVM

## Copyright & License
Copyright (c) 2020 VMO JSC. All rights reserved.