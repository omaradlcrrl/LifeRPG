package org.example.liferpg.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.em
import kotlinx.coroutines.launch
import org.example.liferpg.model.NivelImpacto
import org.example.liferpg.model.TipoAtributo
import org.example.liferpg.model.actividadesPredefinidas
import org.jetbrains.compose.resources.painterResource

val colorFuerza = Color(0xFFCC1A1A)
val colorSabiduria = Color(0xFF7C4DFF)
val colorEnergia = Color(0xFFF5A623)
val colorEstructura = Color(0xFF795548)
val colorIluminacion = Color(0xFF00BCD4)
val colorVoluntad = Color(0xFFFF5722)

fun obtenerColorAtributo(atributo: TipoAtributo): Color = when(atributo) {
    TipoAtributo.FUERZA -> colorFuerza
    TipoAtributo.SABIDURIA -> colorSabiduria
    TipoAtributo.ENERGIA -> colorEnergia
    TipoAtributo.ESTRUCTURA -> colorEstructura
    TipoAtributo.ILUMINACION -> colorIluminacion
    TipoAtributo.VOLUNTAD -> colorVoluntad
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionsScreen(
    viewModel: MisionesViewModel,
    onMissionCompleted: (Mision) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showSheet by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFF000000))) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(viewModel.misiones) { mision ->
                MissionItem(
                    mision = mision,
                    onComplete = { 
                        if (viewModel.completarMision(it)) {
                            onMissionCompleted(mision)
                        }
                    }
                )
            }
        }

        FloatingActionButton(
            onClick = { showSheet = true },
            containerColor = Color(0xFFFF1A1A),
            contentColor = Color.White,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Text("+", fontSize = 28.sp, fontWeight = FontWeight.Medium)
        }

        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                containerColor = Color(0xFF0D0D0D),
                contentColor = Color.White
            ) {
                AddMissionSheetContent(
                    onAdd = { titulo, tipoAtributo, impacto ->
                        viewModel.agregarMision(
                            Mision(
                                titulo = titulo,
                                tipoAtributo = tipoAtributo,
                                nombreAtributo = tipoAtributo.nombreVisible,
                                colorAtributo = obtenerColorAtributo(tipoAtributo),
                                puntos = impacto.puntos.toInt()
                            )
                        )
                        showSheet = false
                    }
                )
            }
        }
    }
}

enum class MissionRegistrationStep {
    SELECT_ATTRIBUTE, SELECT_ACTIVITY, SELECT_IMPACT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMissionSheetContent(onAdd: (String, TipoAtributo, NivelImpacto) -> Unit) {
    var currentStep by remember { mutableStateOf(MissionRegistrationStep.SELECT_ATTRIBUTE) }
    var selectedAttribute by remember { mutableStateOf<TipoAtributo?>(null) }
    var title by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).padding(bottom = 32.dp).defaultMinSize(minHeight = 400.dp)
    ) {
        when(currentStep) {
            MissionRegistrationStep.SELECT_ATTRIBUTE -> {
                Text("1. Selecciona el Atributo", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF1A1A))
                Spacer(modifier = Modifier.height(16.dp))
                MissionAttributeSelectionGrid { atributo ->
                    selectedAttribute = atributo
                    currentStep = MissionRegistrationStep.SELECT_ACTIVITY
                }
            }
            MissionRegistrationStep.SELECT_ACTIVITY -> {
                Text("2. ¿Qué has hecho para ${selectedAttribute?.nombreVisible}?", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF1A1A))
                Spacer(modifier = Modifier.height(16.dp))
                MissionActivitySelectionList(
                    atributo = selectedAttribute!!,
                    onActivitySelected = { actividad ->
                        title = actividad
                        currentStep = MissionRegistrationStep.SELECT_IMPACT
                    }
                )
            }
            MissionRegistrationStep.SELECT_IMPACT -> {
                Text("3. Nivel de Impacto", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF1A1A))
                Text("¿Qué magnitud tuvo esta actividad?", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
                MissionImpactSelectionList { impacto ->
                    onAdd(title, selectedAttribute!!, impacto)
                }
            }
        }
    }
}

@Composable
fun MissionAttributeSelectionGrid(onAttributeSelected: (TipoAtributo) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        val rows = TipoAtributo.entries.chunked(2)
        for (row in rows) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                for (atributo in row) {
                    val colorAtributo = obtenerColorAtributo(atributo)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp)
                            .clickable { onAttributeSelected(atributo) }
                            .border(1.dp, colorAtributo.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(atributo.obtenerRecursoIcono()),
                                contentDescription = atributo.nombreVisible,
                                modifier = Modifier.size(32.dp),
                                tint = colorAtributo
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(atributo.nombreVisible, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colorAtributo)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionActivitySelectionList(atributo: TipoAtributo, onActivitySelected: (String) -> Unit) {
    var customActivity by remember { mutableStateOf("") }
    val colorAtributo = obtenerColorAtributo(atributo)

    Column {
        OutlinedTextField(
            value = customActivity,
            onValueChange = { customActivity = it },
            label = { Text("Misión personalizada", color = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorAtributo,
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                if (customActivity.isNotBlank()) {
                    TextButton(onClick = { onActivitySelected(customActivity) }) {
                        Text("Añadir", color = colorAtributo, fontWeight = FontWeight.Bold)
                    }
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Sugerencias:", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        
        val activities = actividadesPredefinidas[atributo] ?: emptyList()
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(activities) { actividad ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onActivitySelected(actividad) }
                        .border(0.5.dp, Color(0xFF333333), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                ) {
                    Text(
                        text = actividad,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun MissionImpactSelectionList(onImpactSelected: (NivelImpacto) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NivelImpacto.entries.forEach { impacto ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onImpactSelected(impacto) }
                    .border(0.5.dp, Color(0xFF333333), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(impacto.nombreVisible, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("+${impacto.puntos.toInt()} pts", color = Color(0xFFFF1A1A), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MissionItem(
    mision: Mision,
    onComplete: (String) -> Unit
) {
    val progressAnim = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0D0D0D))
            .border(0.5.dp, Color(0xFF1E1E1E), RoundedCornerShape(10.dp))
            .pointerInput(mision.estaCompletada) {
                if (mision.estaCompletada) return@pointerInput
                detectTapGestures(
                    onPress = {
                        scope.launch {
                            progressAnim.animateTo(
                                targetValue = 1f,
                                animationSpec = tween(800, easing = FastOutSlowInEasing)
                            )
                            if (progressAnim.value >= 1f) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onComplete(mision.id)
                            }
                        }
                        val released = tryAwaitRelease()
                        if (!released || progressAnim.value < 1f) {
                            scope.launch {
                                progressAnim.animateTo(0f, tween(200))
                            }
                        }
                    }
                )
            }
    ) {
        Box(modifier = Modifier.matchParentSize(), contentAlignment = Alignment.CenterStart) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(if (mision.estaCompletada) 1f else progressAnim.value)
                    .background(if (mision.estaCompletada) Color(0xFF1A0000) else Color(0xFF3A0000))
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(mision.colorAtributo)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mision.titulo,
                    color = if (mision.estaCompletada) Color(0xFF444444) else Color(0xFFE0E0E0),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (mision.estaCompletada) TextDecoration.LineThrough else TextDecoration.None
                )
                Text(
                    text = mision.nombreAtributo.uppercase(),
                    color = Color(0xFF444444),
                    fontSize = 11.sp,
                    letterSpacing = 0.05.em
                )
            }
            Text(
                text = if (mision.estaCompletada) "✓" else "+${mision.puntos} pts",
                color = if (mision.estaCompletada) Color(0xFF2A4A2A) else Color(0xFFCC2222),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
