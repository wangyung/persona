package com.github.wangyung.persona.particle

import android.util.Size
import com.github.wangyung.persona.particle.generator.RandomizeParticleGenerator
import com.github.wangyung.persona.particle.generator.parameter.ParticleGeneratorParameters
import com.github.wangyung.persona.particle.mock.FakeCircleShape
import com.github.wangyung.persona.particle.mock.NotAliveTransformation
import com.github.wangyung.persona.particle.transformation.ParticleTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.withTimeout
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

private const val WAIT_TIMEOUT_MS = 5000L

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class ParticleSystemTest {

    private val mockParameters: ParticleSystemParameters = ParticleSystemParameters()
    private val mockGenerator: RandomizeParticleGenerator = RandomizeParticleGenerator(
        dimension = Size(10, 10),
        parameters = ParticleGeneratorParameters(count = 10),
        shapeProvider = { FakeCircleShape(radius = 1) },
    )
    private lateinit var mockTransformation: ParticleTransformation
    private lateinit var particleSystem: DefaultParticleSystem

    @Before
    fun setUp() {
        mockTransformation = ParticleTransformation { _, _ -> }
    }

    @Test
    fun `The particle system is running when it is created`() {
        // given
        particleSystem = DefaultParticleSystem(
            dimension = Size(10, 10),
            parameters = mockParameters,
            generator = mockGenerator,
            transformation = mockTransformation,
            coroutineDispatcher = UnconfinedTestDispatcher(),
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
            coroutineDispatcher = UnconfinedTestDispatcher(),
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
            coroutineDispatcher = UnconfinedTestDispatcher(),
            autoStart = true,
        )

        // then
        assertEquals(10, particleSystem.particles.size)
    }

    @Test
    fun `The iteration flow works correctly`() = runBlocking {
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
        particleSystem.start()
        val testResult = withTimeout(WAIT_TIMEOUT_MS) {
            particleSystem.iterationFlow.take(10).toList()
        }
        particleSystem.stop()

        // then
        assertEquals(10, testResult.count())
        // The state flow may conflate the emissions, so only verify the iterations are
        // monotonically increasing.
        testResult.zipWithNext().forEach { (previous, next) ->
            assertTrue(next > previous)
        }
    }

    @Test
    fun `Reset the particles when restartWhenAllDead is true`() = runBlocking {
        // given
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
        // All particles are dead at iteration 9, then the system restarts and generates the new
        // particles.
        withTimeout(WAIT_TIMEOUT_MS) {
            while (particleSystem.particles == originalParticles) {
                delay(10)
            }
        }

        // then
        assertTrue(particleSystem.isRunning)
        assertNotEquals(originalParticles, particleSystem.particles)
    }

    @Test
    fun `The particle system would be stopped when restartWhenAllDead is false`() {
        // given
        val notAliveTransformation = NotAliveTransformation(notAliveAtIteration = 0L)

        // when
        particleSystem = DefaultParticleSystem(
            parameters = ParticleSystemParameters(restartWhenAllDead = false),
            generator = mockGenerator,
            dimension = Size(10, 10),
            transformation = notAliveTransformation,
            coroutineDispatcher = UnconfinedTestDispatcher(),
            autoStart = true,
        )

        // then
        assertFalse(particleSystem.isRunning)
        assertEquals(10, particleSystem.notAliveParticleIds.count())
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
