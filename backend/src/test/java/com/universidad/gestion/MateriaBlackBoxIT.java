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

class MateriaBlackBoxIT extends BaseIntegracionPruebasCajaNegra {

    @Test
    @DisplayName("Lista inicial de materias")
    void debeListarLaSemillaInicialDeMaterias() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/materias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[0].codigo", is("MAT-001")));
    }

    @ParameterizedTest(name = "Codigo invalido: {0}")
    @CsvSource({
            "MAT-7,El codigo debe tener el formato MAT-XXX donde XXX son 3 digitos (ej: MAT-001)",
            "MAT-12,El codigo debe tener el formato MAT-XXX donde XXX son 3 digitos (ej: MAT-001)",
            "ABC-001,El codigo debe tener el formato MAT-XXX donde XXX son 3 digitos (ej: MAT-001)"
    })
    void debeRechazarCodigoInvalido(String codigo, String mensaje) throws Exception {
        Map<String, Object> body = Map.of(
                "codigo", codigo,
                "nombre", "Materia Valida",
                "creditos", 4,
                "cupoMaximo", 20
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/materias")
                        .contentType(jsonType())
                        .content(json(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages.codigo", is(mensaje)));
    }

    @Test
    @DisplayName("Codigo duplicado")
    void debeRechazarCodigoDuplicado() throws Exception {
        Map<String, Object> body = Map.of(
                "codigo", "MAT-001",
                "nombre", "Materia Duplicada",
                "creditos", 4,
                "cupoMaximo", 20
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/materias")
                        .contentType(jsonType())
                        .content(json(body)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message", is("El codigo 'MAT-001' ya esta registrado")));
    }

    @ParameterizedTest(name = "Creditos invalido: {0}")
    @CsvSource({
            "0,Los creditos deben ser al menos 1",
            "7,Los creditos no pueden ser mayores a 6",
            "-1,Los creditos deben ser al menos 1"
    })
    void debeRechazarCreditosInvalidos(Integer creditos, String mensaje) throws Exception {
        Map<String, Object> body = Map.of(
                "codigo", "MAT-777",
                "nombre", "Materia Creditos",
                "creditos", creditos,
                "cupoMaximo", 20
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/materias")
                        .contentType(jsonType())
                        .content(json(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages.creditos", is(mensaje)));
    }

    @ParameterizedTest(name = "Cupo invalido: {0}")
    @CsvSource({
            "4,El cupo maximo debe ser al menos 5",
            "51,El cupo maximo no puede ser mayor a 50",
            "-1,El cupo maximo debe ser al menos 5"
    })
    void debeRechazarCupoInvalido(Integer cupo, String mensaje) throws Exception {
        Map<String, Object> body = Map.of(
                "codigo", "MAT-778",
                "nombre", "Materia Cupo",
                "creditos", 4,
                "cupoMaximo", cupo
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/materias")
                        .contentType(jsonType())
                        .content(json(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages.cupoMaximo", is(mensaje)));
    }

    @Test
    @DisplayName("Materia valida")
    void debePersistirMateriaValida() throws Exception {
        Map<String, Object> body = Map.of(
                "codigo", "MAT-777",
                "nombre", "Analisis Numerico",
                "creditos", 4,
                "cupoMaximo", 20
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/materias")
                        .contentType(jsonType())
                        .content(json(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo", is("MAT-777")))
                .andExpect(jsonPath("$.creditos", is(4)))
                .andExpect(jsonPath("$.cupoMaximo", is(20)));
    }

    @ParameterizedTest(name = "Nombre invalido: {0}")
    @CsvSource({
            "AB,El nombre debe tener entre 3 y 100 caracteres",
            "A,El nombre debe tener entre 3 y 100 caracteres",
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,El nombre debe tener entre 3 y 100 caracteres"
    })
    void debeRechazarNombreInvalido(String nombre, String mensaje) throws Exception {
        Map<String, Object> body = Map.of(
                "codigo", "MAT-779",
                "nombre", nombre,
                "creditos", 4,
                "cupoMaximo", 20
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/materias")
                        .contentType(jsonType())
                        .content(json(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages.nombre", is(mensaje)));
    }
}