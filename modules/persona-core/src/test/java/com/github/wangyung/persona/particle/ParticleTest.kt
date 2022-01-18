package com.github.wangyung.persona.particle

import org.junit.Test
import kotlin.test.assertEquals

class ParticleTest {
    @Test
    fun `Create particle correctly via particleOf`() {
        // given
        val particle =
            particleOf(id = 101, initialIteration = 100, x = 100f, y = 101f, rotation = 90f)

        // then
        assertEquals(101, particle.id)
        assertEquals(100, particle.initialIteration)
        assertEquals(100f, particle.x)
        assertEquals(101f, particle.y)
        assertEquals(90f, particle.rotation)
    }
}
