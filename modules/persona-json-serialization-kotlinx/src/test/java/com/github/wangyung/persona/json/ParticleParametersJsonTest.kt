package com.github.wangyung.persona.json

import com.github.wangyung.persona.json.model.ParticleParameters
import com.github.wangyung.persona.particle.ParticleSystemParameters
import com.github.wangyung.persona.particle.generator.parameter.InitialConstraints
import com.github.wangyung.persona.particle.generator.parameter.ParticleGeneratorParameters
import com.github.wangyung.persona.particle.generator.parameter.SourceEdge
import com.github.wangyung.persona.particle.transformation.BlinkTransformationParameters
import com.github.wangyung.persona.particle.transformation.CompositeTransformationParameters
import com.github.wangyung.persona.particle.transformation.RotationTransformationParameters
import com.github.wangyung.persona.particle.transformation.ScaleAndFadeTransformationParameters
import com.github.wangyung.persona.particle.transformation.ScaleTransformationParameters
import com.github.wangyung.persona.particle.transformation.SequenceTransformationParameters
import com.github.wangyung.persona.particle.transformation.SineWaveTranslateTransformationParameters
import com.github.wangyung.persona.particle.transformation.TranslateTransformationParameters
import kotlinx.serialization.SerializationException
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ParticleParametersJsonTest {

    private val generatorParameters = ParticleGeneratorParameters(
        count = 125,
        particleWidthRange = 1..2,
        particleHeightRange = 5..15,
        randomizeInitialXY = false,
        speedRange = 1f..2f,
        scaleRange = 0.5f..1.5f,
        angleRange = 80f..100f,
        xRotationalSpeedRange = 0.1f..0.5f,
        zRotationalSpeedRange = 0.2f..0.4f,
        startOffsetRange = 0..60,
        sourceEdges = setOf(SourceEdge.TOP, SourceEdge.RIGHT),
        constraints = listOf(InitialConstraints(limitRange = 0.3f..0.7f)),
    )

    @Test
    fun `Serialize and deserialize the particle parameters correctly`() {
        // given
        val parameters = ParticleParameters(
            name = "Snow",
            systemParameters = ParticleSystemParameters(
                fps = 30,
                autoResetParticles = false,
                restartWhenAllDead = false,
            ),
            generatorParameters = generatorParameters,
            transformationParameters = TranslateTransformationParameters(gravity = 0.15f),
        )

        // when
        val decoded = particleParametersFromJson(parameters.toJsonString())

        // then
        assertEquals("Snow", decoded.name)
        assertEquals(30, decoded.systemParameters.fps)
        assertEquals(false, decoded.systemParameters.autoResetParticles)
        assertEquals(false, decoded.systemParameters.restartWhenAllDead)
        assertEquals(125, decoded.generatorParameters.count)
        assertEquals(1..2, decoded.generatorParameters.particleWidthRange)
        assertEquals(5..15, decoded.generatorParameters.particleHeightRange)
        assertEquals(false, decoded.generatorParameters.randomizeInitialXY)
        assertEquals(1f..2f, decoded.generatorParameters.speedRange)
        assertEquals(0.5f..1.5f, decoded.generatorParameters.scaleRange)
        assertEquals(80f..100f, decoded.generatorParameters.angleRange)
        assertEquals(0.1f..0.5f, decoded.generatorParameters.xRotationalSpeedRange)
        assertEquals(0.2f..0.4f, decoded.generatorParameters.zRotationalSpeedRange)
        assertEquals(0..60, decoded.generatorParameters.startOffsetRange)
        assertEquals(
            setOf(SourceEdge.TOP, SourceEdge.RIGHT),
            decoded.generatorParameters.sourceEdges
        )
        val constraints = decoded.generatorParameters.constraints
        assertEquals(1, constraints?.size)
        assertEquals(0.3f..0.7f, (constraints?.get(0) as InitialConstraints).limitRange)

        val transformation = decoded.transformationParameters
        assertTrue(transformation is TranslateTransformationParameters)
        assertEquals(0.15f, transformation.gravity)
    }

    @Test
    fun `Serialize and deserialize the nested transformation parameters correctly`() {
        // given
        val parameters = ParticleParameters(
            name = "Sakura",
            systemParameters = ParticleSystemParameters(),
            generatorParameters = ParticleGeneratorParameters(count = 40),
            transformationParameters = SequenceTransformationParameters(
                steps = listOf(
                    SequenceTransformationParameters.Step(
                        parameters = CompositeTransformationParameters(
                            parameters = listOf(
                                TranslateTransformationParameters(gravity = 0.1f),
                                RotationTransformationParameters(),
                            )
                        ),
                        duration = 40L,
                    ),
                    SequenceTransformationParameters.Step(
                        parameters = TranslateTransformationParameters(),
                        duration = 30L,
                    ),
                )
            ),
        )

        // when
        val decoded = particleParametersFromJson(parameters.toJsonString())

        // then
        val sequence = decoded.transformationParameters
        assertTrue(sequence is SequenceTransformationParameters)
        assertEquals(2, sequence.steps.size)
        assertEquals(40L, sequence.steps[0].duration)

        val composite = sequence.steps[0].parameters
        assertTrue(composite is CompositeTransformationParameters)
        assertEquals(2, composite.parameters.size)
        val translate = composite.parameters[0]
        assertTrue(translate is TranslateTransformationParameters)
        assertEquals(0.1f, translate.gravity)
        assertTrue(composite.parameters[1] is RotationTransformationParameters)
        assertTrue(sequence.steps[1].parameters is TranslateTransformationParameters)
    }

    @Test
    fun `Serialize and deserialize the blink, sineWaveTranslate, scale and scaleAndFade correctly`() {
        // given
        val parameters = ParticleParameters(
            name = "AllTransformations",
            systemParameters = ParticleSystemParameters(),
            generatorParameters = ParticleGeneratorParameters(count = 10),
            transformationParameters = CompositeTransformationParameters(
                parameters = listOf(
                    BlinkTransformationParameters(frequencyFactorRange = 0.5f..2f),
                    SineWaveTranslateTransformationParameters(
                        frequencyFactor = 3f,
                        amplitude = 4f,
                    ),
                    ScaleTransformationParameters(xDelta = 0.1f, yDelta = 0.2f),
                    ScaleAndFadeTransformationParameters(
                        xDelta = 0.3f,
                        yDelta = 0.4f,
                        alphaDelta = 0.5f,
                    ),
                )
            ),
        )

        // when
        val decoded = particleParametersFromJson(parameters.toJsonString())

        // then
        val composite = decoded.transformationParameters
        assertTrue(composite is CompositeTransformationParameters)
        assertEquals(4, composite.parameters.size)

        val blink = composite.parameters[0]
        assertTrue(blink is BlinkTransformationParameters)
        assertEquals(0.5f..2f, blink.frequencyFactorRange)

        val sineWave = composite.parameters[1]
        assertTrue(sineWave is SineWaveTranslateTransformationParameters)
        assertEquals(3f, sineWave.frequencyFactor)
        assertEquals(4f, sineWave.amplitude)

        val scale = composite.parameters[2]
        assertTrue(scale is ScaleTransformationParameters)
        assertEquals(0.1f, scale.xDelta)
        assertEquals(0.2f, scale.yDelta)

        val scaleAndFade = composite.parameters[3]
        assertTrue(scaleAndFade is ScaleAndFadeTransformationParameters)
        assertEquals(0.3f, scaleAndFade.xDelta)
        assertEquals(0.4f, scaleAndFade.yDelta)
        assertEquals(0.5f, scaleAndFade.alphaDelta)
    }

    @Test
    fun `Deserialize the new transformation types from a json string`() {
        // given
        val jsonString = """
            {
              "name": "TwinkleStar",
              "systemParameters": {},
              "generatorParameters": { "count": 250 },
              "transformationParameters": {
                "type": "blink",
                "frequencyFactorRange": { "from": 0.5, "to": 2.0 }
              }
            }
        """.trimIndent()

        // when
        val decoded = particleParametersFromJson(jsonString)

        // then
        val blink = decoded.transformationParameters
        assertTrue(blink is BlinkTransformationParameters)
        assertEquals(0.5f..2f, blink.frequencyFactorRange)
    }

    @Test
    fun `Deserialize the particle parameters from a json string`() {
        // given
        val jsonString = """
            {
              "name": "Rain",
              "systemParameters": { "fps": 30 },
              "generatorParameters": {
                "count": 400,
                "particleWidthRange": { "from": 1, "to": 2 },
                "particleHeightRange": { "from": 5, "to": 15 },
                "speedRange": { "from": 10.0, "to": 30.0 },
                "angleRange": { "from": 85.0, "to": 95.0 },
                "sourceEdges": ["TOP"],
                "unknownKey": true
              },
              "transformationParameters": { "type": "translate", "gravity": 0.5 },
              "shapeParameters": { "color": "#FF0000", "strokeWidth": 2 }
            }
        """.trimIndent()

        // when
        val decoded = particleParametersFromJson(jsonString)

        // then
        assertEquals("Rain", decoded.name)
        assertEquals(30, decoded.systemParameters.fps)
        assertEquals(true, decoded.systemParameters.autoResetParticles)
        assertEquals(400, decoded.generatorParameters.count)
        assertEquals(1..2, decoded.generatorParameters.particleWidthRange)
        assertEquals(10f..30f, decoded.generatorParameters.speedRange)
        assertEquals(85f..95f, decoded.generatorParameters.angleRange)
        assertEquals(setOf(SourceEdge.TOP), decoded.generatorParameters.sourceEdges)
        assertNull(decoded.generatorParameters.constraints)
        val transformation = decoded.transformationParameters
        assertTrue(transformation is TranslateTransformationParameters)
        assertEquals(0.5f, transformation.gravity)
        assertEquals("2", decoded.shapeParameters?.get("strokeWidth").toString())
    }

    @Test
    fun `Throws the SerializationException when the json is malformed`() {
        assertFailsWith<SerializationException> {
            particleParametersFromJson("{ \"name\": \"broken\" }")
        }
    }
}
