package com.github.wangyung.persona.particle.generator

import android.util.Size
import com.github.wangyung.persona.particle.Instinct
import com.github.wangyung.persona.particle.MutableParticle
import com.github.wangyung.persona.particle.ParticleShape
import com.github.wangyung.persona.particle.generator.parameter.InitialConstraints
import com.github.wangyung.persona.particle.generator.parameter.ParticleGeneratorParameters
import com.github.wangyung.persona.particle.generator.parameter.SourceEdge
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * The particle generator that generates the particle randomly in the given [Size].
 */
class RandomizeParticleGenerator(
    private val parameters: ParticleGeneratorParameters,
    private val dimension: Size,
    private val shapeProvider: ShapeProvider,
) : ParticleGenerator {

    private val sourceEdgeSet: List<SourceEdge> = parameters.sourceEdges.toList()
    private var autoIncrementId: AtomicLong = AtomicLong(0)
    private val initialConstraints: List<InitialConstraints>? =
        parameters.constraints?.filterIsInstance(InitialConstraints::class.java)

    override fun resetParticle(particle: MutableParticle) {
        val (x, y) = getRandomXY(particle.instinct, initialConstraints = initialConstraints)
        particle.x = x
        particle.y = y
        val shape = particle.instinct.shape
        val particleInstinct = Instinct(
            speed = getRandomFloatSafely(parameters.speedRange),
            angle = getRandomFloatSafely(parameters.angleRange),
            scaleX = getRandomFloatSafely(parameters.scaleRange),
            scaleY = getRandomFloatSafely(parameters.scaleRange),
            width = getRandomWidth(shape),
            height = getRandomHeight(shape),
            xRotationalSpeed = getRandomFloatSafely(parameters.xRotationalSpeedRange),
            zRotationalSpeed = getRandomFloatSafely(parameters.zRotationalSpeedRange),
            shape = shape
        )
        particle.rotation = 0f
        particle.xRotationWidth = particleInstinct.width.toFloat()
        particle.instinct = particleInstinct
    }

    override fun createParticles(): List<MutableParticle> {
        val mutableParticles: MutableList<MutableParticle> = mutableListOf()
        repeat(parameters.count) {
            mutableParticles.add(createParticle(parameters.randomizeInitialXY))
        }
        return mutableParticles
    }

    private fun createParticle(randomizeInitialXY: Boolean): MutableParticle {
        val shape = shapeProvider.provide()
        val instinct = Instinct(
            width = getRandomWidth(shape),
            height = getRandomHeight(shape),
            speed = getRandomFloatSafely(parameters.speedRange),
            angle = getRandomFloatSafely(parameters.angleRange),
            xRotationalSpeed = getRandomFloatSafely(parameters.xRotationalSpeedRange),
            zRotationalSpeed = getRandomFloatSafely(parameters.zRotationalSpeedRange),
            scaleX = getRandomFloatSafely(parameters.scaleRange),
            scaleY = getRandomFloatSafely(parameters.scaleRange),
            startOffset = Random.nextInt(parameters.startOffsetRange),
            shape = shape
        )
        val (x, y) = if (randomizeInitialXY) {
            Pair(getRandomX().toFloat(), getRandomY().toFloat())
        } else {
            getRandomXY(instinct, initialConstraints = initialConstraints)
        }
        return MutableParticle(
            id = autoIncrementId.getAndIncrement(),
            x = x,
            y = y,
            scaleX = instinct.scaleX,
            scaleY = instinct.scaleY,
            instinct = instinct,
        )
    }

    @Suppress("ForbiddenComment")
    private fun getRandomXY(
        instinct: Instinct,
        initialConstraints: List<InitialConstraints>?
    ): Pair<Float, Float> {
        val halfWidth = instinct.width / 2
        val halfHeight = instinct.height / 2

        // If source edge set is empty, use TOP as fallback.
        if (sourceEdgeSet.isEmpty()) {
            return Pair(getRandomX().coerceIn(halfWidth, dimension.width).toFloat(), 0f)
        }

        val edgeCount = sourceEdgeSet.count()
        val index = if (edgeCount > 1) {
            Random.nextInt(0, edgeCount)
        } else {
            edgeCount - 1
        }
        // TODO: Support multiple constraints later.
        return when (sourceEdgeSet[index]) {
            SourceEdge.TOP -> {
                Pair(
                    if (initialConstraints.isNullOrEmpty()) {
                        (getRandomX() - halfWidth).toFloat()
                    } else {
                        (getRandomFloatSafely(initialConstraints[0].limitRange) * dimension.width) - halfWidth
                    },
                    -halfHeight.toFloat()
                )
            }
            SourceEdge.BOTTOM -> {
                Pair(
                    (getRandomX() - halfWidth).toFloat(),
                    dimension.height.toFloat()
                )
            }
            SourceEdge.LEFT -> {
                Pair(
                    0f,
                    if (initialConstraints == null) {
                        (getRandomY() - halfHeight).toFloat()
                    } else {
                        getRandomFloatSafely(initialConstraints[0].limitRange) * dimension.height - halfHeight
                    }
                )
            }
            SourceEdge.RIGHT -> {
                Pair(
                    dimension.width.toFloat(),
                    (getRandomY() - halfHeight).toFloat()
                )
            }
        }
    }

    private fun getRandomX(): Int = Random.nextInt(dimension.width)
    private fun getRandomY(): Int = Random.nextInt(dimension.height)

    private fun getRandomWidth(shape: ParticleShape): Int =
        when (shape) {
            is ParticleShape.Text -> {
                shape.nativePaint.getTextBounds(shape.text, 0, shape.text.count(), shape.textBounds)
                shape.textBounds.width()
            }
            is ParticleShape.Path -> {
                shape.path.getBounds().width.toInt()
            }
            is ParticleShape.Image -> {
                if (shape.useImageSize) {
                    shape.image.width
                } else {
                    Random.nextInt(parameters.particleWidthRange)
                }
            }
            is ParticleShape.Circle -> {
                shape.radius
            }
            else -> Random.nextInt(parameters.particleWidthRange)
        }

    private fun getRandomHeight(shape: ParticleShape): Int =
        when (shape) {
            is ParticleShape.Text -> {
                shape.nativePaint.getTextBounds(shape.text, 0, shape.text.count(), shape.textBounds)
                shape.textBounds.height()
            }
            is ParticleShape.Path -> {
                shape.path.getBounds().height.toInt()
            }
            is ParticleShape.Image -> {
                if (shape.useImageSize) {
                    shape.image.height
                } else {
                    Random.nextInt(parameters.particleWidthRange)
                }
            }
            is ParticleShape.Circle -> {
                shape.radius
            }
            else -> Random.nextInt(parameters.particleHeightRange)
        }

    @Suppress("SwallowedException")
    private fun getRandomFloatSafely(floatRange: ClosedFloatingPointRange<Float>): Float =
        try {
            floatRange.nextFloat()
        } catch (e: IllegalArgumentException) {
            floatRange.start
        }
}
