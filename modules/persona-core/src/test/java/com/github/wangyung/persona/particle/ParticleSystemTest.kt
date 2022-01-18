package com.github.wangyung.persona.particle

import android.util.Size
import androidx.compose.ui.graphics.Color
import com.github.wangyung.persona.particle.generator.RandomizeParticleGenerator
import com.github.wangyung.persona.particle.generator.parameter.ParticleGeneratorParameters
import com.github.wangyung.persona.particle.mock.NotAliveTransformation
import com.github.wangyung.persona.particle.transformation.ParticleTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class ParticleSystemTest {

    private val mockParameters: ParticleSystemParameters = ParticleSystemParameters()
    private val mockGenerator: RandomizeParticleGenerator = RandomizeParticleGenerator(
        dimension = Size(10, 10),
        parameters = ParticleGeneratorParameters(
            count = 10,
            shapeProvider = { ParticleShape.Circle(color = Color.White, radius = 1) }
        )
    )
    private lateinit var mockTransformation: ParticleTransformation
    private lateinit var particleSystem: DefaultParticleSystem

    @Before
    fun setUp() {
        mockTransformation = mock { }
    }

    @Test
    fun `The particle system is running when it is created`() {
        // given
        particleSystem = DefaultParticleSystem(
            dimension = Size(10, 10),
            parameters = mockParameters,
            generator = mockGenerator,
            transformation = mockTransformation,
            coroutineDispatcher = TestCoroutineDispatcher(),
            autoStart = true,
        )

        // then
        assertTrue(particleSystem.isRunning)
    }

    @Test
    fun `The particle system is not running when stop() is invoked`() {
        // given
        particleSystem = DefaultParticleSystem(
            dimension = Size(10, 10),
            parameters = mockParameters,
            generator = mockGenerator,
            transformation = mockTransformation,
            coroutineDispatcher = TestCoroutineDispatcher(),
            autoStart = true,
        )
        // when
        particleSystem.stop()

        // then
        assertFalse(particleSystem.isRunning)
    }

    @Test
    fun `The count of particles is the same as the count in the given parameter`() {
        // given
        particleSystem = DefaultParticleSystem(
            dimension = Size(10, 10),
            parameters = mockParameters,
            generator = mockGenerator,
            transformation = mockTransformation,
            coroutineDispatcher = TestCoroutineDispatcher(),
            autoStart = true,
        )

        // then
        assertEquals(10, particleSystem.particles.size)
    }

    @Test
    fun `The iteration flow works correctly`() = runBlockingTest {
        val testResult = mutableListOf<Long>()
        // given
        // use default dispatcher to get expected result.
        particleSystem = DefaultParticleSystem(
            dimension = Size(10, 10),
            parameters = mockParameters,
            generator = mockGenerator,
            transformation = mockTransformation,
            autoStart = false,
            coroutineDispatcher = Dispatchers.Default
        )

        // when
        val job = launch {
            particleSystem.start()
            particleSystem.iterationFlow.toList(testResult)
        }

        while (testResult.count() < 10) {
            delay(10)
        }
        particleSystem.stop()

        // then
        assertEquals(10, testResult.count())

        testResult.forEachIndexed { index, value ->
            assertEquals((index + 1).toLong(), value)
        }
        job.cancel()
    }

    @Test
    fun `Reset the particles when restartWhenAllDead is true`() = runBlockingTest {
        // given
        val iterations = mutableListOf<Long>()
        val notAliveTransformation = NotAliveTransformation(notAliveAtIteration = 9L)
        particleSystem = DefaultParticleSystem(
            dimension = Size(10, 10),
            parameters = mockParameters,
            generator = mockGenerator,
            transformation = notAliveTransformation,
            autoStart = true,
            coroutineDispatcher = Dispatchers.Default
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
        particleSystem = DefaultParticleSystem(
            parameters = ParticleSystemParameters(restartWhenAllDead = false),
            generator = mockGenerator,
            dimension = Size(10, 10),
            transformation = notAliveTransformation,
            coroutineDispatcher = TestCoroutineDispatcher(),
            autoStart = true,
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

    @Test
    fun `ParticleSystem factory method should return DefaultParticleSystem`() {
        // given
        val particleSystem = particleSystem(
            dimension = Size(10, 10),
            generator = mockGenerator,
            parameters = mockParameters
        )

        // then
        assertTrue(particleSystem is DefaultParticleSystem)
    }
}
