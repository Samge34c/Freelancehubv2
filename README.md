FreelanceHub
Plataforma de contratación freelance con pago simulado, evidencias y arbitraje
Integrantes:
Samuel Riveros
Julian Caicedo
Joseph Ibarra
Descripción general
FreelanceHub es una plataforma web creada para simular el proceso de contratación de servicios freelance. La idea principal es que un cliente pueda publicar un proyecto, recibir cotizaciones de profesionales, aceptar una propuesta, crear un pago simulado, recibir evidencias del trabajo realizado y, si no está conforme, abrir un proceso de arbitraje para que un administrador revise el caso.
El proyecto está dividido en dos partes principales:
freelancehub: backend desarrollado con Java y Spring Boot.
frontend: interfaz web desarrollada con React y Vite.
El sistema maneja tres roles: CLIENTE, PROFESIONAL y ADMIN. Cada rol tiene permisos y pantallas diferentes dentro de la aplicación.
Estructura del proyecto
FreelanceHub/
freelancehub/   Backend Spring Boot
frontend/       Frontend React + Vite
Tecnologías utilizadas
Backend:
Java 21
Spring Boot 3.3.5
Spring Security
JWT
Spring Data JPA
Hibernate
PostgreSQL / Supabase
Maven
Jakarta Bean Validation
Swagger / OpenAPI
MapStruct
Frontend:
React
Vite
JavaScript
Axios
React Router DOM
CSS
Roles del sistema
CLIENTE:
El cliente puede registrarse, iniciar sesión, crear proyectos, adjuntar archivos con instrucciones, recibir cotizaciones, aceptar propuestas, crear pagos simulados, revisar evidencias, aprobar entregas, liberar pagos o solicitar arbitraje si no está conforme.
PROFESIONAL:
El profesional puede registrarse, iniciar sesión, ver proyectos abiertos, descargar archivos adjuntos del proyecto, enviar cotizaciones, ver pagos retenidos, subir evidencias de entrega y responder arbitrajes.
ADMIN:
El administrador puede iniciar sesión, revisar casos de arbitraje, analizar la información enviada por el cliente y el profesional, y tomar una decisión final sobre el caso.
Funcionalidades principales
Registro de cliente.
Registro de profesional.
Inicio de sesión con JWT.
Menú por roles.
Creación de proyectos.
Adjuntos de instrucciones en proyectos.
Visualización de proyectos abiertos.
Envío de cotizaciones.
Aceptación de cotizaciones.
Pago simulado tipo escrow.
Visualización del pago retenido.
Subida de evidencias.
Aprobación de evidencias.
Liberación del pago.
Solicitud de arbitraje.
Respuesta del profesional al arbitraje.
Resolución del arbitraje por parte del administrador.
Carga y descarga protegida de archivos.
Distribución del trabajo en Git
El proyecto se organizó por ramas para que cada integrante trabajara una parte específica. Primero se integró todo en la rama develop y al final se pasó la versión completa a main.
Ramas utilizadas:
main
develop
samuel/backend-config-security
julian/backend-domain-data
joseph/backend-business-flow
joseph/frontend-integration
Aportes por integrante
Samuel Riveros
Rama:
samuel/backend-config-security
Samuel trabajó la base inicial del backend, la configuración general del proyecto, la seguridad, la autenticación, el manejo de JWT y el manejo global de errores.
Archivos y carpetas principales:
pom.xml
application.properties
FreelanceHubApplication.java
config/
config/jwt/
security/
exception/
AuthController.java
AuthService.java
controllers/dtos/auth/
Aunque AuthController está dentro de la carpeta controllers y AuthService está dentro de services, estos archivos hacen parte del módulo de autenticación y seguridad. Por eso fueron trabajados por Samuel.
Esta parte permite que el usuario pueda registrarse, iniciar sesión, recibir un token JWT y acceder al sistema dependiendo de su rol.
Julian Caicedo
Rama:
julian/backend-domain-data
Julian trabajó la capa de dominio del backend. Esta capa define cómo se representa la información del sistema en la base de datos y cómo se transportan los datos entre el backend y el frontend.
Archivos y carpetas principales:
entities/
enums/
repositories/
mapper/
controllers/dtos/request/
controllers/dtos/responses/
En esta parte se encuentran las entidades JPA, los estados del sistema, los repositorios, los DTOs y los mappers.
Julian también trabajó validaciones en los DTOs usando anotaciones como:
@NotBlank
@NotNull
@Email
@Size
@Positive
Estas validaciones permiten revisar que los datos enviados al backend sean correctos antes de ejecutar la lógica del sistema.
Joseph Ibarra
Rama backend:
joseph/backend-business-flow
Joseph trabajó los controladores y servicios de negocio del backend. Esta parte contiene la lógica principal del sistema y conecta las peticiones HTTP con las reglas de negocio.
Archivos y carpetas principales:
controllers/
services/
Excepción:
AuthController y AuthService no corresponden a Joseph, porque pertenecen al módulo de autenticación trabajado por Samuel.
En esta rama se trabajaron los flujos principales de:
Proyectos.
Cotizaciones.
Pagos simulados.
Evidencias.
Adjuntos de proyecto.
Usuarios.
Categorías.
Habilidades.
Arbitrajes.
Rama frontend:
joseph/frontend-integration
Joseph también integró el frontend completo al final. Esta rama se subió de última porque el frontend depende de los endpoints y flujos que ya estaban implementados en el backend.
En el frontend se trabajó:
Login por roles.
Registro de cliente y profesional.
Menú por roles.
Panel de cliente.
Panel de profesional.
Panel de administrador.
Creación de proyectos.
Adjuntos.
Cotizaciones.
Pagos simulados.
Evidencias.
Arbitrajes.
Conexión con backend usando Axios.
Flujo principal del sistema
El cliente inicia sesión.
El cliente crea un proyecto.
El cliente puede adjuntar un archivo con instrucciones.
El profesional inicia sesión.
El profesional ve los proyectos abiertos.
El profesional revisa el proyecto y envía una cotización.
El cliente revisa las cotizaciones recibidas.
El cliente acepta una cotización.
El proyecto pasa a estado EN_CONTRATO.
El cliente crea un pago simulado.
El pago queda en estado RETENIDO.
El profesional ve que el pago está retenido.
El profesional sube una evidencia de entrega.
El cliente revisa la evidencia.
Si está conforme, el cliente aprueba la evidencia.
El cliente libera el pago.
El pago pasa a LIBERADO.
El proyecto queda CERRADO.
Flujo de arbitraje
El arbitraje se usa cuando el cliente no está conforme con la evidencia entregada por el profesional.
El profesional sube una evidencia.
El cliente revisa la evidencia.
Si no está conforme, el cliente solicita arbitraje.
El cliente escribe el motivo del reclamo y puede adjuntar un archivo de soporte.
El pago queda retenido mientras el arbitraje está activo.
El profesional revisa el arbitraje.
El profesional responde y puede adjuntar soporte.
El administrador revisa el caso.
El administrador toma una decisión.
Resultados posibles del arbitraje:
A favor del profesional:
El pago pasa a LIBERADO.
El proyecto pasa a CERRADO.
La evidencia queda aprobada.
A favor del cliente:
El pago pasa a DEVUELTO.
El proyecto pasa a CANCELADO.
La evidencia queda rechazada.
Solicitud de corrección:
El pago sigue RETENIDO.
El proyecto pasa a EN_REVISION.
El profesional puede corregir y subir una nueva evidencia.
Pago simulado
El pago simulado representa un modelo académico tipo escrow. No se mueve dinero real ni se conecta con una pasarela de pagos externa.
El objetivo de este flujo es simular una garantía entre cliente y profesional. El cliente registra un pago que queda retenido mientras el profesional realiza la entrega. El pago solo se libera si el cliente aprueba la evidencia o si el administrador resuelve un arbitraje a favor del profesional.
Estados principales del pago:
RETENIDO
LIBERADO
DEVUELTO
CANCELADO
Evidencias y archivos
El sistema maneja archivos en varias partes del flujo:
Adjuntos de proyecto:
El cliente puede subir archivos con instrucciones o requisitos al crear un proyecto.
Evidencias:
El profesional puede subir archivos como prueba de entrega.
Soportes de arbitraje:
El cliente y el profesional pueden adjuntar archivos durante el proceso de arbitraje.
Las descargas de archivos están protegidas mediante autenticación y validación de permisos.
Seguridad
El sistema utiliza autenticación con JWT y autorización por roles.
Flujo general de seguridad:
El usuario inicia sesión.
El backend valida sus credenciales.
El backend genera un token JWT.
El frontend guarda el token.
En cada petición protegida, el frontend envía el token.
El backend valida el token.
El backend identifica el usuario y su rol.
La capa de servicios valida si el usuario puede realizar la acción solicitada.
Además de proteger las rutas, también se valida la propiedad de los recursos. Por ejemplo, un cliente no puede gestionar proyectos de otro cliente y un profesional no puede responder arbitrajes que no le correspondan.
Arquitectura del backend
El backend está organizado por capas:
config/: configuración general, seguridad, Swagger y datos iniciales.
config/jwt/: generación, validación y filtro JWT.
security/: usuario autenticado y utilidades de seguridad.
controllers/: endpoints REST.
services/: reglas de negocio.
repositories/: acceso a base de datos.
entities/: modelo JPA.
enums/: estados y roles.
mapper/: conversión de entidades a DTOs.
exception/: manejo centralizado de errores.
Flujo técnico de una petición:
Frontend React
-> Axios envía la petición con token
-> Filtro JWT valida autenticación
-> Controller recibe la petición
-> Service aplica reglas de negocio
-> Repository consulta o guarda en base de datos
-> Backend responde al frontend
Requisitos previos
Antes de ejecutar el proyecto se necesita tener instalado:
Java 21
Maven
Node.js
npm
Git
PostgreSQL o una base de datos Supabase
Variables de entorno
El backend usa variables de entorno para conectarse a la base de datos y configurar JWT.
Ejemplo:
DB_URL=jdbc:postgresql://TU_HOST:5432/postgres?sslmode=require
DB_USERNAME=TU_USUARIO
DB_PASSWORD=TU_PASSWORD
JWT_SECRET=TU_SECRETO_DE_32_BYTES_O_MAS
JWT_EXPIRATION=86400000
SERVER_PORT=8080
No se deben subir credenciales reales al repositorio.
Ejecutar backend
Desde la raíz del proyecto:
cd freelancehub
mvn clean compile
mvn spring-boot:run
El backend queda disponible en:
http://localhost:8080
Swagger queda disponible en:
http://localhost:8080/swagger-ui.html
Ejecutar frontend
Desde la raíz del proyecto:
cd frontend
npm install
npm run dev
El frontend queda disponible en:
http://localhost:5173
Usuarios de prueba
Administrador:
Correo: admin@freelancehub.com
Contraseña: Admin123!
Cliente:
Correo: cliente@test.com
Contraseña: Cliente123!
Profesional:
Correo: profesional@test.com
Contraseña: Profesional123!
Endpoints principales
La API usa el prefijo:
/api/v1
Auth:
POST /api/v1/auth/login
POST /api/v1/auth/register/client
POST /api/v1/auth/register/professional
GET  /api/v1/auth/me
Projects:
POST /api/v1/projects
GET  /api/v1/projects/open
GET  /api/v1/projects/my
GET  /api/v1/projects/{id}
PUT  /api/v1/projects/{id}
DELETE /api/v1/projects/{id}
Quotes:
POST /api/v1/projects/{id}/quotes
GET  /api/v1/projects/{id}/quotes
PUT  /api/v1/projects/{id}/quotes/{quoteId}/accept
GET  /api/v1/quotes/my
Payments:
POST /api/v1/projects/{projectId}/payments/simulate
GET  /api/v1/projects/{projectId}/payments
PUT  /api/v1/payments/{paymentId}/release
GET  /api/v1/payments/my
Evidences:
POST /api/v1/projects/{projectId}/evidences
GET  /api/v1/projects/{projectId}/evidences
GET  /api/v1/evidences/my
GET  /api/v1/evidences/{id}/download
Attachments:
POST /api/v1/projects/{projectId}/attachments
GET  /api/v1/projects/{projectId}/attachments
GET  /api/v1/projects/attachments/{id}/download
Arbitrations:
POST /api/v1/evidences/{id}/arbitrations
GET  /api/v1/arbitrations/my
POST /api/v1/arbitrations/{id}/response
GET  /api/v1/admin/arbitrations
GET  /api/v1/admin/arbitrations/{id}
PUT  /api/v1/admin/arbitrations/{id}/resolve
Reglas de negocio importantes
El correo del usuario debe ser único.
El usuario solo puede actuar según su rol.
El cliente solo puede gestionar sus propios proyectos.
El profesional solo puede cotizar proyectos abiertos.
Un profesional no puede cotizar dos veces el mismo proyecto.
Solo el cliente dueño puede aceptar cotizaciones.
Al aceptar una cotización, las demás cotizaciones quedan rechazadas.
El pago simulado queda RETENIDO antes de la entrega.
El profesional solo puede subir evidencia cuando existe pago retenido.
No se puede liberar un pago si existe un arbitraje activo.
Si el administrador falla a favor del profesional, el pago pasa a LIBERADO.
Si el administrador falla a favor del cliente, el pago pasa a DEVUELTO.
Si el administrador solicita corrección, el pago sigue RETENIDO.
Notas finales
FreelanceHub es un proyecto académico. El pago implementado es simulado y no representa una transacción real.
El propósito del sistema es mostrar una solución full stack con autenticación, roles, persistencia, validaciones, carga y descarga de archivos, reglas de negocio y un flujo completo entre cliente, profesional y administrador.
