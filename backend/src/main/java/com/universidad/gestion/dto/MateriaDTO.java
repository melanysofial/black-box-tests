package com.universidad.gestion.dto;

import jakarta.validation.constraints.*;

public class MateriaDTO {

    @NotBlank(message = "El codigo es obligatorio")
    @Pattern(regexp = "^MAT-\\d{3}$", message = "El codigo debe tener el formato MAT-XXX donde XXX son 3 digitos (ej: MAT-001)")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @NotNull(message = "Los creditos son obligatorios")
    @Min(value = 1, message = "Los creditos deben ser al menos 1")
    @Max(value = 6, message = "Los creditos no pueden ser mayores a 6")
    private Integer creditos;

    @NotNull(message = "El cupo maximo es obligatorio")
    @Min(value = 5, message = "El cupo maximo debe ser al menos 5")
    @Max(value = 50, message = "El cupo maximo no puede ser mayor a 50")
    private Integer cupoMaximo;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getCreditos() { return creditos; }
    public void setCreditos(Integer creditos) { this.creditos = creditos; }

    public Integer getCupoMaximo() { return cupoMaximo; }
    public void setCupoMaximo(Integer cupoMaximo) { this.cupoMaximo = cupoMaximo; }
}
