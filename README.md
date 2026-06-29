# Sistema de Gestión Universitaria

**Asignatura:** Validación y Verificación de Software — EPN  
**Técnicas:** Partición de Equivalencia, Análisis de Límites, Tablas de Decisión, Transición de Estados, Casos de Uso

Sistema web para administrar estudiantes, materias y matrículas universitarias. Desarrollado como material de práctica para pruebas de caja negra.

- **Backend:** Java 17 + Spring Boot 3.3 + H2 en memoria
- **Frontend:** HTML5 + CSS3 + Vanilla JavaScript
- **Proxy:** Nginx (evita problemas de CORS, sirve frontend y backend desde el mismo origen)

---

## Requisitos Previos

### Opción A: Con Docker (recomendado)

- [Docker](https://docs.docker.com/get-docker/) y Docker Compose instalados.

### Opción B: Sin Docker (desarrollo)

- **Java 17+** instalado ([Temurin](https://adoptium.net/) recomendado)
- **Maven 3.8+** instalado ([Apache Maven](https://maven.apache.org/download.cgi))
- **Python 3** (solo para servir el frontend localmente)

---

## Ejecución con Docker

### 1. Construir e iniciar

```bash
docker compose up --build
```

El primer build del backend tarda unos minutos (descarga de dependencias Maven). Los builds posteriores usan caché.

### 2. Acceder

| Recurso | URL |
|----------|-----|
| **Aplicación** | http://localhost |
| **API (directa)** | http://localhost/api/estudiantes |
| **Consola H2** | http://localhost/h2-console/ |

> **Datos de conexión H2:**  
> JDBC URL: `jdbc:h2:mem:universidad_db`  
> User: `sa` / Password: (vacío)

### 3. Detener

```bash
docker compose down
```

---

## Ejecución sin Docker (Desarrollo)

### 1. Backend

```bash
cd backend
mvn spring-boot:run
```

El backend arranca en `http://localhost:8080`.

### 2. Frontend

Abrir `frontend/index.html` directamente en el navegador, o servir con Python:

```bash
cd frontend
python3 -m http.server 3000
```

Abrir `http://localhost:3000`.

> **Nota sobre CORS:** En modo desarrollo el backend incluye `CorsConfig.java` que permite peticiones desde cualquier origen (`*`). Esto es solo para desarrollo local; en producción el proxy Nginx resuelve CORS al servir todo desde el mismo origen.

---

## Estructura del Proyecto

```
black-box-tests/
├── docker-compose.yml          # Orquestación Docker
├── nginx/
│   └── nginx.conf              # Reverse proxy + static files
├── backend/
│   ├── Dockerfile              # Multi-stage build (Maven → JRE)
│   ├── pom.xml
│   └── src/main/java/com/universidad/gestion/
│       ├── model/              # Entidades JPA (Estudiante, Materia, Matricula)
│       ├── dto/                # Objetos de transferencia (validación de formularios)
│       ├── repository/         # Interfaces JPA
│       ├── service/            # Lógica de negocio (reglas que probar)
│       ├── controller/         # API REST
│       ├── exception/          # Manejo de errores
│       └── config/             # CORS, datos semilla
├── frontend/
│   ├── index.html              # Página de inicio
│   ├── css/styles.css          # Estilos
│   ├── js/
│   │   ├── api.js              # Helper fetch() para consumir API
│   │   ├── estudiantes.js      # CRUD estudiantes
│   │   ├── materias.js         # CRUD materias
│   │   └── matriculas.js       # Matricular + calificar
│   └── pages/
│       ├── estudiantes.html    # Formulario estudiantes
│       ├── materias.html       # Formulario materias
│       └── matriculas.html     # Formularios matrícula y notas
└── docs/
    ├── requerimientos-funcionales.md   # Especificación del sistema
    └── guia-docente.md                 # Soluciones (solo docente)
```

---

## API REST

### Estudiantes — `/api/estudiantes`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/estudiantes` | Listar todos |
| `GET` | `/api/estudiantes/{id}` | Obtener por ID |
| `POST` | `/api/estudiantes` | Crear estudiante |
| `PUT` | `/api/estudiantes/{id}` | Actualizar estudiante |
| `DELETE` | `/api/estudiantes/{id}` | Eliminar estudiante |

### Materias — `/api/materias`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/materias` | Listar todas |
| `GET` | `/api/materias/{id}` | Obtener por ID |
| `POST` | `/api/materias` | Crear materia |
| `PUT` | `/api/materias/{id}` | Actualizar materia |
| `DELETE` | `/api/materias/{id}` | Eliminar materia |

### Matrículas — `/api/matriculas`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/matriculas` | Listar todas |
| `GET` | `/api/matriculas/{id}` | Obtener por ID |
| `GET` | `/api/matriculas/estudiante/{id}` | Filtrar por estudiante |
| `GET` | `/api/matriculas/materia/{id}` | Filtrar por materia |
| `POST` | `/api/matriculas` | Matricular estudiante |
| `PUT` | `/api/matriculas/{id}/calificar` | Registrar nota |
| `DELETE` | `/api/matriculas/{id}` | Eliminar matrícula |

---

## Datos de Prueba

El sistema carga automáticamente al iniciar:

- **5 estudiantes** (3 ACTIVO, 1 INACTIVO, 1 SUSPENDIDO)
- **6 materias** (créditos 3-6, cupo 5-40)
- **9 matrículas** (varias MATRICULADO, 1 APROBADO, 1 REPROBADO)

Los datos se reinician cada vez que se apaga el servidor (H2 en memoria).

---

## Para los Estudiantes

1. Leer `docs/requerimientos-funcionales.pdf` (especificación del sistema).
2. Levantar la aplicación con `docker compose up`.
3. Diseñar casos de prueba aplicando las 4 técnicas de caja negra.
4. Ejecutar las pruebas en el sistema.
5. Entregar informe según las consignas de la sección 7 del documento de requisitos.

**No se requiere instalar Java ni Maven — Docker se encarga de todo.**
