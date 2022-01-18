package com.github.wangyung.persona.particle.transformation

import com.github.wangyung.persona.particle.MutableParticle
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class CompositeTransformationTest {

    lateinit var compositeTransformation: CompositeTransformation
    lateinit var mockParticle: MutableParticle
    private val mockAlphaTransformation: ParticleTransformation = MockAlphaTransformation()
    private val mockRotationTransformation: ParticleTransformation = MockRotationTransformation()

    @Before
    fun setUp() {
        compositeTransformation = CompositeTransformation(listOf(
            mockAlphaTransformation,
            mockRotationTransformation
        ))
        mockParticle = MutableParticle(id = 100)
    }

    @Test
    fun `Create CompositeTransformation with a transformation list`() {
        // when
        compositeTransformation.transform(mockParticle, 1)

        // then
        assertEquals(0.5f, mockParticle.alpha)
        assertEquals(180f, mockParticle.rotation)
    }

    @Test
    fun `Add multiple transformations and work correctly`() {
        // given
        val compositeTransformation = CompositeTransformation()
        compositeTransformation.add(mockAlphaTransformation)
        compositeTransformation.add(mockRotationTransformation)

        // when
        compositeTransformation.transform(mockParticle, 1)

        // then
        assertEquals(0.5f, mockParticle.alpha)
        assertEquals(180f, mockParticle.rotation)
    }

    @Test
    fun `Remove a transformation and work correctly`() {
        // when
        compositeTransformation.remove(mockAlphaTransformation)
        compositeTransformation.transform(mockParticle, 1)

        // then
        assertEquals(1f, mockParticle.alpha)
        assertEquals(180f, mockParticle.rotation)
    }

    @Test
    fun `Clear the CompositeTransformation and it should not change particle`() {
        // when
        compositeTransformation.clear()
        compositeTransformation.transform(mockParticle, 1)

        // then
        assertEquals(1f, mockParticle.alpha)
        assertEquals(0f, mockParticle.rotation)
    }
}
