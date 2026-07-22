package com.github.wangyung.persona.json.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
private data class IntRangeSurrogate(val from: Int, val to: Int)

@Serializable
private data class FloatRangeSurrogate(val from: Float, val to: Float)

/**
 * Serializes an [IntRange] as `{"from": x, "to": y}`.
 */
object IntRangeSerializer : KSerializer<IntRange> {
    override val descriptor: SerialDescriptor = IntRangeSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: IntRange) =
        encoder.encodeSerializableValue(
            IntRangeSurrogate.serializer(),
            IntRangeSurrogate(from = value.first, to = value.last)
        )

    override fun deserialize(decoder: Decoder): IntRange =
        decoder.decodeSerializableValue(IntRangeSurrogate.serializer()).let { it.from..it.to }
}

/**
 * Serializes a [ClosedFloatingPointRange] of [Float] as `{"from": x, "to": y}`.
 */
object FloatRangeSerializer : KSerializer<ClosedFloatingPointRange<Float>> {
    override val descriptor: SerialDescriptor = FloatRangeSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: ClosedFloatingPointRange<Float>) =
        encoder.encodeSerializableValue(
            FloatRangeSurrogate.serializer(),
            FloatRangeSurrogate(from = value.start, to = value.endInclusive)
        )

    override fun deserialize(decoder: Decoder): ClosedFloatingPointRange<Float> =
        decoder.decodeSerializableValue(FloatRangeSurrogate.serializer()).let { it.from..it.to }
}
