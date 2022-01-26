package com.github.wangyung.app.model

import android.content.res.Resources
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.wangyung.persona.particle.ParticleShape
import com.github.wangyung.persona.render.ComposeParticleShape
import java.lang.IllegalArgumentException
import kotlin.random.Random
import kotlin.random.nextInt

private fun sakura(): Path =
    Path().apply {
        moveTo(0f, 25f)
        quadraticBezierTo(12.5f, 0f, 30f, 15f)
        lineTo(20f, 20f)
        lineTo(30f, 25f)
        quadraticBezierTo(12.5f, 40f, 0f, 25f)
        close()
    }

internal fun createSakuraParticle(strokeRange: IntRange): ParticleShape =
    ComposeParticleShape.Path(
        strokeWidth = Random.nextInt(strokeRange),
        color = Color(0xFFFA9599),
        path = sakura()
    )

internal fun createRainParticleShape(strokeRange: IntRange): ParticleShape.Line =
    ComposeParticleShape.Line(
        strokeWidth = Random.nextInt(strokeRange).toFloat(),
        color = Color.LightGray,
    )

@Suppress("SwallowedException")
internal fun createShowParticle(radiusRange: IntRange): ParticleShape.Circle =
    ComposeParticleShape.Circle(
        color = Color.White,
        radius = try {
            Random.nextInt(radiusRange)
        } catch (e: IllegalArgumentException) {
            Random.nextInt(IntRange(radiusRange.first, radiusRange.first))
        }
    )

internal fun createMoneyParticle(fontSizeRange: IntRange): ParticleShape =
    ComposeParticleShape.Text(
        text = "\uD83D\uDCB5", // money
        fontSize = Random.nextInt(fontSizeRange).sp,
        borderWidth = 1.dp,
        color = Color.Blue,
    )

internal fun createBirdParticle(resource: Resources, @DrawableRes resId: Int): ParticleShape =
    ComposeParticleShape.Image(
        image = ImageBitmap.imageResource(resource, id = resId)
    )

internal fun createStarParticle(color: Color, radiusRange: IntRange): ParticleShape.Circle =
    ComposeParticleShape.Circle(
        color = color,
        radius = Random.nextInt(radiusRange)
    )

internal fun createConfettiParticle(colors: List<Color>): ParticleShape.Rectangle =
    ComposeParticleShape.Rectangle(backgroundColor = colors.random())
