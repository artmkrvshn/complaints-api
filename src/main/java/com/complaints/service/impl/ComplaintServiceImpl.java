package com.complaints.service.impl;

import com.complaints.dto.request.ComplaintCreateRequest;
import com.complaints.dto.request.ComplaintUpdateRequest;
import com.complaints.dto.response.ComplaintResponse;
import com.complaints.dto.response.CustomerResponse;
import com.complaints.entity.Complaint;
import com.complaints.entity.Customer;
import com.complaints.entity.enums.Status;
import com.complaints.exception.EntityNotFoundException;
import com.complaints.exception.UnableToModifyException;
import com.complaints.repository.ComplaintRepository;
import com.complaints.security.UserDetailsImpl;
import com.complaints.service.ComplaintService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository repo;
    private final ModelMapper mapper;

    public ComplaintServiceImpl(ComplaintRepository repo, ModelMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public List<ComplaintResponse> findAll() {
        log.info("Getting all complaints");
        List<Complaint> complaints = repo.findAll();
        return complaints.stream()
                .map(this::map)
                .toList();
    }

    @Override
    public List<ComplaintResponse> findAll(PageRequest pageRequest) {
        log.info("Getting all complaints with pagination: {}", pageRequest);
        List<Complaint> complaints = repo.findAll(pageRequest).getContent();
        return complaints.stream()
                .map(this::map)
                .toList();
    }

    @Override
    public ComplaintResponse findById(Long id) {
        Complaint complaint = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Complaint with id " + id + " not found"));
        return this.map(complaint);
    }

    @Override
    public ComplaintResponse save(ComplaintCreateRequest request) {
        Complaint complaintToSave = this.map(request);
        Complaint savedComplaint = repo.save(complaintToSave);
        return this.map(savedComplaint);
    }

    @Override
    public void deleteById(Long id) {
        Complaint complaint = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Complaint with id " + id + " not found"));
        Customer authenticatedCustomer = getAuthenticatedCustomer();
        if (!complaint.getCustomer().getId().equals(authenticatedCustomer.getId())) {
            throw new AccessDeniedException("You do not have permission to modify this complaint.");
        }
        complaint.setStatus(Status.CANCELED);
        repo.save(complaint);
    }

    @Override
    public ComplaintResponse update(Long id, ComplaintUpdateRequest updateRequest) {
        Complaint complaintToUpdate = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Complaint with id " + id + " not found"));
        Customer authenticatedCustomer = getAuthenticatedCustomer();
        if (!complaintToUpdate.getCustomer().getId().equals(authenticatedCustomer.getId())) {
            throw new AccessDeniedException("You do not have permission to modify this complaint.");
        }

        Set<Status> modifiableStatuses = EnumSet.of(Status.OPEN, Status.IN_PROGRESS);
        Status currentStatus = complaintToUpdate.getStatus();
        if (!modifiableStatuses.contains(currentStatus)) {
            throw new UnableToModifyException("Cannot update complaint with status " + currentStatus + ".");
        }

        complaintToUpdate = this.map(updateRequest);

        Complaint complaint = repo.save(complaintToUpdate);
        return this.map(complaint);
    }

    private ComplaintResponse map(Complaint complaint) {
        ComplaintResponse dto = mapper.map(complaint, ComplaintResponse.class);
        CustomerResponse customerResponse = mapper.map(complaint.getCustomer(), CustomerResponse.class);
        dto.setCustomer(customerResponse);
        return dto;
    }

    private Complaint map(ComplaintCreateRequest request) {
        Complaint complaint = new Complaint();
        complaint.setProductId(request.getProductId());
        complaint.setCustomer(getAuthenticatedCustomer());
        complaint.setDate(request.getDate());
        complaint.setDescription(request.getDescription());
        complaint.setStatus(request.getStatus());
        return complaint;
    }

    private Complaint map(ComplaintUpdateRequest request) {
        Complaint complaint = new Complaint();
        complaint.setProductId(request.getProductId());
        complaint.setCustomer(getAuthenticatedCustomer());
        complaint.setDescription(request.getDescription());
        complaint.setStatus(request.getStatus());
        return complaint;
    }

    private Customer getAuthenticatedCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.customer();
    }
}
