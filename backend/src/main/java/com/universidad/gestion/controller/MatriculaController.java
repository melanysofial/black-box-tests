package com.universidad.gestion.controller;

import com.universidad.gestion.dto.MatriculaDTO;
import com.universidad.gestion.dto.NotaDTO;
import com.universidad.gestion.model.Matricula;
import com.universidad.gestion.service.MatriculaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @GetMapping
    public List<Matricula> listar() {
        return matriculaService.listarTodas();
    }

    @GetMapping("/{id}")
    public Matricula obtener(@PathVariable Long id) {
        return matriculaService.buscarPorId(id);
    }

    @GetMapping("/estudiante/{estudianteId}")
    public List<Matricula> listarPorEstudiante(@PathVariable Long estudianteId) {
        return matriculaService.listarPorEstudiante(estudianteId);
    }

    @GetMapping("/materia/{materiaId}")
    public List<Matricula> listarPorMateria(@PathVariable Long materiaId) {
        return matriculaService.listarPorMateria(materiaId);
    }

    @PostMapping
    public ResponseEntity<Matricula> matricular(@Valid @RequestBody MatriculaDTO dto) {
        Matricula matricula = matriculaService.matricular(dto.getEstudianteId(), dto.getMateriaId());
        return ResponseEntity.status(HttpStatus.CREATED).body(matricula);
    }

    @PutMapping("/{id}/calificar")
    public Matricula calificar(@PathVariable Long id, @Valid @RequestBody NotaDTO dto) {
        BigDecimal nota = new BigDecimal(dto.getNota());
        return matriculaService.calificar(id, nota);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        matriculaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
