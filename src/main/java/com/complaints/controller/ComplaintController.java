package com.complaints.controller;

import com.complaints.dto.request.ComplaintCreateRequest;
import com.complaints.dto.request.ComplaintUpdateRequest;
import com.complaints.dto.response.ComplaintResponse;
import com.complaints.service.ComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/complaints", produces = "application/json")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @Operation(summary = "Get all complaints", description = "Returns a list of all complaints.")
    @ApiResponse(responseCode = "200", description = "Successful operation, returns a list of complaints", content = @Content(schema = @Schema(implementation = ComplaintResponse.class)))
    @GetMapping()
    public ResponseEntity<List<ComplaintResponse>> getComplaints(@RequestParam(value = "page", required = false) Integer page,
                                                                 @RequestParam(value = "size", required = false) Integer size,
                                                                 @RequestParam(value = "sort", required = false) String sort) {
        if (page == null || size == null || sort == null || sort.isBlank()) {
            List<ComplaintResponse> complaints = complaintService.findAll();
            return ResponseEntity.ok(complaints);
        }
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sort));
        List<ComplaintResponse> complaints = complaintService.findAll(pageRequest);
        return ResponseEntity.ok(complaints);
    }

    @Operation(summary = "Get complaint by ID", description = "Returns a single complaint based on the provided ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Complaint found and returned", content = @Content(schema = @Schema(implementation = ComplaintResponse.class))),
            @ApiResponse(responseCode = "404", description = "Complaint not found", content = @Content(schema = @Schema(implementation = ComplaintResponse.class))),
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<ComplaintResponse> getComplaintById(@PathVariable("id") Long id) {
        ComplaintResponse complaintResponse = complaintService.findById(id);
        return ResponseEntity.ok(complaintResponse);
    }

    @Operation(summary = "Add a new complaint", description = "Creates a new complaint and returns the created object.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Complaint successfully created", content = @Content(schema = @Schema(implementation = ComplaintResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ComplaintResponse.class))),
            @ApiResponse(responseCode = "403", description = "Do not have permission to modify this complaint", content = @Content(schema = @Schema(implementation = ComplaintResponse.class))),
    })
    @PostMapping()
    public ResponseEntity<ComplaintResponse> addComplaint(@RequestBody @Valid ComplaintCreateRequest createRequest) {
        ComplaintResponse savedComplaintResponse = complaintService.save(createRequest);
        return new ResponseEntity<>(savedComplaintResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing complaint", description = "Updates the specified complaint with new data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Complaint successfully ", content = @Content(schema = @Schema(implementation = ComplaintResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ComplaintResponse.class))),
            @ApiResponse(responseCode = "403", description = "Do not have permission to modify this complaint", content = @Content(schema = @Schema(implementation = ComplaintResponse.class))),
    })
    @PutMapping("/{id}")
    public ResponseEntity<ComplaintResponse> updateComplaint(@PathVariable("id") Long id, @RequestBody @Valid ComplaintUpdateRequest updateRequest) {
        ComplaintResponse complaintResponse = complaintService.update(id, updateRequest);
        return ResponseEntity.ok(complaintResponse);
    }

    @Operation(summary = "Delete a complaint", description = "Deletes the complaint identified by the given ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Complaint successfully deleted", content = @Content(schema = @Schema(implementation = ComplaintResponse.class))),
            @ApiResponse(responseCode = "403", description = "Do not have permission to modify this complaint", content = @Content(schema = @Schema(implementation = ComplaintResponse.class))),
            @ApiResponse(responseCode = "404", description = "Complaint not found", content = @Content(schema = @Schema(implementation = ComplaintResponse.class))),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComplaint(@PathVariable("id") Long id) {
        complaintService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
