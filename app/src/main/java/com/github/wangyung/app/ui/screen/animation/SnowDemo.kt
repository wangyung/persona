package com.github.wangyung.app.ui.screen.animation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.wangyung.app.model.AnimationParameterSet
import com.github.wangyung.app.model.AnimationType
import com.github.wangyung.app.model.DEFAULT_SNOW_MAX_RADIUS
import com.github.wangyung.app.model.DEFAULT_SNOW_MIN_RADIUS
import com.github.wangyung.app.model.createShowParticle

@Composable
fun SnowDemo() {
    val animationType = AnimationType.Snow
    val generatorParameters = animationType.toGeneratorParameters(LocalContext.current.resources)
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
        parameterSet = parameterSet
    ) {
        val modifier = Modifier.fillMaxWidth()
        var minSnowRadius by remember {
            mutableStateOf(DEFAULT_SNOW_MIN_RADIUS)
        }
        var maxSnowRadius by remember {
            mutableStateOf(DEFAULT_SNOW_MAX_RADIUS)
        }
        Column(modifier = modifier) {
            SliderWithValueText(
                title = "Particle Count:",
                modifier = modifier,
                sliderRange = 25f..200f,
                intOnly = true,
                defaultSliderValue = parameterSet.generatorParameters.count.toFloat()
            ) { newCount ->
                parameterSet = parameterSet.copy(
                    generatorParameters = parameterSet.generatorParameters.copy(count = newCount.toInt())
                )
            }

            SliderWithValueText(
                title = "Min radius:",
                modifier = modifier,
                sliderRange = 1f..10f,
                defaultSliderValue = DEFAULT_SNOW_MIN_RADIUS.toFloat()
            ) { newMinRadius ->
                minSnowRadius = newMinRadius.toInt()
                parameterSet = parameterSet.copy(
                    generatorParameters = parameterSet.generatorParameters.copy(
                        shapeProvider = {
                            createShowParticle(
                                IntRange(
                                    minSnowRadius,
                                    maxSnowRadius
                                )
                            )
                        }
                    )
                )
            }
            SliderWithValueText(
                title = "Max radius:",
                modifier = modifier,
                sliderRange = 1f..10f,
                defaultSliderValue = DEFAULT_SNOW_MAX_RADIUS.toFloat()
            ) { newMaxRadius ->
                maxSnowRadius = newMaxRadius.toInt()
                parameterSet = parameterSet.copy(
                    generatorParameters = parameterSet.generatorParameters.copy(
                        shapeProvider = {
                            createShowParticle(
                                IntRange(
                                    minSnowRadius,
                                    newMaxRadius.toInt()
                                )
                            )
                        }
                    )
                )
            }

            SwitchWithText(
                title = "Random initial X&Y:",
                modifier = modifier,
                isChecked = parameterSet.generatorParameters.randomizeInitialXY
            ) { checked ->
                parameterSet = parameterSet.copy(
                    generatorParameters = parameterSet.generatorParameters.copy(
                        randomizeInitialXY = checked
                    )
                )
            }
            Spacer(modifier = Modifier.padding(8.dp))

            SwitchWithText(
                title = "Auto Reset Particles:",
                modifier = modifier,
                isChecked = parameterSet.particleSystemParameters.autoResetParticles,
            ) { checked ->
                parameterSet = parameterSet.copy(
                    particleSystemParameters = parameterSet.particleSystemParameters.copy(
                        autoResetParticles = checked
                    )
                )
            }
            Spacer(modifier = Modifier.padding(8.dp))

            SwitchWithText(
                title = "Auto reset system",
                modifier = modifier,
                isChecked = parameterSet.particleSystemParameters.restartWhenAllDead,
            ) { checked ->
                parameterSet = parameterSet.copy(
                    particleSystemParameters = parameterSet.particleSystemParameters.copy(
                        restartWhenAllDead = checked
                    )
                )
            }
        }
    }
}
