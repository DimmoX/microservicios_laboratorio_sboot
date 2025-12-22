package com.gestion_labs.ms_gestion_labs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.gestion_labs.ms_gestion_labs.dto.AgendaExamenDTO;
import com.gestion_labs.ms_gestion_labs.service.agenda.AgendaService;

/**
 * CONTROLADOR DEPRECADO - NO USAR
 * Usar AgendaController en su lugar que tiene seguridad implementada
 */
//@RestController
//@RequestMapping("/agenda")
public class AgendaExamenController {

    @Autowired
    private AgendaService agendaService;

    @GetMapping
    public ResponseEntity<List<AgendaExamenDTO>> getAllAgendas() {
        return ResponseEntity.ok(agendaService.findAll());
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<AgendaExamenDTO>> getAgendasByPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(agendaService.findByPaciente(pacienteId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendaExamenDTO> getAgendaById(@PathVariable Long id) {
        return ResponseEntity.ok(agendaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AgendaExamenDTO> createAgenda(@RequestBody AgendaExamenDTO dto) {
        return ResponseEntity.ok(agendaService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgendaExamenDTO> updateFechaHora(@PathVariable Long id, @RequestBody AgendaExamenDTO dto) {
        return ResponseEntity.ok(agendaService.updateFechaHora(id, dto.getFechaHora()));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<AgendaExamenDTO> cancelarAgenda(@PathVariable Long id) {
        return ResponseEntity.ok(agendaService.cancelar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAgenda(@PathVariable Long id) {
        agendaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
