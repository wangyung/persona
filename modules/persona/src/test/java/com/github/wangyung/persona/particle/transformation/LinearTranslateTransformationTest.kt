package com.github.wangyung.persona.particle.transformation

import com.github.wangyung.persona.particle.Instinct
import com.github.wangyung.persona.particle.MutableParticle
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LinearTranslateTransformationTest {

    lateinit var mockParticle: MutableParticle
    lateinit var linearTranslateTransformation: LinearTranslateTransformation

    @Before
    fun setUp() {
        mockParticle = MutableParticle(id = 200, instinct = Instinct(speed = 10f, angle = 45f))
        linearTranslateTransformation = LinearTranslateTransformation()
    }

    @Test
    fun `The x, y are updated correctly at angle 45`() {
        // when
        mockParticle = MutableParticle(id = 200, instinct = Instinct(speed = 10f, angle = 45f))
        linearTranslateTransformation.transform(mockParticle, 1)

        // then
        assertEquals(7.071068f, mockParticle.x)
        assertEquals(7.071068f, mockParticle.y)
    }

    @Test
    fun `The x, y are updated correctly at angle 90`() {
        // when
        mockParticle = MutableParticle(id = 200, instinct = Instinct(speed = 10f, angle = 90f))
        linearTranslateTransformation.transform(mockParticle, 1)

        // then
        // since cos 90 is not 0, use a small number to verify the result
        assertTrue(mockParticle.x < 0.00001)
        assertEquals(10f, mockParticle.y)
    }
}
