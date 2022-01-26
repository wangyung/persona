package com.github.wangyung.persona.particle

/**
 * The group of particle shapes
 */
interface ParticleShape {
    val width: Int
    val height: Int

    interface Path : ParticleShape

    interface Line : ParticleShape

    interface Image : ParticleShape

    interface Text : ParticleShape

    interface Rectangle : ParticleShape

    interface Circle : ParticleShape

    companion object {
        /**
         * For width and height, if the width or height is [SAME_AS_PARTICLE], drawing the shape by
         * particle's width and height.
         */
        const val SAME_AS_PARTICLE = -1
    }
}
