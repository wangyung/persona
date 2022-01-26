package com.github.wangyung.persona.ui.component

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.github.wangyung.persona.particle.Instinct
import com.github.wangyung.persona.particle.Particle
import com.github.wangyung.persona.particle.ParticleSystem
import com.github.wangyung.persona.particle.particleOf
import com.github.wangyung.persona.render.ComposeParticleShape
import com.github.wangyung.persona.render.drawCircle
import com.github.wangyung.persona.render.drawImage
import com.github.wangyung.persona.render.drawLine
import com.github.wangyung.persona.render.drawPath
import com.github.wangyung.persona.render.drawRectangle
import com.github.wangyung.persona.render.drawText

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

        when (val shape = particle.instinct.shape as ComposeParticleShape) {
            is ComposeParticleShape.Circle -> particle.drawCircle(this, shape)
            is ComposeParticleShape.Line -> particle.drawLine(this, shape)
            is ComposeParticleShape.Text -> {
                val paint = shape.nativePaint.apply {
                    strokeWidth = shape.borderWidth.toPx()
                    color = shape.color.toArgb()
                    textSize = shape.fontSize.toPx()
                }
                particle.drawText(this, paint, shape)
            }
            is ComposeParticleShape.Path -> particle.drawPath(this, shape)
            is ComposeParticleShape.Image -> particle.drawImage(this, shape)
            is ComposeParticleShape.Rectangle -> particle.drawRectangle(this, shape)
        }
    }
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
                angle = 80f,
                zRotationalSpeed = 2f,
                shape = ComposeParticleShape.Circle(
                    color = Color.White,
                    radius = 10,
                ),
            ),
            rotation = 30f,
        )
        drawParticles(listOf(particle))
    }
}
