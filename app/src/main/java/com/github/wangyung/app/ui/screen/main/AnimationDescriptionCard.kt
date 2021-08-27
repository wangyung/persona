package com.github.wangyung.app.ui.screen.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.wangyung.app.model.AnimationInfo
import com.github.wangyung.app.model.AnimationType

@Composable
fun AnimationDescriptionCard(
    animationInfo: AnimationInfo,
    modifier: Modifier = Modifier,
    navigateToAnimationDemo: (AnimationType) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { navigateToAnimationDemo(animationInfo.animationType) }
            .padding(16.dp)
    ) {
        val imageModifier = Modifier
            .heightIn(min = 150.dp)
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.medium)

        Image(
            painter = painterResource(animationInfo.thumbnailId),
            contentDescription = null,
            modifier = imageModifier,
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(16.dp))

        Title(animationInfo.title)
        Description(animationInfo.description)
    }
}

@Composable
private fun Title(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.h6,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun Description(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.subtitle2,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
