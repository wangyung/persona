package com.github.wangyung.persona.render

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.NativePaint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.github.wangyung.persona.particle.Particle

private val debugBorderStroke = Stroke(width = 1f)

fun Particle.drawCircle(drawScope: DrawScope, circle: ComposeParticleShape.Circle) {
    val center = Offset(x, y)
    drawScope.withTransform(transformBlock = {
        scale(scaleX = scaleX, scaleY = scaleY, pivot = center)
    }) {
        drawScope.drawCircle(
            color = circle.color.copy(alpha = alpha),
            radius = instinct.width.toFloat(),
            center = center
        )
    }
}

fun Particle.drawLine(drawScope: DrawScope, line: ComposeParticleShape.Line) =
    drawScope.drawLine(
        color = line.color,
        strokeWidth = line.strokeWidth,
        start = Offset(x - instinct.width / 2, y = y - instinct.height / 2),
        end = Offset(x + instinct.width / 2, y = y + instinct.height / 2)
    )

@Suppress("MagicNumber")
fun Particle.drawText(
    drawScope: DrawScope,
    nativePaint: NativePaint,
    textShape: ComposeParticleShape.Text
) {
    nativePaint.getTextBounds(textShape.text, 0, textShape.text.count(), textShape.textBounds)
    val textBounds = textShape.textBounds
    drawScope.withTransform(transformBlock = {
        val pivot =
            Offset(
                textBounds.centerX().toFloat(),
                textBounds.centerY().toFloat()
            )
        translate(left = x, top = y)
        rotate(rotation, pivot = pivot)
        scale(scaleX = scaleX, scaleY = scaleY, pivot = pivot)
    }) {
        nativePaint.alpha = (this@drawText.alpha * 255).toInt()
        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawText(
                textShape.text, 0, textShape.text.count(), 0f, 0f, nativePaint
            )
        }
    }
}

fun Particle.drawPath(
    drawScope: DrawScope,
    pathShape: ComposeParticleShape.Path,
    drawDebugBorder: Boolean = false
) {
    val pathBound = pathShape.path.getBounds()
    drawScope.withTransform(transformBlock = {
        val pivot = pathBound.center
        translate(left = x, top = y)
        rotate(rotation, pivot = pivot)
        scale(scaleX = scaleX, scaleY = scaleY, pivot = pivot)
    }) {
        drawPath(path = pathShape.path, color = pathShape.color)
        if (drawDebugBorder) {
            drawRect(Color.Red, pathBound.topLeft, pathBound.size, style = debugBorderStroke)
        }
    }
}

fun Particle.drawImage(drawScope: DrawScope, imageShape: ComposeParticleShape.Image) {
    val topLeft = IntOffset((x - instinct.width / 2).toInt(), (y - instinct.height / 2).toInt())
    drawScope.drawImage(
        image = imageShape.image,
        dstOffset = topLeft,
        dstSize = IntSize(instinct.width, instinct.height),
        colorFilter = imageShape.colorFilter
    )
}

fun Particle.drawRectangle(drawScope: DrawScope, rectangleShape: ComposeParticleShape.Rectangle) {
    drawScope.withTransform(transformBlock = {
        val pivot = Offset(instinct.width / 2f, instinct.height / 2f)
        translate(left = x, top = y)
        rotate(rotation, pivot = pivot)
        scale(scaleX = scaleX, scaleY = scaleY, pivot = pivot)
    }) {
        drawRect(
            color = rectangleShape.backgroundColor,
            size = Size(instinct.width.toFloat(), instinct.height.toFloat())
        )
    }
}
