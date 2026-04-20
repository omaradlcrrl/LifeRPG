package org.example.liferpg.data

import com.russhwolf.settings.Settings
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RepositorioProgreso(private val settings: Settings = Settings()) {
    private val json = Json { ignoreUnknownKeys = true }

    fun guardarAtributosValores(valores: Map<String, Float>) {
        val str = json.encodeToString(valores)
        settings.putString("atributos_valores", str)
    }

    fun cargarAtributosValores(): Map<String, Float> {
        val str = settings.getStringOrNull("atributos_valores") ?: return emptyMap()
        return try {
            json.decodeFromString<Map<String, Float>>(str)
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun guardarMisiones(misionesDto: List<MisionDto>) {
        val str = json.encodeToString(misionesDto)
        settings.putString("misiones_dto", str)
    }

    fun cargarMisiones(): List<MisionDto> {
        val str = settings.getStringOrNull("misiones_dto") ?: return emptyList()
        return try {
            json.decodeFromString<List<MisionDto>>(str)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getUltimaFecha(): String {
        return settings.getStringOrNull("ultimaFecha") ?: ""
    }

    fun guardarUltimaFecha(fecha: String) {
        settings.putString("ultimaFecha", fecha)
    }
}
