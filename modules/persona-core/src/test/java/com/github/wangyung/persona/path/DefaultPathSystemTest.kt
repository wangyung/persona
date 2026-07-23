package com.github.wangyung.persona.path

import com.github.wangyung.persona.particle.ParticlePoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultPathSystemTest {

    private fun createPath() =
        MorphablePath(id = 0, originalPoints = listOf(ParticlePoint(0f, 0f)))

    @Test
    fun `The system updates the iteration and applies the transformation`() = runTest {
        // given a system that ticks every 20ms.
        var transformedCount = 0
        val pathSystem = pathSystem(
            paths = listOf(createPath()),
            transformation = { _, _ -> transformedCount++ },
            parameters = PathSystemParameters(fps = 50),
            autoStart = false,
            coroutineDispatcher = StandardTestDispatcher(testScheduler),
        )
        assertFalse(pathSystem.isRunning)

        // when
        pathSystem.start()
        advanceTimeBy(105)

        // then
        assertTrue(pathSystem.isRunning)
        assertTrue(pathSystem.iterationFlow.value >= 5)
        assertTrue(transformedCount >= 5)

        pathSystem.stop()
        advanceTimeBy(100)
    }

    @Test
    fun `The system stops updating the iteration after stop`() = runTest {
        // given
        val pathSystem = pathSystem(
            paths = listOf(createPath()),
            transformation = { _, _ -> },
            parameters = PathSystemParameters(fps = 50),
            autoStart = true,
            coroutineDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceTimeBy(105)

        // when
        pathSystem.stop()
        advanceTimeBy(100)
        val iterationAfterStop = pathSystem.iterationFlow.value

        // then
        assertFalse(pathSystem.isRunning)
        advanceTimeBy(200)
        assertEquals(iterationAfterStop, pathSystem.iterationFlow.value)
    }
}
