package com.github.wangyung.persona.particle.transformation

import com.github.wangyung.persona.particle.MutableParticle
import com.github.wangyung.persona.particle.generator.nextFloat
import kotlin.math.abs
import kotlin.math.sin

/**
 * The transformation that simulates the blink effect by changing the alpha in the particle.
 * Each particle blinks with its own frequency picked randomly from [frequencyFactorRange].
 */
class BlinkTransformation(
    private val frequencyFactorRange: ClosedFloatingPointRange<Float>,
) : ParticleTransformation {

    private val frequencyMap: MutableMap<Long, Float> = mutableMapOf()

    override fun transform(particle: MutableParticle, iteration: Long) {
        val frequency = frequencyMap.getOrPut(particle.id) {
            frequencyFactorRange.nextFloat()
        }.toDouble()
        particle.alpha = abs(sin(Math.toRadians(iteration.toDouble() * frequency)).toFloat())
    }
}
