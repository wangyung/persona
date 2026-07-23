package com.github.wangyung.persona.path.transformation

import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import com.github.wangyung.persona.path.MorphablePath
import kotlin.math.cos
import kotlin.math.sin

private const val TWO_PI = (Math.PI * 2).toFloat()

/**
 * The transformation that displaces every point of the path around its original position with a
 * traveling sine wave, so the shape looks like it is wobbling.
 *
 * The wave phase is distributed along the points by [waveCount] full periods, so for a closed
 * path the displacement is seamless where the last point connects back to the first one.
 *
 * @property amplitude The maximum displacement of a point in pixels.
 * @property angularSpeed How fast the wave travels, in radians per iteration.
 * @property waveCount The number of the full wave periods along the path.
 */
class WavePathTransformation(
    @FloatRange(from = 0.0)
    private val amplitude: Float,
    private val angularSpeed: Float,
    @IntRange(from = 1)
    private val waveCount: Int = 3,
) : PathTransformation {

    override fun transform(path: MorphablePath, iteration: Long) {
        val time = iteration * angularSpeed
        val phaseStep = TWO_PI * waveCount / path.pointCount
        for (index in 0 until path.pointCount) {
            val phase = time + index * phaseStep
            path.xs[index] = path.originalX(index) + amplitude * sin(phase)
            path.ys[index] = path.originalY(index) + amplitude * cos(phase)
        }
    }
}
