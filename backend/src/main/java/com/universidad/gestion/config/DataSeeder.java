package com.universidad.gestion.config;

import com.universidad.gestion.model.*;
import com.universidad.gestion.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(EstudianteRepository estudianteRepo,
                               MateriaRepository materiaRepo,
                               MatriculaRepository matriculaRepo) {
        return args -> {
            // --- Estudiantes ---
            Estudiante e1 = estudianteRepo.save(new Estudiante("Juan", "Perez", "juan.perez@email.com",
                    LocalDate.of(2000, 5, 15)));
            Estudiante e2 = estudianteRepo.save(new Estudiante("Maria", "Garcia", "maria.garcia@email.com",
                    LocalDate.of(1999, 8, 22)));
            Estudiante e3 = estudianteRepo.save(new Estudiante("Carlos", "Lopez", "carlos.lopez@email.com",
                    LocalDate.of(2001, 2, 10)));
            e3.setEstado(EstadoEstudiante.INACTIVO);
            estudianteRepo.save(e3);
            Estudiante e4 = estudianteRepo.save(new Estudiante("Ana", "Martinez", "ana.martinez@email.com",
                    LocalDate.of(2003, 11, 30)));
            Estudiante e5 = estudianteRepo.save(new Estudiante("Pedro", "Sanchez", "pedro.sanchez@email.com",
                    LocalDate.of(1998, 4, 5)));
            e5.setEstado(EstadoEstudiante.SUSPENDIDO);
            estudianteRepo.save(e5);

            // --- Materias ---
            Materia m1 = materiaRepo.save(new Materia("MAT-001", "Calculo Diferencial", 6, 30));
            Materia m2 = materiaRepo.save(new Materia("MAT-002", "Algebra Lineal", 4, 25));
            Materia m3 = materiaRepo.save(new Materia("MAT-003", "Programacion I", 5, 20));
            Materia m4 = materiaRepo.save(new Materia("MAT-004", "Fisica General", 6, 35));
            Materia m5 = materiaRepo.save(new Materia("MAT-005", "Estadistica Basica", 3, 40));
            Materia m6 = materiaRepo.save(new Materia("MAT-006", "Base de Datos", 4, 5)); // cupo casi lleno

            // --- Matriculas ---
            // Juan: matriculado en 3 materias
            matriculaRepo.save(new Matricula(e1, m1));
            Matricula matE1M2 = new Matricula(e1, m2);
            matE1M2.setNota(new BigDecimal("8.50"));
            matE1M2.setEstado(EstadoMatricula.APROBADO);
            matriculaRepo.save(matE1M2);
            matriculaRepo.save(new Matricula(e1, m3));

            // Maria: matriculada en 2 materias
            matriculaRepo.save(new Matricula(e2, m1));
            Matricula matE2M3 = new Matricula(e2, m3);
            matE2M3.setNota(new BigDecimal("4.00"));
            matE2M3.setEstado(EstadoMatricula.REPROBADO);
            matriculaRepo.save(matE2M3);

            // Ana: matriculada en 1 materia
            matriculaRepo.save(new Matricula(e4, m4));

            // Llenar cupo de MAT-006 para pruebas de cupo
            matriculaRepo.save(new Matricula(e1, m6));
            matriculaRepo.save(new Matricula(e2, m6));
            matriculaRepo.save(new Matricula(e4, m6));
            // Ya hay 3 de 5, el cupo restante es 2
        };
    }
}
