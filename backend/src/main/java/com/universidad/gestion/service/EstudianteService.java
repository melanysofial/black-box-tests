package com.universidad.gestion.service;

import com.universidad.gestion.dto.EstudianteDTO;
import com.universidad.gestion.exception.BusinessRuleException;
import com.universidad.gestion.exception.ResourceNotFoundException;
import com.universidad.gestion.model.EstadoEstudiante;
import com.universidad.gestion.model.Estudiante;
import com.universidad.gestion.repository.EstudianteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;

    public EstudianteService(EstudianteRepository estudianteRepository) {
        this.estudianteRepository = estudianteRepository;
    }

    public List<Estudiante> listarTodos() {
        return estudianteRepository.findAll();
    }

    public Estudiante buscarPorId(Long id) {
        return estudianteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante con ID " + id + " no encontrado"));
    }

    public Estudiante crear(EstudianteDTO dto) {
        // Regla: email unico
        if (estudianteRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessRuleException("El email '" + dto.getEmail() + "' ya esta registrado");
        }

        LocalDate fechaNac = LocalDate.parse(dto.getFechaNacimiento());

        // Regla: estudiante debe tener al menos 17 anios
        int edad = Period.between(fechaNac, LocalDate.now()).getYears();
        if (edad < 17) {
            throw new BusinessRuleException("El estudiante debe tener al menos 17 anios. Edad calculada: " + edad);
        }

        Estudiante estudiante = new Estudiante();
        estudiante.setNombre(dto.getNombre().trim());
        estudiante.setApellido(dto.getApellido().trim());
        estudiante.setEmail(dto.getEmail().trim().toLowerCase());
        estudiante.setFechaNacimiento(fechaNac);
        estudiante.setEstado(EstadoEstudiante.ACTIVO);

        return estudianteRepository.save(estudiante);
    }

    public Estudiante actualizar(Long id, EstudianteDTO dto) {
        Estudiante existente = buscarPorId(id);

        // Regla: email unico (si cambia)
        if (!existente.getEmail().equalsIgnoreCase(dto.getEmail())
                && estudianteRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessRuleException("El email '" + dto.getEmail() + "' ya esta registrado por otro estudiante");
        }

        LocalDate fechaNac = LocalDate.parse(dto.getFechaNacimiento());
        int edad = Period.between(fechaNac, LocalDate.now()).getYears();
        if (edad < 17) {
            throw new BusinessRuleException("El estudiante debe tener al menos 17 anios. Edad calculada: " + edad);
        }

        existente.setNombre(dto.getNombre().trim());
        existente.setApellido(dto.getApellido().trim());
        existente.setEmail(dto.getEmail().trim().toLowerCase());
        existente.setFechaNacimiento(fechaNac);

        // Actualizar estado si se proporciona
        if (dto.getEstado() != null && !dto.getEstado().isBlank()) {
            try {
                existente.setEstado(EstadoEstudiante.valueOf(dto.getEstado().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BusinessRuleException("Estado invalido. Valores permitidos: ACTIVO, INACTIVO, GRADUADO, SUSPENDIDO");
            }
        }

        return estudianteRepository.save(existente);
    }

    public void eliminar(Long id) {
        Estudiante estudiante = buscarPorId(id);
        estudianteRepository.delete(estudiante);
    }
}
