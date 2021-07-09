package com.github.wangyung.persona.particle.transformation

import com.github.wangyung.persona.particle.MutableParticle
import com.github.wangyung.persona.particle.Particle

/**
 * The contractor of updating [Particle].
 */
interface ParticleTransformation {
    /**
     * Updates [MutableParticle] at the given iteration.
     */
    fun transform(particle: MutableParticle, iteration: Long)
}
