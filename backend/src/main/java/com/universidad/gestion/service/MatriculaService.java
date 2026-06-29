package com.universidad.gestion.service;

import com.universidad.gestion.exception.BusinessRuleException;
import com.universidad.gestion.exception.ResourceNotFoundException;
import com.universidad.gestion.model.*;
import com.universidad.gestion.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final EstudianteRepository estudianteRepository;
    private final MateriaRepository materiaRepository;

    public MatriculaService(MatriculaRepository matriculaRepository,
                            EstudianteRepository estudianteRepository,
                            MateriaRepository materiaRepository) {
        this.matriculaRepository = matriculaRepository;
        this.estudianteRepository = estudianteRepository;
        this.materiaRepository = materiaRepository;
    }

    public List<Matricula> listarTodas() {
        return matriculaRepository.findAll();
    }

    public Matricula buscarPorId(Long id) {
        return matriculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matricula con ID " + id + " no encontrada"));
    }

    public List<Matricula> listarPorEstudiante(Long estudianteId) {
        Estudiante e = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));
        return matriculaRepository.findByEstudiante(e);
    }

    public List<Matricula> listarPorMateria(Long materiaId) {
        Materia m = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada"));
        return matriculaRepository.findByMateria(m);
    }

    /**
     * REGLA DE NEGOCIO — TABLA DE DECISION:
     * Un estudiante puede matricularse en una materia si y solo si:
     *   C1: El estudiante EXISTE
     *   C2: La materia EXISTE
     *   C3: El estudiante esta ACTIVO
     *   C4: La materia tiene CUPO DISPONIBLE
     *   C5: El estudiante NO esta ya matriculado en esa materia
     */
    public Matricula matricular(Long estudianteId, Long materiaId) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante con ID " + estudianteId + " no encontrado"));

        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new ResourceNotFoundException("Materia con ID " + materiaId + " no encontrada"));

        // C3: Estudiante debe estar ACTIVO
        if (estudiante.getEstado() != EstadoEstudiante.ACTIVO) {
            throw new BusinessRuleException(
                    "No se puede matricular: el estudiante esta en estado " + estudiante.getEstado()
                            + ". Solo estudiantes ACTIVOS pueden matricularse.");
        }

        // C4: Verificar cupo disponible
        long matriculados = matriculaRepository.countByMateriaAndEstado(materia, EstadoMatricula.MATRICULADO);
        if (matriculados >= materia.getCupoMaximo()) {
            throw new BusinessRuleException(
                    "No se puede matricular: la materia '" + materia.getNombre()
                            + "' alcanzo su cupo maximo de " + materia.getCupoMaximo() + " estudiantes.");
        }

        // C5: No estar ya matriculado en la misma materia
        if (matriculaRepository.existsByEstudianteAndMateria(estudiante, materia)) {
            throw new BusinessRuleException(
                    "No se puede matricular: el estudiante ya esta matriculado en '" + materia.getNombre() + "'.");
        }

        Matricula matricula = new Matricula(estudiante, materia);
        return matriculaRepository.save(matricula);
    }

    /**
     * REGLA DE NEGOCIO — TRANSICION DE ESTADOS:
     *   MATRICULADO + nota >= 6.0 -> APROBADO
     *   MATRICULADO + nota < 6.0  -> REPROBADO
     *   Si ya esta APROBADO o REPROBADO, no se puede volver a calificar.
     */
    public Matricula calificar(Long matriculaId, BigDecimal nota) {
        Matricula matricula = buscarPorId(matriculaId);

        // Validar transicion de estado
        if (matricula.getEstado() != EstadoMatricula.MATRICULADO) {
            throw new BusinessRuleException(
                    "No se puede calificar: la matricula ya esta en estado " + matricula.getEstado()
                            + ". Solo matriculas en estado MATRICULADO pueden ser calificadas.");
        }

        // Normalizar nota a 2 decimales
        BigDecimal notaNormalizada = nota.setScale(2, RoundingMode.HALF_UP);

        // Transicion de estado basada en la nota
        if (notaNormalizada.compareTo(new BigDecimal("6.00")) >= 0) {
            matricula.setEstado(EstadoMatricula.APROBADO);
        } else {
            matricula.setEstado(EstadoMatricula.REPROBADO);
        }

        matricula.setNota(notaNormalizada);
        return matriculaRepository.save(matricula);
    }

    public void eliminar(Long id) {
        Matricula matricula = buscarPorId(id);
        matriculaRepository.delete(matricula);
    }
}
