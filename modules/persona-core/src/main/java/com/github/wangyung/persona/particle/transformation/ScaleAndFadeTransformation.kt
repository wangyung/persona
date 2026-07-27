package com.github.wangyung.persona.particle.transformation

import com.github.wangyung.persona.particle.MutableParticle

/**
 * The transformation that changes the scale and fades the alpha in the particle.
 */
class ScaleAndFadeTransformation(
    private val xDelta: Float = 0f,
    private val yDelta: Float = 0f,
    private val alphaDelta: Float = 0f,
) : ParticleTransformation {

    override fun transform(particle: MutableParticle, iteration: Long) {
        particle.alpha = (particle.instinct.alpha - alphaDelta * iteration).coerceAtLeast(0f)
        particle.scaleX = particle.instinct.scaleX + iteration * xDelta
        particle.scaleY = particle.instinct.scaleY + iteration * yDelta
    }
}
