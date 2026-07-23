package com.github.wangyung.app.model

import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.Rect
import android.util.Size
import com.github.wangyung.persona.particle.ParticlePoint
import com.github.wangyung.persona.path.MorphablePath
import kotlin.math.max

private const val DEFAULT_POINT_SPACING_PX = 6f
private const val MIN_POINTS_PER_CONTOUR = 8

/**
 * Converts the given single line [text] into the [MorphablePath]s. The text is converted into the
 * outline path by [android.graphics.Paint.getTextPath], then every contour of the outline is
 * sampled into the points with roughly [pointSpacingPx] pixels between them (one [MorphablePath]
 * per contour, so the holes like the inner ring of the letter O keep their own contour). The text
 * is scaled to fit the [dimension] and centered in it.
 */
@Suppress("ReturnCount")
fun sampleTextMorphablePaths(
    text: String,
    dimension: Size,
    pointSpacingPx: Float = DEFAULT_POINT_SPACING_PX,
): List<MorphablePath> {
    if (text.isBlank() || pointSpacingPx <= 0f) return emptyList()
    if (dimension.width <= 0 || dimension.height <= 0) return emptyList()

    val paint = createFittedTextPaint(text, dimension) ?: return emptyList()
    val textBounds = Rect()
    paint.getTextBounds(text, 0, text.length, textBounds)
    if (textBounds.isEmpty) return emptyList()

    // The origin is the baseline position that centers the text in the dimension.
    val originX = (dimension.width - textBounds.width()) / 2f - textBounds.left
    val originY = (dimension.height - textBounds.height()) / 2f - textBounds.top
    val textPath = Path()
    paint.getTextPath(text, 0, text.length, originX, originY, textPath)

    return sampleContours(textPath, pointSpacingPx)
}

private fun sampleContours(textPath: Path, pointSpacingPx: Float): List<MorphablePath> {
    val morphablePaths = mutableListOf<MorphablePath>()
    val pathMeasure = PathMeasure(textPath, false)
    val position = FloatArray(2)
    var id = 0L
    do {
        val contourLength = pathMeasure.length
        if (contourLength > 0f) {
            val pointCount =
                max(MIN_POINTS_PER_CONTOUR, (contourLength / pointSpacingPx).toInt())
            val points = ArrayList<ParticlePoint>(pointCount)
            for (index in 0 until pointCount) {
                // Don't sample the end of the contour, it is the same as the start point for the
                // closed contours.
                pathMeasure.getPosTan(index * contourLength / pointCount, position, null)
                points.add(ParticlePoint(position[0], position[1]))
            }
            morphablePaths.add(
                MorphablePath(
                    id = id++,
                    originalPoints = points,
                    isClosed = pathMeasure.isClosed,
                )
            )
        }
    } while (pathMeasure.nextContour())
    return morphablePaths
}
