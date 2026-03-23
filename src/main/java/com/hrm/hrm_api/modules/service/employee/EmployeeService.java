package com.hrm.hrm_api.modules.service.employee;

import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.base.SelectOptionDto;
import com.hrm.hrm_api.modules.dto.request.EmployeeSearchRequest;
import com.hrm.hrm_api.modules.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import com.hrm.hrm_api.modules.dto.response.KnownFaceUploadResponseDto;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

public interface EmployeeService {
    Page<Employee> findAll(Pageable pageable);
    Employee findById(UUID id);
    Employee create(Employee entity);
    Employee update(UUID id, Employee entity);
    void delete(UUID id);
    PaginationResponse<Employee> search(EmployeeSearchRequest request);
    List<SelectOptionDto> getSelectOptions();
    
    /**
     * Upload known face images for an employee to Python API
     * @param employeeId Employee ID
     * @param files List of image files to upload
     * @return Upload response from Python API
     */
    KnownFaceUploadResponseDto uploadKnownFaces(String employeeId, List<MultipartFile> files);
}
