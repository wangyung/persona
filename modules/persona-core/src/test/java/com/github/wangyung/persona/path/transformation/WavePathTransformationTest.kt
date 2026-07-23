package com.github.wangyung.persona.path.transformation

import com.github.wangyung.persona.particle.ParticlePoint
import com.github.wangyung.persona.path.MorphablePath
import org.junit.Test
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WavePathTransformationTest {

    private val singlePointPath =
        MorphablePath(id = 0, originalPoints = listOf(ParticlePoint(10f, 20f)))

    @Test
    fun `The point is displaced around its original position`() {
        // given
        val transformation = WavePathTransformation(amplitude = 5f, angularSpeed = 0.1f)

        // when the phase is 0 at iteration 0: sin(0) = 0, cos(0) = 1.
        transformation.transform(singlePointPath, 0)

        // then
        assertEquals(10f, singlePointPath.xs[0])
        assertEquals(25f, singlePointPath.ys[0])
    }

    @Test
    fun `The displacement is computed from the original points so it doesn't drift`() {
        // given
        val transformation = WavePathTransformation(amplitude = 5f, angularSpeed = 0.1f)

        // when the transformation is applied multiple times at the same iteration
        transformation.transform(singlePointPath, 7)
        val x = singlePointPath.xs[0]
        val y = singlePointPath.ys[0]
        transformation.transform(singlePointPath, 7)

        // then the result is the same.
        assertEquals(x, singlePointPath.xs[0])
        assertEquals(y, singlePointPath.ys[0])
    }

    @Test
    fun `The displacement is bounded by the amplitude`() {
        // given
        val path = MorphablePath(
            id = 0,
            originalPoints = List(20) { ParticlePoint(it.toFloat(), it.toFloat()) },
        )
        val amplitude = 3f
        val transformation = WavePathTransformation(amplitude = amplitude, angularSpeed = 0.3f)

        // when
        transformation.transform(path, 123)

        // then
        for (index in 0 until path.pointCount) {
            assertTrue(abs(path.xs[index] - path.originalX(index)) <= amplitude)
            assertTrue(abs(path.ys[index] - path.originalY(index)) <= amplitude)
        }
    }

    @Test
    fun `The zero amplitude doesn't change the points`() {
        // given
        val transformation = WavePathTransformation(amplitude = 0f, angularSpeed = 0.5f)

        // when
        transformation.transform(singlePointPath, 42)

        // then
        assertEquals(10f, singlePointPath.xs[0])
        assertEquals(20f, singlePointPath.ys[0])
    }
}
