package com.github.wangyung.persona.particle

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.compose.ui.util.fastForEach
import com.github.wangyung.persona.particle.generator.ParticleGenerator
import com.github.wangyung.persona.particle.transformation.LinearTranslateTransformation
import com.github.wangyung.persona.particle.transformation.ParticleTransformation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

private const val TAG = "ParticlesSystem"
private const val ONE_SEC_MS = 1000L

/**
 * A particles system that generates and manipulate particles within the size (width, height).
 * It updates the iteration as [StateFlow] in a coroutine according to the given coroutine
 * dispatcher. It updates all particles by [ParticleSystemParameters.fps] times per second.
 */
@Suppress("LongParameterList")
class ParticleSystem(
    val width: Int,
    val height: Int,
    val parameters: ParticleSystemParameters,
    private val generator: ParticleGenerator,
    private val transformation: ParticleTransformation = LinearTranslateTransformation(),
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    val particles: List<Particle>
        get() = mutableParticles

    private var mutableParticles: List<MutableParticle> = generator.createParticles()
    @VisibleForTesting
    internal val notAliveParticleIds: MutableSet<Long> = mutableSetOf()

    var isRunning: Boolean = true
        private set

    private val coroutineScope: CoroutineScope =
        CoroutineScope(coroutineDispatcher + SupervisorJob())
    private var mutableIterationStateFlow: MutableStateFlow<Long> = MutableStateFlow(0L)
    val iterationFlow: StateFlow<Long> = mutableIterationStateFlow

    init {
        start()
    }

    /**
     * Stop the particle system.
     */
    fun stop() {
        isRunning = false
    }

    private fun start() {
        coroutineScope.launch {
            while (isRunning) {
                val executedTimeMs = measureTimeMillis {
                    updateParticles(mutableIterationStateFlow.value++)
                }
                if (notAliveParticleIds.count() == particles.count()) {
                    if (parameters.restartWhenAllDead) {
                        reset()
                    } else {
                        isRunning = false
                    }
                } else {
                    delay(timeMillis = ONE_SEC_MS / parameters.fps - executedTimeMs)
                }
            }
        }
    }

    private fun reset() {
        notAliveParticleIds.clear()
        mutableParticles = generator.createParticles()
        mutableIterationStateFlow.value = 0
    }

    private fun updateParticles(iteration: Long) =
        mutableParticles.fastForEach {
            it.update(iteration)
        }

    private fun MutableParticle.update(iteration: Long) {
        if (!isAlive) return

        this.iteration = iteration
        if (isOutOfBound(width, height)) {
            if (parameters.autoResetParticles) {
                initialIteration = iteration
                generator.resetParticle(this)
            } else {
                isAlive = false
            }
        } else if (shouldBeDraw) {
            transformation.transform(this, iteration)
        }
        if (!isAlive) {
            Log.d(TAG, "particle$id is not alive at iteration $iteration")
            notAliveParticleIds.add(id)
        }
    }

    /**
     * checks if the particle is out of bound in the system.
     * It doesn't consider the width and height after rotation. So it may have some inaccuracy.
     */
    private fun Particle.isOutOfBound(width: Int, height: Int): Boolean {
        val halfWidth: Int = (this.instinct.width / 2).coerceAtLeast(1)
        val halfHeight: Int = (this.instinct.height / 2).coerceAtLeast(1)
        return x + halfWidth < 0 ||
                x - halfWidth > width ||
                y + halfHeight < 0 ||
                y - halfHeight > height
    }
}
