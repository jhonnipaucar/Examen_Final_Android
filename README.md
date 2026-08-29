MIS GASTOS APP

Una aplicación móvil Android nativa y completa diseñada para gestionar el registro de gastos personales de forma local y realizar conversiones de moneda en tiempo real mediante conexión a internet.

DESCRIPCIÓN DE LA APLICACIÓN
"Mis Gastos" permite al usuario registrar sus finanzas personales, guardar fotos de sus comprobantes físicos usando la cámara del hardware o la galería, y mantener un historial financiero detallado
Incorpora un sistema de conversión automática a USD y una estrategia "Offline-First" para no perder registros si falla la conexión. Además, incluye soporte nativo para Modo Oscuro gestionado por el usuario.

ARQUITECTURA
El proyecto fue construido utilizando MVVM (Model-View-ViewModel) combinado con el Patrón Repositorio (Clean Architecture).

*   Capa UI: Desarrollada 100% con Jetpack Compose, utilizando navegación reactiva y LazyColumn para el manejo eficiente de listas dinámicas en el historial.
*   ViewModel: Centraliza la lógica de negocio y expone los estados a la interfaz visual utilizando flujos reactivos (StateFlow).
*   Repositorio: Actúa como mediador para la persistencia de datos aplicando el principio de abstracción. La UI no conoce el origen de la información y se comunica únicamente con el ViewModel. Por su parte, el ViewModel centraliza la lógica de negocio: gestiona las peticiones externas de red (Retrofit) de manera asíncrona y delega todo el almacenamiento y lectura local (DAOs y DataStore) exclusivamente a través del Repositorio.
*   Corrutinas: Se utilizaron flujos asíncronos (viewModelScope.launch) para todas las operaciones pesadas y llamadas a red, asegurando un rendimiento óptimo.

FUENTES DE DATOS

*   Persistencia Local (Room): Se utilizó Room para el almacenamiento estructurado de los gastos e historial del usuario[cite: 1].
*   Persistencia de Preferencias (DataStore): Manejo de las configuraciones del usuario (activación del Modo Oscuro) de manera reactiva y persistente[cite: 1].
*   Consumo de API REST (Retrofit):
    *   API Utilizada: ExchangeRate-API (Tipos de cambio de divisas).
    *   Manejo completo de asincronía incluyendo los estados visuales de "Cargando", "Éxito" y "Error de Red" (permitiendo guardar el gasto con su valor nominal si no hay internet).

CAPTURAS DE PANTALLA

*   Pantalla de Inicio
*   
<img width="720" height="1600" alt="WhatsApp Image 2026-08-23 at 3 53 56 PM" src="https://github.com/user-attachments/assets/d35f375f-bd7e-4b21-9d50-9a8433b19dc5" />

*   Pantalla del Historial Completo

<img width="720" height="1600" alt="WhatsApp Image 2026-08-23 at 3 28 40 PM (2)" src="https://github.com/user-attachments/assets/6d570e22-3fe7-430a-b261-765498a0a4b9" />

*   Pantalla de Registro de Nuevo Gasto

<img width="1080" height="2400" alt="WhatsApp Image 2026-08-23 at 3 28 40 PM (3)" src="https://github.com/user-attachments/assets/3132e0bd-1c4b-4c10-9467-6b7bd5797886" />

*   Pantalla de Detalles y Foto del Comprobante

  <img width="720" height="1600" alt="WhatsApp Image 2026-08-23 at 3 28 40 PM (4)" src="https://github.com/user-attachments/assets/4a14d5e7-ad02-462f-8bbb-e234fd56c6c0" />

*   Pantalla de Ajustes (Modo Oscuro activado)

<img width="1080" height="2400" alt="WhatsApp Image 2026-08-23 at 3 28 39 PM" src="https://github.com/user-attachments/assets/d9254f63-8dcb-4ae0-8353-d4117c558b9f" />

CAPTURA DEL DIAGRAMA

<img width="3582" height="4982" alt="Untitled diagram-2026-08-23-205854" src="https://github.com/user-attachments/assets/f3bcbcac-a75c-4076-816d-9a4eee41827e" />

Proyecto Final de Aplicaciones Móviles.
