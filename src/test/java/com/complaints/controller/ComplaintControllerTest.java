package com.complaints.controller;

import com.complaints.dto.response.ComplaintResponse;
import com.complaints.dto.response.CustomerResponse;
import com.complaints.entity.enums.Status;
import com.complaints.exception.EntityNotFoundException;
import com.complaints.service.ComplaintService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ComplaintController.class)
public class ComplaintControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ComplaintService complaintService;

    @Test
    void contextLoads() {
        assertNotNull(mockMvc);
        assertNotNull(objectMapper);
        assertNotNull(complaintService);
    }

    private List<CustomerResponse> getCustomers() {
        return List.of(
                new CustomerResponse("john.doe@email.com", "John Doe"),
                new CustomerResponse("jane.smith@email.com", "Jane Smith")
        );
    }

    private List<ComplaintResponse> getComplaints() {
        return List.of(
                new ComplaintResponse(1L, 101L, getCustomers().getFirst(), LocalDate.of(2024, 12, 10), "Product arrived damaged", Status.OPEN),
                new ComplaintResponse(2L, 102L, getCustomers().getFirst(), LocalDate.of(2024, 12, 9), "Late delivery", Status.IN_PROGRESS),
                new ComplaintResponse(3L, 103L, getCustomers().getFirst(), LocalDate.of(2024, 12, 8), "Received wrong product", Status.IN_PROGRESS),
                new ComplaintResponse(4L, 104L, getCustomers().getFirst(), LocalDate.of(2024, 12, 7), "Missing parts in the package", Status.ACCEPTED),
                new ComplaintResponse(5L, 105L, getCustomers().getLast(), LocalDate.of(2024, 12, 6), "Request for refund", Status.ACCEPTED),
                new ComplaintResponse(6L, 106L, getCustomers().getLast(), LocalDate.of(2024, 12, 5), "Product stopped working after one day", Status.CANCELED),
                new ComplaintResponse(7L, 107L, getCustomers().getLast(), LocalDate.of(2024, 12, 4), "Duplicate order received", Status.CANCELED),
                new ComplaintResponse(8L, 108L, getCustomers().getLast(), LocalDate.of(2024, 12, 3), "Broken seal on product", Status.REJECTED),
                new ComplaintResponse(9L, 109L, getCustomers().getLast(), LocalDate.of(2024, 12, 2), "Warranty claim issue", Status.REJECTED),
                new ComplaintResponse(10L, 110L, getCustomers().getLast(), LocalDate.of(2024, 12, 1), "Unresponsive customer support", Status.OPEN)
        );
    }

    @Test
    void givenValidComplaintId_whenGetComplaint_thenReturnComplaint() throws Exception {
        Long validComplaintId = 1L;
        ComplaintResponse mockComplaintResponse = new ComplaintResponse(validComplaintId, 123L, new CustomerResponse("test@email.com", "John Doe"), LocalDate.now(), "Description", Status.IN_PROGRESS);

        Mockito.when(complaintService.findById(validComplaintId)).thenReturn(mockComplaintResponse);

        mockMvc.perform(get("/api/v1/complaints/{id}", validComplaintId))
                .andExpectAll(
                        status().is2xxSuccessful(),
                        jsonPath("$.id").value(validComplaintId),
                        jsonPath("$.productId").value(mockComplaintResponse.getProductId()),
                        jsonPath("$.customer.name").value(mockComplaintResponse.getCustomer().getName()),
                        jsonPath("$.customer.email").value(mockComplaintResponse.getCustomer().getEmail()),
                        jsonPath("$.description").value(mockComplaintResponse.getDescription()),
                        jsonPath("$.status").value(mockComplaintResponse.getStatus().name()));

        Mockito.verify(complaintService, times(1)).findById(validComplaintId);
    }


    @Test
    void givenInvalidComplaintId_whenGetComplaint_thenReturnNotFound() throws Exception {
        Mockito.when(complaintService.findById(anyLong())).thenThrow(new EntityNotFoundException("Not found"));

        mockMvc.perform(get("/api/complaints/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetAllComplaints_thenReturnAllComplaints() throws Exception {
        List<ComplaintResponse> complaints = List.of(
                new ComplaintResponse(1L, 101L, getCustomers().getFirst(), LocalDate.of(2024, 12, 10), "Product arrived damaged", Status.OPEN),
                new ComplaintResponse(2L, 102L, getCustomers().getFirst(), LocalDate.of(2024, 12, 9), "Late delivery", Status.IN_PROGRESS),
                new ComplaintResponse(3L, 103L, getCustomers().getFirst(), LocalDate.of(2024, 12, 8), "Received wrong product", Status.IN_PROGRESS)
        );

        Mockito.when(complaintService.findAll()).thenReturn(complaints);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/complaints"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(complaints.size()));

        for (int i = 0; i < complaints.size(); i++) {
            resultActions.andExpectAll(
                    jsonPath("$[" + i + "].id").value(complaints.get(i).getId()),
                    jsonPath("$[" + i + "].productId").value(complaints.get(i).getProductId()),
                    jsonPath("$[" + i + "].customer.name").value(complaints.get(i).getCustomer().getName()),
                    jsonPath("$[" + i + "].customer.email").value(complaints.get(i).getCustomer().getEmail()),
                    jsonPath("$[" + i + "].description").value(complaints.get(i).getDescription()),
                    jsonPath("$[" + i + "].status").value(complaints.get(i).getStatus().name())
            );
        }
    }

    @Test
    void givenValidComplaintRequest_whenCreateNewComplaint_thenCreateNewCompliant() throws Exception {

//        ComplaintResponse request = new ComplaintResponse(null, 103L, "New Complaint", "OPEN");
//        ComplaintResponse response = new ComplaintResponse(3L, 103L, "New Complaint", "OPEN");
//
//        Mockito.when(complaintService.createComplaint(any())).thenReturn(response);
//
//        mockMvc.perform(post("/api/complaints")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"complaintId\":103,\"details\":\"New Complaint\",\"status\":\"OPEN\"}"))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.complaintId").value(103))
//                .andExpect(jsonPath("$.details").value("New Complaint"))
//                .andExpect(jsonPath("$.status").value("OPEN"));

    }

    @Test
    void givenInvalidComplaintRequest_whenCreateNewComplaint_thenReturnBadRequest() throws Exception {
    }

    @Test
    void givenValidComplaintId_whenDeleteComplaint_thenSetStatusCanceled() throws Exception {
    }

    @Test
    void givenInvalidComplaintId_whenDeleteComplaint_thenReturnNotFound() throws Exception {
    }


    @Test
    void test() throws Exception {
        mockMvc.perform(get("/api/v1/complaints")).andExpect(
                jsonPath("$.length()").value(10));
    }


    /*
    @Test
    fun `given invalid URL with invalid custom key - when post Link - then return Bad Request`() {
        val key = "?//key#//"
        val url = "?//invalidURL#//.com"
        val request = LinkRequest(url, key)
        val requestJson: String = jsonMapper.writeValueAsString(request)

        mockMvc.perform(
            post("/api/v1/shortener")
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isBadRequest,
            jsonPath("$.title").value(BAD_REQUEST.reasonPhrase),
            jsonPath("$.status").value(BAD_REQUEST.value()),
            jsonPath("$.detail").exists()
        )
    }
     */
}
