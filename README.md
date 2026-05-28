# FreelanceHub

## Plataforma de contratación freelance con pago simulado, evidencias y arbitraje

FreelanceHub es una plataforma web creada para simular el proceso de contratación de servicios freelance.  
El sistema permite que un cliente publique un proyecto, reciba cotizaciones de profesionales, acepte una propuesta, cree un pago simulado, reciba evidencias del trabajo realizado y, si no está conforme, abra un proceso de arbitraje para que un administrador revise el caso.

## Integrantes

| Integrante | Responsabilidad principal |
|---|---|
| Samuel Riveros | Configuración, seguridad, autenticación JWT, excepciones y arranque del backend |
| Julian Caicedo | Dominio, entidades, DTOs, repositories, mappers y modelo de datos |
| Joseph Ibarra | Services, controllers de negocio, frontend, evidencias, pagos y arbitraje |

## Estructura del proyecto

```text
FreelanceHub/
├── freelancehub/   # Backend Spring Boot
└── frontend/       # Frontend React + Vite
Tecnologías utilizadas
Backend
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
Frontend
React
Vite
JavaScript
Axios
React Router DOM
CSS
Roles del sistema
Cliente

El cliente puede:

Registrarse e iniciar sesión.
Crear proyectos.
Adjuntar archivos con instrucciones.
Recibir cotizaciones.
Aceptar propuestas.
Crear pagos simulados.
Revisar evidencias.
Aprobar entregas.
Liberar pagos.
Solicitar arbitraje si no está conforme.
Profesional

El profesional puede:

Registrarse e iniciar sesión.
Ver proyectos abiertos.
Descargar archivos adjuntos del proyecto.
Enviar cotizaciones.
Ver pagos retenidos.
Subir evidencias de entrega.
Responder arbitrajes.
Administrador

El administrador puede:

Iniciar sesión.
Revisar casos de arbitraje.
Analizar la información enviada por el cliente y el profesional.
Tomar una decisión final sobre el caso.
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
Componentes elevadores
1. Pago simulado tipo escrow

El sistema permite crear un pago simulado que queda en estado RETENIDO.
El pago solo cambia de estado cuando el cliente aprueba la evidencia o cuando el administrador resuelve un arbitraje.

Estados principales:

RETENIDO
LIBERADO
DEVUELTO
CANCELADO
2. Documentos y evidencias

El sistema permite cargar archivos asociados a proyectos y entregas.
El cliente puede adjuntar instrucciones al crear un proyecto y el profesional puede subir evidencias del trabajo realizado.

3. Arbitraje administrativo

Si el cliente no está conforme con la evidencia entregada, puede abrir un arbitraje.
El profesional responde el caso y el administrador decide:

A favor del profesional: el pago pasa a LIBERADO.
A favor del cliente: el pago pasa a DEVUELTO.
Corrección solicitada: el pago sigue RETENIDO.
Distribución del trabajo en Git

El proyecto se organizó por ramas para que cada integrante trabajara una parte específica.

Primero se integró todo en la rama develop y al final se pasó la versión completa a main.

Ramas utilizadas
main
develop
samuel/backend-config-security
julian/backend-domain-data
joseph/backend-business-flow
joseph/frontend-integration
Aportes por integrante
Samuel Riveros

Rama: samuel/backend-config-security

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

Julian Caicedo

Rama: julian/backend-domain-data

Julian trabajó la capa de dominio del backend. Esta capa define cómo se representa la información del sistema en la base de datos y cómo se transportan los datos entre el backend y el frontend.

Archivos y carpetas principales:

entities/
enums/
repositories/
mapper/
controllers/dtos/request/
controllers/dtos/responses/

En esta parte se encuentran las entidades JPA, los estados del sistema, los repositorios, los DTOs y los mappers.

Joseph Ibarra

Ramas:

joseph/backend-business-flow
joseph/frontend-integration

Joseph trabajó los controllers y services de negocio, además de la integración del frontend.

Archivos y carpetas principales:

controllers/, excepto AuthController.java
services/, excepto AuthService.java
frontend/src/

Joseph implementó el flujo funcional del sistema:

Proyectos.
Cotizaciones.
Pagos simulados.
Evidencias.
Adjuntos.
Arbitraje.
Integración visual con React.
Ejecución del proyecto
Requisitos previos
Java 21
Maven
Node.js
npm
PostgreSQL o Supabase
Git
Ejecutar backend
cd freelancehub
mvn clean compile
mvn spring-boot:run
Ejecutar frontend
cd frontend
npm install
npm run dev
URLs principales
Backend: http://localhost:8080
Frontend: http://localhost:5173
Swagger: http://localhost:8080/swagger-ui.html
Usuarios de prueba
Rol	Correo	Contraseña
ADMIN	admin@freelancehub.com	Admin123!
CLIENTE	cliente@test.com	Cliente123!
PROFESIONAL	profesional@test.com	Profesional123!
Flujo de demostración recomendado
Iniciar sesión como cliente.
Crear un proyecto con archivo adjunto.
Iniciar sesión como profesional.
Ver proyectos abiertos.
Descargar el archivo adjunto del proyecto.
Enviar una cotización.
Iniciar sesión como cliente.
Aceptar la cotización.
Crear un pago simulado.
Verificar que el pago queda en estado RETENIDO.
Iniciar sesión como profesional.
Subir evidencia de entrega.
Iniciar sesión como cliente.
Aprobar evidencia y liberar pago.

Flujo alternativo:

El cliente no aprueba la evidencia.
El cliente abre arbitraje.
El profesional responde el arbitraje.
El administrador revisa el caso.
El administrador resuelve a favor del cliente, del profesional o solicita corrección.
Seguridad

El backend utiliza Spring Security con JWT.
Las rutas de login y registro son públicas.
Las demás rutas requieren autenticación mediante:

Authorization: Bearer <token>

Las contraseñas se almacenan cifradas con BCrypt.

Estado del proyecto

El proyecto cuenta con:

Backend funcional.
Frontend funcional.
Login por roles.
Flujo de proyecto, cotización, pago, evidencia y arbitraje.
Carga y descarga de archivos.
Seguridad JWT.
Validaciones.
Manejo de excepciones.
Documentación técnica final.
Funcionalidades pendientes o mejoras futuras
Dashboard analítico avanzado.
Calificación mutua completa.
Pruebas unitarias.
Notificaciones internas.
Chat en tiempo real.
Pago real con pasarela bancaria.
Firma digital certificada.
Nota

Este proyecto fue desarrollado con fines académicos.
El pago simulado no procesa dinero real ni almacena información bancaria.


## Después de pegarlo

Haz commit desde GitHub con mensaje:

```txt
docs: mejorar formato del README

Con eso el README ya no se verá como un bloque feo, sino organizado con títulos, tablas, listas y comandos.
