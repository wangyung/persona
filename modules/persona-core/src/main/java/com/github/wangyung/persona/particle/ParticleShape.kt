package com.github.wangyung.persona.particle

import android.graphics.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.NativePaint
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.graphics.Path as ComposePath

/**
 * The group of particle shapes
 */
sealed class ParticleShape {

    /**
     * The path style.
     */
    data class Path(
        val strokeWidth: Int,
        val color: Color,
        val path: ComposePath,
    ) : ParticleShape()

    /**
     * The circle style.
     */
    data class Circle(val color: Color, val radius: Int) : ParticleShape()

    /**
     * The line style.
     */
    data class Line(
        val strokeWidth: Float,
        val color: Color,
    ) : ParticleShape()

    /**
     * The image style.
     */
    data class Image(
        val image: ImageBitmap,
        val useImageSize: Boolean = true,
        val colorFilter: ColorFilter? = null,
    ) : ParticleShape()

    /**
     * The text style. It uses [NativePaint] inside.
     */
    data class Text(
        val text: String,
        val fontSize: TextUnit,
        val borderWidth: Dp,
        val color: Color,
    ) : ParticleShape() {
        val textBounds: Rect = Rect()
        val nativePaint: NativePaint = NativePaint().apply {
            color = this@Text.color.toArgb()
        }
    }

    /**
     * The rectangle style.
     */
    data class Rectangle(
        val backgroundColor: Color
    ) : ParticleShape()
}
