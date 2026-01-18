package com.shivu.userapplication.service;

import com.shivu.userapplication.exception.ResourceNotFoundException;
import com.shivu.userapplication.exception.UserAlreadyExistsException;
import com.shivu.userapplication.exception.UserNotFoundException;

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
		// No roles assigned initially. User must be approved by admin.

		String resetPasswordToken = null;
		ApplicationUser newUser = new ApplicationUser(0, username, encodedPassword, authorities, userDepartment, email,
				resetPasswordToken);
		newUser.setStatus(ApplicationUser.UserStatus.PENDING);
		return userRepository.save(newUser);
	}

	public LoginResponseDTO loginUser(String username, String password) {

		try {
			Authentication auth = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(username, password));

			ApplicationUser user = userRepository.findByUsername(username)
					.orElseThrow(() -> new UserNotFoundException("User not found"));

			if (user.getStatus() == ApplicationUser.UserStatus.PENDING) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account is awaiting admin approval");
			} else if (user.getStatus() == ApplicationUser.UserStatus.REJECTED) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account has been rejected");
			}

			String token = tokenService.generateJwt(auth);
			System.out.println(user.getDepartment().getDepartmentName());
			return new LoginResponseDTO(user, token);

		} catch (AuthenticationException e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials!");
		}
	}
}