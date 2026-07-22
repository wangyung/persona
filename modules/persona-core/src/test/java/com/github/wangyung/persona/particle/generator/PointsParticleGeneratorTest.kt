package com.github.wangyung.persona.particle.generator

import com.github.wangyung.persona.particle.ParticlePoint
import com.github.wangyung.persona.particle.mock.FakeCircleShape
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PointsParticleGeneratorTest {

    private val points = listOf(
        ParticlePoint(1f, 2f),
        ParticlePoint(3f, 4f),
        ParticlePoint(5f, 6f),
    )

    private val generator = PointsParticleGenerator(
        points = points,
        shapeProvider = { FakeCircleShape(radius = 2) },
    )

    @Test
    fun `One particle is created at each point`() {
        // when
        val particles = generator.createParticles()

        // then
        assertEquals(points.count(), particles.count())
        particles.forEachIndexed { index, particle ->
            assertEquals(points[index].x, particle.x)
            assertEquals(points[index].y, particle.y)
            assertEquals(4, particle.instinct.width)
            assertEquals(4, particle.instinct.height)
        }
    }

    @Test
    fun `The particle ids are stable across generations`() {
        // when
        val firstGeneration = generator.createParticles()
        val secondGeneration = generator.createParticles()

        // then
        assertEquals(
            firstGeneration.map { it.id },
            secondGeneration.map { it.id },
        )
        assertEquals(listOf(0L, 1L, 2L), firstGeneration.map { it.id })
    }

    @Test
    fun `Resetting the particle moves it back to its original point`() {
        // given
        val particle = generator.createParticles()[1]
        particle.x = 100f
        particle.y = 100f

        // when
        generator.resetParticle(particle)

        // then
        assertEquals(points[1].x, particle.x)
        assertEquals(points[1].y, particle.y)
    }

    @Test
    fun `The start offset is picked from the given range`() {
        // given
        val generatorWithOffset = PointsParticleGenerator(
            points = points,
            shapeProvider = { FakeCircleShape() },
            startOffsetRange = 5..5,
        )

        // when
        val particles = generatorWithOffset.createParticles()

        // then
        particles.forEach { assertEquals(5, it.instinct.startOffset) }
    }

    @Test
    fun `Creating the generator with empty points throws an exception`() {
        assertFailsWith<IllegalArgumentException> {
            PointsParticleGenerator(points = emptyList(), shapeProvider = { FakeCircleShape() })
        }
    }
}
