package com.github.wangyung.persona.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.github.wangyung.persona.particle.Instinct
import com.github.wangyung.persona.particle.Particle
import com.github.wangyung.persona.particle.ParticleShape
import com.github.wangyung.persona.particle.ParticleSystem
import com.github.wangyung.persona.particle.particleOf

@Composable
fun ParticleBox(
    modifier: Modifier,
    particleSystem: ParticleSystem,
) {
    val particleSystemState = remember {
        mutableStateOf(particleSystem)
    }
    var iteration by remember {
        mutableStateOf(0L)
    }
    val iterationState = particleSystem.iterationFlow.collectAsState()
    // trigger the recomposition
    iteration = iterationState.value
    particleSystemState.value = particleSystem
    Box(modifier = modifier) {
        Canvas(
            modifier = modifier,
            // Only draw the particles inside the Box. PartcileSystem may have bigger size than
            // the Box.
            onDraw = { clipRect { drawParticles(particleSystem.particles) } }
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(Unit, lifecycleOwner) {
        val lifecycleObserver = ParticleBoxLifecycleObserver(particleSystemState)
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            particleSystem.stop()
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }
}

/**
 * A custom [LifecycleObserver] that handles OnPause and OnResume.
 */
private class ParticleBoxLifecycleObserver(
    private val particleSystemState: State<ParticleSystem>
) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        particleSystemState.value.stop()
        particleSystemState.value.iterationFlow
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        particleSystemState.value.start()
    }
}

private fun DrawScope.drawParticles(particles: List<Particle>?) {
    particles?.fastForEach { particle ->
        if (!particle.shouldBeDraw) return@fastForEach

        when (val shape = particle.instinct.shape) {
            is ParticleShape.Circle -> particle.drawCircle(this, shape)
            is ParticleShape.Line -> particle.drawLine(this, shape)
            is ParticleShape.Text -> {
                val paint = shape.nativePaint.apply {
                    strokeWidth = shape.border.toPx()
                    color = shape.color.toArgb()
                    textSize = shape.fontSize.toPx()
                }
                particle.drawText(this, paint, shape)
            }
            is ParticleShape.Path -> particle.drawPath(this, shape)
            is ParticleShape.Image -> particle.drawImage(this, shape)
        }
    }
}

private fun Particle.drawCircle(drawScope: DrawScope, circle: ParticleShape.Circle) {
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

private fun Particle.drawLine(drawScope: DrawScope, line: ParticleShape.Line) =
    drawScope.drawLine(
        color = line.color,
        start = Offset(x - instinct.width / 2, y = y - instinct.height / 2),
        end = Offset(x + instinct.width / 2, y = y + instinct.height / 2)
    )

@Suppress("MagicNumber")
private fun Particle.drawText(
    drawScope: DrawScope,
    nativePaint: NativePaint,
    textShape: ParticleShape.Text
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

private fun Particle.drawPath(
    drawScope: DrawScope,
    pathShape: ParticleShape.Path,
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
            drawRect(Color.Red, pathBound.topLeft, pathBound.size, style = Stroke(width = 1f))
        }
    }
}

private fun Particle.drawImage(drawScope: DrawScope, imageShape: ParticleShape.Image) {
    val topLeft = IntOffset((x - instinct.width / 2).toInt(), (y - instinct.height / 2).toInt())
    drawScope.drawImage(
        image = imageShape.image,
        dstOffset = topLeft,
        dstSize = IntSize(instinct.width, instinct.height),
        colorFilter = imageShape.colorFilter
    )
}

@Composable
@Preview
fun ParticleBoxPreview() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val particle = particleOf(
            id = 1L,
            x = 100f,
            y = 100f,
            instinct = Instinct(
                width = 100,
                height = 100,
                speed = 5f,
                angle = 80,
                zRotationalSpeed = 2f,
                shape = ParticleShape.Circle(
                    color = Color.White,
                    radius = 10,
                ),
            ),
            rotation = 30f,
        )
        drawParticles(listOf(particle))
    }
}
