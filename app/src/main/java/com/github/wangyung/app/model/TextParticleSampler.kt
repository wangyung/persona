package com.github.wangyung.app.model

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.util.Size
import com.github.wangyung.persona.particle.ParticlePoint

private const val BASE_TEXT_SIZE = 128f
private const val MAX_TEXT_WIDTH_RATIO = 0.85f
private const val MAX_TEXT_HEIGHT_RATIO = 0.5f
private const val SAMPLE_STEP_PX = 2
private const val ALPHA_THRESHOLD = 128

/**
 * Renders the given single line [text] into an offscreen bitmap and samples the opaque pixels as
 * the [ParticlePoint]s in the coordinate system of the particle system with the given [dimension].
 * The text is scaled to fit the [dimension] and centered in it. The returned list always has
 * [count] points (the sampled pixels are cycled if there are less than [count] of them), so two
 * samplings with the same [count] can be zipped into the morphing pairs.
 */
@Suppress("ReturnCount")
fun sampleTextParticlePoints(
    text: String,
    dimension: Size,
    count: Int,
): List<ParticlePoint> {
    if (text.isBlank() || count <= 0) return emptyList()
    if (dimension.width <= 0 || dimension.height <= 0) return emptyList()

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Typeface.DEFAULT_BOLD
        textSize = BASE_TEXT_SIZE
        color = Color.WHITE
    }
    val baseTextWidth = paint.measureText(text)
    if (baseTextWidth <= 0f) return emptyList()

    // Scale the text to fit the dimension.
    val scale = minOf(
        dimension.width * MAX_TEXT_WIDTH_RATIO / baseTextWidth,
        dimension.height * MAX_TEXT_HEIGHT_RATIO / BASE_TEXT_SIZE,
    )
    paint.textSize = BASE_TEXT_SIZE * scale
    val textBounds = Rect()
    paint.getTextBounds(text, 0, text.length, textBounds)
    if (textBounds.isEmpty) return emptyList()

    val bitmap = Bitmap.createBitmap(
        textBounds.width(),
        textBounds.height(),
        Bitmap.Config.ARGB_8888,
    )
    Canvas(bitmap).drawText(text, -textBounds.left.toFloat(), -textBounds.top.toFloat(), paint)

    val offsetX = (dimension.width - bitmap.width) / 2f
    val offsetY = (dimension.height - bitmap.height) / 2f
    val points = sampleOpaquePixels(bitmap, offsetX, offsetY)
    bitmap.recycle()
    if (points.isEmpty()) return emptyList()

    points.shuffle()
    return List(count) { points[it % points.size] }
}

private fun sampleOpaquePixels(
    bitmap: Bitmap,
    offsetX: Float,
    offsetY: Float,
): MutableList<ParticlePoint> {
    val pixels = IntArray(bitmap.width * bitmap.height)
    bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
    val points = mutableListOf<ParticlePoint>()
    for (y in 0 until bitmap.height step SAMPLE_STEP_PX) {
        for (x in 0 until bitmap.width step SAMPLE_STEP_PX) {
            if (Color.alpha(pixels[y * bitmap.width + x]) >= ALPHA_THRESHOLD) {
                points.add(ParticlePoint(offsetX + x, offsetY + y))
            }
        }
    }
    return points
}
