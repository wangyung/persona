package com.github.wangyung.persona.path.transformation

import com.github.wangyung.persona.path.MorphablePath

/**
 * The contractor of updating [MorphablePath]. Unlike the particle transformations that update the
 * whole particle state, a path transformation moves the points of the path, so the shape looks
 * like it is changing.
 */
fun interface PathTransformation {
    /**
     * Updates the points of the [MorphablePath] at the given iteration. The displacement should be
     * computed from the original points (see [MorphablePath.originalX]) so the shape doesn't
     * drift over the iterations.
     */
    fun transform(path: MorphablePath, iteration: Long)
}
