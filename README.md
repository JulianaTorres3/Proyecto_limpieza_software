# 🧹 Servicio de Limpieza - Proyecto Desarrollo

Aplicación backend para gestión de solicitudes de servicio de limpieza, con frontend en Angular y base de datos en Railway.

##  Requisitos previos

Antes de instalar la aplicación, asegúrate de tener instalado:

| Herramienta | Versión | Descarga |
|-------------|---------|----------|
| **Java JDK** | 17 o superior | [Descargar](https://adoptium.net/) |
| **Node.js** | 18 o superior | [Descargar](https://nodejs.org/) |
| **Git** | Cualquier versión | [Descargar](https://git-scm.com/) |
| **MySQL Workbench** | Cualquier versión | [Descargar](https://dev.mysql.com/downloads/workbench/) |

##  Instalación paso a paso

### Paso 1: Clonar el repositorio

Abre la terminal (CMD, PowerShell o Git Bash) y ejecuta:

```bash
git clone https://github.com/JulianaTorres3/limpieza-proyecto
cd limpieza-proyecto
``` 

### Paso 2: Configurar la base de datos (Railway)
#### Inicia sesión en Railway.app

Ve a tu dashboard y selecciona tu base de datos MySQL

#### Copia los datos de conexión:

- Host: mysql.railway.internal

- Puerto: 3306

- Nombre DB: railway

- Usuario: root

- Contraseña: mjBMKvZEpLopGwDWHoBDUOuurNDxhLkw

### Paso 3: Configurar el backend (Spring Boot)
#### 3.1 Configurar archivo application.properties
Navega a la carpeta del backend:

```bash
cd limpieza_proyecto/src/main/resources/
```
#### 3.2 Edita el archivo application.properties
```bash
# Configuración de la base de datos
spring.datasource.url=jdbc:mysql://nozomi.proxy.rlwy.net:35702/railway
spring.datasource.username=root
spring.datasource.password=mjBMKvZEpLopGwDWHoBDUOuurNDxhLkw
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Configuración de JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Puerto del servidor
server.port=8080
```
#### 3.3 Ejecutar el backend
```bash
# En Windows
mvnw.cmd spring-boot:run

# En Mac/Linux
./mvnw spring-boot:run
```
### ✅ Verifica que funciona: Abre tu navegador en http://localhost:8080/api/requests
Importante: Deja esta terminal abierta mientras trabajes. Para el frontend, abre una nueva terminal.

### Paso 4: Configurar el frontend (Angular) 
Abre una nueva terminal (sin cerrar la del backend):
```bash
cd frontend

# Instalar dependencias (solo la primera vez)
npm install

# Ejecutar la aplicación Angular
npm start
```
### ✅ Abre tu navegador en: http://localhost:4200

### Paso 5: Ya esta todo listo! 
Ahora puedes usar la aplicación:

- Frontend: http://localhost:4200

- Backend API: http://localhost:8080/api/requests
  
## 📦 Estructura del proyecto
```bash
limpieza-proyecto/
├── backend/
│   ├── src/main/java/com/proyecto/limpieza_proyecto/
│   │   ├── Controller/
│   │   ├── Model/
│   │   ├── Repository/
│   │   └── Service/
│   └── pom.xml
├── frontend/
│   ├── src/
│   └── package.json
└── README.md
```
## 🔧 Endpoints de la API
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/requests` | Crear nueva solicitud |
| GET | `/api/requests` | Listar todas |
| GET | `/api/requests/{id}` | Obtener una |
| PUT | `/api/requests/{id}` | Actualizar |
| PATCH | `/api/requests/{id}/status?status=CONFIRMADA` | Cambiar estado |
| DELETE | `/api/requests/{id}` | Eliminar |
| GET | `/api/requests/status/PENDIENTE` | Filtrar por estado |

### Si tienes dudas, no dudes en contactarnos :) 
