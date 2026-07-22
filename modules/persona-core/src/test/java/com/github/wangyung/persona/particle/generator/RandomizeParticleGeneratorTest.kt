package com.github.wangyung.persona.particle.generator

import android.util.Size
import com.github.wangyung.persona.particle.generator.parameter.ParticleGeneratorParameters
import com.github.wangyung.persona.particle.generator.parameter.SourceEdge
import com.github.wangyung.persona.particle.mock.FakeCircleShape
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class RandomizeParticleGeneratorTest {

    lateinit var randomizeParticleGenerator: RandomizeParticleGenerator

    @Before
    fun setUp() {
        randomizeParticleGenerator = RandomizeParticleGenerator(
            parameters = ParticleGeneratorParameters(
                count = 10,
                randomizeInitialXY = true,
            ),
            dimension = Size(10, 10),
            shapeProvider = { FakeCircleShape(radius = 1) },
        )
    }

    @Test
    fun `The properties of the particle are different after reset`() {
        // given
        val particles = randomizeParticleGenerator.createParticles()
        val x = particles[0].x

        // when
        randomizeParticleGenerator.resetParticle(particle = particles[0])

        // then
        assertNotEquals(x, particles[0].x)
    }

    @Test
    fun `The y would be the same after reset if randomizeInitialXY is false with source edge top and bottom`() {
        // given
        randomizeParticleGenerator = RandomizeParticleGenerator(
            parameters = ParticleGeneratorParameters(
                count = 10,
                randomizeInitialXY = false,
                sourceEdges = setOf(SourceEdge.TOP, SourceEdge.BOTTOM),
            ),
            dimension = Size(10, 10),
            shapeProvider = { FakeCircleShape(radius = 1) },
        )
        val particles = randomizeParticleGenerator.createParticles()
        // when
        randomizeParticleGenerator.resetParticle(particle = particles[0])

        // then
        assertTrue(particles[0].y == 0f || particles[0].y == 10f)
    }

    @Test
    fun `The x would be the same after reset if randomizeInitialXY is false with source edge left and right`() {
        // given
        randomizeParticleGenerator = RandomizeParticleGenerator(
            parameters = ParticleGeneratorParameters(
                count = 10,
                randomizeInitialXY = false,
                sourceEdges = setOf(SourceEdge.LEFT, SourceEdge.RIGHT),
            ),
            dimension = Size(10, 10),
            shapeProvider = { FakeCircleShape(radius = 1) },
        )
        val particles = randomizeParticleGenerator.createParticles()

        // when
        randomizeParticleGenerator.resetParticle(particle = particles[0])

        // then
        assertTrue(particles[0].x == 0f || particles[0].x == 10f)
    }
}
