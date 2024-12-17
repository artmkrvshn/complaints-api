package com.complaints.controller;

import com.complaints.config.SecurityConfig;
import com.complaints.dto.request.ComplaintCreateRequest;
import com.complaints.dto.request.ComplaintUpdateRequest;
import com.complaints.dto.response.ComplaintResponse;
import com.complaints.entity.enums.Status;
import com.complaints.service.ComplaintService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ComplaintController.class)
@Import(SecurityConfig.class)
public class ComplaintControllerTest2 {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ComplaintService complaintService;

    @Autowired
    private ObjectMapper objectMapper;

    @WithAnonymousUser
    @Test
    void getComplaints_shouldReturnListOfComplaints() throws Exception {
        ComplaintResponse response = new ComplaintResponse();
        response.setId(1L);
        response.setDescription("Description");
        // set other fields

        when(complaintService.findAll()).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/v1/complaints"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Description"));
    }

    @WithAnonymousUser
    @Test
    void getComplaintById_shouldReturnComplaint() throws Exception {
        ComplaintResponse response = new ComplaintResponse();
        response.setId(1L);
        response.setDescription("Test complaint");

        when(complaintService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/complaints/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Test complaint"));
    }

    @WithMockUser
    @Test
    void addComplaint_shouldCreateComplaintAndReturn201() throws Exception {
        ComplaintCreateRequest createRequest = new ComplaintCreateRequest();
        createRequest.setProductId(1L);
        createRequest.setDescription("New complaint");
        createRequest.setDate(LocalDate.now());
        createRequest.setStatus(Status.OPEN);

        ComplaintResponse createdResponse = new ComplaintResponse();
        createdResponse.setId(2L);
        createdResponse.setDescription("New complaint");

        when(complaintService.save(ArgumentMatchers.any())).thenReturn(createdResponse);

        mockMvc.perform(post("/api/v1/complaints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.description").value("New complaint"));
    }

    @WithMockUser
    @Test
    void updateComplaint_shouldUpdateComplaint() throws Exception {
        ComplaintUpdateRequest updateRequest = new ComplaintUpdateRequest();
        updateRequest.setProductId(101L);
        updateRequest.setDescription("Updated complaint");
        updateRequest.setStatus(Status.OPEN);

        ComplaintResponse updatedResponse = new ComplaintResponse();
        updatedResponse.setId(3L);
        updatedResponse.setProductId(101L);
        updatedResponse.setCustomer(null);
        updatedResponse.setDate(LocalDate.now());
        updatedResponse.setDescription("Updated complaint");
        updatedResponse.setStatus(Status.OPEN);

        when(complaintService.update(ArgumentMatchers.eq(3L), ArgumentMatchers.any())).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/complaints/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.description").value("Updated complaint"));
    }

    @WithMockUser
    @Test
    void deleteComplaint_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/complaints/4"))
                .andExpect(status().isNoContent());
    }

}
