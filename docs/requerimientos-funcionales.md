# Requisitos Funcionales — Sistema de Gestión Universitaria

**Asignatura:** Validación y Verificación de Software  
**Técnicas:** Partición de Equivalencia, Análisis de Límites, Tablas de Decisión, Transición de Estados, Casos de Uso  
**Tecnologías:** Java Spring Boot (backend) + HTML/CSS/Vanilla JS (frontend)

---

## 1. Descripción General del Sistema

El Sistema de Gestión Universitaria permite administrar **estudiantes**, **materias** y **matrículas** (inscripción de estudiantes en materias y registro de notas).

El sistema expone una API REST documentada en este documento y cuenta con un frontend web con formularios HTML para cada operación.

### 1.1 Módulos

| Módulo | Operaciones |
|--------|------------|
| **Estudiantes** | Crear, listar, editar, eliminar |
| **Materias** | Crear, listar, editar, eliminar |
| **Matrículas** | Matricular estudiante, calificar, listar, eliminar |

### 1.2 URL de la API

```
http://localhost:8080/api
```

### 1.3 Formato de Errores

Todos los endpoints devuelven errores con el siguiente formato JSON:

```json
{
  "timestamp": "2026-06-29T10:30:00",
  "status": 400,
  "error": "Error de Validacion",
  "messages": {
    "nombre": "El nombre debe tener entre 2 y 50 caracteres",
    "email": "El email debe tener un formato valido (ej: nombre@dominio.com)"
  }
}
```

Códigos HTTP usados:
- `200` — Éxito
- `201` — Recurso creado
- `400` — Error de validación de campos
- `404` — Recurso no encontrado
- `422` — Regla de negocio violada
- `500` — Error interno

---

## 2. Módulo de Estudiantes

**Endpoint base:** `/api/estudiantes`

### 2.1 Documento de Entrada (Formulario)

| Campo | Tipo | Obligatorio | Validaciones |
|-------|------|:-----------:|-------------|
| `nombre` | string | Sí | Solo letras y espacios (A-Z, a-z, À-ÿ, espacio). Longitud: 2–50 caracteres. |
| `apellido` | string | Sí | Solo letras y espacios. Longitud: 2–50 caracteres. |
| `email` | string | Sí | Formato email válido (`usuario@dominio.com`). Único en el sistema. |
| `fechaNacimiento` | string (YYYY-MM-DD) | Sí | Fecha en pasado. Edad calculada ≥ 17 años. |
| `estado` | string | No (solo edición) | Valores: `ACTIVO`, `INACTIVO`, `GRADUADO`, `SUSPENDIDO`. |

### 2.2 Reglas de Negocio

- **RN-E01:** El email debe ser único. No puede haber dos estudiantes con el mismo email.
- **RN-E02:** El estudiante debe tener al menos 17 años al momento del registro.
- **RN-E03:** El nombre y apellido solo pueden contener letras (incluyendo acentos) y espacios. No se permiten números ni caracteres especiales.
- **RN-E04:** El estado por defecto al crear un estudiante es `ACTIVO`. Solo se puede cambiar mediante edición.

### 2.3 Operaciones REST

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/estudiantes` | Listar todos los estudiantes |
| `GET` | `/api/estudiantes/{id}` | Obtener un estudiante por ID |
| `POST` | `/api/estudiantes` | Crear un nuevo estudiante |
| `PUT` | `/api/estudiantes/{id}` | Actualizar un estudiante existente |
| `DELETE` | `/api/estudiantes/{id}` | Eliminar un estudiante |

---

## 3. Módulo de Materias

**Endpoint base:** `/api/materias`

### 3.1 Documento de Entrada (Formulario)

| Campo | Tipo | Obligatorio | Validaciones |
|-------|------|:-----------:|-------------|
| `codigo` | string | Sí | Formato `MAT-XXX` donde X es dígito (0-9). Ej: `MAT-001`. Único. |
| `nombre` | string | Sí | Longitud: 3–100 caracteres. |
| `creditos` | integer | Sí | Rango: 1–6 inclusive. |
| `cupoMaximo` | integer | Sí | Rango: 5–50 inclusive. |

### 3.2 Reglas de Negocio

- **RN-M01:** El código debe seguir el formato exacto `MAT-XXX` (MAT- seguido de exactamente 3 dígitos).
- **RN-M02:** El código es único. No puede haber dos materias con el mismo código.
- **RN-M03:** Los créditos deben estar en el rango 1–6.
- **RN-M04:** El cupo máximo debe estar en el rango 5–50.

### 3.3 Operaciones REST

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/materias` | Listar todas las materias |
| `GET` | `/api/materias/{id}` | Obtener una materia por ID |
| `POST` | `/api/materias` | Crear una nueva materia |
| `PUT` | `/api/materias/{id}` | Actualizar una materia existente |
| `DELETE` | `/api/materias/{id}` | Eliminar una materia |

---

## 4. Módulo de Matrículas y Notas

**Endpoint base:** `/api/matriculas`

### 4.1 Documento de Entrada: Matricular

| Campo | Tipo | Obligatorio | Validaciones |
|-------|------|:-----------:|-------------|
| `estudianteId` | integer | Sí | Debe corresponder a un estudiante existente. |
| `materiaId` | integer | Sí | Debe corresponder a una materia existente. |

### 4.2 Documento de Entrada: Calificar

| Campo | Tipo | Obligatorio | Validaciones |
|-------|------|:-----------:|-------------|
| `nota` | decimal | Sí | Rango: 0.00 – 10.00. Máximo 2 decimales. |

### 4.3 Reglas de Negocio — Matricular

**RN-MAT01 — Tabla de Decisión.** Un estudiante puede matricularse en una materia si y solo si se cumplen **todas** las siguientes condiciones:

| ID | Condición | Descripción |
|----|-----------|-------------|
| **C1** | Estudiante existe | El `estudianteId` pertenece a un estudiante registrado |
| **C2** | Materia existe | El `materiaId` pertenece a una materia registrada |
| **C3** | Estudiante ACTIVO | El estudiante tiene estado `ACTIVO` |
| **C4** | Materia tiene cupo | La cantidad de estudiantes en estado `MATRICULADO` es menor al `cupoMaximo` de la materia |
| **C5** | No duplicado | El estudiante no tiene una matrícula existente en esa materia |

**Si cualquiera de las condiciones falla, la matrícula es RECHAZADA.**

### 4.4 Reglas de Negocio — Calificar (Transición de Estados)

**RN-MAT02 — Máquina de Estados.** Una matrícula tiene 3 estados:

```
                  ┌──────────────┐
                  │              │
       matricular │ MATRICULADO  │
       ──────────▶│   (inicial)  │
                  │              │
                  └──────┬───────┘
                         │
                    calificar
                         │
              ┌──────────┴──────────┐
              │                     │
          nota ≥ 6.0           nota < 6.0
              │                     │
              ▼                     ▼
        ┌──────────┐         ┌──────────┐
        │ APROBADO │         │ REPROBADO│
        │ (final)  │         │ (final)  │
        └──────────┘         └──────────┘
```

- Una matrícula en estado `APROBADO` o `REPROBADO` **no puede volver a ser calificada**.
- La nota de corte es **6.00**. Nota ≥ 6.00 → APROBADO. Nota < 6.00 → REPROBADO.

### 4.5 Operaciones REST

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/matriculas` | Listar todas las matrículas |
| `GET` | `/api/matriculas/{id}` | Obtener matrícula por ID |
| `GET` | `/api/matriculas/estudiante/{id}` | Listar matrículas de un estudiante |
| `GET` | `/api/matriculas/materia/{id}` | Listar matrículas de una materia |
| `POST` | `/api/matriculas` | Matricular estudiante en materia |
| `PUT` | `/api/matriculas/{id}/calificar` | Registrar nota de una matrícula |
| `DELETE` | `/api/matriculas/{id}` | Eliminar una matrícula |

## 5. Datos de Prueba Precargados (Seed Data)

El sistema inicia con los siguientes datos para facilitar las pruebas:

### 5.1 Estudiantes

| ID | Nombre | Apellido | Email | F. Nacimiento | Estado |
|----|--------|----------|-------|--------------|--------|
| 1 | Juan | Perez | juan.perez@email.com | 2000-05-15 | ACTIVO |
| 2 | Maria | Garcia | maria.garcia@email.com | 1999-08-22 | ACTIVO |
| 3 | Carlos | Lopez | carlos.lopez@email.com | 2001-02-10 | INACTIVO |
| 4 | Ana | Martinez | ana.martinez@email.com | 2003-11-30 | ACTIVO |
| 5 | Pedro | Sanchez | pedro.sanchez@email.com | 1998-04-05 | SUSPENDIDO |

### 5.2 Materias

| ID | Código | Nombre | Créditos | Cupo Máx. |
|----|--------|--------|----------|-----------|
| 1 | MAT-001 | Calculo Diferencial | 6 | 30 |
| 2 | MAT-002 | Algebra Lineal | 4 | 25 |
| 3 | MAT-003 | Programacion I | 5 | 20 |
| 4 | MAT-004 | Fisica General | 6 | 35 |
| 5 | MAT-005 | Estadistica Basica | 3 | 40 |
| 6 | MAT-006 | Base de Datos | 4 | 5 |

### 5.3 Matrículas

| ID | Estudiante | Materia | Nota | Estado |
|----|-----------|---------|------|--------|
| 1 | Juan Perez | MAT-001 | — | MATRICULADO |
| 2 | Juan Perez | MAT-002 | 8.50 | APROBADO |
| 3 | Juan Perez | MAT-003 | — | MATRICULADO |
| 4 | Maria Garcia | MAT-001 | — | MATRICULADO |
| 5 | Maria Garcia | MAT-003 | 4.00 | REPROBADO |
| 6 | Ana Martinez | MAT-004 | — | MATRICULADO |
| 7 | Juan Perez | MAT-006 | — | MATRICULADO |
| 8 | Maria Garcia | MAT-006 | — | MATRICULADO |
| 9 | Ana Martinez | MAT-006 | — | MATRICULADO |

## 6. Instrucciones para Ejecutar el Proyecto

### 6.1 Backend

```bash
cd backend
./mvnw spring-boot:run
# O si tienes Maven instalado:
# mvn spring-boot:run
```

El servidor arranca en `http://localhost:8080`.

- Consola H2: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:universidad_db`, user: `sa`, password: vacío)

### 6.2 Frontend

Abrir `frontend/index.html` directamente en el navegador, o servir con cualquier servidor HTTP simple:

```bash
cd frontend
python3 -m http.server 3000
```

Luego abrir `http://localhost:3000`.

---

## 7. Consignas para los Estudiantes

Para cada módulo, se debe entregar:

1. **Partición de Equivalencia y Análisis de Límites:**
   - Identificar las clases de equivalencia (válidas e inválidas) para cada campo de los formularios de **Estudiante** y **Materia**.
   - Identificar valores límite para los campos numéricos y de longitud.
   - Diseñar al menos 3 casos de prueba por campo, indicando: dato de entrada, clase de equivalencia, resultado esperado.

2. **Tablas de Decisión:**
   - Construir la tabla de decisión completa para la operación de matricular (32 combinaciones = 2^5 condiciones).
   - Simplificar la tabla identificando combinaciones imposibles o redundantes.
   - Seleccionar al menos 1 caso de prueba por cada regla significativa.

3. **Transición de Estados:**
   - Dibujar el diagrama de estados de una matrícula.
   - Diseñar casos de prueba que cubran todas las transiciones válidas y al menos 2 transiciones inválidas.
   - Ejecutar las pruebas usando los datos semilla como base.

4. **Casos de Uso:**
   - Identificar 2 casos de uso del sistema a partir de los requisitos (se sugiere: Matricular estudiante, Registrar nota).
   - Diseñar casos de prueba para: flujo principal, flujos alternativos y flujos de error.
   - Especificar: precondiciones, datos de entrada, resultado esperado, postcondiciones.

5. **Informe:**
   - Documentar todos los casos de prueba diseñados.
   - Ejecutar las pruebas en el sistema (manual o automatizado).
   - Reportar: casos de prueba ejecutados, resultados obtenidos, defectos encontrados.

---

## 8. Criterios de Evaluación

| Técnica | Puntos | Criterios |
|---------|:------:|-----------|
| **Partición de equivalencia** | 25 | Identifica clases válidas e inválidas para todos los campos de los formularios de Estudiante y Materia. Separa correctamente por tipo de entrada. |
| **Análisis de límites** | 20 | Identifica valores frontera para todos los rangos numéricos (créditos, cupo, nota) y de longitud (nombre, apellido, email, código). Incluye tanto límites válidos como inválidos. |
| **Tablas de decisión** | 20 | Construye la tabla de decisión completa para la operación de matricular (32 combinaciones). Simplifica correctamente identificando reglas redundantes e imposibles. Diseña al menos 1 caso de prueba por cada regla significativa. |
| **Transición de estados** | 15 | Dibuja el diagrama de estados de una matrícula con los 3 estados y las transiciones correctas. Diseña casos de prueba que cubren todas las transiciones válidas y al menos 2 inválidas. |
| **Casos de uso** | 20 | Identifica correctamente 2 casos de uso a partir de los requisitos. Diseña casos de prueba para flujo principal, flujos alternativos y flujos de error. Especifica precondiciones, datos de entrada, resultado esperado y postcondiciones. |
| **Total** | **100** | |

### Formato de Entrega

- Documento PDF o Word con todos los casos de prueba.
- Incluir: nombre del estudiante, fecha.
- Para cada caso de prueba especificar: ID, técnica aplicada, dato de entrada, resultado esperado, resultado obtenido, ¿pasó? (Sí/No).
- Si se detectan defectos, documentarlos por separado indicando: descripción, pasos para reproducir, resultado esperado vs obtenido.
