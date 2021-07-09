package com.github.wangyung.persona.particle

import androidx.compose.ui.graphics.Color
import com.github.wangyung.persona.particle.generator.RandomizeParticleGenerator
import com.github.wangyung.persona.particle.generator.parameter.RandomizeParticleGeneratorParameters
import com.github.wangyung.persona.particle.mock.NotAliveTransformation
import com.github.wangyung.persona.particle.transformation.ParticleTransformation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class ParticleSystemTest {

    private val mockParameters: ParticleSystemParameters = ParticleSystemParameters()
    private val mockGenerator: RandomizeParticleGenerator = RandomizeParticleGenerator(
        parameters = RandomizeParticleGeneratorParameters(
            count = 10,
            shapeProvider = { ParticleShape.Circle(color = Color.White, radius = 1) }
        ),
        width = 10,
        height = 10,
    )
    private lateinit var mockTransformation: ParticleTransformation
    private lateinit var particleSystem: ParticleSystem

    @Before
    fun setUp() {
        mockTransformation = mock {  }
    }

    @Test
    fun `The particle system is running when it is created`() {
        // given
        particleSystem = ParticleSystem(
            width = 10,
            height = 10,
            parameters = mockParameters,
            generator = mockGenerator,
            transformation = mockTransformation,
            coroutineDispatcher = TestCoroutineDispatcher()
        )

        // then
        assertTrue(particleSystem.isRunning)
    }

    @Test
    fun `The particle system is not running when stop() is invoked`() {
        // given
        particleSystem = ParticleSystem(
            width = 10,
            height = 10,
            parameters = mockParameters,
            generator = mockGenerator,
            transformation = mockTransformation,
            coroutineDispatcher = TestCoroutineDispatcher()
        )
        // when
        particleSystem.stop()

        // then
        assertFalse(particleSystem.isRunning)
    }

    @Test
    fun `The count of particles is the same as the count in the given parameter`() {
        // given
        particleSystem = ParticleSystem(
            width = 10,
            height = 10,
            parameters = mockParameters,
            generator = mockGenerator,
            transformation = mockTransformation,
            coroutineDispatcher = TestCoroutineDispatcher()
        )

        // then
        assertEquals(10, particleSystem.particles.size)
    }

    @Test
    fun `The iteration flow works correctly`() = runBlockingTest {
        val testResult = mutableListOf<Long>()
        // given
        // use default dispatcher to get expected result.
        particleSystem = ParticleSystem(
            width = 10,
            height = 10,
            parameters = mockParameters,
            generator = mockGenerator,
            transformation = mockTransformation,
        )

        // when
        val job = launch {
            particleSystem.iterationFlow.toList(testResult)
        }

        while (testResult.count() < 10) {
            delay( 10)
        }
        particleSystem.stop()

        // then
        testResult.forEachIndexed { index, value ->
            assertEquals((index + 1).toLong(), value)
        }
        assertEquals(10, testResult.count())
        job.cancel()
    }

    @Test
    fun `Reset the particles when restartWhenAllDead is true`() = runBlockingTest {
        // given
        val iterations = mutableListOf<Long>()
        val notAliveTransformation = NotAliveTransformation(notAliveAtIteration = 9L)
        particleSystem = ParticleSystem(
            width = 10,
            height = 10,
            parameters = mockParameters,
            generator = mockGenerator,
            transformation = notAliveTransformation,
        )
        val originalParticles = particleSystem.particles

        // when
        val job = launch {
            particleSystem.iterationFlow.toList(iterations)
        }

        while (iterations.count() < 11) {
            delay(10)
        }
        val newParticles = particleSystem.particles

        // then
        assertTrue(particleSystem.isRunning)
        assertNotEquals(originalParticles, newParticles)
        job.cancel()
    }

    @Test
    fun `The particle system would be stopped when restartWhenAllDead is false`() = runBlockingTest {
        // given
        val iterations = mutableListOf<Long>()
        val notAliveTransformation = NotAliveTransformation(notAliveAtIteration = 0L)
        particleSystem = ParticleSystem(
            parameters = ParticleSystemParameters(restartWhenAllDead = false),
            generator = mockGenerator,
            width = 10,
            height = 10,
            transformation = notAliveTransformation,
            coroutineDispatcher = TestCoroutineDispatcher()
        )

        // when
        val job = launch {
            particleSystem.iterationFlow.toList(iterations)
        }
        // then
        assertFalse(particleSystem.isRunning)
        assertEquals(10, particleSystem.notAliveParticleIds.count())
        job.cancel()
    }
}
