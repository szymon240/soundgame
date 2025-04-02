# SOUNDGAME

![Workflow status](https://github.com/szymon240/soundgame/actions/workflows/build.yml/badge.svg)
![License](https://img.shields.io/badge/License-MIT-red)
![Android](https://img.shields.io/badge/Android-gray?logo=android)



## About 

This repository includes client game app for Android devices written in Kotlin. Game is a part of engineering thesis which has been defended 06.02.2025 at Poznań University of Technology with best possible grade 5.0. App requires a running server for this app which code is available in this [repository](https://github.com/szymon240/soundgame_server). Main goal of this app is to allow users to enhance their listening skills.

Main features of this game are:
- three game modes, each focuses on different part of listening: rhythm, pitch and sound recognition
- audio files do not occupy any space on device's drive - because each is downloaded from the server
- ranking system for players (works only with server)
- achievements system for motivating the player
- two languages English and Polish
- interface is written with OpenGL ES without using premade interface framework like Jetpack Compose etc 

## How to run this code

App uses Java 17 with target Android SDK version being level 33.0

In order to run this code Android Studio with GUI, like any other Android Studio project or on Linux with android-sdk and gradle.

```bash
# assuming you have gradle, andorid-sdk properly configured, read .github/workflows/build.yml to see how app is build
  chmod +x gradlew
 ./gradlew assembleDebug
```

Android Studio version used during the development process was Android Studio Ladybug.

## About game

Screenshots below show what app menus look like. First screenshot shows the menu after launching the app with connection to the server, the second one shows menu when there is no connection, and the last one shows the menu after the first launch with a prompt to the user asking for entering a name.  Here text in Polish although every piece of text has its translation after changing the language. 

<p align="center">
  <img src="https://github.com/szymon240/soundgame/blob/main/preview/menus.png?raw=true" width="800" title="App's game modes"> </img>
</p>

Second screenshot depicts app game modes. From left to right: 
- __Rhythm Mode__ - user has to repeat played random rhythm by pushing the button in right time intervals.
- __Instrumental Mode__ - user has to determine which of answers matches instrument which has been played.
- __Pitch Mode__ - user has goal is to choose if played sound has higher or lower pitch than previous one.

<p align="center">
  <img src="https://github.com/szymon240/soundgame/blob/main/preview/gamemodes.png?raw=true" width="800" title="App's game modes"> </img>
</p>

