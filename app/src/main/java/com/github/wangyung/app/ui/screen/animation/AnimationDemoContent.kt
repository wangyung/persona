package com.github.wangyung.app.ui.screen.animation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.Slider
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.wangyung.persona.particle.generator.parameter.SourceEdge
import java.util.Locale
import kotlin.math.abs

private const val MIN_SLIDER_INTERVAL = 0.01f

@Composable
fun SliderWithValueText(
    title: String,
    modifier: Modifier,
    sliderRange: ClosedFloatingPointRange<Float> = 0f..1f,
    intOnly: Boolean = false,
    defaultSliderValue: Float = sliderRange.start,
    onValueChanged: ((Float) -> Unit)? = null
) {
    ComposableWithTextTitle(title = title, modifier = modifier) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            var sliderPosition by remember { mutableStateOf(defaultSliderValue) }
            Slider(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                value = sliderPosition,
                onValueChange = { newValue ->
                    if (intOnly) {
                        val newValueInt = newValue.toInt()
                        if (sliderPosition.toInt() != newValueInt) {
                            onValueChanged?.invoke(newValueInt.toFloat())
                            sliderPosition = newValue
                        }
                    } else {
                        if (abs(sliderPosition - newValue) >= MIN_SLIDER_INTERVAL) {
                            onValueChanged?.invoke(newValue)
                        }
                        sliderPosition = newValue
                    }
                },
                valueRange = sliderRange
            )
            val displayText = if (intOnly) {
                sliderPosition.toInt().toString()
            } else {
                String.format(locale = Locale.US, "%.2f", sliderPosition)
            }
            Text(
                modifier = Modifier.fillMaxWidth(0.15f),
                textAlign = TextAlign.Center,
                text = displayText,
            )
        }
    }
}

@Composable
fun ComposableWithTextTitle(title: String, modifier: Modifier, content: @Composable () -> Unit) {
    Column(modifier = modifier) {
        Text(text = title)
        content()
    }
}

@Composable
fun StartEdgeCheckBoxes(
    modifier: Modifier,
    sourceEdges: Set<SourceEdge>,
    onCheckedChange: ((SourceEdge, Boolean) -> Unit)? = null
) {
    ComposableWithTextTitle(title = "Start edge:", modifier = modifier) {
        val supportedEdges = SourceEdge.values()
        Row(
            modifier = Modifier.padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            supportedEdges.forEach { edge ->
                val isChecked = edge in sourceEdges
                Row(modifier = Modifier.padding(8.dp)) {
                    Checkbox(checked = isChecked, onCheckedChange = { checked ->
                        onCheckedChange?.invoke(edge, checked)
                    })
                    Text(edge.name)
                }
            }
        }
    }
}

@Composable
fun SwitchWithText(
    title: String,
    modifier: Modifier,
    isChecked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)? = null
) {
    ComposableWithTextTitle(title = "Start edge:", modifier = modifier) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(checked = isChecked, onCheckedChange = {
                onCheckedChange?.invoke(it)
            })
            Text(title)
        }
    }
}
