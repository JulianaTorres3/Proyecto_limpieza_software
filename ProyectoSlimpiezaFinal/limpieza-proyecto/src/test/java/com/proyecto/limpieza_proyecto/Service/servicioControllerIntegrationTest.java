package com.proyecto.limpieza_proyecto.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.proyecto.limpieza_proyecto.Model.estado;
import com.proyecto.limpieza_proyecto.Model.servicio;
import com.proyecto.limpieza_proyecto.Model.tipo;
import com.proyecto.limpieza_proyecto.Service.serviceRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(servicioController.class)
@DisplayName("Pruebas de Integración - servicioController")
class servicioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private serviceRequest service;

    private ObjectMapper objectMapper;
    private servicio solicitudEjemplo;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        solicitudEjemplo = new servicio();
        solicitudEjemplo.setId(1L);
        solicitudEjemplo.setClientName("Laura Martínez");
        solicitudEjemplo.setAddress("Av. El Poblado # 1-100");
        solicitudEjemplo.setServiceType(tipo.PROFUNDA);
        solicitudEjemplo.setServiceDate(LocalDate.of(2025, 7, 15));
        solicitudEjemplo.setStatus(estado.PENDIENTE);
    }

    // =========================================================
    // POST /api/requests
    // =========================================================
    @Nested
    @DisplayName("POST /api/requests")
    class PostTests {

        @Test
        @DisplayName("Crea solicitud y retorna HTTP 201")
        void deberiaCrearSolicitudYRetornar201() throws Exception {
            when(service.createRequest(any())).thenReturn(solicitudEjemplo);

            mockMvc.perform(post("/api/requests")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitudEjemplo)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.clientName").value("Laura Martínez"))
                    .andExpect(jsonPath("$.status").value("PENDIENTE"))
                    .andExpect(jsonPath("$.serviceType").value("PROFUNDA"));
        }

        @Test
        @DisplayName("Retorna HTTP 400 si datos son inválidos")
        void deberiaRetornar400SiDatosInvalidos() throws Exception {
            when(service.createRequest(any()))
                    .thenThrow(new IllegalArgumentException("El nombre del cliente es obligatorio"));

            mockMvc.perform(post("/api/requests")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }

    // =========================================================
    // GET /api/requests
    // =========================================================
    @Nested
    @DisplayName("GET /api/requests")
    class GetAllTests {

        @Test
        @DisplayName("Retorna todas las solicitudes con HTTP 200")
        void deberiaRetornarTodasLasSolicitudes() throws Exception {
            when(service.getAllRequests()).thenReturn(List.of(solicitudEjemplo));

            mockMvc.perform(get("/api/requests"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].clientName").value("Laura Martínez"));
        }

        @Test
        @DisplayName("Retorna lista vacía si no hay solicitudes")
        void deberiaRetornarListaVacia() throws Exception {
            when(service.getAllRequests()).thenReturn(List.of());

            mockMvc.perform(get("/api/requests"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    // =========================================================
    // GET /api/requests/{id}
    // =========================================================
    @Nested
    @DisplayName("GET /api/requests/{id}")
    class GetByIdTests {

        @Test
        @DisplayName("Retorna solicitud por ID con HTTP 200")
        void deberiaRetornarSolicitudPorId() throws Exception {
            when(service.getRequestById(1L)).thenReturn(solicitudEjemplo);

            mockMvc.perform(get("/api/requests/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.address").value("Av. El Poblado # 1-100"));
        }

        @Test
        @DisplayName("Retorna HTTP 500 si ID no existe (sin handler global)")
        void deberiaFallarSiIdNoExiste() throws Exception {
            when(service.getRequestById(99L))
                    .thenThrow(new RuntimeException("Solicitud no encontrada con id: 99"));

            mockMvc.perform(get("/api/requests/99"))
                    .andExpect(status().is5xxServerError());
        }
    }

    // =========================================================
    // PUT /api/requests/{id}
    // =========================================================
    @Nested
    @DisplayName("PUT /api/requests/{id}")
    class PutTests {

        @Test
        @DisplayName("Actualiza solicitud existente")
        void deberiaActualizarSolicitud() throws Exception {
            servicio actualizado = new servicio();
            actualizado.setId(1L);
            actualizado.setClientName("Laura Actualizada");
            actualizado.setAddress("Nueva Dirección 123");
            actualizado.setServiceType(tipo.VENTANAS);
            actualizado.setServiceDate(LocalDate.of(2025, 8, 1));
            actualizado.setStatus(estado.PENDIENTE);

            when(service.updateRequest(eq(1L), any())).thenReturn(actualizado);

            mockMvc.perform(put("/api/requests/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(actualizado)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.clientName").value("Laura Actualizada"))
                    .andExpect(jsonPath("$.serviceType").value("VENTANAS"));
        }

        @Test
        @DisplayName("Falla si la solicitud no está en PENDIENTE")
        void deberiaFallarSiNoEstaPendiente() throws Exception {
            when(service.updateRequest(eq(1L), any()))
                    .thenThrow(new IllegalStateException("Solo se pueden editar solicitudes en estado PENDIENTE"));

            mockMvc.perform(put("/api/requests/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(solicitudEjemplo)))
                    .andExpect(status().is5xxServerError());
        }
    }

    // =========================================================
    // PATCH /api/requests/{id}/status
    // =========================================================
    @Nested
    @DisplayName("PATCH /api/requests/{id}/status")
    class PatchStatusTests {

        @Test
        @DisplayName("Cambia estado de solicitud correctamente")
        void deberiaCambiarEstado() throws Exception {
            solicitudEjemplo.setStatus(estado.CONFIRMADA);
            when(service.updateStatus(1L, estado.CONFIRMADA)).thenReturn(solicitudEjemplo);

            mockMvc.perform(patch("/api/requests/1/status")
                            .param("status", "CONFIRMADA"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("CONFIRMADA"));
        }

        @Test
        @DisplayName("Falla al cambiar estado de solicitud CANCELADA")
        void deberiaFallarCambioEstadoCancelada() throws Exception {
            when(service.updateStatus(eq(1L), any()))
                    .thenThrow(new IllegalStateException("No se puede cambiar el estado de una solicitud cancelada"));

            mockMvc.perform(patch("/api/requests/1/status")
                            .param("status", "CONFIRMADA"))
                    .andExpect(status().is5xxServerError());
        }
    }

    // =========================================================
    // DELETE /api/requests/{id}
    // =========================================================
    @Nested
    @DisplayName("DELETE /api/requests/{id}")
    class DeleteTests {

        @Test
        @DisplayName("Elimina solicitud y retorna HTTP 204")
        void deberiaEliminarSolicitud() throws Exception {
            doNothing().when(service).deleteRequest(1L);

            mockMvc.perform(delete("/api/requests/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Falla al eliminar solicitud no PENDIENTE")
        void deberiaFallarEliminarNoPermitido() throws Exception {
            org.mockito.Mockito.doThrow(new IllegalStateException("Solo se pueden eliminar solicitudes en estado PENDIENTE"))
                    .when(service).deleteRequest(1L);

            mockMvc.perform(delete("/api/requests/1"))
                    .andExpect(status().is5xxServerError());
        }
    }

    // =========================================================
    // GET /api/requests/status/{status}
    // =========================================================
    @Nested
    @DisplayName("GET /api/requests/status/{status}")
    class GetByStatusTests {

        @Test
        @DisplayName("Filtra solicitudes por estado PENDIENTE")
        void deberiaFiltrarPorEstado() throws Exception {
            when(service.getByStatus(estado.PENDIENTE)).thenReturn(List.of(solicitudEjemplo));

            mockMvc.perform(get("/api/requests/status/PENDIENTE"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].status").value("PENDIENTE"));
        }
    }

    // =========================================================
    // GET /api/requests/stats/*
    // =========================================================
    @Nested
    @DisplayName("GET /api/requests/stats/*")
    class StatsEndpointTests {

        @Test
        @DisplayName("Retorna estadísticas por mes")
        void deberiaRetornarEstadisticasPorMes() throws Exception {
            List<Map<String, Object>> stats = List.of(
                    Map.of("month", "May 2025", "count", 3),
                    Map.of("month", "Jun 2025", "count", 7)
            );
            when(service.getStatsByMonth()).thenReturn(stats);

            mockMvc.perform(get("/api/requests/stats/by-month"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].month").value("May 2025"))
                    .andExpect(jsonPath("$[0].count").value(3));
        }

        @Test
        @DisplayName("Retorna estadísticas por tipo de servicio")
        void deberiaRetornarEstadisticasPorTipo() throws Exception {
            List<Map<String, Object>> stats = List.of(
                    Map.of("type", "HOGAR", "count", 10),
                    Map.of("type", "OFICINA", "count", 4)
            );
            when(service.getStatsByType()).thenReturn(stats);

            mockMvc.perform(get("/api/requests/stats/by-type"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].type").value("HOGAR"))
                    .andExpect(jsonPath("$[0].count").value(10));
        }
    }
}
