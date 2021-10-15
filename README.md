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
    
