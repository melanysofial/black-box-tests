package com.universidad.gestion;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MatriculaBlackBoxIT extends BaseIntegracionPruebasCajaNegra {

    @Test
    @DisplayName("Lista inicial de matriculas")
    void debeListarLaSemillaInicialDeMatriculas() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/matriculas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(9)));
    }

    @Test
    @DisplayName("Matricula valida")
    void debeCrearMatriculaValida() throws Exception {
        Map<String, Object> body = Map.of(
                "estudianteId", 4,
                "materiaId", 5
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/matriculas")
                        .contentType(jsonType())
                        .content(json(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado", is("MATRICULADO")))
                .andExpect(jsonPath("$.estudiante.id", is(4)))
                .andExpect(jsonPath("$.materia.id", is(5)));
    }

    @Test
    @DisplayName("Estudiante inexistente")
    void debeRechazarEstudianteInexistente() throws Exception {
        Map<String, Object> body = Map.of(
                "estudianteId", 999,
                "materiaId", 2
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/matriculas")
                        .contentType(jsonType())
                        .content(json(body)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Estudiante con ID 999 no encontrado")));
    }

    @Test
    @DisplayName("Materia inexistente")
    void debeRechazarMateriaInexistente() throws Exception {
        Map<String, Object> body = Map.of(
                "estudianteId", 1,
                "materiaId", 999
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/matriculas")
                        .contentType(jsonType())
                        .content(json(body)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Materia con ID 999 no encontrada")));
    }

    @Test
    @DisplayName("Estudiante inactivo")
    void debeRechazarEstudianteInactivo() throws Exception {
        Map<String, Object> body = Map.of(
                "estudianteId", 3,
                "materiaId", 2
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/matriculas")
                        .contentType(jsonType())
                        .content(json(body)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message", containsString("estado INACTIVO")));
    }

    @Test
    @DisplayName("Duplicado")
    void debeRechazarMatriculaDuplicada() throws Exception {
        Map<String, Object> body = Map.of(
                "estudianteId", 1,
                "materiaId", 1
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/matriculas")
                        .contentType(jsonType())
                        .content(json(body)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message", containsString("ya esta matriculado")));
    }

    @Test
    @DisplayName("Sin cupo")
    void debeRechazarSinCupoDisponible() throws Exception {
        Long estudianteUnoId = crearEstudianteTemporal("Estrella", "Uno", "estrella.uno@email.com");
        Long estudianteDosId = crearEstudianteTemporal("Estrella", "Dos", "estrella.dos@email.com");
        Long estudianteTresId = crearEstudianteTemporal("Estrella", "Tres", "estrella.tres@email.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/matriculas")
                        .contentType(jsonType())
                        .content(json(Map.of("estudianteId", estudianteUnoId, "materiaId", 6))))
                .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/matriculas")
                        .contentType(jsonType())
                        .content(json(Map.of("estudianteId", estudianteDosId, "materiaId", 6))))
                .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/matriculas")
                        .contentType(jsonType())
                        .content(json(Map.of("estudianteId", estudianteTresId, "materiaId", 6))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message", containsString("alcanzo su cupo maximo")));
    }

    private Long crearEstudianteTemporal(String nombre, String apellido, String email) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/estudiantes")
                        .contentType(jsonType())
                        .content(json(Map.of(
                                "nombre", nombre,
                                "apellido", apellido,
                                "email", email,
                                "fechaNacimiento", "2000-01-01"
                        ))))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("id").asLong();
    }

    @Test
    @DisplayName("Nota aprobatoria")
    void debeAprobarConNotaAlta() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/matriculas/1/calificar")
                        .contentType(jsonType())
                        .content(json(Map.of("nota", "6.00"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("APROBADO")))
                .andExpect(jsonPath("$.nota", is(6.00)));
    }

    @Test
    @DisplayName("Nota reprobatoria")
    void debeReprobarConNotaBaja() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/matriculas/3/calificar")
                        .contentType(jsonType())
                        .content(json(Map.of("nota", "5.99"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("REPROBADO")))
                .andExpect(jsonPath("$.nota", is(5.99)));
    }

    @Test
    @DisplayName("Ya aprobada")
    void debeRechazarCalificacionDeAprobada() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/matriculas/2/calificar")
                        .contentType(jsonType())
                        .content(json(Map.of("nota", "7.00"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message", containsString("Solo matriculas en estado MATRICULADO pueden ser calificadas")));
    }

    @Test
    @DisplayName("Ya reprobada")
    void debeRechazarCalificacionDeReprobada() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/matriculas/5/calificar")
                        .contentType(jsonType())
                        .content(json(Map.of("nota", "7.00"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message", containsString("Solo matriculas en estado MATRICULADO pueden ser calificadas")));
    }

    @Test
    @DisplayName("Redondeo de nota")
    void debeRedondearNotaA2Decimales() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/matriculas/4/calificar")
                        .contentType(jsonType())
                        .content(json(Map.of("nota", "9.875"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("APROBADO")))
                .andExpect(jsonPath("$.nota", is(9.88)));
    }
}