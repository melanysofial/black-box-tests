package com.universidad.gestion.controller;

import com.universidad.gestion.dto.MateriaDTO;
import com.universidad.gestion.model.Materia;
import com.universidad.gestion.service.MateriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materias")
public class MateriaController {

    private final MateriaService materiaService;

    public MateriaController(MateriaService materiaService) {
        this.materiaService = materiaService;
    }

    @GetMapping
    public List<Materia> listar() {
        return materiaService.listarTodas();
    }

    @GetMapping("/{id}")
    public Materia obtener(@PathVariable Long id) {
        return materiaService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Materia> crear(@Valid @RequestBody MateriaDTO dto) {
        Materia creada = materiaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    public Materia actualizar(@PathVariable Long id, @Valid @RequestBody MateriaDTO dto) {
        return materiaService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        materiaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
