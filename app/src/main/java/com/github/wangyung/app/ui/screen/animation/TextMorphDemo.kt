package com.github.wangyung.app.ui.screen.animation

import android.util.Size
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.wangyung.app.model.AnimationType
import com.github.wangyung.app.model.sampleTextParticlePoints
import com.github.wangyung.persona.particle.ParticleSystem
import com.github.wangyung.persona.particle.ParticleSystemParameters
import com.github.wangyung.persona.particle.generator.PointsParticleGenerator
import com.github.wangyung.persona.particle.generator.ShapeProvider
import com.github.wangyung.persona.particle.particleSystem
import com.github.wangyung.persona.particle.transformation.MoveToTargetTransformation
import com.github.wangyung.persona.particle.transformation.ParticleTargetProvider
import com.github.wangyung.persona.particle.transformation.ParticleTransformation
import com.github.wangyung.persona.particle.transformation.SequenceTransformation
import com.github.wangyung.persona.render.ComposeParticleShape
import com.github.wangyung.persona.ui.component.ParticleBox

private const val DEFAULT_FROM_TEXT = "HELLO"
private const val DEFAULT_TO_TEXT = "WORLD"
private const val DEFAULT_PARTICLE_COUNT = 600
private const val DEFAULT_MORPH_DURATION = 90
private const val DEFAULT_HOLD_DURATION = 45
private const val PARTICLE_RADIUS = 3
private const val FPS = 60

private val backgroundColor = Color(0xFF1A237E)
private val particleColors = listOf(
    Color(0xFF80DEEA),
    Color(0xFFF48FB1),
    Color(0xFFFFF59D),
    Color(0xFFA5D6A7),
    Color.White,
)

@Composable
fun TextMorphDemo() {
    val animationType = AnimationType.TextMorph
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = animationType.toTitle(),
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        var fromText by remember { mutableStateOf(DEFAULT_FROM_TEXT) }
        var toText by remember { mutableStateOf(DEFAULT_TO_TEXT) }
        var particleCount by remember { mutableStateOf(DEFAULT_PARTICLE_COUNT) }
        var morphDuration by remember { mutableStateOf(DEFAULT_MORPH_DURATION) }
        var holdDuration by remember { mutableStateOf(DEFAULT_HOLD_DURATION) }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .background(backgroundColor)
        ) {
            val dimension = Size(constraints.maxWidth, constraints.maxHeight)
            val morphParameters = TextMorphParameters(
                fromText = fromText,
                toText = toText,
                particleCount = particleCount,
                morphDuration = morphDuration.toLong(),
                holdDuration = holdDuration.toLong(),
            )
            val particleSystem = remember(morphParameters, dimension) {
                createTextMorphParticleSystem(morphParameters, dimension)
            }
            if (particleSystem != null) {
                DisposableEffect(particleSystem) {
                    onDispose { particleSystem.stop() }
                }
                ParticleBox(modifier = Modifier.fillMaxSize(), particleSystem = particleSystem)
            } else {
                Text(
                    text = "Type some text to morph",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        val scrollState = rememberScrollState()
        val modifier = Modifier.fillMaxWidth()
        Column(modifier = modifier.verticalScroll(scrollState)) {
            Row(modifier = modifier) {
                OutlinedTextField(
                    value = fromText,
                    onValueChange = { fromText = it },
                    label = { Text("From text") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                )
                OutlinedTextField(
                    value = toText,
                    onValueChange = { toText = it },
                    label = { Text("To text") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            SliderWithValueText(
                title = "Particle Count:",
                modifier = modifier,
                sliderRange = 100f..2000f,
                intOnly = true,
                defaultSliderValue = particleCount.toFloat()
            ) { newCount ->
                particleCount = newCount.toInt()
            }
            SliderWithValueText(
                title = "Morph Duration (iterations):",
                modifier = modifier,
                sliderRange = 10f..300f,
                intOnly = true,
                defaultSliderValue = morphDuration.toFloat()
            ) { newDuration ->
                morphDuration = newDuration.toInt()
            }
            SliderWithValueText(
                title = "Hold Duration (iterations):",
                modifier = modifier,
                sliderRange = 0f..300f,
                intOnly = true,
                defaultSliderValue = holdDuration.toFloat()
            ) { newDuration ->
                holdDuration = newDuration.toInt()
            }
            val navigationBarBottom =
                WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(navigationBarBottom + 8.dp)
            )
        }
    }
}

private data class TextMorphParameters(
    val fromText: String,
    val toText: String,
    val particleCount: Int,
    val morphDuration: Long,
    val holdDuration: Long,
)

/**
 * Creates a particle system where the particles form the from-text, then morph into the to-text
 * and back, looping forever. One cycle is: hold → morph to the to-text → hold → morph back. When
 * the sequence ends all particles die and the system restarts (restartWhenAllDead), which resets
 * the particles to the from-text points and loops the animation.
 */
private fun createTextMorphParticleSystem(
    parameters: TextMorphParameters,
    dimension: Size,
): ParticleSystem? {
    val fromPoints =
        sampleTextParticlePoints(parameters.fromText, dimension, parameters.particleCount)
    val toPoints =
        sampleTextParticlePoints(parameters.toText, dimension, parameters.particleCount)
    if (fromPoints.isEmpty() || toPoints.isEmpty()) return null

    val generator = PointsParticleGenerator(
        points = fromPoints,
        shapeProvider = ShapeProvider {
            ComposeParticleShape.Circle(
                color = particleColors.random(),
                radius = PARTICLE_RADIUS,
            )
        },
    )
    val toTargets = ParticleTargetProvider { particle ->
        toPoints[(particle.id % toPoints.size).toInt()]
    }
    val backToStarts = ParticleTargetProvider { particle ->
        fromPoints[(particle.id % fromPoints.size).toInt()]
    }
    val morphDuration = parameters.morphDuration
    val holdDuration = parameters.holdDuration
    val holdTransformation = ParticleTransformation { _, _ -> }
    val transformation = SequenceTransformation().apply {
        add(holdTransformation, holdDuration)
        add(MoveToTargetTransformation(toTargets, morphDuration), morphDuration)
        add(holdTransformation, holdDuration)
        add(MoveToTargetTransformation(backToStarts, morphDuration), morphDuration)
    }
    return particleSystem(
        dimension = dimension,
        parameters = ParticleSystemParameters(
            fps = FPS,
            autoResetParticles = false,
            restartWhenAllDead = true,
        ),
        generator = generator,
        transformation = transformation,
    )
}
