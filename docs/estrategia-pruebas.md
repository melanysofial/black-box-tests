# Estrategia de Pruebas Aplicada

## Esquema breve

```mermaid
flowchart TD
    A[Requisitos funcionales] --> B[Identificar campos y reglas]
    B --> C[Particion de equivalencia]
    B --> D[Analisis de limites]
    B --> E[Tabla de decision]
    B --> F[Transicion de estados]
    B --> G[Casos de uso]

    C --> H[Diseñar casos de prueba]
    D --> H
    E --> H
    F --> H
    G --> H

    H --> I[Ejecutar pruebas en backend]
    I --> J[Validar respuestas HTTP y mensajes]
    J --> K[Registrar resultados y defectos]
```

## Resumen corto

- Se partio de los requisitos funcionales del sistema.
- Se separaron las pruebas por modulo: Estudiantes, Materias y Matriculas.
- Se aplicaron las cuatro tecnicas pedidas:
  - Particion de equivalencia.
  - Analisis de limites.
  - Tabla de decision para matricular.
  - Transicion de estados para calificar.
- Se agregaron casos de uso para matricular estudiante y registrar nota.
- Las pruebas se implementaron como integracion contra el backend con H2 y datos semilla.
- La validacion final se hizo con `mvn test`.

## Resultado

- Suite automatizada ejecutada: 39 pruebas.
- Resultado: 0 fallos, 0 errores.
