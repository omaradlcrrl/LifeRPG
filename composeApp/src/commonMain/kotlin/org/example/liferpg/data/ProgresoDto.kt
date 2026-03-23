package org.example.liferpg.data

import kotlinx.serialization.Serializable

@Serializable
data class MisionDto(
    val id: String,
    val titulo: String,
    val tipoAtributoNombre: String,
    val puntos: Int,
    val estaCompletada: Boolean
)
