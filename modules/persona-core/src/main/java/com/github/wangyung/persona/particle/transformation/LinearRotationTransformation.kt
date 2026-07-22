package com.github.wangyung.persona.particle.transformation

import com.github.wangyung.persona.particle.MutableParticle
import java.lang.Math.abs

private const val FULL_ROTATION_DEGREE = 360f
private const val HALF_RATIO = 0.5f

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
            if (rotation >= FULL_ROTATION_DEGREE) {
                rotation -= FULL_ROTATION_DEGREE
            }
            scaleY = abs(xRotationWidth / instinct.width - HALF_RATIO) * 2
        }
    }
}
