package com.github.wangyung.app.model

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.wangyung.persona.json.personaJson
import com.github.wangyung.persona.json.serializer.IntRangeSerializer
import com.github.wangyung.persona.particle.generator.ShapeProvider
import com.github.wangyung.persona.render.ComposeParticleShape
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * The json schema for the `shapeParameters` in the animation json file. The `type` field decides
 * the shape: `line`, `circle`, `text`, `image`, `rectangle` or `path`.
 *
 * Colors are hex strings: `#RRGGBB` or `#AARRGGBB`. Ranges are `{"from": x, "to": y}` and a random
 * value in the range (inclusive) is picked per particle, so a single json can create varied
 * particles.
 */
@Serializable
sealed class ShapeParameters {

    /** A line with a random stroke width from [strokeWidthRange]. */
    @Serializable
    @SerialName("line")
    data class Line(
        @Serializable(with = IntRangeSerializer::class)
        val strokeWidthRange: IntRange = 1..1,
        val color: String,
    ) : ShapeParameters()

    /** A circle with a random radius from [radiusRange]. */
    @Serializable
    @SerialName("circle")
    data class Circle(
        val color: String,
        @Serializable(with = IntRangeSerializer::class)
        val radiusRange: IntRange,
    ) : ShapeParameters()

    /** A text (or emoji) with a random font size from [fontSizeRange]. */
    @Serializable
    @SerialName("text")
    data class Text(
        val text: String,
        @Serializable(with = IntRangeSerializer::class)
        val fontSizeRange: IntRange,
        val color: String,
        val borderWidth: Int = 1,
    ) : ShapeParameters()

    /** An image from the drawable resource with the given name. */
    @Serializable
    @SerialName("image")
    data class Image(val drawableName: String) : ShapeParameters()

    /** A rectangle with the background color randomly picked from [colors]. */
    @Serializable
    @SerialName("rectangle")
    data class Rectangle(val colors: List<String>) : ShapeParameters()

    /** A path described by the svg path data with a random stroke width from [strokeWidthRange]. */
    @Serializable
    @SerialName("path")
    data class Path(
        val pathData: String,
        @Serializable(with = IntRangeSerializer::class)
        val strokeWidthRange: IntRange = 1..1,
        val color: String,
    ) : ShapeParameters()
}

/**
 * Decodes the [ShapeParameters] from the `shapeParameters` json object in the animation json file.
 */
fun JsonObject.toShapeParameters(): ShapeParameters =
    personaJson.decodeFromJsonElement(ShapeParameters.serializer(), this)

/**
 * Creates the [ShapeProvider] that creates the shapes described by the [ShapeParameters].
 */
@Suppress("DiscouragedApi")
fun ShapeParameters.toShapeProvider(context: Context): ShapeProvider =
    when (this) {
        is ShapeParameters.Line -> ShapeProvider {
            ComposeParticleShape.Line(
                strokeWidth = strokeWidthRange.random().toFloat(),
                color = color.toComposeColor(),
            )
        }
        is ShapeParameters.Circle -> ShapeProvider {
            ComposeParticleShape.Circle(
                color = color.toComposeColor(),
                radius = radiusRange.random(),
            )
        }
        is ShapeParameters.Text -> ShapeProvider {
            ComposeParticleShape.Text(
                text = text,
                fontSize = fontSizeRange.random().sp,
                borderWidth = borderWidth.dp,
                color = color.toComposeColor(),
            )
        }
        is ShapeParameters.Image -> {
            val resId = context.resources.getIdentifier(
                drawableName,
                "drawable",
                context.packageName,
            )
            require(resId != 0) { "Unknown drawable: $drawableName" }
            val image = ImageBitmap.imageResource(context.resources, resId)
            ShapeProvider { ComposeParticleShape.Image(image = image) }
        }
        is ShapeParameters.Rectangle -> {
            val parsedColors = colors.map { it.toComposeColor() }
            ShapeProvider {
                ComposeParticleShape.Rectangle(backgroundColor = parsedColors.random())
            }
        }
        is ShapeParameters.Path -> ShapeProvider {
            ComposeParticleShape.Path(
                strokeWidth = strokeWidthRange.random(),
                color = color.toComposeColor(),
                path = PathParser().parsePathString(pathData).toPath(),
            )
        }
    }

private const val RGB_LENGTH = 6
private const val ARGB_LENGTH = 8
private const val HEX_RADIX = 16
private const val ALPHA_MASK = 0xFF000000

internal fun String.toComposeColor(): Color {
    val hex = removePrefix("#")
    val argb = when (hex.length) {
        RGB_LENGTH -> ALPHA_MASK or hex.toLong(HEX_RADIX)
        ARGB_LENGTH -> hex.toLong(HEX_RADIX)
        else -> throw IllegalArgumentException("Unsupported color format: $this")
    }
    return Color(argb)
}
