package com.github.wangyung.persona.particle

import androidx.annotation.FloatRange
import androidx.annotation.IntRange

/**
 * The particle is the basic unit that represents in the [ParticleSystem].
 * The [ParticlesBox] would draw it on the canvas.
 *
 * @property xRotationWidth Simulate the rotation on X axis.
 */
@Suppress("ForbiddenComment")
interface Particle {
    val id: Long
    val initialIteration: Long
    val iteration: Long
    val x: Float
    val y: Float
    val rotation: Float
    val scaleX: Float
    val scaleY: Float
    val alpha: Float
    val color: Color
    val instinct: Instinct
    val isAlive: Boolean
    val shouldBeDraw: Boolean
        get() = isAlive && iteration >= instinct.startOffset

    // TODO: Replace it after using Matrix to handle the rotation.
    val xRotationWidth: Float
}

/**
 * The mutable version of [Particle]. Only the id and instinct are immutable, other properties are
 * mutable.
 */
@Suppress("LongParameterList")
class MutableParticle(
    override val id: Long,
    override var initialIteration: Long = 0,
    override var iteration: Long = 0,
    override var x: Float = 0f,
    override var y: Float = 0f,
    override var rotation: Float = 0f,
    override var scaleX: Float = 1f,
    override var scaleY: Float = 1f,
    override var alpha: Float = 1f,
    override var color: Color = Color.Transparent,
    override var instinct: Instinct,
    override var xRotationWidth: Float = instinct.shape.width.toFloat(),
) : Particle {
    override var isAlive: Boolean = true
}

/**
 * The instinct properties of the particle. All are immutable.
 */
data class Instinct(
    @IntRange(from = 1)
    val width: Int = 1,

    @IntRange(from = 1)
    val height: Int = 1,

    @FloatRange(from = 0.0)
    val speed: Float = 0f,

    @FloatRange(from = 0.0, to = 360.0)
    val angle: Float = 0f,

    @FloatRange(from = 0.0)
    val xRotationalSpeed: Float = 0f,

    @FloatRange(from = 0.0)
    val zRotationalSpeed: Float = 0f,

    @FloatRange(from = 0.0)
    val scaleX: Float = 1f,

    @FloatRange(from = 0.0)
    val scaleY: Float = 1f,

    @FloatRange(from = 0.0, to = 1.0)
    val alpha: Float = 1f,

    @IntRange(from = 0)
    val startOffset: Int = 0,

    val shape: ParticleShape,
)

/**
 * Creates the [Particle] instance.
 */
@Suppress("LongParameterList")
fun particleOf(
    id: Long,
    initialIteration: Long = 0,
    iteration: Long = 0,
    x: Float = 0f,
    y: Float = 0f,
    rotation: Float = 0f,
    scaleX: Float = 1f,
    scaleY: Float = 1f,
    alpha: Float = 1f,
    color: Color = Color.Transparent,
    instinct: Instinct
): Particle = MutableParticle(
    id = id,
    initialIteration = initialIteration,
    iteration = iteration,
    x = x,
    y = y,
    rotation = rotation,
    scaleX = scaleX,
    scaleY = scaleY,
    alpha = alpha,
    color = color,
    instinct = instinct
)
