package com.github.wangyung.persona.particle.transformation

import com.github.wangyung.persona.particle.Instinct
import com.github.wangyung.persona.particle.MutableParticle
import com.github.wangyung.persona.particle.ParticlePoint
import com.github.wangyung.persona.particle.mock.FakeCircleShape
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class MoveToTargetTransformationTest {

    private lateinit var mockParticle: MutableParticle
    private val target = ParticlePoint(100f, 50f)
    private val targetProvider = ParticleTargetProvider { target }

    @Before
    fun setUp() {
        mockParticle = MutableParticle(id = 0, instinct = Instinct(shape = FakeCircleShape()))
    }

    @Test
    fun `The particle moves to the target linearly and stays at the target after the duration`() {
        // given
        val transformation = MoveToTargetTransformation(
            targetProvider = targetProvider,
            duration = 10,
            easing = Easing.Linear,
        )

        // when the transformation is applied at the first time, the start point is captured.
        transformation.transform(mockParticle, 0)
        // then
        assertEquals(0f, mockParticle.x)
        assertEquals(0f, mockParticle.y)

        // when
        transformation.transform(mockParticle, 5)
        // then
        assertEquals(50f, mockParticle.x)
        assertEquals(25f, mockParticle.y)

        // when
        transformation.transform(mockParticle, 10)
        // then
        assertEquals(100f, mockParticle.x)
        assertEquals(50f, mockParticle.y)

        // when the iteration exceeds the duration, the particle stays at the target.
        transformation.transform(mockParticle, 20)
        // then
        assertEquals(100f, mockParticle.x)
        assertEquals(50f, mockParticle.y)
    }

    @Test
    fun `The progress is computed from the iteration of the first transform`() {
        // given
        val transformation = MoveToTargetTransformation(
            targetProvider = targetProvider,
            duration = 10,
            easing = Easing.Linear,
        )

        // when the first transform happens at iteration 5
        transformation.transform(mockParticle, 5)
        transformation.transform(mockParticle, 10)

        // then only half of the duration has passed.
        assertEquals(50f, mockParticle.x)
        assertEquals(25f, mockParticle.y)
    }

    @Test
    fun `The easing shapes the moving progress`() {
        // given
        val transformation = MoveToTargetTransformation(
            targetProvider = targetProvider,
            duration = 4,
            easing = Easing.EaseInOutCubic,
        )

        // when the fraction is 0.25, the eased fraction is 4 * 0.25^3 = 0.0625
        transformation.transform(mockParticle, 0)
        transformation.transform(mockParticle, 1)

        // then
        assertEquals(6.25f, mockParticle.x)
        assertEquals(3.125f, mockParticle.y)
    }

    @Test
    fun `The particle isn't updated when the target is null`() {
        // given
        val transformation = MoveToTargetTransformation(
            targetProvider = { null },
            duration = 10,
        )
        mockParticle.x = 10f
        mockParticle.y = 20f

        // when
        transformation.transform(mockParticle, 5)

        // then
        assertEquals(10f, mockParticle.x)
        assertEquals(20f, mockParticle.y)
    }

    @Test
    fun `The start point is captured again when the particle system restarts`() {
        // given
        val transformation = MoveToTargetTransformation(
            targetProvider = targetProvider,
            duration = 10,
            easing = Easing.Linear,
        )
        transformation.transform(mockParticle, 5)
        transformation.transform(mockParticle, 10)
        assertEquals(50f, mockParticle.x)

        // when the system restarts, the iteration becomes smaller than the captured one.
        mockParticle.x = 0f
        mockParticle.y = 0f
        transformation.transform(mockParticle, 0)

        // then the start point is re-captured and the particle stays at the new start point.
        assertEquals(0f, mockParticle.x)
        assertEquals(0f, mockParticle.y)

        // when
        transformation.transform(mockParticle, 5)
        // then
        assertEquals(50f, mockParticle.x)
        assertEquals(25f, mockParticle.y)
    }

    @Test
    fun `The transformation can be composed in the SequenceTransformation`() {
        // given a sequence that holds the particle for 10 iterations then moves it for 10.
        val sequenceTransformation = SequenceTransformation().apply {
            add(ParticleTransformation { _, _ -> }, duration = 10)
            add(
                MoveToTargetTransformation(
                    targetProvider = targetProvider,
                    duration = 10,
                    easing = Easing.Linear,
                ),
                duration = 10,
            )
        }

        // when the hold transformation is active.
        sequenceTransformation.transform(mockParticle, 5)
        // then
        assertEquals(0f, mockParticle.x)

        // when the move transformation becomes active.
        sequenceTransformation.transform(mockParticle, 10)
        sequenceTransformation.transform(mockParticle, 15)

        // then the particle moved half way to the target.
        assertEquals(50f, mockParticle.x)
        assertEquals(25f, mockParticle.y)
    }
}
