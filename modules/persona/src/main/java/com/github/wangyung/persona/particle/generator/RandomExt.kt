package com.github.wangyung.persona.particle.generator

import kotlin.math.nextDown
import kotlin.random.Random

/**
 * The Float version of [Random.nextDouble] as the extension method of [ClosedFloatingPointRange].
 */
fun ClosedFloatingPointRange<Float>.nextFloat(): Float {
    // Follows the implementation of Random.nextDouble in stdlib
    checkRangeBounds(start, endInclusive)
    val size = endInclusive - start
    val r = if (size.isInfinite() && start.isFinite() && endInclusive.isFinite()) {
        val r1 = Random.nextFloat() * (endInclusive / 2 - start / 2)
        start + r1 + r1
    } else {
        start + Random.nextFloat() * size
    }
    return if (r >= endInclusive) endInclusive.nextDown() else r
}

private fun checkRangeBounds(from: Float, until: Float) = require(until > from) {
    boundsErrorMessage(from, until)
}

private fun boundsErrorMessage(from: Any, until: Any) = "Random range is empty: [$from, $until)."
