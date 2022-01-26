package com.github.wangyung.persona.particle

import android.util.Log
import android.util.Size
import androidx.annotation.VisibleForTesting
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
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.system.measureTimeMillis

private const val TAG = "ParticlesSystem"
private const val ONE_SEC_MS = 1000L

/**
 * The fundamental interface of the particle system.
 */
interface ParticleSystem {
    /**
     * The dimension of the particle system. All particles would only be alive in the area.
     */
    val dimension: Size

    /**
     * The [ParticleSystemParameters] to setup the particle system.
     */
    val parameters: ParticleSystemParameters

    /**
     * The list of the particles.
     */
    val particles: List<Particle>

    /**
     * It is true if the particle system is running.
     */
    val isRunning: Boolean

    /**
     * The flow of the iterations in the particle system. The system is using a [StateFlow] to
     * update the state and trigger the [ParticleBox] to draw the state.
     */
    val iterationFlow: StateFlow<Long>

    /**
     * Stops the particle system.
     */
    fun stop()

    /**
     * Starts the particle system.
     */
    fun start()
}

/**
 * Creates the particle system.
 */
fun particleSystem(
    dimension: Size,
    parameters: ParticleSystemParameters,
    generator: ParticleGenerator,
    autoStart: Boolean = true,
    transformation: ParticleTransformation = LinearTranslateTransformation(),
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
): ParticleSystem =
    DefaultParticleSystem(
        dimension = dimension,
        parameters = parameters,
        generator = generator,
        transformation = transformation,
        autoStart = autoStart,
        coroutineDispatcher = coroutineDispatcher
    )

/**
 * A particles system that generates and manipulate particles within the size (width, height).
 * It updates the iteration as [StateFlow] in a coroutine according to the given coroutine
 * dispatcher. It updates all particles by [ParticleSystemParameters.fps] times per second.
 */
@Suppress("LongParameterList")
class DefaultParticleSystem internal constructor(
    override val dimension: Size,
    override val parameters: ParticleSystemParameters,
    private val generator: ParticleGenerator,
    private val transformation: ParticleTransformation,
    autoStart: Boolean,
    coroutineDispatcher: CoroutineDispatcher,
) : ParticleSystem {
    override val particles: List<Particle>
        get() = mutableParticles

    private var mutableParticles: List<MutableParticle> = generator.createParticles()
    @VisibleForTesting
    internal val notAliveParticleIds: MutableSet<Long> = mutableSetOf()

    override var isRunning: Boolean = false
        private set

    private val coroutineScope: CoroutineScope =
        CoroutineScope(coroutineDispatcher + SupervisorJob())
    private var mutableIterationStateFlow: MutableStateFlow<Long> = MutableStateFlow(0L)
    override val iterationFlow: StateFlow<Long> = mutableIterationStateFlow

    init {
        if (autoStart) {
            start()
        }
    }

    override fun stop() {
        isRunning = false
    }

    override fun start() {
        if (isRunning) return

        isRunning = true
        Log.d(TAG, "The particle system is started")
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
            Log.d(TAG, "The particle system is stopped")
        }
    }

    private fun reset() {
        notAliveParticleIds.clear()
        mutableParticles = generator.createParticles()
        mutableIterationStateFlow.value = 0
    }

    private fun updateParticles(iteration: Long) {
        mutableParticles.fastForEach {
            it.update(iteration)
        }
    }

    private fun MutableParticle.update(iteration: Long) {
        if (!isAlive) return

        this.iteration = iteration
        if (isOutOfBound(dimension.width, dimension.height)) {
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

@OptIn(ExperimentalContracts::class)
internal inline fun <T> List<T>.fastForEach(action: (T) -> Unit) {
    contract { callsInPlace(action) }
    for (index in indices) {
        val item = get(index)
        action(item)
    }
}
