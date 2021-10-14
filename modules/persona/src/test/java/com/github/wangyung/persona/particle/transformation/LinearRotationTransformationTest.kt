package com.github.wangyung.persona.particle.transformation

import com.github.wangyung.persona.particle.Instinct
import com.github.wangyung.persona.particle.MutableParticle
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class LinearRotationTransformationTest {

    lateinit var mockParticle: MutableParticle
    lateinit var linearRotationTransformation: LinearRotationTransformation

    @Before
    fun setUp() {
        mockParticle = MutableParticle(id = 300, instinct = Instinct(zRotationalSpeed = 10f))
        linearRotationTransformation = LinearRotationTransformation()
    }

    @Test
    fun `Update the rotation of particle correctly`() {
        // when
        linearRotationTransformation.transform(mockParticle, 1)

        // then
        assertEquals(10f, mockParticle.rotation)
    }
}
