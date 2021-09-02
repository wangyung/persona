package com.github.wangyung.persona.particle.generator

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class RandomExtTest {
    @Test
    fun `Get nextFloat correctly in the valid float range`() {
        val randomFloat = (5f..10f).nextFloat()
        assertTrue(randomFloat >= 5f)
        assertTrue(randomFloat <= 10f)
    }

    @Test
    fun `Throws an exception when the range is invalid`() {
        val floatRange = 10f.rangeTo(5f)
        assertFailsWith<IllegalArgumentException> { floatRange.nextFloat() }
    }

    @Test
    fun `When the range is infinite`() {
        val randomFloat = (Float.NEGATIVE_INFINITY..Float.POSITIVE_INFINITY).nextFloat()
        assertEquals(Float.NaN, randomFloat)
    }
}
