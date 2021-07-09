package com.github.wangyung.persona.particle.transformation

import com.github.wangyung.persona.particle.MutableParticle

/**
 * The transformation that updates the rotation by the rotational speed.
 */
class LinearRotationTransformation : ParticleTransformation {
    override fun transform(particle: MutableParticle, iteration: Long) {
        val rotationalSpeed = particle.instinct.rotationalSpeed
        particle.rotation += rotationalSpeed
    }
}
