package com.github.wangyung.app.transformation

import com.github.wangyung.persona.particle.MutableParticle
import com.github.wangyung.persona.particle.transformation.ParticleTransformation
import kotlin.math.cos
import kotlin.math.sin

/**
 * The transformation that change the position x of the given particle by horizontal speed and
 * multiply the sin function and change the position y by the vertical speed.
 */
class HorizontalSpeedWithSinParticleTransformation(
    private val frequencyFactor: Float = 2f,
    private val amplitude: Float = 2f
) : ParticleTransformation {
    override fun transform(particle: MutableParticle, iteration: Long) {
        val interval = iteration - particle.initialIteration
        val speed = particle.instinct.speed
        val angle = particle.instinct.angle
        val rotationalSpeed = particle.instinct.zRotationalSpeed
        val horizontalVelocity =
            speed * amplitude * (sin(Math.toRadians(interval.toDouble() * frequencyFactor))).toFloat() *
                    cos(Math.toRadians(angle.toDouble())).toFloat()
        val verticalVelocity =
            speed * sin(Math.toRadians(angle.toDouble())).toFloat()

        particle.y += verticalVelocity
        particle.x += horizontalVelocity

        particle.rotation += rotationalSpeed
    }
}
