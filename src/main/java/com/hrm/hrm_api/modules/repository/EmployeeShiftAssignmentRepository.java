package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.EmployeeShiftAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeShiftAssignmentRepository extends JpaRepository<EmployeeShiftAssignment, UUID>, JpaSpecificationExecutor<EmployeeShiftAssignment> {
    
    /**
     * Find the active shift assignment for an employee on a specific date
     * @param employeeId Employee ID
     * @param date The date to check
     * @return The active shift assignment if found
     */
    @Query(value = "SELECT * FROM employee_shift_assignments " +
            "WHERE employee_id = :employeeId " +
            "AND start_date <= :date " +
            "AND (end_date IS NULL OR end_date >= :date) " +
            "LIMIT 1", nativeQuery = true)
    Optional<EmployeeShiftAssignment> findActiveAssignmentOnDate(
            @Param("employeeId") UUID employeeId,
            @Param("date") LocalDate date);

    /**
     * Find all employee assignments for a specific shift
     * @param shiftId Shift ID
     * @return List of employee shift assignments
     */
    List<EmployeeShiftAssignment> findByShiftId(UUID shiftId);
}