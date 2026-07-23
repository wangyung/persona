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
import androidx.compose.material.Switch
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
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.github.wangyung.app.model.AnimationType
import com.github.wangyung.app.model.sampleTextMorphablePaths
import com.github.wangyung.persona.path.PathSystemParameters
import com.github.wangyung.persona.path.pathSystem
import com.github.wangyung.persona.path.transformation.WavePathTransformation
import com.github.wangyung.persona.ui.component.MorphablePathBox

private const val DEFAULT_TEXT = "HELLO"
private const val DEFAULT_AMPLITUDE = 6f
private const val DEFAULT_WAVE_SPEED = 0.15f
private const val DEFAULT_WAVE_COUNT = 4
private const val STROKE_WIDTH = 3f
private const val FPS = 60

private val backgroundColor = Color(0xFF1A237E)
private val pathColor = Color(0xFF80DEEA)

@Composable
fun TextPathDemo() {
    val animationType = AnimationType.TextPathMorph
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

        var text by remember { mutableStateOf(DEFAULT_TEXT) }
        var amplitude by remember { mutableStateOf(DEFAULT_AMPLITUDE) }
        var waveSpeed by remember { mutableStateOf(DEFAULT_WAVE_SPEED) }
        var waveCount by remember { mutableStateOf(DEFAULT_WAVE_COUNT) }
        var drawAsStroke by remember { mutableStateOf(false) }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .background(backgroundColor)
        ) {
            val dimension = Size(constraints.maxWidth, constraints.maxHeight)
            val pathSystem = remember(text, amplitude, waveSpeed, waveCount, dimension) {
                val morphablePaths = sampleTextMorphablePaths(text, dimension)
                if (morphablePaths.isEmpty()) {
                    null
                } else {
                    pathSystem(
                        paths = morphablePaths,
                        transformation = WavePathTransformation(
                            amplitude = amplitude,
                            angularSpeed = waveSpeed,
                            waveCount = waveCount,
                        ),
                        parameters = PathSystemParameters(fps = FPS),
                    )
                }
            }
            if (pathSystem != null) {
                DisposableEffect(pathSystem) {
                    onDispose { pathSystem.stop() }
                }
                MorphablePathBox(
                    modifier = Modifier.fillMaxSize(),
                    pathSystem = pathSystem,
                    color = pathColor,
                    style = if (drawAsStroke) Stroke(width = STROKE_WIDTH) else Fill,
                )
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
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Text") },
                singleLine = true,
                modifier = modifier
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = drawAsStroke, onCheckedChange = { drawAsStroke = it })
                Text(if (drawAsStroke) "Stroke the outline" else "Fill the shape")
            }
            SliderWithValueText(
                title = "Wave Amplitude (px):",
                modifier = modifier,
                sliderRange = 0f..30f,
                defaultSliderValue = amplitude
            ) { newAmplitude ->
                amplitude = newAmplitude
            }
            SliderWithValueText(
                title = "Wave Speed (radians/iteration):",
                modifier = modifier,
                sliderRange = 0.01f..0.5f,
                defaultSliderValue = waveSpeed
            ) { newSpeed ->
                waveSpeed = newSpeed
            }
            SliderWithValueText(
                title = "Wave Count:",
                modifier = modifier,
                sliderRange = 1f..10f,
                intOnly = true,
                defaultSliderValue = waveCount.toFloat()
            ) { newWaveCount ->
                waveCount = newWaveCount.toInt()
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
