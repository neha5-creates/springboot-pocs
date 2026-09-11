package com.poc.service;

import org.springframework.stereotype.Service;

import com.poc.repository.EmployeeDocumentRepository;

@Service
public class EmployeeDocumentService {

    private final
    EmployeeDocumentRepository repository;

    private final
    FileStorageService fileStorageService;

    public EmployeeDocumentService(

         EmployeeDocumentRepository repository,

         FileStorageService
             fileStorageService) {

        this.repository = repository;
        this.fileStorageService =
                fileStorageService;
    }

    

}