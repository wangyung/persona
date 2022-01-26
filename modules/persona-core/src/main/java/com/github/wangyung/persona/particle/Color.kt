package com.github.wangyung.persona.particle

@JvmInline
value class Color(val value: Int) {
    companion object {
        val Transparent = Color(0x00000000)
    }
}
