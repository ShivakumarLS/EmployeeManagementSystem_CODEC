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

        @PostConstruct
        public void ensureAdminUsersAreActive() {
                // Fix for existing admin users who may have PENDING status
                // This ensures admin users can always log in
                userRepository.findAll().stream()
                                .filter(user -> user.getAuthorities().stream()
                                                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN")))
                                .filter(user -> user.getStatus() != UserStatus.ACTIVE)
                                .forEach(user -> {
                                        user.setStatus(UserStatus.ACTIVE);
                                        userRepository.save(user);
                                        System.out.println("Activated admin user: " + user.getUsername());
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
                // Only create test users if users table is empty
                if (userRepository.count() == 0) {
                        assignRolesToDepartments();
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

        public void assignRolesToDepartments() {
                List<Role> roles = new ArrayList<>();
                roles = roleRepository.findAll();
                List<Department> departments = new ArrayList<>();
                departments = departmentRepository.findAll();

                Set<Role> userRole = new HashSet<>();
                userRole.add(roles.get(1));

                Set<Role> payrollRole = new HashSet<>();
                payrollRole.add(roles.get(2));
                payrollRole.add(roles.get(6));

                Set<Role> hrROle = new HashSet<>();
                hrROle.add(roles.get(3));
                hrROle.add(roles.get(6));

                Set<Role> financeRole = new HashSet<>();
                financeRole.add(roles.get(4));
                financeRole.add(roles.get(6));

                Set<Role> salesRole = new HashSet<>();
                salesRole.add(roles.get(5));
                salesRole.add(roles.get(6));

                Set<Role> itRole = new HashSet<>();
                itRole.add(roles.get(7));
                itRole.add(roles.get(6));

                String password = "$2a$10$uS8T6TSRun9UocX9snIQIOd97Yr91jmPUOEepDgzJnqPRTVzcn10q";
                userRepository.save(new ApplicationUser(4, "testUser", password,
                                userRole, departments.get(2), "User@email.com", null));

                userRepository.save(new ApplicationUser(4, "payroll1", password,
                                payrollRole, departments.get(2), "payroll1@email.com", null));
                userRepository.save(new ApplicationUser(4, "payroll2", password,
                                payrollRole, departments.get(2), "payroll2@email.com", null));

                userRepository.save(new ApplicationUser(5, "hr1", password,
                                hrROle, departments.get(3), "hr1@email.com", null));
                userRepository.save(new ApplicationUser(6, "hr2", password,
                                hrROle, departments.get(3), "hr2@email.com", null));

                userRepository.save(new ApplicationUser(7, "finance1", password,
                                financeRole, departments.get(4), "finance1@email.com", null));
                userRepository.save(new ApplicationUser(8, "finance2", password,
                                financeRole, departments.get(4), "finance2@email.com", null));

                userRepository.save(new ApplicationUser(9, "sales1", password,
                                salesRole, departments.get(5), "sales1@email.com", null));
                userRepository.save(new ApplicationUser(10, "sales2", password,
                                salesRole, departments.get(5), "sales2@email.com", null));

                userRepository.save(new ApplicationUser(11, "it1", password,
                                itRole, departments.get(7), "it1@email.com", null));
                userRepository.save(new ApplicationUser(12, "it2", password,
                                itRole, departments.get(7), "it2@email.com", null));

        }

}
