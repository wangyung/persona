package com.github.wangyung.persona.particle.generator

import com.github.wangyung.persona.particle.MutableParticle

interface ParticleGenerator {
    /**
     * Resets the properties of the given particle when the particle is out of bounds.
     */
    fun resetParticle(particle: MutableParticle)

    /**
     * Creates the list of [MutableParticle].
     */
    fun createParticles(): List<MutableParticle>
}
