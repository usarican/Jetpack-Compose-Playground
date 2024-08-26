package com.example.composeplayground.animations.customAnimations

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

@Composable
fun CircleProgressBarAnimation(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        var progress by remember { mutableFloatStateOf(0f) }
        var currentProgress by remember { mutableFloatStateOf(0f) }

        val animatedProgress = remember {
            Animatable(0f)
        }

        val animatedScale = remember {
            Animatable(0f)
        }
        LaunchedEffect(progress) {
            launch {
                animatedScale.snapTo(0f)
                animatedScale.animateTo(
                    targetValue = (progress.toInt() / 100.0).toFloat() + 1.5f,
                    animationSpec = tween(1500),
                )
            }
            launch {
                progressFlow(progress,1500).collect {
                    currentProgress = it.toFloat()
                }
            }
            launch {
                animatedProgress.snapTo(0f)
                animatedProgress.animateTo(
                    targetValue = progress,
                    animationSpec = tween(1500),
                )
            }
        }

        val animatedColor by animateColorAsState(
            when (animatedProgress.value) {
                in 0f..25f -> Color.Red
                in 25f..50f -> Color.Magenta
                in 50f..75f -> Color.Green
                in 75f..100f -> Color.Blue
                else -> Color.LightGray
            },
            animationSpec = spring(2f),
            label = "Color Animation"
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            CircleProgressAnimation(
                animatedProgress = animatedProgress,
                animatedColor = animatedColor,
                size = 128.dp,
                modifier = Modifier.align(Alignment.Center)
            )
            ProgressTextAnimation(
                progress = currentProgress,
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.Center),
                animatedColor = animatedColor,
                animatedScale = animatedScale
            )

            Button(
                modifier = Modifier.align(Alignment.BottomCenter).padding(32.dp),
                onClick = {
                    progress = (Math.random() * 100).toInt().toFloat()
                }) {
                Text(text = "Change Progress")
            }
        }
        Log.d("Progress", "CircleProgressBarAnimation: $progress")

    }
}

@Composable
fun ProgressTextAnimation(
    progress: Float,
    animatedColor: Color,
    animatedScale: Animatable<Float, *>,
    modifier: Modifier = Modifier
) {
    Text(
        text = "${(progress).toInt()}%",
        color = animatedColor,
        modifier = modifier
            .graphicsLayer {
                scaleX = animatedScale.value
                scaleY = animatedScale.value
                transformOrigin = TransformOrigin.Center
            },
        style = LocalTextStyle.current.copy(textMotion = TextMotion.Animated)
    )
}

@Composable
fun CircleProgressAnimation(
    size: Dp = 96.dp,
    strokeWidth: Dp = 12.dp,
    backgroundArcColor: Color = Color.LightGray,
    animatedColor: Color,
    animatedProgress: Animatable<Float, *>,
    modifier: Modifier = Modifier
) {

    Canvas(
        modifier = modifier
            .size(size)
    ) {

        drawArc(
            color = backgroundArcColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            size = Size(size.toPx(), size.toPx()),
            style = Stroke(width = strokeWidth.toPx())
        )

        drawArc(
            color = animatedColor,
            startAngle = -90f,
            sweepAngle = (animatedProgress.value / 100) * 360f,
            alpha = 0.8f,
            useCenter = false,
            size = Size(size.toPx(), size.toPx()),
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )
    }
}

fun progressFlow(targetProgress: Float,totalTime : Long): Flow<Int> {
    return flow {
        var progress = 0f
        while (progress <= targetProgress) {
            emit(progress.toInt())
            progress += 1f
            delay((totalTime / targetProgress).toLong())
        }
    }
}