package com.github.wangyung.persona.json

import android.util.Size
import com.github.wangyung.persona.particle.ParticleShape
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFalse

private class FakeCircleShape(radius: Int = 1) : ParticleShape.Circle {
    override val width: Int = radius * 2
    override val height: Int = radius * 2
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class ParticleSystemFromJsonTest {

    private val jsonString = """
        {
          "name": "Snow",
          "systemParameters": { "fps": 60 },
          "generatorParameters": {
            "count": 25,
            "speedRange": { "from": 1.0, "to": 2.0 },
            "angleRange": { "from": 80.0, "to": 100.0 },
            "sourceEdges": ["TOP"]
          },
          "transformationParameters": {
            "type": "composite",
            "transformations": [
              { "type": "translate", "gravity": 0.1 },
              { "type": "rotation" }
            ]
          }
        }
    """.trimIndent()

    @Test
    fun `Create the particle system from a json string`() {
        // when
        val particleSystem = particleSystemFromJson(
            jsonString = jsonString,
            dimension = Size(100, 100),
            shapeProvider = { FakeCircleShape(radius = 1) },
            autoStart = false,
        )

        // then
        assertFalse(particleSystem.isRunning)
        assertEquals(25, particleSystem.particles.size)
        assertEquals(60, particleSystem.parameters.fps)
        assertEquals(Size(100, 100), particleSystem.dimension)
    }
}
