package com.github.wangyung.persona.particle.transformation

import com.github.wangyung.persona.particle.MutableParticle

/**
 * The transformation that changes the scale linearly in the particle.
 */
class LinearScaleTransformation(
    private val xDelta: Float = 0f,
    private val yDelta: Float = 0f,
) : ParticleTransformation {

    override fun transform(particle: MutableParticle, iteration: Long) {
        val interval = iteration - particle.initialIteration
        particle.scaleX = particle.instinct.scaleX + interval * xDelta
        particle.scaleY = particle.instinct.scaleY + interval * yDelta
    }
}
