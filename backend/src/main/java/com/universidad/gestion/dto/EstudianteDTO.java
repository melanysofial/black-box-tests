package com.universidad.gestion.dto;

import jakarta.validation.constraints.*;

public class EstudianteDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]+$", message = "El nombre solo puede contener letras y espacios")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]+$", message = "El apellido solo puede contener letras y espacios")
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato valido (ej: nombre@dominio.com)")
    private String email;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "La fecha debe tener el formato YYYY-MM-DD")
    private String fechaNacimiento;

    private String estado;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
