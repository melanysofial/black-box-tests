package com.universidad.gestion.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El codigo es obligatorio")
    @Pattern(regexp = "^MAT-\\d{3}$", message = "El codigo debe tener el formato MAT-XXX (ej: MAT-001)")
    @Column(nullable = false, unique = true, length = 7)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotNull(message = "Los creditos son obligatorios")
    @Min(value = 1, message = "Los creditos deben ser al menos 1")
    @Max(value = 6, message = "Los creditos no pueden ser mayores a 6")
    @Column(nullable = false)
    private Integer creditos;

    @NotNull(message = "El cupo maximo es obligatorio")
    @Min(value = 5, message = "El cupo maximo debe ser al menos 5")
    @Max(value = 50, message = "El cupo maximo no puede ser mayor a 50")
    @Column(name = "cupo_maximo", nullable = false)
    private Integer cupoMaximo;

    public Materia() {}

    public Materia(String codigo, String nombre, Integer creditos, Integer cupoMaximo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.creditos = creditos;
        this.cupoMaximo = cupoMaximo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getCreditos() { return creditos; }
    public void setCreditos(Integer creditos) { this.creditos = creditos; }

    public Integer getCupoMaximo() { return cupoMaximo; }
    public void setCupoMaximo(Integer cupoMaximo) { this.cupoMaximo = cupoMaximo; }
}
