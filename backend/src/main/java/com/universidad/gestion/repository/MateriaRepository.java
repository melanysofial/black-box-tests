package com.universidad.gestion.repository;

import com.universidad.gestion.model.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MateriaRepository extends JpaRepository<Materia, Long> {
    boolean existsByCodigo(String codigo);
    Optional<Materia> findByCodigo(String codigo);
}
