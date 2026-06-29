package com.universidad.gestion.repository;

import com.universidad.gestion.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    boolean existsByEmail(String email);
    Optional<Estudiante> findByEmail(String email);
}
