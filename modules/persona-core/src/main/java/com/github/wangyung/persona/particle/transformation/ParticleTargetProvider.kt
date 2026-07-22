package com.github.wangyung.persona.particle.transformation

import com.github.wangyung.persona.particle.Particle
import com.github.wangyung.persona.particle.ParticlePoint

/**
 * The interface to provide the target [ParticlePoint] of the given [Particle]. It is used by
 * [MoveToTargetTransformation] to know where each particle should move to.
 */
fun interface ParticleTargetProvider {
    /**
     * Returns the target point of the given [particle], or null if the particle has no target and
     * should stay untouched.
     */
    fun getTarget(particle: Particle): ParticlePoint?
}
