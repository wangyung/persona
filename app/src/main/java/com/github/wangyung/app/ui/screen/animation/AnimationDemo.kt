package com.github.wangyung.app.ui.screen.animation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.github.wangyung.app.model.AnimationParameterSet
import com.github.wangyung.app.model.AnimationType
import com.github.wangyung.app.ui.components.WeatherAnimationBox
import com.github.wangyung.persona.particle.generator.ShapeProvider
import com.google.accompanist.insets.LocalWindowInsets

@Composable
fun AnimationDemo(
    animationType: AnimationType,
    parameterSet: AnimationParameterSet,
    shapeProvider: ShapeProvider,
    showMoon: Boolean = false,
    showLandscape: Boolean = true,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        var showDebugLayer: Boolean by remember { mutableStateOf(false) }
        Text(
            text = animationType.toTitle(),
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        WeatherAnimationBox(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f),
            animationType = animationType,
            parameterSet = parameterSet,
            showMoon = showMoon,
            showLandscape = showLandscape,
            showDebugLayer = showDebugLayer,
            shapeProvider = shapeProvider
        )
        Spacer(modifier = Modifier.height(16.dp))

        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
        ) {
            Row {
                Button(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth(0.5f),
                    onClick = {
                        showDebugLayer = !showDebugLayer
                    }
                ) {
                    if (showDebugLayer) {
                        Text("Hide debug layer")
                    } else {
                        Text("Show debug layer")
                    }
                }
            }
            Spacer(modifier = Modifier.padding(8.dp))
            content()
            val navigationBarBottom = with(LocalDensity.current) {
                LocalWindowInsets.current.navigationBars.bottom.toDp()
            }
            // Add extra space for scrolling
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(navigationBarBottom + 8.dp)
            )
        }
    }
}
