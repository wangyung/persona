package com.github.wangyung.persona.path

import com.github.wangyung.persona.particle.fastForEach
import com.github.wangyung.persona.path.transformation.PathTransformation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

private const val ONE_SEC_MS = 1000L

/**
 * The parameters for [PathSystem].
 *
 * @property fps The frequency of updating the [PathSystem] per second.
 */
data class PathSystemParameters(
    val fps: Int = 60,
)

/**
 * The fundamental interface of the path system. It is the sibling of the
 * [com.github.wangyung.persona.particle.ParticleSystem] for the shapes that are described by the
 * points of a path instead of the independent particles, ex: a text outline that changes its
 * shape.
 */
interface PathSystem {
    /**
     * The [PathSystemParameters] to setup the path system.
     */
    val parameters: PathSystemParameters

    /**
     * The list of the morphable paths.
     */
    val paths: List<MorphablePath>

    /**
     * It is true if the path system is running.
     */
    val isRunning: Boolean

    /**
     * The flow of the iterations in the path system. The system is using a [StateFlow] to update
     * the state and trigger the renderer to draw the state.
     */
    val iterationFlow: StateFlow<Long>

    /**
     * Stops the path system.
     */
    fun stop()

    /**
     * Starts the path system.
     */
    fun start()
}

/**
 * Creates the path system.
 */
fun pathSystem(
    paths: List<MorphablePath>,
    transformation: PathTransformation,
    parameters: PathSystemParameters = PathSystemParameters(),
    autoStart: Boolean = true,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
): PathSystem =
    DefaultPathSystem(
        paths = paths,
        transformation = transformation,
        parameters = parameters,
        autoStart = autoStart,
        coroutineDispatcher = coroutineDispatcher,
    )

/**
 * A path system that updates the points of every [MorphablePath] by the given
 * [PathTransformation]. It updates the iteration as [StateFlow] in a coroutine according to the
 * given coroutine dispatcher, [PathSystemParameters.fps] times per second.
 */
class DefaultPathSystem internal constructor(
    override val paths: List<MorphablePath>,
    private val transformation: PathTransformation,
    override val parameters: PathSystemParameters,
    autoStart: Boolean,
    coroutineDispatcher: CoroutineDispatcher,
) : PathSystem {

    override var isRunning: Boolean = false
        private set

    private val coroutineScope: CoroutineScope =
        CoroutineScope(coroutineDispatcher + SupervisorJob())
    private val mutableIterationStateFlow: MutableStateFlow<Long> = MutableStateFlow(0L)
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
        coroutineScope.launch {
            while (isRunning) {
                val executedTimeMs = measureTimeMillis {
                    updatePaths(mutableIterationStateFlow.value++)
                }
                delay(timeMillis = ONE_SEC_MS / parameters.fps - executedTimeMs)
            }
        }
    }

    private fun updatePaths(iteration: Long) {
        paths.fastForEach {
            transformation.transform(it, iteration)
        }
    }
}
