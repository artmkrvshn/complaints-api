package com.complaints.dto.response;

import com.complaints.entity.enums.Status;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ComplaintResponse {

    private Long id;
    private Long productId;
    private CustomerResponse customer;
    private LocalDate date;
    private String description;
    private Status status;

    public ComplaintResponse() {
    }

    public ComplaintResponse(Long id, Long productId, CustomerResponse customer, LocalDate date, String description, Status status) {
        this.id = id;
        this.productId = productId;
        this.customer = customer;
        this.date = date;
        this.description = description;
        this.status = status;
    }
}
