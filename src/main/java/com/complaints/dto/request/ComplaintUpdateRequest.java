package com.complaints.dto.request;

import com.complaints.entity.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ComplaintUpdateRequest {

    @NotNull(message = "Product ID cannot be null")
    @Positive(message = "Product ID must be a positive number")
    private Long productId;

    @NotBlank(message = "Description should not be empty")
    private String description;

    @NotNull(message = "Status cannot be null")
    private Status status;

}