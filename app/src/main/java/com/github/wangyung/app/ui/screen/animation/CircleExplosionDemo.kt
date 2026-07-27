package com.github.wangyung.app.ui.screen.animation

import android.util.Size
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.wangyung.app.model.AnimationType
import com.github.wangyung.persona.particle.Instinct
import com.github.wangyung.persona.particle.MutableParticle
import com.github.wangyung.persona.particle.ParticlePoint
import com.github.wangyung.persona.particle.ParticleSystem
import com.github.wangyung.persona.particle.ParticleSystemParameters
import com.github.wangyung.persona.particle.generator.ParticleGenerator
import com.github.wangyung.persona.particle.particleSystem
import com.github.wangyung.persona.particle.transformation.CompositeTransformation
import com.github.wangyung.persona.particle.transformation.Easing
import com.github.wangyung.persona.particle.transformation.MoveToTargetTransformation
import com.github.wangyung.persona.particle.transformation.ParticleTargetProvider
import com.github.wangyung.persona.particle.transformation.ParticleTransformation
import com.github.wangyung.persona.particle.transformation.ScaleAndFadeTransformation
import com.github.wangyung.persona.particle.transformation.SequenceTransformation
import com.github.wangyung.persona.render.ComposeParticleShape
import com.github.wangyung.persona.ui.component.ParticleBox
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

private const val TWO_PI = (Math.PI * 2).toFloat()
private const val DEFAULT_PARTICLE_COUNT = 240
private const val DEFAULT_CIRCLE_RADIUS_PERCENT = 15
private const val DEFAULT_EXPLODE_DURATION = 60
private const val DEFAULT_HOLD_DURATION = 30
private const val PERCENT = 100f

// How far the particles fly, relative to the smaller side of the box.
private const val MIN_EXPLODE_DISTANCE_RATIO = 0.5f
private const val MAX_EXPLODE_DISTANCE_RATIO = 1.2f

// The small random rotation of the flying direction, in radians.
private const val ANGLE_JITTER = 0.15f
private const val MIN_DOT_RADIUS = 2
private const val MAX_DOT_RADIUS = 4
private const val FPS = 60

private const val CIRCLE_PARTICLE_ID = 0L

private val backgroundColor = Color(0xFF10101A)
private val circleColor = Color(0xFFFFB74D)
private val particleColors = listOf(
    Color.White,
    Color(0xFFFFF59D),
    Color(0xFFFFB74D),
    Color(0xFFFF7043),
    Color(0xFFEF5350),
)

@Composable
fun CircleExplosionDemo() {
    val animationType = AnimationType.CircleExplosion
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

        var particleCount by remember { mutableStateOf(DEFAULT_PARTICLE_COUNT) }
        var circleRadiusPercent by remember { mutableStateOf(DEFAULT_CIRCLE_RADIUS_PERCENT) }
        var explodeDuration by remember { mutableStateOf(DEFAULT_EXPLODE_DURATION) }
        var holdDuration by remember { mutableStateOf(DEFAULT_HOLD_DURATION) }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .background(backgroundColor)
        ) {
            val dimension = Size(constraints.maxWidth, constraints.maxHeight)
            val explosionParameters = CircleExplosionParameters(
                particleCount = particleCount,
                circleRadiusRatio = circleRadiusPercent / PERCENT,
                explodeDuration = explodeDuration.toLong(),
                holdDuration = holdDuration.toLong(),
            )
            val particleSystem = remember(explosionParameters, dimension) {
                createCircleExplosionParticleSystem(explosionParameters, dimension)
            }
            if (particleSystem != null) {
                DisposableEffect(particleSystem) {
                    onDispose { particleSystem.stop() }
                }
                ParticleBox(modifier = Modifier.fillMaxSize(), particleSystem = particleSystem)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        val scrollState = rememberScrollState()
        val modifier = Modifier.fillMaxWidth()
        Column(modifier = modifier.verticalScroll(scrollState)) {
            SliderWithValueText(
                title = "Particle Count:",
                modifier = modifier,
                sliderRange = 60f..600f,
                intOnly = true,
                defaultSliderValue = particleCount.toFloat()
            ) { newCount ->
                particleCount = newCount.toInt()
            }
            SliderWithValueText(
                title = "Circle Radius (% of the box):",
                modifier = modifier,
                sliderRange = 5f..30f,
                intOnly = true,
                defaultSliderValue = circleRadiusPercent.toFloat()
            ) { newPercent ->
                circleRadiusPercent = newPercent.toInt()
            }
            SliderWithValueText(
                title = "Explode Duration (iterations):",
                modifier = modifier,
                sliderRange = 20f..180f,
                intOnly = true,
                defaultSliderValue = explodeDuration.toFloat()
            ) { newDuration ->
                explodeDuration = newDuration.toInt()
            }
            SliderWithValueText(
                title = "Hold Duration (iterations):",
                modifier = modifier,
                sliderRange = 0f..120f,
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

private data class CircleExplosionParameters(
    val particleCount: Int,
    val circleRadiusRatio: Float,
    val explodeDuration: Long,
    val holdDuration: Long,
)

/**
 * Creates a particle system where one solid circle is drawn during the hold, then bursts into
 * the debris particles that explode radially outward with the randomized distances and
 * directions, fading out on the way.
 *
 * The circle and the debris follow their own [SequenceTransformation]:
 * - The circle: hold, then it dies and disappears when the explosion starts.
 * - The debris: hidden during the hold (by [Instinct.startOffset]) → move to the explosion
 *   target with the ease-out easing while fading, then they die.
 * When all particles are dead the system restarts (restartWhenAllDead), which resets the solid
 * circle and loops the animation.
 */
private fun createCircleExplosionParticleSystem(
    parameters: CircleExplosionParameters,
    dimension: Size,
): ParticleSystem? {
    if (dimension.width <= 0 || dimension.height <= 0 || parameters.particleCount <= 0) {
        return null
    }

    val (debrisPoints, targetPoints) = createExplosionPoints(parameters, dimension)
    val minSide = min(dimension.width, dimension.height)
    val generator = CircleExplosionParticleGenerator(
        center = ParticlePoint(dimension.width / 2f, dimension.height / 2f),
        circleRadius = (minSide * parameters.circleRadiusRatio).toInt(),
        debrisPoints = debrisPoints,
        debrisStartOffset = parameters.holdDuration.toInt(),
    )
    return particleSystem(
        dimension = dimension,
        parameters = ParticleSystemParameters(
            fps = FPS,
            autoResetParticles = false,
            restartWhenAllDead = true,
        ),
        generator = generator,
        transformation = createExplosionTransformation(parameters, targetPoints),
    )
}

/**
 * Creates the transformation that dispatches to the [SequenceTransformation] of the solid circle
 * or the one of the debris by the particle id.
 */
private fun createExplosionTransformation(
    parameters: CircleExplosionParameters,
    targetPoints: List<ParticlePoint>,
): ParticleTransformation {
    val explodeTargets = ParticleTargetProvider { particle ->
        targetPoints[((particle.id - 1) % targetPoints.size).toInt()]
    }
    val holdTransformation = ParticleTransformation { _, _ -> }
    // The circle only holds; when its sequence ends the sequence marks it dead, so it disappears
    // exactly when the explosion starts.
    val circleTransformation = SequenceTransformation().apply {
        add(holdTransformation, parameters.holdDuration)
    }
    // The debris appears at the hold duration (its startOffset), and the sequence timeline of a
    // particle starts from its startOffset, so no hold step is needed here.
    val debrisTransformation = SequenceTransformation().apply {
        add(
            CompositeTransformation(
                listOf(
                    MoveToTargetTransformation(
                        targetProvider = explodeTargets,
                        duration = parameters.explodeDuration,
                        easing = Easing.EaseOutCubic,
                    ),
                    ScaleAndFadeTransformation(
                        alphaDelta = 1f / parameters.explodeDuration,
                    ),
                )
            ),
            parameters.explodeDuration,
        )
    }
    return ParticleTransformation { particle, iteration ->
        if (particle.id == CIRCLE_PARTICLE_ID) {
            circleTransformation.transform(particle, iteration)
        } else {
            debrisTransformation.transform(particle, iteration)
        }
    }
}

/**
 * The generator that creates one solid circle at the [center] and one hidden debris dot at each
 * of the [debrisPoints]. The debris particles use [Instinct.startOffset] so they aren't drawn
 * until the explosion starts.
 */
private class CircleExplosionParticleGenerator(
    private val center: ParticlePoint,
    private val circleRadius: Int,
    private val debrisPoints: List<ParticlePoint>,
    private val debrisStartOffset: Int,
) : ParticleGenerator {

    override fun createParticles(): List<MutableParticle> {
        val particles = ArrayList<MutableParticle>(debrisPoints.size + 1)
        particles.add(
            MutableParticle(
                id = CIRCLE_PARTICLE_ID,
                x = center.x,
                y = center.y,
                instinct = Instinct(
                    width = circleRadius,
                    height = circleRadius,
                    shape = ComposeParticleShape.Circle(
                        color = circleColor,
                        radius = circleRadius,
                    ),
                ),
            )
        )
        debrisPoints.forEachIndexed { index, point ->
            val dotRadius = Random.nextInt(MIN_DOT_RADIUS, MAX_DOT_RADIUS + 1)
            particles.add(
                MutableParticle(
                    id = index + 1L,
                    x = point.x,
                    y = point.y,
                    instinct = Instinct(
                        width = dotRadius,
                        height = dotRadius,
                        startOffset = debrisStartOffset,
                        shape = ComposeParticleShape.Circle(
                            color = particleColors.random(),
                            radius = dotRadius,
                        ),
                    ),
                )
            )
        }
        return particles
    }

    override fun resetParticle(particle: MutableParticle) {
        if (particle.id == CIRCLE_PARTICLE_ID) {
            particle.x = center.x
            particle.y = center.y
        } else {
            val point = debrisPoints[((particle.id - 1) % debrisPoints.size).toInt()]
            particle.x = point.x
            particle.y = point.y
        }
    }
}

/**
 * Creates the points inside the solid circle and the explosion target of every point. The points
 * are spread evenly over the disk (the radius uses the square root of a random value, so the
 * density is uniform). Every particle flies radially in its own randomized distance and slightly
 * rotated direction, so the explosion looks organic instead of a perfectly growing disk.
 */
private fun createExplosionPoints(
    parameters: CircleExplosionParameters,
    dimension: Size,
): Pair<List<ParticlePoint>, List<ParticlePoint>> {
    val centerX = dimension.width / 2f
    val centerY = dimension.height / 2f
    val minSide = min(dimension.width, dimension.height)
    val circleRadius = minSide * parameters.circleRadiusRatio
    val distanceRatioRange = MAX_EXPLODE_DISTANCE_RATIO - MIN_EXPLODE_DISTANCE_RATIO

    val startPoints = ArrayList<ParticlePoint>(parameters.particleCount)
    val targetPoints = ArrayList<ParticlePoint>(parameters.particleCount)
    repeat(parameters.particleCount) { index ->
        val angle = TWO_PI * index / parameters.particleCount
        val startRadius = circleRadius * sqrt(Random.nextFloat())
        startPoints.add(
            ParticlePoint(
                centerX + startRadius * cos(angle),
                centerY + startRadius * sin(angle),
            )
        )
        val explodeDistance =
            minSide * (MIN_EXPLODE_DISTANCE_RATIO + distanceRatioRange * Random.nextFloat())
        val explodeAngle = angle + (Random.nextFloat() * 2f - 1f) * ANGLE_JITTER
        targetPoints.add(
            ParticlePoint(
                centerX + (startRadius + explodeDistance) * cos(explodeAngle),
                centerY + (startRadius + explodeDistance) * sin(explodeAngle),
            )
        )
    }
    return Pair(startPoints, targetPoints)
}
