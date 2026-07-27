# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Persona is a customizable particle animation library for Jetpack Compose on Android (Kotlin only, no KMP). It is published to JitPack as `com.github.wangyung:persona`. The repo also contains a demo `app` module that exercises the library.

## Build, Test, and Lint

Uses the Gradle wrapper (`./gradlew`). Module names come from the directory names under `modules/` (see "Module system" below).

```bash
# Build everything
./gradlew build

# Build a single library module
./gradlew :persona-core:assembleRelease

# Run unit tests (only persona-core currently has tests)
./gradlew :persona-core:testDebugUnitTest

# Run a single test class
./gradlew :persona-core:testDebugUnitTest --tests "com.github.wangyung.persona.particle.ParticleSystemTest"

# Static analysis (detekt is applied to every subproject)
./gradlew detekt                    # fails the build on issues
./gradlew detekt -PdetektIgnoreFailures   # sanity check without failing

# Build & install the demo app
./gradlew :app:assembleDebug

# Publish the library to Maven local (what JitPack effectively runs)
./gradlew :persona-core:publishReleasePublicationToMavenLocal
```

Detekt config lives at `config/detekt/detekt-config.yml`; reports are written to `build/reports/detekt/`.

## Module system

`settings.gradle` auto-includes every directory under `modules/` that has a `build.gradle`. To add a module, use the scaffolding tool rather than copying by hand — it fills in the template under `template/module/`:

```bash
python3 tools/module-create.py MODULE_NAME
```

All dependency and SDK versions live in the version catalog `gradle/libs.versions.toml`. Reference them as `libs.kotlin.stdlib`, `libs.compose.bom`, `libs.versions.compileSdk.get().toInteger()`, etc. — do not hardcode versions in module `build.gradle` files. In applied script plugins (`gradle/detekt.gradle`, `gradle/jacoco.gradle`) the type-safe accessors are unavailable; they use `VersionCatalogsExtension.named("libs")` instead. Detekt is wired in globally via `build.gradle` → `gradle/detekt.gradle` (`buildUponDefaultConfig` with overrides in `config/detekt/detekt-config.yml`).

The build runs on Gradle 9 / AGP 9 with **built-in Kotlin**: modules do not apply `kotlin-android` (AGP 9 rejects it). Compose modules apply `org.jetbrains.kotlin.plugin.compose` plus `buildFeatures { compose true }`; there is no `composeOptions`/`kotlinCompilerExtensionVersion` (the Compose compiler ships with Kotlin). JVM target is set via the project-level `kotlin { compilerOptions { jvmTarget } }` block.

Module dependency graph (all under package `com.github.wangyung.persona`):

- **persona-core** — pure logic, no Compose UI. The published artifact (`artifactId=persona`). Its library version is set in `modules/persona-core/version.properties`, separate from the app version in `appVersion.properties`.
- **persona-shape-render-android** — depends on `persona-core`; Compose `DrawScope` extension functions that render each shape.
- **persona-compose** — depends on `persona-core`, `api`-exposes `persona-shape-render-android`; provides the `ParticleBox` composable.
- **persona-json-serialization-kotlinx** — depends on `persona-core`; creates animations from JSON via kotlinx.serialization. Entry points in `PersonaJson.kt`: `particleSystemFromJson()`, `particleParametersFromJson()`, `ParticleParameters.toJsonString()/toParticleSystem()`. Core parameter classes are serialized through surrogate `KSerializer`s registered as `@Contextual` in `personaJson`; shapes are never serialized — callers pass a `ShapeProvider` (the optional `shapeParameters` JsonObject carries custom shape settings).
- **app** — demo, depends on the three consumer modules above. Every `AnimationType` is also described by a json asset (`app/src/main/assets/animations/<value>.json`, loaded via `AnimationTypeJson.kt`); the app-side `shapeParameters` schema lives in `ShapeParameters.kt` (`line`, `circle`, `text`, `image`, `rectangle`, `path`). A unit test (`AnimationTypeJsonAssetTest`) keeps the json assets in parity with the Kotlin definitions — update both together.

## Architecture

The system separates *what particles are* (core), *how they move* (transformation), *where they come from* (generator), and *how they are drawn* (render/compose). Understanding these four collaborators is the key to the codebase.

**`ParticleSystem`** (`persona-core/.../particle/ParticleSystem.kt`) is the engine. `DefaultParticleSystem` runs a coroutine loop on `Dispatchers.Default` that ticks `parameters.fps` times per second. Each tick increments an `iteration` counter exposed as a `StateFlow<Long>` (`iterationFlow`). On each tick it walks every particle: if out of bounds it either resets it via the generator (`autoResetParticles`) or marks it dead; otherwise it applies the `ParticleTransformation`. When all particles die it either restarts (`restartWhenAllDead`) or stops. Create instances via the `particleSystem(...)` factory, not the constructor.

**`ParticleGenerator`** (`.../particle/generator/`) creates the initial `List<MutableParticle>` and resets individual particles when they leave the bounds. `RandomizeParticleGenerator` is the built-in implementation, configured by `RandomizeParticleGeneratorParameters` (count, speed/angle ranges, source edges, and a `shapeProvider` lambda that supplies each particle's shape).

**`ParticleTransformation`** (`.../particle/transformation/`) is the single-method contract `transform(particle, iteration)` that mutates a particle's position/rotation/scale/alpha for the given iteration. Built-ins include `LinearTranslateTransformation` and `LinearRotationTransformation`. Two combinators compose them: `CompositeTransformation` (apply several at once) and `SequenceTransformation` (apply in order over time). `DurationalDecoratorTransformation` bounds a transformation's lifetime.

**Rendering** is intentionally decoupled from core. `Particle.shape` is typed as the core `ParticleShape`, but the Compose path casts it to `ComposeParticleShape` (`persona-shape-render-android`), a sealed type with `Circle`, `Line`, `Text`, `Path`, `Image`, `Rectangle`. `ParticleBox` (`persona-compose`) collects `iterationFlow` as Compose state to drive recomposition, draws every particle on a `Canvas` inside a `clipRect`, and registers a `LifecycleObserver` so the system stops on `ON_PAUSE` and starts on `ON_RESUME`.

Rendering flow per frame: system tick → `iterationFlow` emits → `ParticleBox` recomposes → `drawParticles` dispatches on the sealed `ComposeParticleShape` to the matching `DrawScope.draw*` extension.

**Extending the library** means implementing one of the three core interfaces — `ParticleGenerator`, `ParticleTransformation`, or `ParticleSystem` — as noted in the README's Customization section.

## Conventions

- Target JVM 11 across all modules; Compose compiler version is pinned via `versions.composeCompiler`.
- `MutableParticle` is the internal write-side view; `Particle` is the read-side view exposed to renderers. Transformations and generators receive the mutable form.
- `List<T>.fastForEach` in core (and `androidx.compose.ui.util.fastForEach` in the UI layer) are used in hot per-frame loops instead of the allocating iterators — prefer them there.

## Known constraint

If `ParticleBox` is placed inside scrollable content the animation disappears (documented in the README).
