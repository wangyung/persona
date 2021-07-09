package com.github.wangyung.persona.particle.transformation

import com.github.wangyung.persona.particle.MutableParticle
import kotlin.math.cos
import kotlin.math.sin

/**
 * The transformation that translates (x, y) by the speed.
 */
class LinearTranslateTransformation : ParticleTransformation {
    override fun transform(particle: MutableParticle, iteration: Long) {
        val speed = particle.instinct.speed
        val angle = particle.instinct.angle
        val horizontalSpeed =
             speed * cos(Math.toRadians(angle.toDouble())).toFloat()
        val verticalSpeed =
            speed * sin(Math.toRadians(angle.toDouble())).toFloat()

        particle.y = particle.y + verticalSpeed
        particle.x = particle.x + horizontalSpeed
    }
}
