package com.universidad.gestion.service;

import com.universidad.gestion.dto.MateriaDTO;
import com.universidad.gestion.exception.BusinessRuleException;
import com.universidad.gestion.exception.ResourceNotFoundException;
import com.universidad.gestion.model.Materia;
import com.universidad.gestion.repository.MateriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MateriaService {

    private final MateriaRepository materiaRepository;

    public MateriaService(MateriaRepository materiaRepository) {
        this.materiaRepository = materiaRepository;
    }

    public List<Materia> listarTodas() {
        return materiaRepository.findAll();
    }

    public Materia buscarPorId(Long id) {
        return materiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Materia con ID " + id + " no encontrada"));
    }

    public Materia crear(MateriaDTO dto) {
        // Regla: codigo unico
        if (materiaRepository.existsByCodigo(dto.getCodigo())) {
            throw new BusinessRuleException("El codigo '" + dto.getCodigo() + "' ya esta registrado");
        }

        Materia materia = new Materia();
        materia.setCodigo(dto.getCodigo().trim().toUpperCase());
        materia.setNombre(dto.getNombre().trim());
        materia.setCreditos(dto.getCreditos());
        materia.setCupoMaximo(dto.getCupoMaximo());

        return materiaRepository.save(materia);
    }

    public Materia actualizar(Long id, MateriaDTO dto) {
        Materia existente = buscarPorId(id);

        // Regla: codigo unico (si cambia)
        if (!existente.getCodigo().equalsIgnoreCase(dto.getCodigo())
                && materiaRepository.existsByCodigo(dto.getCodigo())) {
            throw new BusinessRuleException("El codigo '" + dto.getCodigo() + "' ya esta registrado por otra materia");
        }

        existente.setCodigo(dto.getCodigo().trim().toUpperCase());
        existente.setNombre(dto.getNombre().trim());
        existente.setCreditos(dto.getCreditos());
        existente.setCupoMaximo(dto.getCupoMaximo());

        return materiaRepository.save(existente);
    }

    public void eliminar(Long id) {
        Materia materia = buscarPorId(id);
        materiaRepository.delete(materia);
    }
}
