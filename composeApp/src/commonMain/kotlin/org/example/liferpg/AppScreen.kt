package org.example.liferpg

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.PI

import androidx.compose.foundation.Image
import org.jetbrains.compose.resources.painterResource
import liferpg.composeapp.generated.resources.Res
import liferpg.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource

import org.example.liferpg.ui.MissionsScreen
import org.example.liferpg.ui.MisionesViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.liferpg.data.RepositorioProgreso
import org.example.liferpg.ui.components.RadarChart

val FondoOscuro = Color(0xFF000000)
val RojoSangre = Color(0xFFFF1A1A)
val ColorHexagonoOscuro = Color(0xFF1E1E24)
val GrisTexto = Color(0xFFAAAAAA)
val SuperficieOscura = Color(0xFF1E1E24)

data class Atributo(
    val nombre: String,
    val valor: Float,
    val recursoIcono: DrawableResource,
    val valorMaximo: Float = 100f
)

@Composable
fun MainScreen() {
    val repositorio = remember { RepositorioProgreso() }

    var atributos by remember {
        val valoresGuardados = repositorio.cargarAtributosValores()
        mutableStateOf(
            listOf(
                Atributo("FUERZA", valoresGuardados["FUERZA"] ?: 30f, Res.drawable.fuerza_icono),
                Atributo("ILUMINACIÓN", valoresGuardados["ILUMINACIÓN"] ?: 30f, Res.drawable.iluminacion_icono),
                Atributo("ESTRUCTURA", valoresGuardados["ESTRUCTURA"] ?: 30f, Res.drawable.estructura_icono),
                Atributo("ENERGÍA", valoresGuardados["ENERGÍA"] ?: 30f, Res.drawable.energia_icono),
                Atributo("VOLUNTAD", valoresGuardados["VOLUNTAD"] ?: 30f, Res.drawable.voluntad_icono),
                Atributo("SABIDURÍA", valoresGuardados["SABIDURÍA"] ?: 30f, Res.drawable.sabiduria_icono)
            )
        )
    }

    LaunchedEffect(atributos) {
        val mapaValores = atributos.associate { it.nombre to it.valor }
        repositorio.guardarAtributosValores(mapaValores)
    }

    var currentTab by remember { mutableStateOf("HOME") }
    val misionesViewModel: MisionesViewModel = viewModel()

    Scaffold(
        containerColor = FondoOscuro,
        topBar = { TopBar(if (currentTab == "HOME") "TU PROGRESO" else "TAREAS") },
        bottomBar = { BottomNavigationBar(currentTab = currentTab, onTabSelected = { currentTab = it }) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(FondoOscuro),
            contentAlignment = Alignment.Center
        ) {
            if (currentTab == "HOME") {
                RadarChart(
                    atributos = atributos,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                )
            } else {
                MissionsScreen(
                    viewModel = misionesViewModel,
                    onMissionCompleted = { misionCompletada ->
                        atributos = atributos.map { atributo ->
                            if (atributo.nombre.equals(misionCompletada.nombreAtributo, ignoreCase = true)) {
                                atributo.copy(valor = minOf(100f, atributo.valor + misionCompletada.puntos))
                            } else {
                                atributo
                            }
                        }
                    },
                    onMissionReverted = { misionRevertida ->
                        atributos = atributos.map { atributo ->
                            if (atributo.nombre.equals(misionRevertida.nombreAtributo, ignoreCase = true)) {
                                // Asegurar que no baja del mínimo (asumiendo 0 o el valor base)
                                atributo.copy(valor = maxOf(0f, atributo.valor - misionRevertida.puntos))
                            } else {
                                atributo
                            }
                        }
                    }
                )
            }
        }
        
    }
}

@Composable
fun TopBar(title: String = "TU PROGRESO") {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(Color(0xFF000000))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                color = RojoSangre.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp,
                letterSpacing = 4.sp
            )
        }
        
        // Fina línea de gradiente rojo justo debajo de la barra base
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            RojoSangre.copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

@Composable
fun BottomNavigationBar(currentTab: String, onTabSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SuperficieOscura)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onTabSelected("HOME") }
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (currentTab == "HOME") Color(0xFF3B1E2B) else Color.Transparent, 
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home",
                    tint = if (currentTab == "HOME") RojoSangre else GrisTexto,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.height(4.dp))
            Text("HOME", color = if (currentTab == "HOME") RojoSangre else GrisTexto, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onTabSelected("TAREAS") }
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (currentTab == "TAREAS") Color(0xFF3B1E2B) else Color.Transparent, 
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.List,
                    contentDescription = "Tareas",
                    tint = if (currentTab == "TAREAS") RojoSangre else GrisTexto,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.height(4.dp))
            Text("TAREAS", color = if (currentTab == "TAREAS") RojoSangre else GrisTexto, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}
