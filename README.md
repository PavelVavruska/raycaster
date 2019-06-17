# Raycaster

Raycaster engine is inspired by 90's PC games. It takes three inputs: 
* 2D array representing the map 
* Player (coordinates, angle of view)
* Configuration (FOV, perspective correction, metric)

## Algorithm

Exactly one ray is cast for every single horizontal pixel of the player's screen. Ray has purpose of detecting objects in the way from player to the nearest wall. Ray stops after collision with the nearest wall and is, during its life, measured by length to construct objects and wall on the screen. Measured lengths of rays are optionally perspectively corrected. Metrics are shown if requested in configuration.

## How to run

`./gradlew clean build`
`./gradlew run`

## How to play

* `W / S    - move forward/backward`
* `A / D    - turn left/right`
* `Q / E    - strafe left/right`
* `P     - turn perspective correction on/off`
* `M     - turn the metrics on/off`
* `N / H - FOV (field of view) settings -/+`

## Changelog

21.04.2019 version - added background image
<img alt="Description" src="https://github.com/PavelVavruska/raycaster/blob/master/raycaster_20190421.png">
11.04.2019 version - added collision detection, added transparent objects with texture, improved texture rendering by using two textures for one object/wall  
<img alt="Description" src="https://github.com/PavelVavruska/raycaster/blob/master/raycaster_20190411.png">
07.04.2019 version - flexible multicore support (Tested on 8C/16T CPU. FPS were increased by a factor of 3.)
<img alt="Description" src="https://github.com/PavelVavruska/raycaster/blob/master/raycaster_20190407.png">
06.04.2019 version
<img alt="Description" src="https://github.com/PavelVavruska/raycaster/blob/master/raycaster_20190406.png">
25.03.2019 version
<img alt="Description" src="https://github.com/PavelVavruska/raycaster/blob/master/raycaster_20180325.png">
24.03.2019 version
<img alt="Description" src="https://github.com/PavelVavruska/raycaster/blob/master/raycaster.png">
