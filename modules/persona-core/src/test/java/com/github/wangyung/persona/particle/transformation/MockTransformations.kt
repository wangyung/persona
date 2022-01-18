package com.github.wangyung.persona.particle.transformation

import com.github.wangyung.persona.particle.MutableParticle

internal class MockAlphaTransformation : ParticleTransformation {
    override fun transform(particle: MutableParticle, iteration: Long) {
        particle.alpha = 0.5f
    }
}

internal class MockRotationTransformation : ParticleTransformation {
    override fun transform(particle: MutableParticle, iteration: Long) {
        particle.rotation = 180f
    }
}
