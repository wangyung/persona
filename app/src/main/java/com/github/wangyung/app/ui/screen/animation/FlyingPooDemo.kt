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
import com.github.wangyung.app.model.DEFAULT_POO_MAX_ROTATIONAL_SPEED
import com.github.wangyung.app.model.DEFAULT_POO_MIN_ROTATIONAL_SPEED

@Composable
fun FlyingPooDemo() {
    val animationType = AnimationType.FlyingPoo
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
                    generatorParameters = parameterSet.generatorParameters.copy(
                        count = newCount.toInt()
                    )
                )
            }

            SliderWithValueText(
                title = "Min rotational speed:",
                modifier = modifier,
                sliderRange = 1f..30f,
                defaultSliderValue = DEFAULT_POO_MIN_ROTATIONAL_SPEED
            ) { newRotationalSpeed ->
                val rangeEnd = parameterSet.generatorParameters.zRotationalSpeedRange.endInclusive
                parameterSet = parameterSet.copy(
                    generatorParameters = parameterSet.generatorParameters.copy(
                        zRotationalSpeedRange = newRotationalSpeed.toFloat().rangeTo(rangeEnd),
                    )
                )
            }
            SliderWithValueText(
                title = "Max rotational speed:",
                modifier = modifier,
                sliderRange = 1f..30f,
                defaultSliderValue = DEFAULT_POO_MAX_ROTATIONAL_SPEED
            ) { newRotationalSpeed ->
                val rangeStart = parameterSet.generatorParameters.zRotationalSpeedRange.start
                parameterSet = parameterSet.copy(
                    generatorParameters = parameterSet.generatorParameters.copy(
                        zRotationalSpeedRange = rangeStart.rangeTo(newRotationalSpeed.toFloat()),
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
