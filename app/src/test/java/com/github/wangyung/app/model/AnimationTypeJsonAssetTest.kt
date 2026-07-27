package com.github.wangyung.app.model

import com.github.wangyung.persona.json.particleParametersFromJson
import com.github.wangyung.persona.particle.transformation.toTransformation
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Verifies that every animation defined in [AnimationType] can be created from its json asset
 * file and that the json describes the same animation as the Kotlin implementation.
 */
class AnimationTypeJsonAssetTest {

    private val allAnimationTypes = listOf(
        AnimationType.Rain,
        AnimationType.Snow,
        AnimationType.Sakura,
        AnimationType.FlyingPoo,
        AnimationType.FlyingMoney,
        AnimationType.FlyingBird,
        AnimationType.TwinkleStar,
        AnimationType.Emotion,
        AnimationType.Confetti,
        AnimationType.TextMorph,
        AnimationType.TextPathMorph,
        AnimationType.CircleExplosion,
    )

    private val expectedShapeTypes = mapOf<AnimationType, Class<out ShapeParameters>>(
        AnimationType.Rain to ShapeParameters.Line::class.java,
        AnimationType.Snow to ShapeParameters.Circle::class.java,
        AnimationType.Sakura to ShapeParameters.Path::class.java,
        AnimationType.FlyingPoo to ShapeParameters.Text::class.java,
        AnimationType.FlyingMoney to ShapeParameters.Text::class.java,
        AnimationType.FlyingBird to ShapeParameters.Image::class.java,
        AnimationType.TwinkleStar to ShapeParameters.Circle::class.java,
        AnimationType.Emotion to ShapeParameters.Text::class.java,
        AnimationType.Confetti to ShapeParameters.Rectangle::class.java,
        AnimationType.TextMorph to ShapeParameters.Circle::class.java,
        AnimationType.TextPathMorph to ShapeParameters.Circle::class.java,
        AnimationType.CircleExplosion to ShapeParameters.Circle::class.java,
    )

    // The unit test runs with the module directory as the working directory.
    private val assetsRoot: File = listOf("src/main/assets", "app/src/main/assets")
        .map(::File)
        .first(File::exists)

    private fun AnimationType.decodeJsonAsset() =
        particleParametersFromJson(File(assetsRoot, jsonAssetPath).readText())

    @Test
    fun `Every animation type has a json asset that can be decoded`() {
        allAnimationTypes.forEach { type ->
            val decoded = type.decodeJsonAsset()
            assertNotNull(decoded, "Failed to decode ${type.jsonAssetPath}")
        }
    }

    @Test
    fun `The json assets describe the same parameters as the animation types`() {
        allAnimationTypes.forEach { type ->
            val decoded = type.decodeJsonAsset()
            assertEquals(
                type.toGeneratorParameters(),
                decoded.generatorParameters,
                "generatorParameters mismatch in ${type.jsonAssetPath}",
            )
            assertEquals(
                type.toParticleSystemParameters(),
                decoded.systemParameters,
                "systemParameters mismatch in ${type.jsonAssetPath}",
            )
        }
    }

    @Test
    fun `The json assets create the same transformation as the animation types`() {
        allAnimationTypes.forEach { type ->
            val expected =
                type.toParticleTransformation(type.toTransformationSystemParameters())
            val decoded = decoded(type)
            assertEquals(
                expected::class,
                decoded::class,
                "transformation mismatch in ${type.jsonAssetPath}",
            )
        }
    }

    @Test
    fun `The json assets contain the expected shape parameters`() {
        allAnimationTypes.forEach { type ->
            val shapeParameters = type.decodeJsonAsset().shapeParameters
            assertNotNull(
                shapeParameters,
                "shapeParameters is missing in ${type.jsonAssetPath}",
            )
            assertEquals(
                expectedShapeTypes.getValue(type),
                shapeParameters.toShapeParameters().javaClass,
                "shape type mismatch in ${type.jsonAssetPath}",
            )
        }
    }

    private fun decoded(type: AnimationType) =
        type.decodeJsonAsset().transformationParameters.toTransformation()
}
