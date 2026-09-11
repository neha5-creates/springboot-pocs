package com.poc.poc.controller;

import com.poc.poc.entity.Employee;
import com.poc.poc.service.EmployeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/employees")
@Tag(name = "Employee API",

description = "Employee Management APIs")
public class EmployeeController {
@Autowired
private EmployeeService service;

@Operation(summary = "Create Employee")
@PostMapping
public Employee createEmployee(

@Valid @RequestBody Employee employee) {
return service.save(employee); 
}
@Operation(summary = "Get All Employees")
@GetMapping("/employees")
public List<Employee> getAllEmployees() {
return service.findAll();
}

@GetMapping
public List<Employee> getAllEmployee() {
return service.getAllEmployee();
}


@GetMapping("/{id}")
public Employee getById(@PathVariable Long id) {
return service.getById(id);
}

}