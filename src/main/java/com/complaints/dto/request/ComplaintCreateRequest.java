package com.complaints.dto.request;

import com.complaints.entity.enums.Status;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ComplaintCreateRequest {

    @NotNull(message = "Product ID cannot be null")
    @Positive(message = "Product ID must be a positive number")
    private Long productId;

    @NotNull(message = "Date cannot be null")
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate date;

    @NotBlank(message = "Description should not be empty")
    private String description;

    @NotNull(message = "Status cannot be null")
    private Status status;

}
