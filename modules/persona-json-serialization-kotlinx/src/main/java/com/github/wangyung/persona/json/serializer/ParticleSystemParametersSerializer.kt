package com.github.wangyung.persona.json.serializer

import com.github.wangyung.persona.particle.ParticleSystemParameters
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
private data class ParticleSystemParametersSurrogate(
    val fps: Int = 60,
    val autoResetParticles: Boolean = true,
    val restartWhenAllDead: Boolean = true,
)

/**
 * Serializes the [ParticleSystemParameters] from the persona-core.
 */
object ParticleSystemParametersSerializer : KSerializer<ParticleSystemParameters> {
    override val descriptor: SerialDescriptor =
        ParticleSystemParametersSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: ParticleSystemParameters) =
        encoder.encodeSerializableValue(
            ParticleSystemParametersSurrogate.serializer(),
            ParticleSystemParametersSurrogate(
                fps = value.fps,
                autoResetParticles = value.autoResetParticles,
                restartWhenAllDead = value.restartWhenAllDead,
            )
        )

    override fun deserialize(decoder: Decoder): ParticleSystemParameters =
        decoder.decodeSerializableValue(ParticleSystemParametersSurrogate.serializer()).let {
            ParticleSystemParameters(
                fps = it.fps,
                autoResetParticles = it.autoResetParticles,
                restartWhenAllDead = it.restartWhenAllDead,
            )
        }
}
