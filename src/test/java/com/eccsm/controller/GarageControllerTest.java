package com.eccsm.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.persistence.EntityNotFoundException;

import com.eccsm.model.Ticket;
import com.eccsm.model.Ticket.Vehicle;
import com.eccsm.repository.ITicket;
import com.eccsm.service.GarageService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GarageControllerTest {

    @MockBean
    private GarageService garageService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Spy
    ITicket ticketRepository;

    @Test
    void shouldParkSuccesfully() throws Exception {
        Ticket ticket = new Ticket(1, "34TC1234", "Red", Vehicle.CAR);

        Mockito.when(garageService.park(ticket)).thenReturn(ticket);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/park")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticket));

        mockMvc.perform(mockRequest).andExpect(status().isCreated());
    }

    @Test
    void shouldGetAllRecordsSuccessfully() throws Exception {
        String status = new String();

        Mockito.when(garageService.status()).thenReturn(status);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/status")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldLeaveSuccesfully() throws Exception {
        long id = 1L;
        doNothing().when(ticketRepository).deleteById(id);

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/leave/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldAppearErrorWhenInvalidPlate() throws Exception {
        Ticket ticket = new Ticket(1, "12345678abc", "Red", Vehicle.CAR);

        Mockito.doThrow(RuntimeException.class).when(garageService).park(ticket);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/park", ticket))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAppearErrorWhenInvalidColor() throws Exception {
        Ticket ticket = new Ticket(1, "34TC1234", "R3d", Vehicle.CAR);

        Mockito.doThrow(RuntimeException.class).when(garageService).park(ticket);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/park", ticket))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAppearErrorWhenInvalidVehicle() throws Exception {
        Ticket ticket = new Ticket(1, "34TC1234", "Red", null);

        Mockito.when(garageService.park(ticket)).thenReturn(ticket);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/park")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticket));

        mockMvc.perform(mockRequest).andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrownTicketNotFoundWhenIdNotExists() throws Exception {
        Mockito.doThrow(EntityNotFoundException.class).when(garageService).leave(anyLong());

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/leave/{id}", 1L))
                .andExpect(status().isBadRequest());
    }

}
