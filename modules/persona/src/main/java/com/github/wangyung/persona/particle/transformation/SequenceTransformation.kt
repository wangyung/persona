package com.github.wangyung.persona.particle.transformation

import androidx.annotation.VisibleForTesting
import com.github.wangyung.persona.particle.MutableParticle

/**
 * The transformation that transforms the particle sequentially in different iteration. Unlike
 * [CompositeTransformation], it only runs single transformation at the same time.
 */
class SequenceTransformation : ParticleTransformation, Durationable {

    private val particleTransformations: MutableList<DurationalDecoratorTransformation> =
        mutableListOf()

    override var duration: Long = 0
        private set

    /**
     * Adds a [ParticleTransformation] for the given [duration].
     */
    fun add(particleTransformation: ParticleTransformation, duration: Long) {
        particleTransformations.add(
            DurationalDecoratorTransformation(
                startFrom = this.duration,
                duration = duration,
                particleTransformation = particleTransformation
            )
        )
        this.duration += duration
    }

    /**
     * Clears all [ParticleTransformation]s.
     */
    fun clear() {
        particleTransformations.clear()
        duration = 0
    }

    @Suppress("ReturnCount")
    override fun transform(particle: MutableParticle, iteration: Long) {
        if (particleTransformations.isEmpty()) return

        val iterationForParticle = iteration - (particle.initialIteration + particle.instinct.startOffset)
        if (iterationForParticle < 0) {
            return
        }

        val durationalTransformation =
            getActivateTransformation(iterationForParticle) ?: kotlin.run {
                if (particle.isAlive) {
                    particle.isAlive = false
                }
                return
            }

        durationalTransformation.transform(particle = particle, iteration = iterationForParticle)
    }

    @VisibleForTesting
    internal fun getActivateTransformation(iteration: Long): DurationalDecoratorTransformation? {
        return particleTransformations.firstOrNull { durationalTransformation ->
            val from = durationalTransformation.startFrom
            val to = from + durationalTransformation.duration
            iteration in from until to
        }
    }
}
