# Description
Persona is a customizable particle animation system for Jetpack Compose on Android (Kotlin Multiplatform not supported yet).
It was inspired by [flux](https://github.com/fidloo/flux). 

# Install
[![](https://jitpack.io/v/wangyung/persona.svg)](https://jitpack.io/#wangyung/persona)

Install the library from jitpack.

Add the repository
```
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```
Add the dependency
```
dependencies {
    implementation "com.github.wangyung:persona:0.6.0"
}

```

# Video Demo
- [Snow](https://user-images.githubusercontent.com/76404/131855273-4dabcc67-04cb-445c-991a-67958bc9f096.mp4)
- [Sakura](https://user-images.githubusercontent.com/76404/131855349-2d61825d-53fd-4521-a3bb-2d71e94da3d6.mp4)

# How to use
- Create the `ParticleSystem`
```kotlin
val dimension = Size(100, 100) // Set the dimension for the particle system.

// Setup the parameters of ParticleSystem
val particleSystemParameters = ParticleSystemParameters(
    fps = 60,
    autoResetParticles = true,
    restartWhenAllDead = true,
)

// Setup the paramters of built-in RandomizeParticleGenerator
val snowParameters = RandomizeParticleGeneratorParameters(
    randomizeInitialXY = true,
    count = 125,
    speedRange = 1f..2f,
    angleRange = IntRange(80, 100),
    rotationalSpeedRange = 0f..0f,
    sourceEdges = setOf(SourceEdge.TOP),
    shapeProvider = { createShowParticle(IntRange(DEFAULT_SNOW_MIN_RADIUS, DEFAULT_SNOW_MAX_RADIUS)) },
)

val generator = RandomizeParticleGenerator(
    parameters = generatorParameters,
    dimension = dimension,
)

val particleSystem = particleSystem(
    dimension = dimension,
    parameters = particleSystemParameters,
    generator = generator,
    autoStart = true,
    transformation = LinearTranslateTransformation()
)
```
- Create the Composable `ParticleBox`
```kotlin
// In the Composable content
ParticleBox(modifier = Modifier.fillMaxSize(), particleSystem = particleSystem)
```

## Create the animation from JSON
The `persona-json-serialization-kotlinx` module can create the `ParticleSystem` from a json string
(powered by kotlinx-serialization). The shape can't be described in json because it is renderer
specific, so provide the `ShapeProvider` when creating the particle system. Use the optional
`shapeParameters` json object to carry your own shape settings.

```kotlin
val jsonString = """
{
  "name": "Snow",
  "systemParameters": { "fps": 60, "autoResetParticles": true, "restartWhenAllDead": true },
  "generatorParameters": {
    "count": 125,
    "randomizeInitialXY": true,
    "speedRange": { "from": 1.0, "to": 2.0 },
    "angleRange": { "from": 80.0, "to": 100.0 },
    "sourceEdges": ["TOP"]
  },
  "transformationParameters": { "type": "translate", "gravity": 0.1 }
}
"""

val particleSystem = particleSystemFromJson(
    jsonString = jsonString,
    dimension = Size(100, 100),
    shapeProvider = { createShowParticle(IntRange(DEFAULT_SNOW_MIN_RADIUS, DEFAULT_SNOW_MAX_RADIUS)) },
)
```

The `transformationParameters` supports `translate`, `rotation`, `composite` and `sequence`:
```json
{
  "type": "sequence",
  "steps": [
    { "transformation": { "type": "translate", "gravity": 0.1 }, "duration": 40 },
    { "transformation": { "type": "rotation" }, "duration": 30 }
  ]
}
```

You can also serialize the parameters with `ParticleParameters.toJsonString()` and parse them with
`particleParametersFromJson()` then create the system via `ParticleParameters.toParticleSystem()`.

## Morph the particles to target points
`PointsParticleGenerator` creates one particle at each given `ParticlePoint`, and
`MoveToTargetTransformation` moves every particle to the target point provided by the
`ParticleTargetProvider` within the duration (shaped by an `Easing`). Combining them with
`SequenceTransformation` creates the morphing animations, ex: the text morph in the demo app that
samples two texts into points and morphs between them:

```kotlin
val generator = PointsParticleGenerator(points = fromPoints, shapeProvider = { myDotShape() })
val transformation = SequenceTransformation().apply {
    add(MoveToTargetTransformation({ particle -> toPoints[particle.id.toInt()] }, duration = 90), 90)
}
```

## Known Issues
- If the `ParticlBox` is in the a scrollable content, the animation would disappear. 

---

## Customization
- Implementing the `ParticleGenerator` for the custom particle generator.
- Implementing the `ParticleTransformation` for the custom particle transformation.
- Implementing the `ParticleSystem` for the custom particle system.

## License

    Copyright 2021 Freddie Wang

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    
