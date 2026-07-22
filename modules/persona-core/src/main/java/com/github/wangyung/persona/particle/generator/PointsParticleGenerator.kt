package com.github.wangyung.persona.particle.generator

import com.github.wangyung.persona.particle.Instinct
import com.github.wangyung.persona.particle.MutableParticle
import com.github.wangyung.persona.particle.ParticlePoint
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * The particle generator that generates one particle at each given [ParticlePoint]. The id of the
 * particle is the index of its point, so the id is stable across [createParticles] invocations and
 * can be used to look up the per-particle data, ex: the target point of the particle in
 * [com.github.wangyung.persona.particle.transformation.MoveToTargetTransformation].
 */
class PointsParticleGenerator(
    private val points: List<ParticlePoint>,
    private val shapeProvider: ShapeProvider,
    private val startOffsetRange: IntRange = 0..0,
) : ParticleGenerator {

    init {
        require(points.isNotEmpty()) { "points cannot be empty" }
    }

    override fun createParticles(): List<MutableParticle> =
        points.mapIndexed { index, point ->
            val shape = shapeProvider.provide()
            MutableParticle(
                id = index.toLong(),
                x = point.x,
                y = point.y,
                instinct = Instinct(
                    width = shape.width.coerceAtLeast(1),
                    height = shape.height.coerceAtLeast(1),
                    startOffset = Random.nextInt(startOffsetRange),
                    shape = shape,
                ),
            )
        }

    /**
     * Moves the particle back to its original point.
     */
    override fun resetParticle(particle: MutableParticle) {
        val point = points[(particle.id % points.size).toInt()]
        particle.x = point.x
        particle.y = point.y
    }
}
