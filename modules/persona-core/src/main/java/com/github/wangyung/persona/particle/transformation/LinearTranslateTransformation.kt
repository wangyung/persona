package com.github.wangyung.persona.particle.transformation

import androidx.annotation.FloatRange
import com.github.wangyung.persona.particle.MutableParticle
import kotlin.math.cos
import kotlin.math.sin

/**
 * The transformation that translates (x, y) by the speed. It only modifies the current particle
 * state when everytime the [transform] is invoked.
 * It would have different result even invokes [transform] multiple times with the same iteration.
 */
class LinearTranslateTransformation(
    @FloatRange(from = 0.0)
    private val gravity: Float = 0.0f
) : ParticleTransformation {
    override fun transform(particle: MutableParticle, iteration: Long) {
        val duration = iteration - particle.initialIteration
        val speed = particle.instinct.speed
        val angle = particle.instinct.angle
        val horizontalSpeed =
            speed * cos(Math.toRadians(angle.toDouble())).toFloat()
        val verticalSpeed =
            speed * sin(Math.toRadians(angle.toDouble())).toFloat()

        particle.y = particle.y + verticalSpeed + (gravity * duration)
        particle.x = particle.x + horizontalSpeed
    }
}
