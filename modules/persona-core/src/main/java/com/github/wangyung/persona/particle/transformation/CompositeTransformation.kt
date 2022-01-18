package com.github.wangyung.persona.particle.transformation

import android.util.Log
import com.github.wangyung.persona.particle.MutableParticle

private const val TAG = "CompositeTransformation"

/**
 * The particle transformation that composites other transformations.
 */
class CompositeTransformation(
    initial: List<ParticleTransformation> = emptyList()
) : ParticleTransformation {

    private val particleTransformations: MutableList<ParticleTransformation> = mutableListOf()

    fun add(particleTransformation: ParticleTransformation) =
        particleTransformations.add(particleTransformation)

    fun remove(particleTransformation: ParticleTransformation) =
        particleTransformations.remove(particleTransformation)

    fun clear() = particleTransformations.clear()

    override fun transform(particle: MutableParticle, iteration: Long) =
        particleTransformations.forEach {
            it.transform(particle = particle, iteration = iteration)
        }

    init {
        if (initial.isEmpty()) {
            Log.w(TAG, "No particle transformation, use LinearTranslateTransformation as default")
            particleTransformations.add(LinearTranslateTransformation())
        } else {
            particleTransformations.addAll(initial)
        }
    }
}
