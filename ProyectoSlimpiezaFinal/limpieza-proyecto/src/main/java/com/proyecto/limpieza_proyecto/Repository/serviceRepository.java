package com.proyecto.limpieza_proyecto.Repository;

import com.proyecto.limpieza_proyecto.Model.servicio;
import com.proyecto.limpieza_proyecto.Model.tipo;
import com.proyecto.limpieza_proyecto.Model.estado;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface serviceRepository extends JpaRepository<servicio, Long> {

    List<servicio> findByStatus(estado status);

    List<servicio> findByServiceType(tipo serviceType);

    @Query("SELECT YEAR(s.serviceDate), MONTH(s.serviceDate), COUNT(s) FROM servicio s GROUP BY YEAR(s.serviceDate), MONTH(s.serviceDate) ORDER BY YEAR(s.serviceDate), MONTH(s.serviceDate)")
    List<Object[]> countByMonth();

    @Query("SELECT s.serviceType, COUNT(s) FROM servicio s GROUP BY s.serviceType")
    List<Object[]> countByServiceType();
}