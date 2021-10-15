package com.github.wangyung.persona.particle.transformation

/**
 * The parameters for transformation
 */
sealed interface TransformationParameters

/**
 * The parameters for [LinearTranslateTransformation].
 */
class TranslateTransformationParameters(val gravity: Float = 0f) : TransformationParameters
