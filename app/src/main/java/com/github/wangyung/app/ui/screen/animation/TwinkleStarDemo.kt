package com.github.wangyung.app.ui.screen.animation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.github.wangyung.app.model.AnimationParameterSet
import com.github.wangyung.app.model.AnimationType

@Composable
fun TwinkleStarDemo() {
    val animationType = AnimationType.TwinkleStar
    val generatorParameters = animationType.toGeneratorParameters()
    var parameterSet by remember {
        mutableStateOf(
            AnimationParameterSet(
                generatorParameters = generatorParameters,
                particleSystemParameters = animationType.toParticleSystemParameters(),
                transformationParameters = animationType.toTransformationSystemParameters()
            )
        )
    }
    AnimationDemo(
        animationType = animationType,
        parameterSet = parameterSet,
        shapeProvider = animationType.toShapeProvider(LocalContext.current.resources),
        showMoon = true,
        showLandscape = false,
    ) {
        val modifier = Modifier.fillMaxWidth()
        Column(modifier = modifier) {
            SliderWithValueText(
                title = "Particle Count:",
                modifier = modifier,
                sliderRange = 10f..500f,
                intOnly = true,
                defaultSliderValue = parameterSet.generatorParameters.count.toFloat()
            ) { newCount ->
                parameterSet = parameterSet.copy(
                    generatorParameters = parameterSet.generatorParameters.copy(count = newCount.toInt())
                )
            }

            val minStartOffset = parameterSet.generatorParameters.startOffsetRange.first
            val maxStartOffset = parameterSet.generatorParameters.startOffsetRange.last
            SliderWithValueText(
                title = "Min Start Offset:",
                modifier = modifier,
                sliderRange = 0f..120f,
                defaultSliderValue = minStartOffset.toFloat()
            ) { newMinStartOffset ->
                parameterSet = parameterSet.copy(
                    generatorParameters = parameterSet.generatorParameters.copy(
                        startOffsetRange = newMinStartOffset.toInt()..maxStartOffset
                    )
                )
            }
            SliderWithValueText(
                title = "Max Start Offset:",
                modifier = modifier,
                sliderRange = 0f..120f,
                defaultSliderValue = maxStartOffset.toFloat()
            ) { newMaxStartOffset ->
                parameterSet = parameterSet.copy(
                    generatorParameters = parameterSet.generatorParameters.copy(
                        startOffsetRange = minStartOffset..newMaxStartOffset.toInt()
                    )
                )
            }
        }
    }
}
