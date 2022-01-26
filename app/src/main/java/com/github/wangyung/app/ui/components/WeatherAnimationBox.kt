package com.github.wangyung.app.ui.components

import android.util.Size
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.wangyung.app.model.AnimationParameterSet
import com.github.wangyung.app.model.AnimationType
import com.github.wangyung.app.viewmodel.ParticlesViewModel
import com.github.wangyung.persona.app.R
import com.github.wangyung.persona.particle.generator.ShapeProvider
import com.github.wangyung.persona.ui.component.ParticleBox
import kotlin.math.sin

private const val MOON_WIDTH = 64
private const val MOON_POSITION = 0.5f
private const val PADDING_FOR_MOON = 80

@Suppress("ComplexMethod")
@Composable
fun WeatherAnimationBox(
    modifier: Modifier,
    animationType: AnimationType,
    parameterSet: AnimationParameterSet,
    shapeProvider: ShapeProvider,
    showMoon: Boolean = false,
    showLandscape: Boolean = true,
    showDebugLayer: Boolean = false,
) {
    BoxWithConstraints(modifier) {
        WeatherAnimationInternal(
            animationType = animationType,
            constraints = constraints,
            showMoon = showMoon,
            showLandscape = showLandscape,
            parameterSet = parameterSet,
            showDebugLayer = showDebugLayer,
            shapeProvider = shapeProvider,
        )
    }
}

@Suppress("ComplexMethod", "ComplexCondition")
@Composable
private fun WeatherAnimationInternal(
    animationType: AnimationType,
    constraints: Constraints,
    parameterSet: AnimationParameterSet,
    showMoon: Boolean,
    showLandscape: Boolean,
    showDebugLayer: Boolean,
    shapeProvider: ShapeProvider
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (backgroundLayer1, backgroundLayer2) = createRefs()
        Image(
            painter = painterResource(id = R.drawable.day),
            contentDescription = "Sky",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(backgroundLayer1) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )
        if (showLandscape) {
            Image(
                painter = painterResource(id = R.drawable.landscape),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(backgroundLayer2) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(backgroundLayer1.bottom)
                    }
            )
        }

        if (showMoon) {
            val width = constraints.maxWidth + MOON_WIDTH + PADDING_FOR_MOON
            val height = constraints.maxHeight + MOON_WIDTH + PADDING_FOR_MOON
            val offsetX = width * MOON_POSITION - MOON_WIDTH - PADDING_FOR_MOON
            val offsetY = height / 2 * (1 - sin(MOON_POSITION * 3).coerceIn(0f, 1f))

            Image(
                painter = painterResource(R.drawable.moon),
                contentDescription = "moon",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .offset(
                        x = with(LocalDensity.current) { offsetX.toDp() },
                        y = with(LocalDensity.current) { offsetY.toDp() }
                    )
                    .size(MOON_WIDTH.dp),
            )
        }

        val viewModel: ParticlesViewModel = viewModel()
        if (viewModel.particleSystem?.isRunning == false ||
            viewModel.generatorParameters != parameterSet.generatorParameters ||
            viewModel.particleSystemParameters != parameterSet.particleSystemParameters ||
            viewModel.transformationParameters != parameterSet.transformationParameters
        ) {
            val theWidth = constraints.maxWidth
            val theHeight: Int = when (animationType) {
                is AnimationType.FlyingBird, is AnimationType.TwinkleStar ->
                    (constraints.maxHeight / 1.5f).toInt()
                else -> constraints.maxHeight
            }
            viewModel.startNewParticlesSystem(
                systemParameters = parameterSet.particleSystemParameters,
                generatorParameters = parameterSet.generatorParameters,
                transformation = animationType.toParticleTransformation(
                    parameterSet.transformationParameters
                ),
                dimension = Size(theWidth, theHeight),
                shapeProvider = shapeProvider
            )
        }
        val particleModifier = when (animationType) {
            is AnimationType.FlyingBird, is AnimationType.TwinkleStar -> Modifier
                .fillMaxWidth()
                .height(with(LocalDensity.current) { constraints.maxHeight.toDp() / 1.5f })
            else -> Modifier.fillMaxSize()
        }
        if (showDebugLayer) {
            Surface(modifier = particleModifier, color = Color.Green.copy(alpha = 0.3f)) { }
        }

        val particleSystem = viewModel.particleSystem ?: return@ConstraintLayout
        ParticleBox(modifier = particleModifier, particleSystem = particleSystem)
    }
}
