package com.poc.repository;

public interface EmployeeDocumentRepository
extends JpaRepository<EmployeeDocument, Long> {

    List<EmployeeDocument>
       findByEmployeeId(Long employeeId);
}