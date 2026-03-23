# LifeRPG ⚔️

LifeRPG es una aplicación móvil multiplataforma desarrollada en **Kotlin Multiplatform (KMP)** con **Compose Multiplatform**. Su objetivo es gamificar tu progreso personal convirtiendo las tareas, rutinas y hábitos diarios en puntos de "atributos" (Fuerza, Sabiduría, Energía, Estructura, Iluminación y Voluntad), representados de forma increíble en un Gráfico de Radar hexagonal dinámico.

## ✨ Características Principales

- **Gráfico de Radar Interactivo**: Diseño visual vanguardista con un hexágono central que se expande basado en la experiencia (puntos) del usuario, dibujado y calculado matemáticamente de forma nativa utilizando el Canvas de Compose.
- **Registro de Misiones**: Flujo interactivo (Bottom Sheet) que permite registrar actividades, asignarles un atributo correspondiente y un "nivel de impacto" para ganar puntos automáticamente.
- **Persistencia de Datos Local**: Todo el progreso y el estado de las misiones se guarda de forma segura e indefinida en el dispositivo usando de base `SharedPreferences` (Android) / `NSUserDefaults` (iOS) para asegurar máxima privacidad e inmediatez de carga.
- **Reseteo Diario (Daily Quests)**: Reconocimiento del ciclo del día local del usuario para limpiar las misiones completadas tras 24 horas y empezar frescos.

## 🛠️ Stack Tecnológico y Arquitectura

- **Lenguaje**: [Kotlin 2.x](https://kotlinlang.org/)
- **UI Framework**: [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- **Gestión de Estado**: `ViewModel` nativo en Compose Multiplatform + `LaunchedEffect` para guardado de estado reactivo.
- **Persistencia y Datos**: 
  - [Multiplatform Settings](https://github.com/russhwolf/multiplatform-settings) para acceso nativo al disco.
  - `kotlinx-serialization-json` para serialización de datos de dominio complejos.

