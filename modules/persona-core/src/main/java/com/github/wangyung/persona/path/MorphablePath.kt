package com.github.wangyung.persona.path

import com.github.wangyung.persona.particle.ParticlePoint

/**
 * A path whose points can be moved while keeping the original geometry, so the shape can be
 * morphed and restored. It is the basic unit of the [PathSystem]. The typical usage is sampling
 * the outline of a text (or any shape) into points and mutating the points every iteration by a
 * [com.github.wangyung.persona.path.transformation.PathTransformation].
 *
 * The current points are exposed as [xs]/[ys] arrays instead of a list of points so the
 * transformations and renderers don't allocate objects in the per-frame hot loop.
 *
 * @property id The identifier of the path.
 * @property isClosed True if the last point connects back to the first point when drawing.
 */
class MorphablePath(
    val id: Long,
    originalPoints: List<ParticlePoint>,
    val isClosed: Boolean = true,
) {
    init {
        require(originalPoints.isNotEmpty()) { "originalPoints cannot be empty" }
    }

    /**
     * The number of the points in the path.
     */
    val pointCount: Int = originalPoints.size

    private val originalXs: FloatArray = FloatArray(pointCount) { originalPoints[it].x }
    private val originalYs: FloatArray = FloatArray(pointCount) { originalPoints[it].y }

    /**
     * The current x coordinates of the points. The transformations mutate them in place.
     */
    val xs: FloatArray = originalXs.copyOf()

    /**
     * The current y coordinates of the points. The transformations mutate them in place.
     */
    val ys: FloatArray = originalYs.copyOf()

    /**
     * Returns the original (unmorphed) x coordinate of the point at [index].
     */
    fun originalX(index: Int): Float = originalXs[index]

    /**
     * Returns the original (unmorphed) y coordinate of the point at [index].
     */
    fun originalY(index: Int): Float = originalYs[index]

    /**
     * Restores all points to the original geometry.
     */
    fun reset() {
        originalXs.copyInto(xs)
        originalYs.copyInto(ys)
    }
}
