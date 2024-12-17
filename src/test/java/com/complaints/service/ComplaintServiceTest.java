package com.complaints.service;

import com.complaints.dto.request.ComplaintCreateRequest;
import com.complaints.dto.request.ComplaintUpdateRequest;
import com.complaints.dto.response.ComplaintResponse;
import com.complaints.entity.Complaint;
import com.complaints.entity.Customer;
import com.complaints.entity.enums.Status;
import com.complaints.exception.EntityNotFoundException;
import com.complaints.exception.UnableToModifyException;
import com.complaints.repository.ComplaintRepository;
import com.complaints.security.UserDetailsImpl;
import com.complaints.service.impl.ComplaintServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class ComplaintServiceTest {

    @Mock
    private ComplaintRepository complaintRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private ComplaintServiceImpl complaintService;

    private Customer customer;
    private Complaint complaint;
    private ComplaintResponse complaintResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        customer = new Customer();
        customer.setId(1L);
        customer.setEmail("test@example.com");

        when(userDetails.customer()).thenReturn(customer);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        complaint = new Complaint();
        complaint.setId(1L);
        complaint.setProductId(100L);
        complaint.setCustomer(customer);
        complaint.setDescription("Test complaint");
        complaint.setDate(LocalDate.now());
        complaint.setStatus(Status.OPEN);

        complaintResponse = new ComplaintResponse();
        complaintResponse.setId(1L);
        complaintResponse.setDescription("Test complaint");
    }

    @Test
    void testFindAll() {
        when(complaintRepository.findAll()).thenReturn(List.of(complaint));
        when(modelMapper.map(complaint, ComplaintResponse.class)).thenReturn(complaintResponse);

        List<ComplaintResponse> result = complaintService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("Test complaint");

        verify(complaintRepository, times(1)).findAll();
    }

    @Test
    void testFindAllWithPagination() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        when(complaintRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(List.of(complaint)));
        when(modelMapper.map(complaint, ComplaintResponse.class)).thenReturn(complaintResponse);

        List<ComplaintResponse> result = complaintService.findAll(pageRequest);

        assertThat(result).hasSize(1);
        verify(complaintRepository, times(1)).findAll(pageRequest);
    }

    @Test
    void testFindById() {
        when(complaintRepository.findById(1L)).thenReturn(Optional.of(complaint));
        when(modelMapper.map(complaint, ComplaintResponse.class)).thenReturn(complaintResponse);

        ComplaintResponse result = complaintService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        verify(complaintRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(complaintRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> complaintService.findById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Complaint with id 1 not found");
    }

    @Test
    void testSaveComplaint() {
        ComplaintCreateRequest request = new ComplaintCreateRequest();
        request.setProductId(100L);
        request.setDescription("New complaint");
        request.setDate(LocalDate.now());
        request.setStatus(Status.OPEN);

        when(complaintRepository.save(any(Complaint.class))).thenReturn(complaint);
        when(modelMapper.map(complaint, ComplaintResponse.class)).thenReturn(complaintResponse);

        ComplaintResponse result = complaintService.save(request);

        assertThat(result.getId()).isEqualTo(1L);
        verify(complaintRepository, times(1)).save(any(Complaint.class));
    }

    @Test
    void testDeleteById_WithAccessDenied() {
        Customer anotherCustomer = new Customer();
        anotherCustomer.setId(2L);
        complaint.setCustomer(anotherCustomer);

        when(complaintRepository.findById(1L)).thenReturn(Optional.of(complaint));

        assertThatThrownBy(() -> complaintService.deleteById(1L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("You do not have permission");

        verify(complaintRepository, times(1)).findById(1L);
        verify(complaintRepository, never()).save(any());
    }

    @Test
    void testUpdateComplaint_WithInvalidStatus() {
        ComplaintUpdateRequest updateRequest = new ComplaintUpdateRequest();
        updateRequest.setStatus(Status.CANCELED);

        complaint.setStatus(Status.CANCELED);

        when(complaintRepository.findById(1L)).thenReturn(Optional.of(complaint));

        assertThatThrownBy(() -> complaintService.update(1L, updateRequest))
                .isInstanceOf(UnableToModifyException.class)
                .hasMessageContaining("Cannot update complaint");

        verify(complaintRepository, times(1)).findById(1L);
    }

}
