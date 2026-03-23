package org.example.liferpg.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.min
import liferpg.composeapp.generated.resources.Res
import liferpg.composeapp.generated.resources.hexagon_background
import org.jetbrains.compose.resources.painterResource
import org.example.liferpg.Atributo
import org.example.liferpg.FondoOscuro
import org.example.liferpg.RojoSangre

class HexagonShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path().apply {
            val radius = min(size.width, size.height) / 2f
            val center = Offset(size.width / 2f, size.height / 2f)
            for (i in 0 until 6) {
                val angle = i * (PI / 3)
                val x = center.x + radius * cos(angle).toFloat()
                val y = center.y + radius * sin(angle).toFloat()
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun RadarChart(
    atributos: List<Atributo>,
    modifier: Modifier = Modifier
) {
    var animTarget by remember { mutableStateOf(0f) }
    val animProgress by animateFloatAsState(
        targetValue = animTarget,
        animationSpec = tween(durationMillis = 800)
    )
    
    LaunchedEffect(Unit) {
        animTarget = 1f
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(Res.drawable.hexagon_background),
            contentDescription = "Radar chart background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )

        // Parche negro para ocultar la marca de agua de Gemini
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-20).dp, y = (-50).dp)
                .size(70.dp)
                .background(FondoOscuro)
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val imgRatio = 894f / 1024f
            val containerRatio = size.width / size.height
            val displayWidth: Float
            val displayHeight: Float
            
            if (containerRatio > imgRatio) {
                displayHeight = size.height
                displayWidth = size.height * imgRatio
            } else {
                displayWidth = size.width
                displayHeight = size.width / imgRatio
            }
            
            val marginLeft = (size.width - displayWidth) / 2
            val marginTop = (size.height - displayHeight) / 2

            val center = Offset(
                marginLeft + displayWidth * 0.50f,
                marginTop + displayHeight * 0.47f
            )
            
            val radius = displayWidth * 0.33f
            val pointsCount = atributos.size
            val angleStep = (2 * PI / pointsCount).toFloat()

            fun polarToOffset(index: Int, normalizedValue: Float): Offset {
                val angle = (index * angleStep) - (PI / 2).toFloat()
                return Offset(
                    center.x + radius * normalizedValue * cos(angle),
                    center.y + radius * normalizedValue * sin(angle)
                )
            }

            val dataPath = Path().apply {
                atributos.forEachIndexed { i, stat ->
                    val normalized = (stat.valor / stat.valorMaximo) * animProgress
                    val point = polarToOffset(i, normalized)
                    if (i == 0) moveTo(point.x, point.y) else lineTo(point.x, point.y)
                }
                close()
            }

            drawPath(
                path = dataPath,
                color = RojoSangre.copy(alpha = 0.25f),
                style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            drawPath(
                path = dataPath,
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFCC0000).copy(alpha = 0.45f),
                        Color(0xFFCC0000).copy(alpha = 0.05f)
                    ),
                    center = center,
                    radius = radius
                )
            )

            drawPath(
                path = dataPath,
                color = RojoSangre.copy(alpha = 0.85f),
                style = Stroke(width = 2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val imgRatio = 894f / 1024f
            val containerRatio = maxWidth.value / maxHeight.value
            val displayWidth: Float
            val displayHeight: Float
            
            if (containerRatio > imgRatio) {
                displayHeight = maxHeight.value
                displayWidth = maxHeight.value * imgRatio
            } else {
                displayWidth = maxWidth.value
                displayHeight = maxWidth.value / imgRatio
            }
            
            val marginLeft = (maxWidth.value - displayWidth) / 2
            val marginTop = (maxHeight.value - displayHeight) / 2

            val centerX = marginLeft + displayWidth * 0.50f
            val centerY = marginTop + displayHeight * 0.46f
            val pointsCount = atributos.size
            val angleStep = (2 * PI / pointsCount).toFloat()
            
            val badgeRadius = displayWidth * 0.355f
            val textDrop = displayWidth * 0.06f
            val boxWidth = 40f
            val boxHeight = 24f

            atributos.forEachIndexed { i, stat ->
                val angle = (i * angleStep) - (PI / 2).toFloat()
                
                val badgeX = centerX + badgeRadius * cos(angle)
                val badgeY = centerY + badgeRadius * sin(angle)
                
                val textX = badgeX
                val textY = badgeY + textDrop

                Box(
                    modifier = Modifier
                        .offset(
                            x = (textX - boxWidth / 2f).dp,
                            y = (textY - boxHeight / 2f).dp
                        )
                        .size(boxWidth.dp, boxHeight.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stat.valor.toInt().toString(),
                        color = RojoSangre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
