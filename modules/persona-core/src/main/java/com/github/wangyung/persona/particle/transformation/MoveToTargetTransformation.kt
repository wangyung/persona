package com.github.wangyung.persona.particle.transformation

import androidx.annotation.IntRange
import com.github.wangyung.persona.particle.MutableParticle
import com.github.wangyung.persona.particle.ParticlePoint

/**
 * The transformation that moves each particle from where it is to the target point provided by
 * the [ParticleTargetProvider] within [duration] iterations. The moving progress is shaped by the
 * given [Easing]. After [duration] iterations the particle stays at the target point.
 *
 * The start point of each particle is captured when the transformation is applied to the particle
 * at the first time, so it can be composed with other transformations by [SequenceTransformation],
 * ex: hold the particles for a while then move them to the target.
 */
class MoveToTargetTransformation(
    private val targetProvider: ParticleTargetProvider,
    @IntRange(from = 1)
    override val duration: Long,
    private val easing: Easing = Easing.EaseInOutCubic,
) : ParticleTransformation, Durationable {

    private class Origin(val point: ParticlePoint, val startIteration: Long)

    private val origins: MutableMap<Long, Origin> = mutableMapOf()

    override fun transform(particle: MutableParticle, iteration: Long) {
        val target = targetProvider.getTarget(particle) ?: return
        var origin = origins[particle.id]
        if (origin == null || iteration < origin.startIteration) {
            // The particle is seen at the first time, or the particle system was restarted.
            origin = Origin(ParticlePoint(particle.x, particle.y), iteration)
            origins[particle.id] = origin
        }
        val fraction = if (duration <= 0) {
            1f
        } else {
            ((iteration - origin.startIteration).toFloat() / duration).coerceIn(0f, 1f)
        }
        val easedFraction = easing.ease(fraction)
        particle.x = origin.point.x + (target.x - origin.point.x) * easedFraction
        particle.y = origin.point.y + (target.y - origin.point.y) * easedFraction
    }
}
