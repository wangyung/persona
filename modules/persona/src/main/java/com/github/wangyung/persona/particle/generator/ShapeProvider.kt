package com.github.wangyung.persona.particle.generator

import com.github.wangyung.persona.particle.ParticleShape

/**
 * The interface to provide the [ParticleShape] instance.
 */
fun interface ShapeProvider {
    fun provide(): ParticleShape
}
