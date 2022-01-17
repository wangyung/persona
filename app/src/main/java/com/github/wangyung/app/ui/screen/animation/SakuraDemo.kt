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

@Composable
fun SakuraDemo() {
    val animationType = AnimationType.Sakura
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
        showMoon = true,
        parameterSet = parameterSet,
        shapeProvider = animationType.toShapeProvider(LocalContext.current.resources)
    ) {
        val modifier = Modifier.fillMaxWidth()
        Column(modifier = modifier) {
            SliderWithValueText(
                title = "Particle Count:",
                modifier = modifier,
                sliderRange = 1f..50f,
                intOnly = true,
                defaultSliderValue = parameterSet.generatorParameters.count.toFloat()
            ) { newCount ->
                parameterSet = parameterSet.copy(
                    generatorParameters = parameterSet.generatorParameters.copy(count = newCount.toInt())
                )
            }

            SliderWithValueText(
                title = "Min speed:",
                modifier = modifier,
                sliderRange = 1f..10f,
                defaultSliderValue = parameterSet.generatorParameters.speedRange.start
            ) { newMinSpeed ->
                parameterSet = parameterSet.copy(
                    generatorParameters = parameterSet.generatorParameters.copy(
                        speedRange = newMinSpeed.toFloat().rangeTo(
                            parameterSet.generatorParameters.speedRange.endInclusive
                        )
                    )
                )
            }
            SliderWithValueText(
                title = "Max speed:",
                modifier = modifier,
                sliderRange = 1f..10f,
                defaultSliderValue = parameterSet.generatorParameters.speedRange.endInclusive
            ) { newMaxSpeed ->
                parameterSet = parameterSet.copy(
                    generatorParameters = parameterSet.generatorParameters.copy(
                        speedRange = parameterSet.generatorParameters.speedRange.start.rangeTo(
                            newMaxSpeed.toFloat()
                        )
                    )
                )
            }
            val sourceEdges = parameterSet.generatorParameters.sourceEdges.toMutableSet()
            StartEdgeCheckBoxes(
                modifier = modifier,
                sourceEdges = sourceEdges,
                onCheckedChange = { edge, checked ->
                    if (checked) {
                        sourceEdges.add(edge)
                    } else {
                        sourceEdges.remove(edge)
                    }
                    parameterSet = parameterSet.copy(
                        generatorParameters = parameterSet.generatorParameters.copy(
                            sourceEdges = sourceEdges
                        )
                    )
                })

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
