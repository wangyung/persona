package com.github.wangyung.persona.particle

/**
 * The parameters for [ParticleSystem].
 *
 * @property fps The frequency of updating the [ParticleSystem] per second.
 * @property autoResetParticles If true, then particle system will reset the particle properties
 * when the particle is out of bound.
 * @property restartWhenAllDead If true, then particle system will restart automatically after all
 * particles are not alive.
 */
data class ParticleSystemParameters(
    val fps: Int = 60,
    val autoResetParticles: Boolean = true,
    val restartWhenAllDead: Boolean = true,
)
