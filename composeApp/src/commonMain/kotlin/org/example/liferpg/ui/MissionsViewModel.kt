package org.example.liferpg.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import org.example.liferpg.model.TipoAtributo
import org.example.liferpg.data.RepositorioProgreso
import org.example.liferpg.data.MisionDto
import kotlinx.datetime.todayIn
import kotlin.random.Random

data class Mision(
    val id: String = Random.nextLong().toString(),
    val titulo: String,
    val tipoAtributo: TipoAtributo,
    val nombreAtributo: String,
    val colorAtributo: Color,
    val puntos: Int,
    val estaCompletada: Boolean = false
)

class MisionesViewModel : ViewModel() {
    private val repositorio = RepositorioProgreso()
    val misiones = mutableStateListOf<Mision>()

    init {
        cargarMisionesGuardadas()
        comprobarNuevoDia()
    }

    private fun cargarMisionesGuardadas() {
        val guardadas = repositorio.cargarMisiones()
        guardadas.forEach { dto ->
            // Use the enum type by safely matching its name
            val tipo = TipoAtributo.entries.find { it.name == dto.tipoAtributoNombre } ?: return@forEach
            val mision = Mision(
                id = dto.id,
                titulo = dto.titulo,
                tipoAtributo = tipo,
                nombreAtributo = tipo.nombreVisible,
                colorAtributo = obtenerColorAtributo(tipo),
                puntos = dto.puntos,
                estaCompletada = dto.estaCompletada
            )
            misiones.add(mision)
        }
    }

    private fun guardarMisionesState() {
        val dtos = misiones.map { 
            MisionDto(it.id, it.titulo, it.tipoAtributo.name, it.puntos, it.estaCompletada)
        }
        repositorio.guardarMisiones(dtos)
    }

    fun agregarMision(mision: Mision) {
        misiones.add(mision)
        guardarMisionesState()
    }

    fun completarMision(id: String): Boolean {
        val i = misiones.indexOfFirst { it.id == id }
        if (i != -1 && !misiones[i].estaCompletada) {
            misiones[i] = misiones[i].copy(estaCompletada = true)
            guardarMisionesState()
            return true
        }
        return false
    }

    fun borrarMision(id: String) {
        val i = misiones.indexOfFirst { it.id == id }
        if (i != -1) {
            misiones.removeAt(i)
            guardarMisionesState()
        }
    }

    fun revertirMision(id: String): Mision? {
        val i = misiones.indexOfFirst { it.id == id }
        if (i != -1 && misiones[i].estaCompletada) {
            misiones[i] = misiones[i].copy(estaCompletada = false)
            guardarMisionesState()
            return misiones[i]
        }
        return null
    }

    fun limpiarCompletadasParaNuevoDia() {
        misiones.clear()
        guardarMisionesState()
    }

    private fun comprobarNuevoDia() {
        val ultimaFecha = repositorio.getUltimaFecha()
        val hoy = kotlinx.datetime.Clock.System.todayIn(kotlinx.datetime.TimeZone.currentSystemDefault()).toString()
        if (ultimaFecha.isNotEmpty() && ultimaFecha != hoy) {
            limpiarCompletadasParaNuevoDia()
        }
        repositorio.guardarUltimaFecha(hoy)
    }
}
