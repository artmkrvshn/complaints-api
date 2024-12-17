package com.complaints.dto;

import java.util.Map;

public record ValidationErrorResponse(int status, String message, Map<String, String> validationErrors) {
}