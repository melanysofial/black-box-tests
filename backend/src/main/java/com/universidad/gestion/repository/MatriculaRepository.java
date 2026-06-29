package com.universidad.gestion.repository;

import com.universidad.gestion.model.EstadoMatricula;
import com.universidad.gestion.model.Estudiante;
import com.universidad.gestion.model.Materia;
import com.universidad.gestion.model.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
    List<Matricula> findByEstudiante(Estudiante estudiante);
    List<Matricula> findByMateria(Materia materia);
    Optional<Matricula> findByEstudianteAndMateria(Estudiante estudiante, Materia materia);
    boolean existsByEstudianteAndMateria(Estudiante estudiante, Materia materia);
    long countByMateriaAndEstado(Materia materia, EstadoMatricula estado);
}
