package com.github.wangyung.persona.json.serializer

import com.github.wangyung.persona.particle.generator.parameter.Constraints
import com.github.wangyung.persona.particle.generator.parameter.InitialConstraints
import com.github.wangyung.persona.particle.generator.parameter.ParticleGeneratorParameters
import com.github.wangyung.persona.particle.generator.parameter.SourceEdge
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

private const val DEFAULT_WIDTH = 12
private const val DEFAULT_HEIGHT = 12

@Serializable
internal sealed class ConstraintsSurrogate {
    @Serializable
    @SerialName("initial")
    data class Initial(
        @Serializable(with = FloatRangeSerializer::class)
        val limitRange: ClosedFloatingPointRange<Float>
    ) : ConstraintsSurrogate()
}

internal fun Constraints.toSurrogate(): ConstraintsSurrogate =
    when (this) {
        is InitialConstraints -> ConstraintsSurrogate.Initial(limitRange = limitRange)
    }

internal fun ConstraintsSurrogate.toConstraints(): Constraints =
    when (this) {
        is ConstraintsSurrogate.Initial -> InitialConstraints(limitRange = limitRange)
    }

@Serializable
private data class ParticleGeneratorParametersSurrogate(
    val count: Int,
    @Serializable(with = IntRangeSerializer::class)
    val particleWidthRange: IntRange = IntRange(DEFAULT_WIDTH, DEFAULT_WIDTH),
    @Serializable(with = IntRangeSerializer::class)
    val particleHeightRange: IntRange = IntRange(DEFAULT_HEIGHT, DEFAULT_HEIGHT),
    val randomizeInitialXY: Boolean = true,
    @Serializable(with = FloatRangeSerializer::class)
    val speedRange: ClosedFloatingPointRange<Float> = 0f..0f,
    @Serializable(with = FloatRangeSerializer::class)
    val scaleRange: ClosedFloatingPointRange<Float> = 1f..1f,
    @Serializable(with = FloatRangeSerializer::class)
    val angleRange: ClosedFloatingPointRange<Float> = 0f..0f,
    @Serializable(with = FloatRangeSerializer::class)
    val xRotationalSpeedRange: ClosedFloatingPointRange<Float> = 0f..0f,
    @Serializable(with = FloatRangeSerializer::class)
    val zRotationalSpeedRange: ClosedFloatingPointRange<Float> = 0f..0f,
    @Serializable(with = IntRangeSerializer::class)
    val startOffsetRange: IntRange = 0..0,
    val sourceEdges: Set<SourceEdge> = setOf(SourceEdge.TOP),
    val constraints: List<ConstraintsSurrogate>? = null,
)

/**
 * Serializes the [ParticleGeneratorParameters] from the persona-core.
 *
 * The `shapeProvider` is not part of the parameters. The shape is renderer specific and should be
 * provided by the caller when creating the particle system.
 */
object ParticleGeneratorParametersSerializer : KSerializer<ParticleGeneratorParameters> {
    override val descriptor: SerialDescriptor =
        ParticleGeneratorParametersSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: ParticleGeneratorParameters) =
        encoder.encodeSerializableValue(
            ParticleGeneratorParametersSurrogate.serializer(),
            ParticleGeneratorParametersSurrogate(
                count = value.count,
                particleWidthRange = value.particleWidthRange,
                particleHeightRange = value.particleHeightRange,
                randomizeInitialXY = value.randomizeInitialXY,
                speedRange = value.speedRange,
                scaleRange = value.scaleRange,
                angleRange = value.angleRange,
                xRotationalSpeedRange = value.xRotationalSpeedRange,
                zRotationalSpeedRange = value.zRotationalSpeedRange,
                startOffsetRange = value.startOffsetRange,
                sourceEdges = value.sourceEdges,
                constraints = value.constraints?.map { it.toSurrogate() },
            )
        )

    override fun deserialize(decoder: Decoder): ParticleGeneratorParameters =
        decoder.decodeSerializableValue(ParticleGeneratorParametersSurrogate.serializer()).let {
            ParticleGeneratorParameters(
                count = it.count,
                particleWidthRange = it.particleWidthRange,
                particleHeightRange = it.particleHeightRange,
                randomizeInitialXY = it.randomizeInitialXY,
                speedRange = it.speedRange,
                scaleRange = it.scaleRange,
                angleRange = it.angleRange,
                xRotationalSpeedRange = it.xRotationalSpeedRange,
                zRotationalSpeedRange = it.zRotationalSpeedRange,
                startOffsetRange = it.startOffsetRange,
                sourceEdges = it.sourceEdges,
                constraints = it.constraints?.map { surrogate -> surrogate.toConstraints() },
            )
        }
}
