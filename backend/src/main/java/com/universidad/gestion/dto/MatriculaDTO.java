package com.universidad.gestion.dto;

import jakarta.validation.constraints.*;

public class MatriculaDTO {

    @NotNull(message = "El ID del estudiante es obligatorio")
    private Long estudianteId;

    @NotNull(message = "El ID de la materia es obligatorio")
    private Long materiaId;

    public Long getEstudianteId() { return estudianteId; }
    public void setEstudianteId(Long estudianteId) { this.estudianteId = estudianteId; }

    public Long getMateriaId() { return materiaId; }
    public void setMateriaId(Long materiaId) { this.materiaId = materiaId; }
}
