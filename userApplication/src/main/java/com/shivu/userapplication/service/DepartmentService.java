package com.shivu.userapplication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.shivu.userapplication.model.Department;
import com.shivu.userapplication.repository.DepartmentRepository;

@Service
public class DepartmentService {
    @Autowired
    DepartmentRepository departmentRepository;

    public void initialiseDepartments() {
        if (departmentRepository.findAll().size() == 8)
            return;
        departmentRepository.save(new Department("ADMIN"));
        departmentRepository.save(new Department("USER"));
        departmentRepository.save(new Department("PAYROLL"));
        departmentRepository.save(new Department("HR"));
        departmentRepository.save(new Department("FINANCE"));
        departmentRepository.save(new Department("SALES"));
        departmentRepository.save(new Department("GENERAL"));
        departmentRepository.save(new Department("IT"));

    }

    public Department getByDepartmentName(String departmentName) {
        return departmentRepository.findFirstByDepartmentName(departmentName).orElseThrow();
    }

}