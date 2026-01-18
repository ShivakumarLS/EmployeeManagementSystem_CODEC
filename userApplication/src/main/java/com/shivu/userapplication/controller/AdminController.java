package com.shivu.userapplication.controller;

import com.shivu.userapplication.exception.ResourceNotFoundException;
import com.shivu.userapplication.exception.UserNotFoundException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shivu.userapplication.model.ApplicationUser;
import com.shivu.userapplication.model.ApplicationUser.UserStatus;
import com.shivu.userapplication.model.Department;
import com.shivu.userapplication.model.DisplayEmployees;
import com.shivu.userapplication.model.Role;
import com.shivu.userapplication.repository.DepartmentRepository;
import com.shivu.userapplication.repository.RoleRepository;
import com.shivu.userapplication.repository.UserRepository;

@RestController
@RequestMapping("/admin")
@CrossOrigin("*")
public class AdminController {

	@GetMapping("/")
	public String helloAdminController() {
		return "Admin level access";
	}

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private RoleRepository roleRepository;

	@GetMapping("/getusers")
	public List<DisplayEmployees> getusers() {
		List<ApplicationUser> users = userRepository.findByStatus(UserStatus.ACTIVE);
		List<DisplayEmployees> showUsers = users.stream()
				.map(displayUser -> new DisplayEmployees(displayUser.getUsername(),
						displayUser.getDepartment().getDepartmentName(), displayUser.getAuthorities()))
				.collect(Collectors.toList());
		return showUsers;
	}

	@GetMapping("/getpendingusers")
	public List<Map<String, Object>> getPendingUsers() {
		List<ApplicationUser> users = userRepository.findByStatus(UserStatus.PENDING);
		return users.stream().map(u -> {
			Map<String, Object> map = new java.util.HashMap<>();
			map.put("username", u.getUsername());
			map.put("email", u.getEmail() != null ? u.getEmail() : "");
			map.put("department", u.getDepartment() != null ? u.getDepartment().getDepartmentName() : "N/A");
			map.put("status", u.getStatus().toString());
			return map;
		}).collect(Collectors.toList());
	}

	@PostMapping("/approve/{uname}")
	public Map<String, Object> approveUser(@PathVariable("uname") String username,
			@RequestBody Map<String, List<String>> body) {
		ApplicationUser user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User not found: " + username));

		// Assign roles from request
		List<String> roleNames = body.get("roles");
		if (roleNames != null && !roleNames.isEmpty()) {
			Set<Role> roles = new HashSet<>();
			for (String roleName : roleNames) {
				roleRepository.findFirstByAuthority(roleName).ifPresent(roles::add);
			}
			if (!roles.isEmpty()) {
				user.setAuthorities(roles);
			}
		}

		// Assign department if provided
		String deptName = body.containsKey("department") && body.get("department") != null
				&& !body.get("department").isEmpty()
						? body.get("department").get(0)
						: null;
		if (deptName != null) {
			Department dept = departmentRepository.findFirstByDepartmentName(deptName).orElse(null);
			if (dept == null) {
				dept = new Department();
				dept.setDepartmentName(deptName);
				departmentRepository.save(dept);
			}
			user.setDepartment(dept);
		}

		user.setStatus(UserStatus.ACTIVE);
		userRepository.save(user);

		return Map.of("success", true, "message", "User approved successfully");
	}

	@PostMapping("/reject/{uname}")
	public Map<String, Object> rejectUser(@PathVariable("uname") String username) {
		ApplicationUser user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User not found: " + username));
		user.setStatus(UserStatus.REJECTED);
		userRepository.save(user);
		return Map.of("success", true, "message", "User rejected");
	}

	@GetMapping("/getroles")
	public List<String> getAllRoles() {
		return roleRepository.findAll().stream()
				.map(Role::getAuthority)
				.collect(Collectors.toList());
	}

	public List<ApplicationUser> getUsers() {
		List<ApplicationUser> users = new ArrayList<>();
		users = userRepository.findAll();
		return users;
	}

	@GetMapping("/getusers/{dept}")
	public List<ApplicationUser> getUsersByDepartment(@PathVariable("dept") String department) {
		ArrayList<ApplicationUser> users = userRepository.findAllByDepartmentName(department);
		return users;
	}

	@GetMapping("/getdepartments")
	public Set<String> getDepartments() {
		Set<String> departments = departmentRepository.findAll()
				.stream().map(u -> u.getDepartmentName()).collect(Collectors.toUnmodifiableSet());
		return departments;
	}

	@GetMapping("/getuser/{uname}")
	public ApplicationUser getuserById(@PathVariable("uname") String uname) {
		String uName = uname;
		Optional<ApplicationUser> user = userRepository.findByUsername(uName);
		return user.orElseThrow(() -> new UserNotFoundException("user not found with username: " + uname));
	}

	@DeleteMapping("/delete/{uname}")
	public Boolean deleteUserById(@PathVariable("uname") String uname) {
		ApplicationUser user = userRepository.findByUsername(uname)
				.orElseThrow(() -> new UserNotFoundException("no user with given username" + uname));
		userRepository.deleteByUsername(uname);
		return true;
	}

	@DeleteMapping("/deleteall")
	public Boolean deleteUsers() {
		List<ApplicationUser> users = userRepository.findAll();
		if (!users.isEmpty()) {
			userRepository.deleteAll();
			return true;
		} else
			throw new ResourceNotFoundException("No users record found to delete");
	}

	@PutMapping("/update/{uname}")
	public ApplicationUser update(@PathVariable("uname") String userName, @RequestBody ApplicationUser user) {
		ApplicationUser existingUser = userRepository.findByUsername(userName)
				.orElseThrow(() -> new UserNotFoundException("User with username " + userName + " does not exist"));

		if (user.getUsername() != null) {
			existingUser.setUsername(user.getUsername());
			userRepository.save(existingUser);
			return existingUser;
		}
		return existingUser;
	}
}
