package com.github.wangyung.app.transformation

import com.github.wangyung.persona.particle.MutableParticle
import com.github.wangyung.persona.particle.generator.nextFloat
import com.github.wangyung.persona.particle.transformation.ParticleTransformation
import kotlin.math.abs
import kotlin.math.sin

/**
 * The transformation that simulates the blink effect by changing the alpha in the particle.
 */
class BlinkParticleTransformation(
    private val frequencyFactorRange: ClosedFloatingPointRange<Float>,
) : ParticleTransformation {

    private val frequencyMap: MutableMap<Long, Float> = mutableMapOf()

    override fun transform(particle: MutableParticle, iteration: Long) {
        if (frequencyMap[particle.id] == null) {
            frequencyMap[particle.id] = frequencyFactorRange.nextFloat()
        }
        val frequency = frequencyMap[particle.id]!!.toDouble()
        particle.alpha = abs(sin(Math.toRadians(iteration.toDouble() * frequency)).toFloat())
    }
}
