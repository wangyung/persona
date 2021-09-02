package com.github.wangyung.persona.particle.transformation

import com.github.wangyung.persona.particle.MutableParticle
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class SequenceTransformationTest {

    private lateinit var sequenceTransformation: SequenceTransformation
    lateinit var mockParticle: MutableParticle
    private val mockAlphaTransformation: ParticleTransformation = MockAlphaTransformation()
    private val mockRotationTransformation: ParticleTransformation = MockRotationTransformation()

    @Before
    fun setUp() {
        sequenceTransformation = SequenceTransformation()
        mockParticle = MutableParticle(id = 101)
    }

    @Test
    fun `The particle only be updated by one transformation at the given iteration`() {
        // given
        sequenceTransformation.add(mockAlphaTransformation, duration = 10)
        sequenceTransformation.add(mockRotationTransformation, duration = 20)

        // when
        sequenceTransformation.transform(mockParticle, 5)
        // then
        assertEquals(0.5f, mockParticle.alpha)
        assertEquals(0f, mockParticle.rotation)
        assertEquals(30, sequenceTransformation.duration)

        // when
        sequenceTransformation.transform(mockParticle, 15)

        // then
        assertEquals(0.5f, mockParticle.alpha)
        assertEquals(180f, mockParticle.rotation)
    }

    @Test
    fun `Clear the transformations won't update the particle`() {
        // given
        sequenceTransformation.add(mockAlphaTransformation, duration = 10)
        sequenceTransformation.add(mockRotationTransformation, duration = 20)

        // when
        sequenceTransformation.clear()
        sequenceTransformation.transform(mockParticle, 5)

        // then
        assertEquals(0, sequenceTransformation.duration)
        assertEquals(1f, mockParticle.alpha)
    }

    @Test
    fun `Get correct transformation by the given iteration`() {
        // given
        sequenceTransformation.add(mockAlphaTransformation, duration = 10)
        sequenceTransformation.add(mockRotationTransformation, duration = 20)

        // when
        val transformation1 = sequenceTransformation.getActivateTransformation(9)
        val transformation2 = sequenceTransformation.getActivateTransformation(10)
        val transformation3 = sequenceTransformation.getActivateTransformation(30)

        // then
        assertEquals(0, transformation1?.startFrom)
        assertEquals(10, transformation1?.duration)
        assertEquals(10, transformation2?.startFrom)
        assertEquals(20, transformation2?.duration)
        assertNull(transformation3)
    }

    @Test
    fun `Set the particle isAlive to false when the iteration exceeds the duration`() {
        // given
        sequenceTransformation.add(mockAlphaTransformation, duration = 10)
        // when
        sequenceTransformation.transform(mockParticle, 10)
        // then
        assertFalse(mockParticle.isAlive)
    }

    @Test
    fun `Don't update the particle if iteration is less than 0`() {
        // given
        sequenceTransformation.add(mockAlphaTransformation, duration = 10)
        // when
        sequenceTransformation.transform(mockParticle, -1)
        // then
        assertEquals(1f, mockParticle.alpha)
    }
}
