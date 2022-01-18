package com.github.wangyung.persona.particle.mock

import com.github.wangyung.persona.particle.MutableParticle
import com.github.wangyung.persona.particle.transformation.ParticleTransformation

/**
 * A mock [ParticleTransformation] that set [Particle.isAlive] to false at the given iteration.
 */
class NotAliveTransformation(private val notAliveAtIteration: Long = 1L) : ParticleTransformation {
    override fun transform(particle: MutableParticle, iteration: Long) {
        if (iteration == notAliveAtIteration) {
            particle.isAlive = false
        }
    }
}
