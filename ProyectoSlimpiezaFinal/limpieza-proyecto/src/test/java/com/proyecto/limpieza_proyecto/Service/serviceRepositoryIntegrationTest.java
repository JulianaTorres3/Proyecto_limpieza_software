package com.proyecto.limpieza_proyecto.Repository;

import com.proyecto.limpieza_proyecto.Model.estado;
import com.proyecto.limpieza_proyecto.Model.servicio;
import com.proyecto.limpieza_proyecto.Model.tipo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Pruebas de Integración - serviceRepository (H2)")
class serviceRepositoryIntegrationTest {

    @Autowired
    private serviceRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        // Solicitud 1: PENDIENTE - HOGAR
        servicio s1 = crearSolicitud("María López", estado.PENDIENTE, tipo.HOGAR, LocalDate.of(2025, 5, 10));
        // Solicitud 2: CONFIRMADA - OFICINA
        servicio s2 = crearSolicitud("Juan Torres", estado.CONFIRMADA, tipo.OFICINA, LocalDate.of(2025, 5, 20));
        // Solicitud 3: REALIZADA - HOGAR
        servicio s3 = crearSolicitud("Ana Ríos", estado.REALIZADA, tipo.HOGAR, LocalDate.of(2025, 6, 5));
        // Solicitud 4: CANCELADA - PROFUNDA
        servicio s4 = crearSolicitud("Pedro Gómez", estado.CANCELADA, tipo.PROFUNDA, LocalDate.of(2025, 6, 15));
        // Solicitud 5: PENDIENTE - VENTANAS
        servicio s5 = crearSolicitud("Sofía Castro", estado.PENDIENTE, tipo.VENTANAS, LocalDate.of(2025, 6, 20));

        repository.saveAll(List.of(s1, s2, s3, s4, s5));
    }

    @Test
    @DisplayName("findByStatus retorna solo solicitudes PENDIENTES")
    void deberiaFiltrarPorEstadoPendiente() {
        List<servicio> pendientes = repository.findByStatus(estado.PENDIENTE);

        assertThat(pendientes).hasSize(2);
        assertThat(pendientes).allMatch(s -> s.getStatus() == estado.PENDIENTE);
    }

    @Test
    @DisplayName("findByStatus retorna solo solicitudes CANCELADAS")
    void deberiaFiltrarPorEstadoCancelada() {
        List<servicio> canceladas = repository.findByStatus(estado.CANCELADA);

        assertThat(canceladas).hasSize(1);
        assertThat(canceladas.get(0).getClientName()).isEqualTo("Pedro Gómez");
    }

    @Test
    @DisplayName("findByServiceType retorna solicitudes de tipo HOGAR")
    void deberiaFiltrarPorTipoHogar() {
        List<servicio> hogares = repository.findByServiceType(tipo.HOGAR);

        assertThat(hogares).hasSize(2);
        assertThat(hogares).allMatch(s -> s.getServiceType() == tipo.HOGAR);
    }

    @Test
    @DisplayName("countByMonth agrupa correctamente por mes")
    void deberiaAgruparPorMes() {
        List<Object[]> resultados = repository.countByMonth();

        // Mayo 2025: 2 solicitudes, Junio 2025: 3 solicitudes
        assertThat(resultados).hasSize(2);

        Object[] mayo = resultados.get(0);
        assertThat(((Number) mayo[0]).intValue()).isEqualTo(2025);
        assertThat(((Number) mayo[1]).intValue()).isEqualTo(5);
        assertThat(((Number) mayo[2]).longValue()).isEqualTo(2L);

        Object[] junio = resultados.get(1);
        assertThat(((Number) junio[1]).intValue()).isEqualTo(6);
        assertThat(((Number) junio[2]).longValue()).isEqualTo(3L);
    }

    @Test
    @DisplayName("countByServiceType cuenta correctamente por tipo")
    void deberiaContarPorTipo() {
        List<Object[]> resultados = repository.countByServiceType();

        assertThat(resultados).isNotEmpty();
        long totalSolicitudes = resultados.stream()
                .mapToLong(r -> ((Number) r[1]).longValue())
                .sum();
        assertThat(totalSolicitudes).isEqualTo(5L);
    }

    @Test
    @DisplayName("prePersist asigna estado PENDIENTE automáticamente")
    void deberiaAsignarEstadoPendienteAlPersistir() {
        servicio nueva = new servicio();
        nueva.setClientName("Test PrePersist");
        nueva.setAddress("Calle Test");
        nueva.setServiceType(tipo.POST_OBRA);
        nueva.setServiceDate(LocalDate.now().plusDays(2));
        // NO se asigna status explícitamente

        servicio guardada = repository.save(nueva);

        assertThat(guardada.getStatus()).isEqualTo(estado.PENDIENTE);
    }

    @Test
    @DisplayName("findByStatus retorna lista vacía si no hay coincidencias")
    void deberiaRetornarListaVaciaSiNoHayCoincidencias() {
        // Solo hay 1 CONFIRMADA, borremos todas y busquemos
        repository.deleteAll();
        List<servicio> resultado = repository.findByStatus(estado.CONFIRMADA);

        assertThat(resultado).isEmpty();
    }

    // ---- Helper ----
    private servicio crearSolicitud(String nombre, estado est, tipo t, LocalDate fecha) {
        servicio s = new servicio();
        s.setClientName(nombre);
        s.setAddress("Dirección de " + nombre);
        s.setServiceType(t);
        s.setServiceDate(fecha);
        s.setStatus(est);
        return s;
    }
}
