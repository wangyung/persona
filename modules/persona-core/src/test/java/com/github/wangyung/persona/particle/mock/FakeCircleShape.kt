package com.github.wangyung.persona.particle.mock

import com.github.wangyung.persona.particle.ParticleShape

/**
 * A fake circle shape for testing. The core doesn't provide any concrete [ParticleShape], the
 * shapes are renderer specific.
 */
class FakeCircleShape(radius: Int = 1) : ParticleShape.Circle {
    override val width: Int = radius * 2
    override val height: Int = radius * 2
}
