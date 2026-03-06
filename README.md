# TherApp

Aplicación Android de **telerehabilitación** que utiliza detección de poses en tiempo real mediante **MediaPipe** para guiar y monitorear ejercicios de fisioterapia. Permite a los pacientes realizar rutinas asignadas mientras la app rastrea articulaciones, calcula ángulos y cuenta repeticiones.

## Características

- **Detección de poses en tiempo real** con MediaPipe Pose Landmarker (modelos lite, full y heavy)
- **Cálculo de ángulos articulares** y conteo automático de repeticiones
- **Grabación de video** de sesiones de ejercicio con CameraX
- **Subida de videos** a Firebase Storage con seguimiento de progreso
- **Gestión de rutinas y terapias**: visualización de entrenamientos programados, detalle de ejercicios con dificultades y rangos de movimiento
- **Autenticación JWT** con refresh token automático
- **Caché local** con Room para funcionamiento offline
- **Selector de articulaciones** configurable para rastrear puntos específicos del cuerpo
- **Controles de cámara**: zoom, flash, cambio de cámara frontal/trasera

## Arquitectura

El proyecto sigue **Clean Architecture** con patrón **MVVM**:

```
app/src/main/java/com/example/therapp/
├── app/                  # Entry point de la app (PoseLandMarkerApp)
├── common/               # Constantes, DataStore, utilidades de API
├── data/                 # Capa de datos
│   ├── auth/             # Repositorio e API de autenticación
│   ├── local/            # Room Database, DAOs
│   ├── routines/         # Repositorio, API, entidades y mappers de rutinas
│   └── user/             # Datos de usuario
├── di/                   # Módulos de Dagger Hilt (API, DB, Repository, UseCases)
├── domain/               # Capa de dominio
│   ├── model/            # Modelos de negocio (Routine, Exercise, Therapy, etc.)
│   ├── repository/       # Interfaces de repositorios
│   └── use_cases/        # Casos de uso (auth, routines)
├── security/             # TokenManager, AuthInterceptor, AuthAuthenticator
├── service/              # VideoStorageRepository (Firebase Storage)
└── ui/                   # Capa de presentación
    ├── components/       # Componentes reutilizables y sistema de pose overlay
    │   └── pose/         # Renderers, calculators, strategies de pose
    ├── global_viewmodels/# AuthViewModel
    ├── navigation/       # Grafos y rutas de navegación
    ├── presenter/        # Pantallas y ViewModels
    │   ├── home/         # Pantalla principal con bottom/drawer navigation
    │   ├── pose_camera/  # Cámara con detección de poses
    │   ├── routines/     # Listado y detalle de rutinas
    │   ├── sign_in/      # Inicio de sesión
    │   ├── splash/       # Splash screen
    │   └── therapies/    # Pantalla de terapias
    └── theme/            # Colores, tipografía, tema Material 3
```

## Stack Tecnológico

- **Lenguaje**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Inyección de dependencias**: Dagger Hilt + KSP
- **Networking**: Ktor + Retrofit + OkHttp
- **Base de datos local**: Room
- **Preferencias**: DataStore
- **Cámara**: CameraX
- **Detección de poses**: MediaPipe Pose Landmarker
- **Almacenamiento de videos**: Firebase Storage
- **Navegación**: Jetpack Navigation (type-safe con Kotlinx Serialization)
- **Reproducción de video**: Media3 / ExoPlayer
- **Carga de imágenes**: Glide Compose

## Requisitos

- Android Studio Hedgehog o superior
- JDK 11
- Android SDK 35 (compilación)
- Dispositivo o emulador con API 24+ (Android 7.0)

## Configuración

1. Clonar el repositorio:
   ```bash
   git clone <url-del-repositorio>
   ```

2. Abrir el proyecto en Android Studio.

3. Configurar Firebase:
   - El archivo `google-services.json` debe estar en `app/`.
   - Asegurarse de tener Firebase Storage habilitado en la consola de Firebase.

4. Configurar la URL del backend en `common/Constants.kt`:
   ```kotlin
   private val IP = "10.0.2.2"   // Para emulador (localhost del host)
   private val PORT = "8000"
   ```

5. Sincronizar Gradle y ejecutar la app.

## Autor

**Santiago Varela Daza**
- Email: svarela03@uan.edu.co
- GitHub: [sanvarela03](https://github.com/sanvarela03)
