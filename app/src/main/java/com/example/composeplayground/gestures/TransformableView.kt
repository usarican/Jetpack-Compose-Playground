package com.example.composeplayground.gestures

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun TransformableView(modifier: Modifier = Modifier) {
    /**
     * Holds the current scale value for the component.
     */
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    /**
     * TransformableState that will be used to control the transformations.
     */
    val transformableState = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        Log.d("TransformableState", "zoomChange: $zoomChange, offsetChange: $offsetChange, rotationChange: $rotationChange")
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale ; scaleY = scale
                translationX = offset.x ; translationY = offset.y
                rotationZ = rotation
            }
            .transformable(state = transformableState)
            .background(Color.Magenta)
            .size(150.dp, 250.dp)
    )
}