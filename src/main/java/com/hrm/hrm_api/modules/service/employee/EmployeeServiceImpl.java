package com.hrm.hrm_api.modules.service.employee;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.base.SelectOptionDto;
import com.hrm.hrm_api.modules.dto.request.EmployeeSearchRequest;
import com.hrm.hrm_api.modules.dto.response.KnownFaceUploadResponseDto;
import com.hrm.hrm_api.modules.dto.response.GetKnownFacesResponseDto;
import com.hrm.hrm_api.modules.entity.Employee;
import com.hrm.hrm_api.modules.entity.Department;
import com.hrm.hrm_api.modules.entity.Position;
import com.hrm.hrm_api.modules.repository.DepartmentRepository;
import com.hrm.hrm_api.modules.repository.EmployeeRepository;
import com.hrm.hrm_api.modules.repository.PositionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final RestTemplate restTemplate;

    @Value("${python-api.base-url:http://localhost:8000}")
    private String pythonApiBaseUrl;

    @Value("${python-api.endpoints.list-known-faces:/api/list}")
    private String listKnownFacesEndpoint;

    @Value("${python-api.endpoints.upload-known-faces:/api/add_known_face}")
    private String uploadKnownFacesEndpoint;

    public EmployeeServiceImpl(
            EmployeeRepository repository,
            DepartmentRepository departmentRepository,
            PositionRepository positionRepository,
            RestTemplate restTemplate
    ) {
        this.employeeRepository = repository;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public Page<Employee> findAll(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

    @Override
    public Employee findById(UUID id) {
        return employeeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Employee.class, id));
    }

    @Override
    @Transactional
    public Employee create(Employee entity) {
        Department resolvedDepartment = resolveDepartment(entity);
        if (resolvedDepartment != null) {
            entity.setDepartment(resolvedDepartment);
        }

        Position resolvedPosition = resolvePosition(entity);
        if (resolvedPosition != null) {
            entity.setPosition(resolvedPosition);
        }

        if (entity.getIsDeleted() == null) {
            entity.setIsDeleted(false);
        }
        return employeeRepository.save(entity);
    }

    @Override
    @Transactional
    public Employee update(UUID id, Employee entity) {
        employeeRepository.normalizeNullVersion(id);
        Employee existing = findById(id);

        existing.setFullName(entity.getFullName());
        existing.setEmployeeCode(entity.getEmployeeCode());
        existing.setEmail(entity.getEmail());
        existing.setPhone(entity.getPhone());
        existing.setEmergencyContact(entity.getEmergencyContact());
        existing.setEmergencyPhone(entity.getEmergencyPhone());
        existing.setSocialInsuranceNumber(entity.getSocialInsuranceNumber());
        existing.setHealthInsuranceNumber(entity.getHealthInsuranceNumber());
        existing.setAddress(entity.getAddress());
        existing.setGender(entity.getGender());
        existing.setDateOfBirth(entity.getDateOfBirth());
        existing.setHireDate(entity.getHireDate());
        existing.setNotes(entity.getNotes());

        Department resolvedDepartment = resolveDepartment(entity);
        if (resolvedDepartment != null) {
            existing.setDepartment(resolvedDepartment);
        } else if (entity.getDepartment() != null) {
            existing.setDepartment(entity.getDepartment());
        }

        Position resolvedPosition = resolvePosition(entity);
        if (resolvedPosition != null) {
            existing.setPosition(resolvedPosition);
        } else if (entity.getPosition() != null) {
            existing.setPosition(entity.getPosition());
        }

        existing.setTaxCode(entity.getTaxCode());
        existing.setBankAccount(entity.getBankAccount());
        existing.setBankName(entity.getBankName());
        existing.setStatus(entity.getStatus());
        existing.setOfficeLocation(entity.getOfficeLocation());

        if (entity.getIsDeleted() != null) {
            existing.setIsDeleted(entity.getIsDeleted());
        }

        return employeeRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException(Employee.class, id);
        }
        employeeRepository.deleteById(id);
    }

    @Override
    public PaginationResponse<Employee> search(EmployeeSearchRequest request) {
        if (request == null) {
            request = new EmployeeSearchRequest();
            request.setSkip(0);
            request.setTake(20);
        }

        int skip = request.getSkip() != null ? request.getSkip() : 0;
        int take = request.getTake() != null && request.getTake() > 0 ? request.getTake() : 20;

        if (skip < 0) skip = 0;
        if (take <= 0) take = 20;
        if (take > 100) take = 100;

        int pageNumber = skip / take;
        int pageSize = take;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "created_at"));

        var filter = request.getWhere();

        Page<Employee> employees = employeeRepository.findByFilters(
            filter != null ? filter.getFullName() : null,
            filter != null ? filter.getEmployeeCode() : null,
            filter != null ? filter.getIsDeleted() : null,
            pageable
        );

        return PaginationResponse.of(
            employees.getContent(),
            employees.getTotalElements(),
            pageNumber,
            pageSize
        );
    }

    @Override
    public List<SelectOptionDto> getSelectOptions() {
        List<Employee> employees = employeeRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc();
        return employees.stream()
                .map(employee -> new SelectOptionDto(employee.getId(), employee.getEmployeeCode() + " - " + employee.getFullName()))
                .collect(Collectors.toList());
    }

    private Department resolveDepartment(Employee entity) {
        if (entity.getDepartment() != null && entity.getDepartment().getId() != null) {
            return departmentRepository.findById(entity.getDepartment().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(Department.class, entity.getDepartment().getId()));
        }
        if (entity.getDepartmentId() != null) {
            return departmentRepository.findById(entity.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(Department.class, entity.getDepartmentId()));
        }
        return null;
    }

    private Position resolvePosition(Employee entity) {
        if (entity.getPosition() != null && entity.getPosition().getId() != null) {
            return positionRepository.findById(entity.getPosition().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(Position.class, entity.getPosition().getId()));
        }
        if (entity.getPositionId() != null) {
            return positionRepository.findById(entity.getPositionId())
                    .orElseThrow(() -> new ResourceNotFoundException(Position.class, entity.getPositionId()));
        }
        return null;
    }

    @Override
    public GetKnownFacesResponseDto getKnownFaces(String employeeId) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID is required");
        }

        try {
            // Call Python API to get known faces list
            String url = pythonApiBaseUrl + listKnownFacesEndpoint + "/" + employeeId;
            log.info("Calling Python API to get known faces for employee: {} - URL: {}", employeeId, url);

            ResponseEntity<GetKnownFacesResponseDto> response = restTemplate.getForEntity(
                    url,
                    GetKnownFacesResponseDto.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Successfully retrieved known faces for employee: {} - Total: {}", 
                        employeeId, response.getBody().getTotalImages());
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to get known faces: Invalid response from Python API");
            }

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                // Return empty list if no known faces found
                log.info("No known faces found for employee: {}", employeeId);
                return GetKnownFacesResponseDto.builder()
                        .status("success")
                        .employeeId(employeeId)
                        .totalImages(0)
                        .images(List.of())
                        .build();
            }
            log.error("HTTP error calling Python API for employee {}: {} - {}", 
                    employeeId, e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(
                    "Failed to get known faces: " + e.getMessage(),
                    e
            );
        } catch (Exception e) {
            log.error("Error retrieving known faces for employee {}: {}", employeeId, e.getMessage());
            // Return empty list on error instead of throwing
            log.info("Returning empty known faces list for employee: {} due to error: {}", employeeId, e.getMessage());
            return GetKnownFacesResponseDto.builder()
                    .status("success")
                    .employeeId(employeeId)
                    .totalImages(0)
                    .images(List.of())
                    .build();
        }
    }

    @Override
    public KnownFaceUploadResponseDto uploadKnownFaces(String employeeId, List<MultipartFile> files) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID is required");
        }

        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("At least one file is required");
        }

        try {
            // Build multipart request
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("employee_id", employeeId);

            // Add all files
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    body.add("files", new ByteArrayResource(file.getBytes()) {
                        @Override
                        public String getFilename() {
                            return file.getOriginalFilename();
                        }
                    });
                }
            }

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            // Call Python API
            String url = pythonApiBaseUrl + uploadKnownFacesEndpoint;
            log.info("Calling Python API: {} for employee: {} with {} files", url, employeeId, files.size());

            ResponseEntity<KnownFaceUploadResponseDto> response = restTemplate.postForEntity(
                    url,
                    request,
                    KnownFaceUploadResponseDto.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Successfully uploaded known faces for employee: {} - Status: {}", 
                        employeeId, response.getBody().getStatus());
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to upload known faces: Invalid response from Python API");
            }

        } catch (HttpClientErrorException e) {
            log.error("HTTP error calling Python API for employee {}: {} - {}", 
                    employeeId, e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(
                    "Failed to upload known faces: " + e.getMessage(),
                    e
            );
        } catch (java.io.IOException e) {
            log.error("IO error while processing files for employee {}: {}", employeeId, e.getMessage());
            throw new RuntimeException(
                    "Failed to process uploaded files: " + e.getMessage(),
                    e
            );
        } catch (Exception e) {
            log.error("Unexpected error uploading known faces for employee {}: {}", employeeId, e.getMessage());
            throw new RuntimeException(
                    "Failed to upload known faces: " + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public void deleteKnownFace(String employeeId, String filename) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID is required");
        }

        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename is required");
        }

        try {
            // Build URL
            String url = pythonApiBaseUrl  + "/api/" + employeeId + "/" + filename;
            log.info("Calling Python API to delete known face for employee: {} - filename: {} - URL: {}", 
                    employeeId, filename, url);

            restTemplate.delete(url);

            log.info("Successfully deleted known face image for employee: {} - filename: {}", 
                    employeeId, filename);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                log.warn("Image not found for employee {}: {}", employeeId, filename);
                throw new RuntimeException("Image not found");
            }
            log.error("HTTP error calling Python API for employee {}: {} - {}", 
                    employeeId, e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(
                    "Failed to delete known face: " + e.getMessage(),
                    e
            );
        } catch (Exception e) {
            log.error("Error deleting known face for employee {}: {}", employeeId, e.getMessage());
            throw new RuntimeException(
                    "Failed to delete known face: " + e.getMessage(),
                    e
            );
        }
    }
}
