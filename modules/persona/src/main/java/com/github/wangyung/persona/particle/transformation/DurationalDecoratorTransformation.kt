package com.github.wangyung.persona.particle.transformation

import com.github.wangyung.persona.particle.MutableParticle

internal class DurationalDecoratorTransformation(
    val startFrom: Long,
    override val duration: Long,
    private val particleTransformation: ParticleTransformation
) : ParticleTransformation, Durationable {
    override fun transform(particle: MutableParticle, iteration: Long) {
        if (iteration < startFrom || iteration >= startFrom + duration) {
            return
        }

        particleTransformation.transform(particle, iteration - startFrom)
    }
}
