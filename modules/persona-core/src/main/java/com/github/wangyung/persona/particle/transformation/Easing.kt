package com.github.wangyung.persona.particle.transformation

import kotlin.math.pow

private const val HALF = 0.5f
private const val EASE_IN_OUT_FACTOR = 4f
private const val CUBIC = 3

/**
 * Maps a fraction in [0, 1] to the eased fraction in [0, 1]. It is used by the transformations
 * that interpolate the particle state over a duration, ex: [MoveToTargetTransformation].
 */
fun interface Easing {
    fun ease(fraction: Float): Float

    companion object {
        /**
         * The linear easing, the eased fraction is the same as the given fraction.
         */
        val Linear: Easing = Easing { it }

        /**
         * The cubic easing that accelerates at the beginning and decelerates at the end.
         */
        val EaseInOutCubic: Easing = Easing { fraction ->
            if (fraction < HALF) {
                EASE_IN_OUT_FACTOR * fraction.pow(CUBIC)
            } else {
                1f - (-2f * fraction + 2f).pow(CUBIC) / 2f
            }
        }
    }
}
