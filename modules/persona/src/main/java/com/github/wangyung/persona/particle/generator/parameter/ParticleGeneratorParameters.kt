package com.github.wangyung.persona.particle.generator.parameter

import com.github.wangyung.persona.particle.ParticleShape
import com.github.wangyung.persona.particle.ParticleSystem

private const val DEFAULT_WIDTH = 12
private const val DEFAULT_HEIGHT = 12

/**
 * The parameters for creating particles randomly.
 *
 * @property count The number of particles should be created.
 * @property particleWidthRange The range of the width for creating the particle.
 * @property particleHeightRange The range of the height for creating the particle.
 * @property randomizeInitialXY If true, set the x, y randomly when creating the particle.
 * @property minSpeed The min speed for a particle. The unit is pixel per iteration.
 * @property maxSpeed The max speed for a particle. The unit is pixel per iteration.
 * @property minScale The min scale for a particle.
 * @property maxScale The max scale for a particle.
 * @property angleRange The range of angles for a particle.
 * @property minRotationalSpeed The min rotational speed for a particle. The unit is pixel per iteration.
 * @property maxRotationalSpeed The max rotational speed for a particle. The unit is pixel per iteration.
 * @property startOffsetRange The range of delay of a particle. The delay would be only effective
 * for the first draw on the canvas. It wouldn't be effective when the particle is reset after out of the system.
 * @property sourceEdges The set of the edges where the particle would appear at the beginning.
 * For example, if the edges are [SourceEdge.TOP] and [SourceEdge.RIGHT], the particle will appear
 * from top or right of the [ParticleSystem].
 * @property constraints The list of constraints that would be applied when generating the
 * particles.
 * @property shapeProvider A lambda for creating the [ParticleShape]
 */
@Suppress("ForbiddenComment")
data class ParticleGeneratorParameters(
    val count: Int,
    val particleWidthRange: IntRange = IntRange(DEFAULT_WIDTH, DEFAULT_WIDTH),
    val particleHeightRange: IntRange = IntRange(DEFAULT_HEIGHT, DEFAULT_HEIGHT),
    val randomizeInitialXY: Boolean = true,
    val speedRange: ClosedFloatingPointRange<Float> = 0f..0f,
    // TODO: divide into scaleX and scaleY
    val scaleRange: ClosedFloatingPointRange<Float> = 1f..1f,
    val angleRange: ClosedFloatingPointRange<Float> = 0f..0f,
    val xRotationalSpeedRange: ClosedFloatingPointRange<Float> = 0f..0f,
    val zRotationalSpeedRange: ClosedFloatingPointRange<Float> = 0f..0f,
    val startOffsetRange: IntRange = 0..0,
    val sourceEdges: Set<SourceEdge> = setOf(SourceEdge.TOP),
    val constraints: List<Constraints>? = null
)
