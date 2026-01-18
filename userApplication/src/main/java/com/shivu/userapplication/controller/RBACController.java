package com.shivu.userapplication.controller;

import org.springframework.web.bind.annotation.RestController;
import com.shivu.userapplication.exception.UserNotFoundException;
import com.shivu.userapplication.model.ApplicationUser;
import com.shivu.userapplication.model.DisplayEmployees;
import com.shivu.userapplication.repository.DepartmentRepository;
import com.shivu.userapplication.repository.RoleRepository;
import com.shivu.userapplication.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class RBACController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @GetMapping("/hr")
    @PreAuthorize("hasRole('HR')")
    public String getHR() {
        return "HR level Access";
    }

    @GetMapping("/Payroll")
    @PreAuthorize("hasRole('PAYROLL')")
    public String getPayroll() {
        return "Payroll level Access";
    }

    @GetMapping("/finance")
    @PreAuthorize("hasRole('FINANCE')")
    public String getFinance() {
        return "Finance level Access";
    }

    @GetMapping("/sales")
    @PreAuthorize("hasRole('SALES')")
    public String getSales() {
        return "Sales level Access";
    }

    @GetMapping("/it")
    @PreAuthorize("hasRole('SALES')")
    public String getIT() {
        return "IT level Access";
    }

    @GetMapping("/getemailrecords")
    @PreAuthorize("hasRole('GENERAL')")
    public String getEmails() throws Exception {
        return "Email Records Access";
    }

    @GetMapping("/datacenter")
    @PreAuthorize("hasRole('IT')")
    public String getDataCenter() {
        return "Data Center Access";
    }

    @GetMapping("/getcustomerrecords")
    @PreAuthorize("hasAnyRole('FINANCE','SALES')")
    public String getCustomerRecords() {
        return "Customer Records Access";
    }

    @GetMapping("/SAP")
    @PreAuthorize("hasRole('SALES')")
    public String getSapRecords() {
        return "SAP Access";
    }

    @GetMapping("/timecards")
    @PreAuthorize("hasAnyRole('HR','PAYROLL')")
    public String getTimeCards() {
        return "timecards";
    }

    @GetMapping("/getemployeerecords")
    @PreAuthorize("hasAnyRole('HR','PAYROLL')")
    public List<DisplayEmployees> getEmployees() throws UserNotFoundException {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(userName);
        List<ApplicationUser> users = userRepository.findAll();

        List<DisplayEmployees> showUsers = users.stream()
                // Filter out users who ONLY have "USER" or "GENERAL" roles
                // i.e. Include users who have at least one role that is NOT "USER" and NOT
                // "GENERAL"
                .filter(user -> user.getAuthorities().stream()
                        .anyMatch(
                                role -> !role.getAuthority().equals("USER") && !role.getAuthority().equals("GENERAL")))
                .map(displayUser -> new DisplayEmployees(displayUser.getUsername(),
                        displayUser.getDepartment().getDepartmentName(),
                        displayUser.getAuthorities()))
                .collect(Collectors.toList());

        return showUsers;
    }

}
