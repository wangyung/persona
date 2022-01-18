package com.github.wangyung.persona.particle.generator.parameter

sealed interface Constraints

/**
 * The constraints that applies for particle initialization. The range is from 0 to 1.0.
 */
class InitialConstraints(val limitRange: ClosedFloatingPointRange<Float>) : Constraints
