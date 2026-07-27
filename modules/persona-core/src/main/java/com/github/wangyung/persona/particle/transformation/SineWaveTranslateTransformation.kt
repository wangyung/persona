package com.github.wangyung.persona.particle.transformation

import com.github.wangyung.persona.particle.MutableParticle
import kotlin.math.cos
import kotlin.math.sin

/**
 * The transformation that changes the position x of the given particle by the horizontal speed
 * multiplied by the sin function and changes the position y by the vertical speed. It creates a
 * swinging (sine wave) falling effect such as snow.
 */
class SineWaveTranslateTransformation(
    private val frequencyFactor: Float = 2f,
    private val amplitude: Float = 2f
) : ParticleTransformation {
    override fun transform(particle: MutableParticle, iteration: Long) {
        val interval = iteration - particle.initialIteration
        val speed = particle.instinct.speed
        val angle = particle.instinct.angle
        val rotationalSpeed = particle.instinct.zRotationalSpeed
        val horizontalVelocity =
            speed * amplitude *
                (sin(Math.toRadians(interval.toDouble() * frequencyFactor))).toFloat() *
                cos(Math.toRadians(angle.toDouble())).toFloat()
        val verticalVelocity =
            speed * sin(Math.toRadians(angle.toDouble())).toFloat()

        particle.y += verticalVelocity
        particle.x += horizontalVelocity

        particle.rotation += rotationalSpeed
    }
}
