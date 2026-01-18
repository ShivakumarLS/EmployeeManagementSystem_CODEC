package com.shivu.userapplication.service;

import com.shivu.userapplication.exception.ResourceNotFoundException;
import com.shivu.userapplication.exception.UserAlreadyExistsException;

import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.shivu.userapplication.model.ApplicationUser;
import com.shivu.userapplication.model.Department;
import com.shivu.userapplication.model.LoginResponseDTO;
import com.shivu.userapplication.model.Role;
import com.shivu.userapplication.repository.DepartmentRepository;
import com.shivu.userapplication.repository.RoleRepository;
import com.shivu.userapplication.repository.UserRepository;

@Service
@Transactional
public class AuthenticationService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TokenService tokenService;

	@Autowired
	DepartmentRepository departmentRepository;

	public ApplicationUser registerUser(String username, String password, String email, String departmentName) {
		if (userRepository.findByUsername(username).isPresent()) {
			throw new UserAlreadyExistsException("User with username " + username + " already exists");
		}

		String encodedPassword = passwordEncoder.encode(password);
		Role userRole = roleRepository.findFirstByAuthority("USER")
				.orElseThrow(() -> new ResourceNotFoundException("Default role 'USER' not found"));
		Role generalRole = roleRepository.findFirstByAuthority("GENERAL")
				.orElseThrow(() -> new ResourceNotFoundException("Default role 'GENERAL' not found"));

		Department userDepartment;
		if (departmentName != null && !departmentName.isEmpty()) {
			userDepartment = departmentRepository.findFirstByDepartmentName(departmentName)
					.orElseGet(() -> {
						Department newDept = new Department(departmentName);
						return departmentRepository.save(newDept);
					});
		} else {
			userDepartment = departmentRepository.findFirstByDepartmentName("USER")
					.orElseGet(() -> {
						Department newDept = new Department(2, "USER");
						return departmentRepository.save(newDept);
					});
		}

		Set<Role> authorities = new HashSet<>();
		authorities.add(userRole);
		authorities.add((generalRole));
		String resetPasswordToken = null;
		// Status is PENDING by default via constructor logic or field initialization,
		// assuming ApplicationUser sets it to PENDING if not specified,
		// or we allow standard constructor which sets status to pending/null.
		// The original code passed 0 as ID.
		return userRepository
				.save(new ApplicationUser(0, username, encodedPassword, authorities, userDepartment, email,
						resetPasswordToken));
	}

	public LoginResponseDTO loginUser(String username, String password) {

		try {
			Authentication auth = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			String token = tokenService.generateJwt(auth);
			System.out.println(userRepository.findByUsername(username).get().getDepartment().getDepartmentName());
			return new LoginResponseDTO(userRepository.findByUsername(username).get(), token);

		} catch (AuthenticationException e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials!");
		}
	}
}