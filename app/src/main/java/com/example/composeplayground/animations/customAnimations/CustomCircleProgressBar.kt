package com.example.composeplayground.animations.customAnimations


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun CustomProgressBar(
    modifier: Modifier = Modifier,
    size: Dp = 128.dp
){
    var progress by remember { mutableFloatStateOf(0f) }
    val animatedProgress = remember { Animatable(0f) }
    val animatedScale = remember { Animatable(0f) }
    val shimmerAnimatedProgress = remember {
        Animatable(0f)
    }

    LaunchedEffect(key1 = progress) {
        launch {
            animatedScale.snapTo(0f)
            animatedScale.animateTo(
                targetValue = (progress.toInt() / 100.0).toFloat() + 1.5f,
                animationSpec = tween(1500),
            )
        }
        launch {
            animatedProgress.snapTo(0f)
            animatedProgress.animateTo(
                targetValue = progress,
                animationSpec = tween(1500),
            )
            shimmerAnimatedProgress.animateTo(
                targetValue = progress,
                animationSpec = infiniteRepeatable(
                    animation = tween(750),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
        launch {
            shimmerAnimatedProgress.snapTo(0f)
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
        modifier = modifier
            .fillMaxSize()
    ) {
        CustomCircleProgressBar(
            progressColor = animatedColor,
            progressValue = animatedProgress.value,
            size = size,
            modifier = Modifier.align(Alignment.Center)
        )
        if (!animatedProgress.isRunning){
            ShimmerEffectArc(
                shimmerProgress = shimmerAnimatedProgress,
                progress = progress,
                size = size,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        ProgressText(
            progressValue = animatedProgress.value,
            textColor = animatedColor,
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.Center),
            fontSize = (size.value / 8 ).sp,
            scaleValue = animatedScale.value
        )

        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp),
            onClick = {
                progress = (Math.random() * 100).toInt().toFloat()
            }) {
            Text(text = "Change Progress")
        }
    }
}

@Composable
fun ProgressText(
    modifier: Modifier = Modifier,
    progressValue : Float,
    scaleValue : Float,
    textColor : Color = Color.Red,
    fontSize : TextUnit = LocalTextStyle.current.fontSize
) {
    Text(
        text = "${(progressValue).toInt()}%",
        color = textColor,
        modifier = modifier.graphicsLayer {
        scaleX = scaleValue
        scaleY = scaleValue
        transformOrigin = TransformOrigin.Center
    },
        fontSize = fontSize,
        style = LocalTextStyle.current.copy(textMotion = TextMotion.Animated)
    )
}



@Composable
fun CustomCircleProgressBar(
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    strokeWidth: Dp = 12.dp,
    backgroundArcColor: Color = Color.LightGray,
    progressValue : Float, /* 0 ile 100 Arasında bir değer */
    progressColor: Color = Color.Red
){
  Canvas(modifier = modifier.size(size)) {
      val strokeWidthPx = strokeWidth.toPx()
      val arcSize = size.toPx() - strokeWidthPx
      drawArc(
          color = backgroundArcColor,
          startAngle = -90f,
          sweepAngle = 360f,
          useCenter = false,
          style = Stroke(
              width = strokeWidth.toPx(),
          ),
          topLeft = Offset(strokeWidthPx/2,strokeWidthPx/2),
          size = Size(arcSize,arcSize)
      )
      drawArc(
          color = progressColor,
          startAngle = -90f,
          sweepAngle = (progressValue / 100) * 360f,
          useCenter = false,
          style = Stroke(
              width = strokeWidth.toPx(),
              cap = StrokeCap.Round
          ),
          topLeft = Offset(strokeWidthPx/2,strokeWidthPx/2),
          size = Size(arcSize,arcSize)
      )
  }
}

@Composable
fun ShimmerEffectArc(
    size: Dp = 96.dp,
    strokeWidth: Dp = 12.dp,
    shimmerProgress: Animatable<Float, *>,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.15f),
        Color.White.copy(alpha = 0.3f),
        Color.White.copy(alpha = 0.5f),
        Color.White.copy(alpha = 0.3f),
        Color.White.copy(alpha = 0.15f),
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = 100f, y = 0.0f),
        end = Offset(x = 400f, y = 270f),
    )

    Canvas(modifier = modifier.size(size)) {
        val strokeWidthPx = strokeWidth.toPx()
        val arcSize = size.toPx() - strokeWidthPx
        drawArc(
            brush = brush,
            startAngle = if (-90f + (((shimmerProgress.value / 100) * 360f) - ((progress / 100) * 45f)) < -90f) -90f else -90f + (((shimmerProgress.value / 100) * 360f) - ((progress / 100) * 45f)),
            sweepAngle = (progress / 100) * 45f,
            alpha = 1f,
            useCenter = false,
            topLeft = Offset(strokeWidthPx/2,strokeWidthPx/2),
            size = Size(arcSize,arcSize),
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun CustomCircleProgressBarPreview(){
    CustomProgressBar()
}