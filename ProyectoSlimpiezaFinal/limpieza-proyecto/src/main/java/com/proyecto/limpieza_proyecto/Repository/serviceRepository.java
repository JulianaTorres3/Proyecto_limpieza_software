package com.proyecto.limpieza_proyecto.Repository;

import com.proyecto.limpieza_proyecto.Model.servicio;
import com.proyecto.limpieza_proyecto.Model.tipo;
import com.proyecto.limpieza_proyecto.Model.estado;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface serviceRepository extends JpaRepository<servicio, Long> {

    // Buscar solicitudes por estado
    List<servicio> findByStatus(estado status);

    // Buscar solicitudes por tipo de servicio
    List<servicio> findByServiceType(tipo serviceType);
}