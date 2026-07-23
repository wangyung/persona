package com.github.wangyung.persona.path

import com.github.wangyung.persona.particle.ParticlePoint
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MorphablePathTest {

    private val points = listOf(
        ParticlePoint(1f, 2f),
        ParticlePoint(3f, 4f),
        ParticlePoint(5f, 6f),
    )

    @Test
    fun `The current points are initialized with the original points`() {
        // when
        val path = MorphablePath(id = 0, originalPoints = points)

        // then
        assertEquals(points.count(), path.pointCount)
        points.forEachIndexed { index, point ->
            assertEquals(point.x, path.xs[index])
            assertEquals(point.y, path.ys[index])
            assertEquals(point.x, path.originalX(index))
            assertEquals(point.y, path.originalY(index))
        }
    }

    @Test
    fun `Resetting the path restores the original points`() {
        // given
        val path = MorphablePath(id = 0, originalPoints = points)
        path.xs[0] = 100f
        path.ys[0] = 100f

        // when
        path.reset()

        // then
        assertEquals(points[0].x, path.xs[0])
        assertEquals(points[0].y, path.ys[0])
    }

    @Test
    fun `Mutating the current points doesn't change the original points`() {
        // given
        val path = MorphablePath(id = 0, originalPoints = points)

        // when
        path.xs[1] = 100f

        // then
        assertEquals(points[1].x, path.originalX(1))
    }

    @Test
    fun `Creating the path with empty points throws an exception`() {
        assertFailsWith<IllegalArgumentException> {
            MorphablePath(id = 0, originalPoints = emptyList())
        }
    }
}
