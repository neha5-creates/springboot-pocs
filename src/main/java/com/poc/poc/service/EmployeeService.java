package com.poc.poc.service;
import com.poc.poc.repository.EmployeeRepository;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.poc.poc.entity.Employee;
import com.poc.poc.exception.ResourceNotFoundException;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository repository;

    public Employee save(Employee employee) {
        return repository.save(employee);
    }

    public List<Employee> findAll() {
        return repository.findAll();
    }

    public List<Employee> getAllEmployee() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllEmployee'");
    }

    // public Employee getById(Long id) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'getById'");
    // }

    public Employee getById(Long id) {

    return repository.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Employee not found with id "
                    + id));
}

  
}