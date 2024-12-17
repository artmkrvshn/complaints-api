package com.complaints.dto.response;

import lombok.Data;

@Data
public class CustomerResponse {

    private String email;
    private String name;

    public CustomerResponse() {
    }

    public CustomerResponse(String email, String name) {
        this.email = email;
        this.name = name;
    }
}
