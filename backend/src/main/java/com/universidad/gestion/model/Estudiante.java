package com.universidad.gestion.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]+$", message = "El nombre solo puede contener letras y espacios")
    @Column(nullable = false, length = 50)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]+$", message = "El apellido solo puede contener letras y espacios")
    @Column(nullable = false, length = 50)
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato valido (ej: nombre@dominio.com)")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoEstudiante estado = EstadoEstudiante.ACTIVO;

    public Estudiante() {}

    public Estudiante(String nombre, String apellido, String email, LocalDate fechaNacimiento) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.fechaNacimiento = fechaNacimiento;
        this.estado = EstadoEstudiante.ACTIVO;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public EstadoEstudiante getEstado() { return estado; }
    public void setEstado(EstadoEstudiante estado) { this.estado = estado; }
}
