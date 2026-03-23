package org.example.liferpg.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.liferpg.model.*
import org.jetbrains.compose.resources.painterResource
import liferpg.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource

val SuperficieOscuraRegistro = Color(0xFF1E1E24)
val RojoSangreRegistro = Color(0xFFFF1A1A)
val GrisTextoRegistro = Color(0xFFAAAAAA)

enum class RegistrationStep {
    SELECCIONAR_ATRIBUTO, SELECCIONAR_ACTIVIDAD, SELECCIONAR_IMPACTO
}

fun TipoAtributo.obtenerRecursoIcono(): DrawableResource {
    return when(this) {
        TipoAtributo.FUERZA -> Res.drawable.fuerza_icono
        TipoAtributo.SABIDURIA -> Res.drawable.sabiduria_icono
        TipoAtributo.ENERGIA -> Res.drawable.energia_icono
        TipoAtributo.ESTRUCTURA -> Res.drawable.estructura_icono
        TipoAtributo.ILUMINACION -> Res.drawable.iluminacion_icono
        TipoAtributo.VOLUNTAD -> Res.drawable.voluntad_icono
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskRegistrationSheet(
    onDismiss: () -> Unit,
    onConfirm: (TipoAtributo, String, NivelImpacto) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    var currentStep by remember { mutableStateOf(RegistrationStep.SELECCIONAR_ATRIBUTO) }
    var selectedAttribute by remember { mutableStateOf<TipoAtributo?>(null) }
    var selectedActivity by remember { mutableStateOf("") }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SuperficieOscuraRegistro,
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 16.dp, end = 16.dp, top = 8.dp)
                .defaultMinSize(minHeight = 400.dp)
        ) {
            when (currentStep) {
                RegistrationStep.SELECCIONAR_ATRIBUTO -> {
                    Text("1. Selecciona el Atributo", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = RojoSangreRegistro)
                    Spacer(modifier = Modifier.height(16.dp))
                    AttributeSelectionGrid { atributo ->
                        selectedAttribute = atributo
                        currentStep = RegistrationStep.SELECCIONAR_ACTIVIDAD
                    }
                }
                RegistrationStep.SELECCIONAR_ACTIVIDAD -> {
                    Text("2. ¿Qué has hecho para ${selectedAttribute?.nombreVisible}?", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = RojoSangreRegistro)
                    Spacer(modifier = Modifier.height(16.dp))
                    ActivitySelectionList(
                        atributo = selectedAttribute!!,
                        onActivitySelected = { actividad ->
                            selectedActivity = actividad
                            currentStep = RegistrationStep.SELECCIONAR_IMPACTO
                        }
                    )
                }
                RegistrationStep.SELECCIONAR_IMPACTO -> {
                    Text("3. Nivel de Impacto", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = RojoSangreRegistro)
                    Text("¿Qué magnitud tuvo esta actividad?", color = GrisTextoRegistro, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    ImpactSelectionList { impacto ->
                        onConfirm(selectedAttribute!!, selectedActivity, impacto)
                    }
                }
            }
        }
    }
}

@Composable
fun AttributeSelectionGrid(onAttributeSelected: (TipoAtributo) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        val rows = TipoAtributo.entries.chunked(2)
        for (row in rows) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                for (atributo in row) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp)
                            .clickable { onAttributeSelected(atributo) },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A35)),
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
                                tint = Color.Unspecified
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(atributo.nombreVisible, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitySelectionList(atributo: TipoAtributo, onActivitySelected: (String) -> Unit) {
    var customActivity by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = customActivity,
            onValueChange = { customActivity = it },
            label = { Text("Actividad personalizada", color = GrisTextoRegistro) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = RojoSangreRegistro,
                unfocusedBorderColor = GrisTextoRegistro,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                if (customActivity.isNotBlank()) {
                    TextButton(onClick = { onActivitySelected(customActivity) }) {
                        Text("Añadir", color = RojoSangreRegistro, fontWeight = FontWeight.Bold)
                    }
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Sugerencias:", color = GrisTextoRegistro, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        
        val activities = actividadesPredefinidas[atributo] ?: emptyList()
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(activities) { actividad ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onActivitySelected(actividad) },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A35)),
                ) {
                    Text(
                        text = actividad,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ImpactSelectionList(onImpactSelected: (NivelImpacto) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NivelImpacto.entries.forEach { impacto ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onImpactSelected(impacto) },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A35)),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(impacto.nombreVisible, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("${impacto.puntos.toInt()} pts", color = RojoSangreRegistro, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
