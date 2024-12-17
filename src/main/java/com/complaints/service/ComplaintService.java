package com.complaints.service;

import com.complaints.dto.request.ComplaintCreateRequest;
import com.complaints.dto.request.ComplaintUpdateRequest;
import com.complaints.dto.response.ComplaintResponse;

import java.util.List;

public interface ComplaintService {

    List<ComplaintResponse> findAll();

    ComplaintResponse findById(Long id);

    ComplaintResponse save(ComplaintCreateRequest request);

    void deleteById(Long id);

    ComplaintResponse update(Long id, ComplaintUpdateRequest complaintResponse);

}
