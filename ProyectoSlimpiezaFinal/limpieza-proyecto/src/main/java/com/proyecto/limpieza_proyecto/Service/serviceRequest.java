package com.proyecto.limpieza_proyecto.Service;

import com.proyecto.limpieza_proyecto.Model.servicio;
import com.proyecto.limpieza_proyecto.Model.estado;
import com.proyecto.limpieza_proyecto.Repository.serviceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class serviceRequest {

    private final serviceRepository repository;

    @Autowired
    public serviceRequest(serviceRepository repository) {
        this.repository = repository;
    }

    // CREAR solicitud - empieza siempre en PENDIENTE
    public servicio createRequest(servicio request) {
        if (request.getClientName() == null || request.getClientName().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio");
        }
        if (request.getAddress() == null || request.getAddress().isEmpty()) {
            throw new IllegalArgumentException("La dirección es obligatoria");
        }
        if (request.getServiceDate() == null) {
            throw new IllegalArgumentException("La fecha del servicio es obligatoria");
        }
        if (request.getServiceType() == null) {
            throw new IllegalArgumentException("El tipo de servicio es obligatorio");
        }
        request.setStatus(estado.PENDIENTE);
        return repository.save(request);
    }

    // LEER todas las solicitudes
    public List<servicio> getAllRequests() {
        return repository.findAll();
    }

    // LEER una solicitud por id
    public servicio getRequestById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con id: " + id));
    }

    // ACTUALIZAR solicitud - solo si está PENDIENTE
    public servicio updateRequest(Long id, servicio updatedRequest) {
        servicio existing = getRequestById(id);

        if (existing.getStatus() != estado.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden editar solicitudes en estado PENDIENTE");
        }

        existing.setClientName(updatedRequest.getClientName());
        existing.setAddress(updatedRequest.getAddress());
        existing.setServiceType(updatedRequest.getServiceType());
        existing.setServiceDate(updatedRequest.getServiceDate());
        existing.setAdditionalNotes(updatedRequest.getAdditionalNotes());
        return repository.save(existing);
    }

    // CAMBIAR ESTADO de una solicitud
    public servicio updateStatus(Long id, estado newStatus) {
        servicio existing = getRequestById(id);

        // No se puede cambiar el estado si ya está CANCELADA o REALIZADA
        if (existing.getStatus() == estado.CANCELADA) {
            throw new IllegalStateException("No se puede cambiar el estado de una solicitud cancelada");
        }
        if (existing.getStatus() == estado.REALIZADA) {
            throw new IllegalStateException("No se puede cambiar el estado de una solicitud ya realizada");
        }

        existing.setStatus(newStatus);
        return repository.save(existing);
    }

    // ELIMINAR solicitud - solo si está PENDIENTE
    public void deleteRequest(Long id) {
        servicio existing = getRequestById(id);

        if (existing.getStatus() != estado.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden eliminar solicitudes en estado PENDIENTE");
        }

        repository.delete(existing);
    }

    // BUSCAR por estado
    public List<servicio> getByStatus(estado status) {
        return repository.findByStatus(status);
    }
}