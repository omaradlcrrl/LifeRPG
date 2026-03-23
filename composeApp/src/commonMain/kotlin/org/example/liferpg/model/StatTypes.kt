package org.example.liferpg.model

import kotlin.math.min
import kotlin.math.max

enum class TipoAtributo(val nombreVisible: String) {
    FUERZA("Fuerza"),
    SABIDURIA("Sabiduría"),
    ENERGIA("Energía"),
    ESTRUCTURA("Estructura"),
    ILUMINACION("Iluminación"),
    VOLUNTAD("Voluntad")
}

enum class NivelImpacto(val puntos: Float, val nombreVisible: String) {
    BAJO(3f, "Bajo"),
    MEDIO(6f, "Medio"),
    ALTO(10f, "Alto")
}

object CalculadoraAtributos {
    const val TOPE_DIARIO = 10f

    fun calcularPuntos(impacto: NivelImpacto): Float {
        return min(TOPE_DIARIO, impacto.puntos)
    }
}

val actividadesPredefinidas = mapOf(
    TipoAtributo.FUERZA to listOf(
        "Gym / pesas", "Correr", "Cardio", "Deportes", "Yoga", "Artes marciales", "Caminar"
    ),
    TipoAtributo.SABIDURIA to listOf(
        "Leer", "Estudiar", "Curso online", "Podcast", "Aprender idioma", "Investigar", "Clases", "Escribir / reflexionar"
    ),
    TipoAtributo.ENERGIA to listOf(
        "Dormir bien", "Alimentación saludable", "Hidratación"
    ),
    TipoAtributo.ESTRUCTURA to listOf(
        "Planificar el día", "Cumplir rutina", "Ordenar / limpiar", "Gestionar finanzas", "Revisar objetivos", "To-do list", "Levantarme a la hora"
    ),
    TipoAtributo.ILUMINACION to listOf(
        "Meditación", "Journaling", "Contacto con naturaleza", "Desconexión digital"
    ),
    TipoAtributo.VOLUNTAD to listOf(
        "Resistir una tentación", "Hacer algo que me da miedo", "Sin móvil X horas", "Cumplir un compromiso difícil"
    )
)
