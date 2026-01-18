package com.shivu.userapplication.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shivu.userapplication.model.ApplicationUser;
import com.shivu.userapplication.model.ApplicationUser.UserStatus;

import jakarta.annotation.PostConstruct;
import com.shivu.userapplication.model.Department;
import com.shivu.userapplication.model.Role;
import com.shivu.userapplication.repository.DepartmentRepository;
import com.shivu.userapplication.repository.RoleRepository;
import com.shivu.userapplication.repository.UserRepository;

@Service
public class RBACService {

        @Autowired
        UserRepository userRepository;

        @Autowired
        RoleRepository roleRepository;

        @Autowired
        DepartmentRepository departmentRepository;

        @Autowired
        org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

        @PostConstruct
        public void ensureAdminUsersAreActive() {
                // Fix for existing admin users who may have PENDING status
                userRepository.findAll().stream()
                                .filter(user -> user.getAuthorities().stream()
                                                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN")))
                                .filter(user -> user.getStatus() != UserStatus.ACTIVE)
                                .forEach(user -> {
                                        user.setStatus(UserStatus.ACTIVE);
                                        userRepository.save(user);
                                        System.out.println("Activated admin user: " + user.getUsername());
                                });

                // Ensure default users exist, have correct password, and are ACTIVE
                ensureDefaultUsersExist();
        }

        private void ensureDefaultUsersExist() {
                if (roleRepository.count() == 0 || departmentRepository.count() == 0) {
                        initialize(); // Ensure roles/depts exist first
                }

                String password = "password";

                // Create or Update Default Users
                createOrUpdateUser("testUser", "PAYROLL", "PAYROLL", password);

                createOrUpdateUser("payroll1", "PAYROLL", "PAYROLL", password);
                createOrUpdateUser("payroll2", "PAYROLL", "PAYROLL", password);

                createOrUpdateUser("hr1", "HR", "HR", password);
                createOrUpdateUser("hr2", "HR", "HR", password);

                createOrUpdateUser("finance1", "FINANCE", "FINANCE", password);
                createOrUpdateUser("finance2", "FINANCE", "FINANCE", password);

                createOrUpdateUser("sales1", "SALES", "SALES", password);
                createOrUpdateUser("sales2", "SALES", "SALES", password);

                createOrUpdateUser("it1", "IT", "IT", password);
                createOrUpdateUser("it2", "IT", "IT", password);
        }

        private void createOrUpdateUser(String username, String roleName, String deptName, String plainPassword) {
                // Check if user exists
                userRepository.findByUsername(username).ifPresentOrElse(
                                user -> {
                                        // Update User
                                        user.setPassword(passwordEncoder.encode(plainPassword));
                                        if (user.getStatus() != UserStatus.ACTIVE) {
                                                user.setStatus(UserStatus.ACTIVE);
                                        }
                                        userRepository.save(user);
                                        System.out.println("Updated default user: " + username);
                                },
                                () -> {
                                        // Create New User
                                        Department dept = departmentRepository.findFirstByDepartmentName(deptName)
                                                        .orElseGet(() -> departmentRepository
                                                                        .save(new Department(deptName)));

                                        Set<Role> roles = new HashSet<>();
                                        roleRepository.findFirstByAuthority(roleName).ifPresent(roles::add);
                                        roleRepository.findFirstByAuthority("GENERAL").ifPresent(roles::add);

                                        String encoded = passwordEncoder.encode(plainPassword);

                                        // Use constructor without ID to let DB generate unique ID
                                        ApplicationUser newUser = new ApplicationUser(
                                                        username,
                                                        encoded,
                                                        roles,
                                                        dept,
                                                        username + "@email.com",
                                                        null);
                                        newUser.setStatus(UserStatus.ACTIVE);
                                        userRepository.save(newUser);
                                        System.out.println("Created default user: " + username);
                                });
        }

        public void initialize() {
                // Only initialize if roles don't exist yet
                if (roleRepository.count() == 0) {
                        initialiseRoles();
                }
                if (departmentRepository.count() == 0) {
                        initialiseDepartments();
                }
        }

        public void initialiseRoles() {
                saveRoleIfNotExists("ADMIN");
                saveRoleIfNotExists("USER");
                saveRoleIfNotExists("PAYROLL");
                saveRoleIfNotExists("HR");
                saveRoleIfNotExists("FINANCE");
                saveRoleIfNotExists("SALES");
                saveRoleIfNotExists("GENERAL");
                saveRoleIfNotExists("IT");
        }

        private void saveRoleIfNotExists(String authority) {
                if (roleRepository.findFirstByAuthority(authority).isEmpty()) {
                        roleRepository.save(new Role(authority));
                }
        }

        public void initialiseDepartments() {
                saveDeptIfNotExists("ADMIN");
                saveDeptIfNotExists("USER");
                saveDeptIfNotExists("PAYROLL");
                saveDeptIfNotExists("HR");
                saveDeptIfNotExists("FINANCE");
                saveDeptIfNotExists("SALES");
                saveDeptIfNotExists("GENERAL");
                saveDeptIfNotExists("IT");
        }

        private void saveDeptIfNotExists(String deptName) {
                if (departmentRepository.findFirstByDepartmentName(deptName).isEmpty()) {
                        departmentRepository.save(new Department(deptName));
                }
        }

}
