package com.universidad.gestion.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia materia;

    @DecimalMin(value = "0.0", message = "La nota no puede ser menor a 0.0")
    @DecimalMax(value = "10.0", message = "La nota no puede ser mayor a 10.0")
    @Digits(integer = 2, fraction = 2, message = "La nota debe tener maximo 2 enteros y 2 decimales")
    @Column(precision = 4, scale = 2)
    private BigDecimal nota;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private EstadoMatricula estado = EstadoMatricula.MATRICULADO;

    public Matricula() {}

    public Matricula(Estudiante estudiante, Materia materia) {
        this.estudiante = estudiante;
        this.materia = materia;
        this.estado = EstadoMatricula.MATRICULADO;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }

    public Materia getMateria() { return materia; }
    public void setMateria(Materia materia) { this.materia = materia; }

    public BigDecimal getNota() { return nota; }
    public void setNota(BigDecimal nota) { this.nota = nota; }

    public EstadoMatricula getEstado() { return estado; }
    public void setEstado(EstadoMatricula estado) { this.estado = estado; }
}
