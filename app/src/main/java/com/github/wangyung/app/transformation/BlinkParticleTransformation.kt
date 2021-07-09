package com.github.wangyung.app.transformation

import com.github.wangyung.persona.particle.MutableParticle
import com.github.wangyung.persona.particle.generator.nextFloat
import com.github.wangyung.persona.particle.transformation.ParticleTransformation
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

/**
 * The transformation that simulates the blink effect by changing the alpha in the particle.
 */
class BlinkParticleTransformation(
    private val minFrequencyFactor: Float,
    private val maxFrequencyFactor: Float
) : ParticleTransformation {

    private val frequencyMap: MutableMap<Long, Float> = mutableMapOf()

    override fun transform(particle: MutableParticle, iteration: Long) {
        if (frequencyMap[particle.id] == null) {
            frequencyMap[particle.id] = Random.nextFloat(minFrequencyFactor, maxFrequencyFactor)
        }
        val frequency = frequencyMap[particle.id]!!.toDouble()
        particle.alpha = abs(sin(Math.toRadians(iteration.toDouble() * frequency)).toFloat())
    }
}
