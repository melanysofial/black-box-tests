package com.universidad.gestion.controller;

import com.universidad.gestion.dto.EstudianteDTO;
import com.universidad.gestion.model.Estudiante;
import com.universidad.gestion.service.EstudianteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteController {

    private final EstudianteService estudianteService;

    public EstudianteController(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;
    }

    @GetMapping
    public List<Estudiante> listar() {
        return estudianteService.listarTodos();
    }

    @GetMapping("/{id}")
    public Estudiante obtener(@PathVariable Long id) {
        return estudianteService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Estudiante> crear(@Valid @RequestBody EstudianteDTO dto) {
        Estudiante creado = estudianteService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public Estudiante actualizar(@PathVariable Long id, @Valid @RequestBody EstudianteDTO dto) {
        return estudianteService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        estudianteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
