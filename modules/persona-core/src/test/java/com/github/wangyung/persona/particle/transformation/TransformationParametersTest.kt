package com.github.wangyung.persona.particle.transformation

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class TransformationParametersTest {

    @Test
    fun `Create LinearTranslateTransformation from TranslateTransformationParameters`() {
        val transformation = TranslateTransformationParameters(gravity = 0.5f).toTransformation()
        assertTrue(transformation is LinearTranslateTransformation)
    }

    @Test
    fun `Create LinearRotationTransformation from RotationTransformationParameters`() {
        val transformation = RotationTransformationParameters().toTransformation()
        assertTrue(transformation is LinearRotationTransformation)
    }

    @Test
    fun `Create BlinkTransformation from BlinkTransformationParameters`() {
        val transformation =
            BlinkTransformationParameters(frequencyFactorRange = 0.5f..2f).toTransformation()
        assertTrue(transformation is BlinkTransformation)
    }

    @Test
    fun `Create SineWaveTranslateTransformation from SineWaveTranslateTransformationParameters`() {
        val transformation = SineWaveTranslateTransformationParameters(
            frequencyFactor = 3f,
            amplitude = 4f,
        ).toTransformation()
        assertTrue(transformation is SineWaveTranslateTransformation)
    }

    @Test
    fun `Create LinearScaleTransformation from ScaleTransformationParameters`() {
        val transformation =
            ScaleTransformationParameters(xDelta = 0.1f, yDelta = 0.2f).toTransformation()
        assertTrue(transformation is LinearScaleTransformation)
    }

    @Test
    fun `Create ScaleAndFadeTransformation from ScaleAndFadeTransformationParameters`() {
        val transformation = ScaleAndFadeTransformationParameters(
            xDelta = 0.1f,
            yDelta = 0.2f,
            alphaDelta = 0.3f,
        ).toTransformation()
        assertTrue(transformation is ScaleAndFadeTransformation)
    }

    @Test
    fun `Create CompositeTransformation from CompositeTransformationParameters`() {
        val transformation = CompositeTransformationParameters(
            parameters = listOf(
                TranslateTransformationParameters(),
                RotationTransformationParameters(),
            )
        ).toTransformation()
        assertTrue(transformation is CompositeTransformation)
    }

    @Test
    fun `Create SequenceTransformation with the total duration from the steps`() {
        val transformation = SequenceTransformationParameters(
            steps = listOf(
                SequenceTransformationParameters.Step(
                    parameters = TranslateTransformationParameters(),
                    duration = 40L,
                ),
                SequenceTransformationParameters.Step(
                    parameters = RotationTransformationParameters(),
                    duration = 30L,
                ),
            )
        ).toTransformation()

        assertTrue(transformation is SequenceTransformation)
        assertEquals(70L, transformation.duration)
    }
}
