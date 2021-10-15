package com.github.wangyung.persona.particle.transformation

import com.github.wangyung.persona.particle.MutableParticle
import java.lang.Math.abs

/**
 * The transformation that updates the rotation by the rotational speed. It only modifies the
 * current particle state when everytime the [transform] is invoked.
 * It would have different result even invokes [transform] multiple times with the same iteration.
 */
class LinearRotationTransformation : ParticleTransformation {
    override fun transform(particle: MutableParticle, iteration: Long) {
        with(particle) {
            xRotationWidth -= abs(instinct.xRotationalSpeed)
            if (xRotationWidth < 0) {
                xRotationWidth = instinct.width.toFloat()
            }
            rotation += instinct.zRotationalSpeed
            if (rotation >= 360) {
                rotation -= 360
            }
            scaleY = abs(xRotationWidth / instinct.width - 0.5f) * 2
        }
    }
}
