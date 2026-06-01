package com.proyecto.limpieza_proyecto.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre del cliente que solicita el servicio
    private String clientName;

    // Dirección donde se realizará el servicio
    private String address;

    // Tipo de servicio: HOGAR, OFICINA, PROFUNDA, POST_OBRA, VENTANAS
    @Enumerated(EnumType.STRING)
    private tipo serviceType;

    // Fecha en que se realizará el servicio
    private LocalDate serviceDate;

    // Estado de la solicitud: PENDIENTE, CONFIRMADA, REALIZADA, CANCELADA
    @Enumerated(EnumType.STRING)
    private estado status;

    // Al crear una solicitud el estado siempre empieza en PENDIENTE
    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = estado.PENDIENTE;
        }
    }
}