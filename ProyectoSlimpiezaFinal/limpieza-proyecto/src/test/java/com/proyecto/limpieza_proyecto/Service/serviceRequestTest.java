package com.proyecto.limpieza_proyecto.Service;

import com.proyecto.limpieza_proyecto.Model.estado;
import com.proyecto.limpieza_proyecto.Model.servicio;
import com.proyecto.limpieza_proyecto.Model.tipo;
import com.proyecto.limpieza_proyecto.Repository.serviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - serviceRequest")
class serviceRequestTest {

    @Mock
    private serviceRepository repository;

    @InjectMocks
    private serviceRequest serviceUnderTest;

    private servicio solicitudValida;

    @BeforeEach
    void setUp() {
        solicitudValida = new servicio();
        solicitudValida.setId(1L);
        solicitudValida.setClientName("Carlos Pérez");
        solicitudValida.setAddress("Calle 10 # 20-30, Medellín");
        solicitudValida.setServiceType(tipo.HOGAR);
        solicitudValida.setServiceDate(LocalDate.now().plusDays(3));
        solicitudValida.setStatus(estado.PENDIENTE);
    }

    // =========================================================
    // PRUEBAS: createRequest
    // =========================================================
    @Nested
    @DisplayName("createRequest()")
    class CreateRequestTests {

        @Test
        @DisplayName("Crea solicitud válida con estado PENDIENTE")
        void deberiaCrearSolicitudValida() {
            when(repository.save(any(servicio.class))).thenReturn(solicitudValida);

            servicio resultado = serviceUnderTest.createRequest(solicitudValida);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getStatus()).isEqualTo(estado.PENDIENTE);
            assertThat(resultado.getClientName()).isEqualTo("Carlos Pérez");
            verify(repository, times(1)).save(any(servicio.class));
        }

        @Test
        @DisplayName("Lanza excepción si clientName es null")
        void deberiaLanzarExcepcionSiClientNameEsNull() {
            solicitudValida.setClientName(null);

            assertThatThrownBy(() -> serviceUnderTest.createRequest(solicitudValida))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("nombre del cliente es obligatorio");

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Lanza excepción si clientName está vacío")
        void deberiaLanzarExcepcionSiClientNameEstaVacio() {
            solicitudValida.setClientName("   ");

            assertThatThrownBy(() -> serviceUnderTest.createRequest(solicitudValida))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("nombre del cliente es obligatorio");
        }

        @Test
        @DisplayName("Lanza excepción si address es null")
        void deberiaLanzarExcepcionSiAddressEsNull() {
            solicitudValida.setAddress(null);

            assertThatThrownBy(() -> serviceUnderTest.createRequest(solicitudValida))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("dirección es obligatoria");
        }

        @Test
        @DisplayName("Lanza excepción si serviceDate es null")
        void deberiaLanzarExcepcionSiServiceDateEsNull() {
            solicitudValida.setServiceDate(null);

            assertThatThrownBy(() -> serviceUnderTest.createRequest(solicitudValida))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("fecha del servicio es obligatoria");
        }

        @Test
        @DisplayName("Lanza excepción si serviceType es null")
        void deberiaLanzarExcepcionSiServiceTypeEsNull() {
            solicitudValida.setServiceType(null);

            assertThatThrownBy(() -> serviceUnderTest.createRequest(solicitudValida))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("tipo de servicio es obligatorio");
        }

        @Test
        @DisplayName("Siempre guarda con estado PENDIENTE, sin importar el estado enviado")
        void deberiaForzarEstadoPendiente() {
            solicitudValida.setStatus(estado.CONFIRMADA); // intento de burlar el sistema
            when(repository.save(any(servicio.class))).thenAnswer(inv -> inv.getArgument(0));

            servicio resultado = serviceUnderTest.createRequest(solicitudValida);

            assertThat(resultado.getStatus()).isEqualTo(estado.PENDIENTE);
        }
    }

    // =========================================================
    // PRUEBAS: getAllRequests / getRequestById
    // =========================================================
    @Nested
    @DisplayName("getAllRequests() / getRequestById()")
    class ReadTests {

        @Test
        @DisplayName("Retorna lista de todas las solicitudes")
        void deberiaRetornarTodasLasSolicitudes() {
            when(repository.findAll()).thenReturn(List.of(solicitudValida));

            List<servicio> resultado = serviceUnderTest.getAllRequests();

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getClientName()).isEqualTo("Carlos Pérez");
        }

        @Test
        @DisplayName("Retorna solicitud por ID existente")
        void deberiaRetornarSolicitudPorId() {
            when(repository.findById(1L)).thenReturn(Optional.of(solicitudValida));

            servicio resultado = serviceUnderTest.getRequestById(1L);

            assertThat(resultado.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Lanza RuntimeException si ID no existe")
        void deberiaLanzarExcepcionSiIdNoExiste() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> serviceUnderTest.getRequestById(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("99");
        }
    }

    // =========================================================
    // PRUEBAS: updateRequest
    // =========================================================
    @Nested
    @DisplayName("updateRequest()")
    class UpdateRequestTests {

        @Test
        @DisplayName("Actualiza solicitud en estado PENDIENTE")
        void deberiaActualizarSolicitudPendiente() {
            servicio datosNuevos = new servicio();
            datosNuevos.setClientName("Ana García");
            datosNuevos.setAddress("Carrera 80 # 5-10");
            datosNuevos.setServiceType(tipo.OFICINA);
            datosNuevos.setServiceDate(LocalDate.now().plusDays(5));

            when(repository.findById(1L)).thenReturn(Optional.of(solicitudValida));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            servicio resultado = serviceUnderTest.updateRequest(1L, datosNuevos);

            assertThat(resultado.getClientName()).isEqualTo("Ana García");
            assertThat(resultado.getServiceType()).isEqualTo(tipo.OFICINA);
        }

        @Test
        @DisplayName("No permite editar solicitud CONFIRMADA")
        void noDeberiaEditarSolicitudConfirmada() {
            solicitudValida.setStatus(estado.CONFIRMADA);
            when(repository.findById(1L)).thenReturn(Optional.of(solicitudValida));

            assertThatThrownBy(() -> serviceUnderTest.updateRequest(1L, new servicio()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PENDIENTE");
        }

        @Test
        @DisplayName("No permite editar solicitud CANCELADA")
        void noDeberiaEditarSolicitudCancelada() {
            solicitudValida.setStatus(estado.CANCELADA);
            when(repository.findById(1L)).thenReturn(Optional.of(solicitudValida));

            assertThatThrownBy(() -> serviceUnderTest.updateRequest(1L, new servicio()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    // =========================================================
    // PRUEBAS: updateStatus
    // =========================================================
    @Nested
    @DisplayName("updateStatus()")
    class UpdateStatusTests {

        @Test
        @DisplayName("Cambia estado de PENDIENTE a CONFIRMADA")
        void deberiaCambiarEstadoPendienteAConfirmada() {
            when(repository.findById(1L)).thenReturn(Optional.of(solicitudValida));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            servicio resultado = serviceUnderTest.updateStatus(1L, estado.CONFIRMADA);

            assertThat(resultado.getStatus()).isEqualTo(estado.CONFIRMADA);
        }

        @Test
        @DisplayName("Cambia estado de CONFIRMADA a REALIZADA")
        void deberiaCambiarEstadoConfirmadaAealizada() {
            solicitudValida.setStatus(estado.CONFIRMADA);
            when(repository.findById(1L)).thenReturn(Optional.of(solicitudValida));
            when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            servicio resultado = serviceUnderTest.updateStatus(1L, estado.REALIZADA);

            assertThat(resultado.getStatus()).isEqualTo(estado.REALIZADA);
        }

        @Test
        @DisplayName("No permite cambiar estado de solicitud CANCELADA")
        void noDeberiaModificarSolicitudCancelada() {
            solicitudValida.setStatus(estado.CANCELADA);
            when(repository.findById(1L)).thenReturn(Optional.of(solicitudValida));

            assertThatThrownBy(() -> serviceUnderTest.updateStatus(1L, estado.CONFIRMADA))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("cancelada");
        }

        @Test
        @DisplayName("No permite cambiar estado de solicitud REALIZADA")
        void noDeberiaModificarSolicitudRealizada() {
            solicitudValida.setStatus(estado.REALIZADA);
            when(repository.findById(1L)).thenReturn(Optional.of(solicitudValida));

            assertThatThrownBy(() -> serviceUnderTest.updateStatus(1L, estado.CANCELADA))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("realizada");
        }
    }

    // =========================================================
    // PRUEBAS: deleteRequest
    // =========================================================
    @Nested
    @DisplayName("deleteRequest()")
    class DeleteRequestTests {

        @Test
        @DisplayName("Elimina solicitud en estado PENDIENTE")
        void deberiaEliminarSolicitudPendiente() {
            when(repository.findById(1L)).thenReturn(Optional.of(solicitudValida));
            doNothing().when(repository).delete(any());

            assertThatCode(() -> serviceUnderTest.deleteRequest(1L))
                    .doesNotThrowAnyException();

            verify(repository, times(1)).delete(solicitudValida);
        }

        @Test
        @DisplayName("No permite eliminar solicitud CONFIRMADA")
        void noDeberiaEliminarSolicitudConfirmada() {
            solicitudValida.setStatus(estado.CONFIRMADA);
            when(repository.findById(1L)).thenReturn(Optional.of(solicitudValida));

            assertThatThrownBy(() -> serviceUnderTest.deleteRequest(1L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PENDIENTE");

            verify(repository, never()).delete(any());
        }

        @Test
        @DisplayName("No permite eliminar solicitud REALIZADA")
        void noDeberiaEliminarSolicitudRealizada() {
            solicitudValida.setStatus(estado.REALIZADA);
            when(repository.findById(1L)).thenReturn(Optional.of(solicitudValida));

            assertThatThrownBy(() -> serviceUnderTest.deleteRequest(1L))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    // =========================================================
    // PRUEBAS: Estadísticas
    // =========================================================
    @Nested
    @DisplayName("getStatsByMonth() / getStatsByType()")
    class StatsTests {

        @Test
        @DisplayName("Retorna estadísticas por mes correctamente formateadas")
        void deberiaRetornarEstadisticasPorMes() {
            List<Object[]> mockRows = List.of(
                    new Object[]{2025, 5, 3L},
                    new Object[]{2025, 6, 7L}
            );
            when(repository.countByMonth()).thenReturn(mockRows);

            List<Map<String, Object>> resultado = serviceUnderTest.getStatsByMonth();

            assertThat(resultado).hasSize(2);
            assertThat(resultado.get(0).get("month")).isEqualTo("May 2025");
            assertThat(resultado.get(0).get("count")).isEqualTo(3L);
            assertThat(resultado.get(1).get("month")).isEqualTo("Jun 2025");
        }

        @Test
        @DisplayName("Retorna estadísticas por tipo de servicio")
        void deberiaRetornarEstadisticasPorTipo() {
            List<Object[]> mockRows = List.of(
                    new Object[]{tipo.HOGAR, 10L},
                    new Object[]{tipo.OFICINA, 5L}
            );
            when(repository.countByServiceType()).thenReturn(mockRows);

            List<Map<String, Object>> resultado = serviceUnderTest.getStatsByType();

            assertThat(resultado).hasSize(2);
            assertThat(resultado.get(0).get("type")).isEqualTo("HOGAR");
            assertThat(resultado.get(0).get("count")).isEqualTo(10L);
        }

        @Test
        @DisplayName("Retorna lista vacía si no hay datos")
        void deberiaRetornarListaVaciaSiNoHayDatos() {
            when(repository.countByMonth()).thenReturn(Collections.emptyList());

            List<Map<String, Object>> resultado = serviceUnderTest.getStatsByMonth();

            assertThat(resultado).isEmpty();
        }
    }
}
