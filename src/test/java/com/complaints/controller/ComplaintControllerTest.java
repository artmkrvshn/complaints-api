package com.complaints.controller;

import com.complaints.config.SecurityConfig;
import com.complaints.dto.request.ComplaintCreateRequest;
import com.complaints.dto.request.ComplaintUpdateRequest;
import com.complaints.dto.response.ComplaintResponse;
import com.complaints.entity.enums.Status;
import com.complaints.exception.EntityNotFoundException;
import com.complaints.service.ComplaintService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ComplaintController.class)
@Import(SecurityConfig.class)
public class ComplaintControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ComplaintService complaintService;

    @Autowired
    private ObjectMapper objectMapper;

    private ComplaintResponse complaint1;
    private ComplaintResponse complaint2;

    @BeforeEach
    void setUp() {
        complaint1 = new ComplaintResponse();
        complaint1.setId(1L);
        complaint1.setDescription("Complaint 1");
        complaint1.setDate(LocalDate.now());
        complaint1.setStatus(Status.OPEN);

        complaint2 = new ComplaintResponse();
        complaint2.setId(2L);
        complaint2.setDescription("Complaint 2");
        complaint2.setDate(LocalDate.now());
        complaint2.setStatus(Status.CANCELED);
    }

    @WithAnonymousUser
    @Test
    void getComplaints_shouldReturnListOfComplaints() throws Exception {
        when(complaintService.findAll()).thenReturn(List.of(complaint1, complaint2));

        mockMvc.perform(get("/api/v1/complaints"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Complaint 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].description").value("Complaint 2"));
    }

    @WithAnonymousUser
    @Test
    void getComplaints_withPaginationAndSorting_shouldReturnComplaints() throws Exception {
        when(complaintService.findAll(ArgumentMatchers.any())).thenReturn(List.of(complaint1));

        mockMvc.perform(get("/api/v1/complaints")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "date"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Complaint 1"));
    }

    @WithAnonymousUser
    @Test
    void getComplaintById_shouldReturnComplaint() throws Exception {
        when(complaintService.findById(1L)).thenReturn(complaint1);

        mockMvc.perform(get("/api/v1/complaints/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Complaint 1"));
    }

    @WithAnonymousUser
    @Test
    void getComplaintById_shouldReturnNotFound() throws Exception {
        when(complaintService.findById(99L)).thenThrow(new EntityNotFoundException("Complaint not found"));

        mockMvc.perform(get("/api/v1/complaints/99"))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    void addComplaint_shouldCreateComplaint() throws Exception {
        ComplaintCreateRequest createRequest = new ComplaintCreateRequest();
        createRequest.setProductId(1L);
        createRequest.setDescription("New Complaint");
        createRequest.setDate(LocalDate.now());
        createRequest.setStatus(Status.OPEN);

        when(complaintService.save(ArgumentMatchers.any())).thenReturn(complaint1);

        mockMvc.perform(post("/api/v1/complaints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Complaint 1"));
    }

    @WithMockUser
    @Test
    void addComplaint_shouldReturnBadRequestForInvalidInput() throws Exception {
        ComplaintCreateRequest createRequest = new ComplaintCreateRequest();

        mockMvc.perform(post("/api/v1/complaints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @Test
    void updateComplaint_shouldUpdateComplaint() throws Exception {
        ComplaintUpdateRequest updateRequest = new ComplaintUpdateRequest();
        updateRequest.setProductId(1L);
        updateRequest.setDescription("Updated Complaint");
        updateRequest.setStatus(Status.CANCELED);

        when(complaintService.update(1L, updateRequest)).thenReturn(complaint2);

        mockMvc.perform(put("/api/v1/complaints/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.description").value("Complaint 2"));
    }

    @WithMockUser
    @Test
    void deleteComplaint_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/complaints/1"))
                .andExpect(status().isNoContent());
    }

    @WithMockUser
    @Test
    void deleteComplaint_shouldReturnNotFound() throws Exception {
        Mockito.doThrow(new EntityNotFoundException("Complaint not found")).when(complaintService).deleteById(99L);

        mockMvc.perform(delete("/api/v1/complaints/99"))
                .andExpect(status().isNotFound());
    }
}
