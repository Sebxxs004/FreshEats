# Fresh Eats 🥗

Fresh Eats es una aplicación moderna y saludable para Android construida nativamente con **Kotlin** y **Jetpack Compose**. Su objetivo principal es combatir el desperdicio de alimentos permitiendo a los usuarios registrar los ingredientes que tienen en casa (su inventario) y sugerirles recetas deliciosas en base a su stock exacto, calculando además lo que les falta para cocinar cada plato.

---

## 🛠️ Arquitectura y Tecnologías

El proyecto sigue una arquitectura **MVVM** (Model-View-ViewModel) limpia, recomendada por Google para el desarrollo moderno en Android.

### Stack Tecnológico Principal
- **Kotlin**: Lenguaje principal de desarrollo.
- **Jetpack Compose**: Kit de herramientas UI moderno, declarativo y reactivo para construir la interfaz.
- **Coroutines & Flow (StateFlow / SharedFlow)**: Manejo asíncrono y programación reactiva bidireccional entre la capa de datos y la UI.
- **Retrofit & OkHttp**: Consumo de la API REST de recetas.
- **Coil**: Carga asíncrona de imágenes y almacenamiento en caché.
- **Jetpack Navigation Compose**: Gestión del enrutamiento de la aplicación (`NavGraph`).

---

## 🔥 Integración con Firebase

Toda la base de datos de usuarios y persistencia de la aplicación se migró exitosamente de bases de datos locales (como Room) a **Cloud Firestore**, garantizando sincronización en tiempo real y persistencia en la nube.

### 1. Firebase Authentication
- Se implementó un flujo completo de Login y Registro de usuarios usando autenticación por **Correo y Contraseña**.
- Estado de autenticación manejado globalmente y persistente (el usuario no necesita iniciar sesión cada vez que abre la app).

### 2. Cloud Firestore
La base de datos NoSQL sigue una estructura orientada al usuario:
- **Colección Raíz**: `users/{userId}` (cada usuario tiene su propio documento de perfil).
- **Subcolección `favorites`**: Almacena los IDs de las recetas que el usuario marca con el corazón. 
- **Subcolección `inventory`**: Guarda los ingredientes físicos que el usuario tiene en su casa (nombre, imagen, cantidad exacta y unidad estandarizada).
  
> [!NOTE]
> Se utilizan **SnapshotListeners** (`addSnapshotListener`) de Firestore convertidos a `callbackFlow` en los repositorios. Esto garantiza que cualquier cambio en la base de datos actualice la UI al instante y reaccione en todas las pantallas.

---

## 🍳 Integración con Spoonacular API

La aplicación es impulsada por la inteligencia gastronómica de **Spoonacular**.

1. **Autocompletado de Ingredientes** (`/food/ingredients/autocomplete`):
   Para evitar que el usuario introduzca datos "basura" en su inventario, todas las adiciones pasan por el endpoint de autocompletado de Spoonacular. Solo se guardan ingredientes reales con su imagen oficial. (Implementado con `debounce` para optimizar llamadas a la red).

2. **Búsqueda Automática de Recetas** (`/recipes/findByIngredients`):
   La pantalla de inicio (`HomeScreen`) lee el inventario del usuario y automáticamente llama a este endpoint pasando una lista separada por comas de los ingredientes que el usuario ya posee.

3. **Detalles Inteligentes y Nutrición** (`/recipes/{id}/information`):
   Al abrir el detalle de una receta, se obtiene toda la información, incluyendo los **pasos de preparación** (`analyzedInstructions`), **nutrición** (extraemos específicamente las calorías) y la lista de ingredientes (`extendedIngredients`).

---

## 🧠 Lógica Inteligente de Inventario (Recipe Detail)

El corazón de la aplicación radica en el `RecipeDetailViewModel`.
Cuando un usuario abre una receta, la aplicación:
1. Pide la lista de ingredientes requeridos a Spoonacular.
2. Descarga la lista de ingredientes actuales del usuario desde Firestore.
3. Utiliza un `combine` flow para cruzar los datos en tiempo real.
4. Calcula matemáticamente el diferencial: Si la receta pide 5 tomates y el usuario tiene 2, la UI avisa en la `RecipeDetailScreen` que **faltan 3 unidades** (Marcado en Naranja), y marca en Verde los que ya están cubiertos.

---

## 🎨 UI y UX (Design System)

La UI fue construida a mano bajo un sistema de diseño propio basado en tonos saludables:
- **Colores**: `GreenPrimary` (Énfasis), `GreenSurface` (Fondos), `OrangePrimary` (Advertencias/Destacados).
- **Microinteracciones**: Efectos Ripple limitados a áreas interactivas, botones flotantes de sumar/restar en el inventario.
- **Formularios robustos**: Dropdown menus (Material 3 Exposed Dropdowns) para estandarizar las unidades de medida (tazas, gramos, litros).

---

## 🚀 Cómo Empezar

1. **Clona este repositorio**.
2. **Configura Firebase**:
   - Crea un proyecto en Firebase Console.
   - Habilita Autenticación por Correo/Contraseña.
   - Crea una base de datos Firestore.
   - Descarga el archivo `google-services.json` y colócalo en el directorio `app/`.
3. **API Key de Spoonacular**:
   - Consigue tu API Key de [Spoonacular](https://spoonacular.com/food-api).
   - Insértala en `SpoonacularApiService.kt` (o utiliza el entorno configurado: `d0fc710568fe4450a1b158bb0e83062c`).
4. Compila y ejecuta (Requiere Android Studio y Kotlin configurado).
