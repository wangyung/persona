package com.github.wangyung.persona.particle.transformation

import com.github.wangyung.persona.particle.MutableParticle

/**
 * The transformation that updates the rotation by the rotational speed. It only modifies the
 * current particle state when everytime the [transform] is invoked.
 * It would have different result even invokes [transform] multiple times with the same iteration.
 */
class LinearRotationTransformation : ParticleTransformation {
    override fun transform(particle: MutableParticle, iteration: Long) {
        val rotationalSpeed = particle.instinct.rotationalSpeed
        particle.rotation += rotationalSpeed
    }
}
