package com.github.wangyung.persona.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.github.wangyung.persona.path.MorphablePath
import com.github.wangyung.persona.path.PathSystem

/**
 * Draws all [MorphablePath]s of the given [PathSystem] and redraws them on every iteration of the
 * system. All paths are combined into one path and drawn with the given [color] and [style], so
 * the holes of the shapes (ex: the inner contour of the letter O) are rendered correctly.
 */
@Composable
fun MorphablePathBox(
    modifier: Modifier,
    pathSystem: PathSystem,
    color: Color,
    style: DrawStyle = Fill,
) {
    val pathSystemState = remember {
        mutableStateOf(pathSystem)
    }
    val iterationState = pathSystem.iterationFlow.collectAsState()
    pathSystemState.value = pathSystem
    // Reuse the same path instance to avoid the allocation on every frame.
    val composePath = remember { Path() }
    Box(modifier = modifier) {
        Canvas(
            modifier = modifier,
            onDraw = {
                // Read the iteration state inside the draw block so that every new iteration
                // invalidates the draw phase and the paths are redrawn.
                iterationState.value
                clipRect { drawMorphablePaths(pathSystem.paths, composePath, color, style) }
            }
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(Unit, lifecycleOwner) {
        val lifecycleObserver = PathBoxLifecycleObserver(pathSystemState)
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            pathSystem.stop()
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }
}

/**
 * A custom [DefaultLifecycleObserver] that handles OnPause and OnResume.
 */
private class PathBoxLifecycleObserver(
    private val pathSystemState: State<PathSystem>
) : DefaultLifecycleObserver {

    override fun onPause(owner: LifecycleOwner) {
        pathSystemState.value.stop()
    }

    override fun onResume(owner: LifecycleOwner) {
        pathSystemState.value.start()
    }
}

private fun DrawScope.drawMorphablePaths(
    morphablePaths: List<MorphablePath>,
    composePath: Path,
    color: Color,
    style: DrawStyle,
) {
    composePath.reset()
    morphablePaths.fastForEach { morphablePath ->
        composePath.moveTo(morphablePath.xs[0], morphablePath.ys[0])
        for (index in 1 until morphablePath.pointCount) {
            composePath.lineTo(morphablePath.xs[index], morphablePath.ys[index])
        }
        if (morphablePath.isClosed) {
            composePath.close()
        }
    }
    drawPath(path = composePath, color = color, style = style)
}
