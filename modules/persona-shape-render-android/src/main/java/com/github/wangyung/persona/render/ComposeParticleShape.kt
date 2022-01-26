package com.github.wangyung.persona.render

import android.graphics.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.NativePaint
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.github.wangyung.persona.particle.ParticleShape
import com.github.wangyung.persona.particle.ParticleShape.Companion.SAME_AS_PARTICLE
import androidx.compose.ui.graphics.Path as ComposePath

/**
 * The ParticleShape for Compose.
 */
sealed class ComposeParticleShape {
    /**
     * The path style.
     */
    data class Path(
        val strokeWidth: Int,
        val color: Color,
        val path: ComposePath,
    ) : ComposeParticleShape(), ParticleShape.Path {
        override val width: Int
            get() = path.getBounds().width.toInt()
        override val height: Int
            get() = path.getBounds().height.toInt()
    }

    /**
     * The circle style.
     */
    data class Circle(
        val color: Color,
        val radius: Int
    ) : ComposeParticleShape(), ParticleShape.Circle {
        override val width: Int
            get() = radius
        override val height: Int
            get() = radius
    }

    /**
     * The line style.
     */
    data class Line(
        val strokeWidth: Float,
        val color: Color,
    ) : ComposeParticleShape(), ParticleShape.Line {
        override val width: Int = SAME_AS_PARTICLE
        override val height: Int = SAME_AS_PARTICLE
    }

    /**
     * The image style.
     */
    data class Image(
        val image: ImageBitmap,
        val useImageSize: Boolean = true,
        val colorFilter: ColorFilter? = null,
    ) : ComposeParticleShape(), ParticleShape.Image {
        override val width: Int
            get() = if (useImageSize) {
                image.width
            } else {
                -1
            }
        override val height: Int
            get() = if (useImageSize) {
                image.height
            } else {
                -1
            }
    }

    /**
     * The text style. It uses [NativePaint] inside.
     */
    data class Text(
        val text: String,
        val fontSize: TextUnit,
        val borderWidth: Dp,
        val color: Color,
    ) : ComposeParticleShape(), ParticleShape.Path {
        val textBounds: Rect = Rect()
        val nativePaint: NativePaint = NativePaint().apply {
            color = this@Text.color.toArgb()
        }
        override val width: Int
            get() = textBounds.width()
        override val height: Int
            get() = textBounds.height()
    }

    /**
     * The rectangle style.
     */
    data class Rectangle(
        val backgroundColor: Color,
    ) : ComposeParticleShape(), ParticleShape.Rectangle {
        override val width: Int = SAME_AS_PARTICLE
        override val height: Int = SAME_AS_PARTICLE
    }
}
