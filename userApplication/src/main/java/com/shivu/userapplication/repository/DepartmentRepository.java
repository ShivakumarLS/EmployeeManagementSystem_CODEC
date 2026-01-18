package com.shivu.userapplication.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shivu.userapplication.model.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

   // HashSet<ApplicationUser> findUsersByDepartment(int departmentId) throws
   // UserNotFoundException;

   Optional<Department> findFirstByDepartmentName(String departmentName);

   Optional<Department> findByDepartmentName(String departmentName);
}
