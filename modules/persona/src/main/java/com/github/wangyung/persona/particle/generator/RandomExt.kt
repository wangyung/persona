package com.github.wangyung.persona.particle.generator

import kotlin.random.Random

fun Random.nextFloat(start: Float, end: Float): Float =
    start + ((end - start) * Random.nextFloat())
