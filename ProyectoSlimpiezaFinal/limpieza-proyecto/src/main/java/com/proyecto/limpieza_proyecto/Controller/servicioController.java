package com.proyecto.limpieza_proyecto.Controller;
import com.proyecto.limpieza_proyecto.Model.servicio;
import com.proyecto.limpieza_proyecto.Model.estado;
import com.proyecto.limpieza_proyecto.Service.serviceRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
@CrossOrigin(origins = "*") // Permite conexión desde Angular
public class servicioController {

    private final serviceRequest service;

    @Autowired
    public servicioController(serviceRequest service) {
        this.service = service;
    }

    // POST /api/requests → Crear solicitud
    @PostMapping
    public ResponseEntity<servicio> createRequest(@RequestBody servicio request) {
        servicio created = service.createRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // GET /api/requests → Ver todas
    @GetMapping
    public List<servicio> getAllRequests() {
        return service.getAllRequests();
    }

    // GET /api/requests/1 → Ver una
    @GetMapping("/{id}")
    public servicio getRequestById(@PathVariable Long id) {
        return service.getRequestById(id);
    }

    // PUT /api/requests/1 → Actualizar datos
    @PutMapping("/{id}")
    public servicio updateRequest(@PathVariable Long id, @RequestBody servicio request) {
        return service.updateRequest(id, request);
    }

    // PATCH /api/requests/1/status → Cambiar estado
    @PatchMapping("/{id}/status")
    public servicio updateStatus(@PathVariable Long id, @RequestParam estado status) {
        return service.updateStatus(id, status);
    }

    // DELETE /api/requests/1 → Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        service.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/requests/status/PENDIENTE → Filtrar por estado
    @GetMapping("/status/{status}")
    public List<servicio> getByStatus(@PathVariable estado status) {
        return service.getByStatus(status);
    }
}
