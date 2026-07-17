package com.universidad.gestion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EstudianteBlackBoxIT extends BaseIntegracionPruebasCajaNegra {

    @Test
    @DisplayName("Lista inicial de estudiantes")
    void debeListarLaSemillaInicialDeEstudiantes() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/estudiantes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].nombre", is("Juan")));
    }

    @ParameterizedTest(name = "Nombre invalido: {0}")
    @CsvSource({
            "A,El nombre debe tener entre 2 y 50 caracteres",
            "Juan3,El nombre solo puede contener letras y espacios",
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,El nombre debe tener entre 2 y 50 caracteres"
    })
    void debeRechazarNombreInvalido(String nombre, String mensaje) throws Exception {
        Map<String, Object> body = Map.of(
                "nombre", nombre,
                "apellido", "Perez",
                "email", "estudiante.nombre." + nombre.length() + "@email.com",
                "fechaNacimiento", "2000-01-01"
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/estudiantes")
                        .contentType(jsonType())
                        .content(json(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages.nombre", is(mensaje)));
    }

    @ParameterizedTest(name = "Apellido invalido: {0}")
    @CsvSource({
            "B,El apellido debe tener entre 2 y 50 caracteres",
            "Perez$,El apellido solo puede contener letras y espacios",
            "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB,El apellido debe tener entre 2 y 50 caracteres"
    })
    void debeRechazarApellidoInvalido(String apellido, String mensaje) throws Exception {
        Map<String, Object> body = Map.of(
                "nombre", "Juan",
                "apellido", apellido,
                "email", "estudiante.apellido." + apellido.length() + "@email.com",
                "fechaNacimiento", "2000-01-01"
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/estudiantes")
                        .contentType(jsonType())
                        .content(json(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages.apellido", is(mensaje)));
    }

    @Test
    @DisplayName("Email duplicado")
    void debeRechazarEmailDuplicado() throws Exception {
        Map<String, Object> body = Map.of(
                "nombre", "Nuevo",
                "apellido", "Alumno",
                "email", "juan.perez@email.com",
                "fechaNacimiento", "2000-01-01"
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/estudiantes")
                        .contentType(jsonType())
                        .content(json(body)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message", is("El email 'juan.perez@email.com' ya esta registrado")));
    }

    @Test
    @DisplayName("Fecha o edad invalida")
    void debeRechazarFechaOEdadInvalida() throws Exception {
        Map<String, Object> body = Map.of(
                "nombre", "Joven",
                "apellido", "Prueba",
                "email", "joven.prueba@email.com",
                "fechaNacimiento", "2010-01-01"
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/estudiantes")
                        .contentType(jsonType())
                        .content(json(body)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("al menos 17 anios")));
    }

    @Test
    @DisplayName("Estado por defecto")
    void debeAsignarEstadoActivoPorDefecto() throws Exception {
        Map<String, Object> body = Map.of(
                "nombre", "Laura",
                "apellido", "Valencia",
                "email", "laura.valencia@email.com",
                "fechaNacimiento", "2000-02-10"
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/estudiantes")
                        .contentType(jsonType())
                        .content(json(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado", is("ACTIVO")))
                .andExpect(jsonPath("$.email", is("laura.valencia@email.com")));
    }

    @Test
    @DisplayName("Cambio de estado")
    void debePermitirCambioDeEstado() throws Exception {
        Map<String, Object> body = Map.of(
                "nombre", "Juan",
                "apellido", "Perez",
                "email", "juan.perez@email.com",
                "fechaNacimiento", "2000-05-15",
                "estado", "INACTIVO"
        );

        mockMvc.perform(MockMvcRequestBuilders.put("/api/estudiantes/1")
                        .contentType(jsonType())
                        .content(json(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("INACTIVO")));
    }

    @Test
    @DisplayName("Estado invalido")
    void debeRechazarEstadoInvalido() throws Exception {
        Map<String, Object> body = Map.of(
                "nombre", "Juan",
                "apellido", "Perez",
                "email", "juan.perez@email.com",
                "fechaNacimiento", "2000-05-15",
                "estado", "EXPULSADO"
        );

        mockMvc.perform(MockMvcRequestBuilders.put("/api/estudiantes/1")
                        .contentType(jsonType())
                        .content(json(body)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message", is("Estado invalido. Valores permitidos: ACTIVO, INACTIVO, GRADUADO, SUSPENDIDO")));
    }
}