package com.universidad.gestion.dto;

import jakarta.validation.constraints.*;

public class NotaDTO {

    @NotNull(message = "La nota es obligatoria")
    @DecimalMin(value = "0.0", message = "La nota no puede ser menor a 0.0")
    @DecimalMax(value = "10.0", message = "La nota no puede ser mayor a 10.0")
    private String nota;

    public String getNota() { return nota; }
    public void setNota(String nota) { this.nota = nota; }
}
