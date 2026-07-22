package com.github.wangyung.persona.particle.transformation

import org.junit.Test
import kotlin.test.assertEquals

class EasingTest {

    @Test
    fun `The linear easing returns the same fraction`() {
        assertEquals(0f, Easing.Linear.ease(0f))
        assertEquals(0.25f, Easing.Linear.ease(0.25f))
        assertEquals(1f, Easing.Linear.ease(1f))
    }

    @Test
    fun `The ease-in-out cubic easing starts at 0 and ends at 1`() {
        assertEquals(0f, Easing.EaseInOutCubic.ease(0f))
        assertEquals(0.5f, Easing.EaseInOutCubic.ease(0.5f))
        assertEquals(1f, Easing.EaseInOutCubic.ease(1f))
    }

    @Test
    fun `The ease-in-out cubic easing accelerates at the beginning`() {
        assertEquals(0.0625f, Easing.EaseInOutCubic.ease(0.25f))
        assertEquals(0.9375f, Easing.EaseInOutCubic.ease(0.75f))
    }
}
