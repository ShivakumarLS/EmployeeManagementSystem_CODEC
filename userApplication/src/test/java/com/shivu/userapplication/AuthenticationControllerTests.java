package com.shivu.userapplication;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import com.shivu.userapplication.service.AuthenticationService;
import com.shivu.userapplication.repository.UserRepository;
import com.shivu.userapplication.repository.RoleRepository;
import com.shivu.userapplication.repository.DepartmentRepository;
import com.shivu.userapplication.model.LoginResponseDTO;
import com.shivu.userapplication.model.ApplicationUser;
import com.shivu.userapplication.model.RegistrationDTO;
import com.shivu.userapplication.model.Department;
import com.shivu.userapplication.model.Role;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTests {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	AuthenticationService authenticationService;

	@MockBean
	UserRepository userRepository;

	@MockBean
	RoleRepository roleRepository;

	@MockBean
	DepartmentRepository departmentRepository;

	@Test
	public void testLogin_Success() throws Exception {
		LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
		ApplicationUser user = new ApplicationUser();
		user.setUsername("admin");
		user.setPassword("password");
		user.setDepartment(new Department("IT"));
		user.setAuthorities(Set.of(new Role(1, "ADMIN")));
		loginResponseDTO.setUser(user);
		loginResponseDTO.setJwt("mock-jwt-token");

		when(authenticationService.loginUser("admin", "password")).thenReturn(loginResponseDTO);

		String requestBody = "{\"username\": \"admin\", \"password\": \"password\"}";

		mockMvc.perform(MockMvcRequestBuilders.post("/auth/loginb")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.username").value("admin"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.jwt").value("mock-jwt-token"));
	}

	@Test
	public void testRegister_Success() throws Exception {
		RegistrationDTO regDto = new RegistrationDTO("testuser", "password", "test@email.com");
		regDto.setDepartment("IT");
		ApplicationUser user = new ApplicationUser();
		user.setUsername("testuser");
		user.setDepartment(new Department("IT"));
		user.setAuthorities(Set.of(new Role(1, "USER")));

		when(userRepository.findByUsername("testuser")).thenReturn(java.util.Optional.empty());
		when(userRepository.findByEmail("test@email.com")).thenReturn(null);
		when(authenticationService.registerUser(anyString(), anyString(), anyString(), anyString())).thenReturn(user);

		String requestBody = "{\"username\": \"testuser\", \"password\": \"password\",\"email\":\"test@email.com\", \"department\":\"IT\"}";

		mockMvc.perform(MockMvcRequestBuilders.post("/auth/registerb")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.username").value("testuser"));
	}

	@Test
	public void testRegister_UserAlreadyExists() throws Exception {
		when(userRepository.findByUsername("existinguser")).thenReturn(java.util.Optional.of(new ApplicationUser()));

		String requestBody = "{\"username\": \"existinguser\", \"password\": \"password\",\"email\":\"test@email.com\"}";

		mockMvc.perform(MockMvcRequestBuilders.post("/auth/registerb")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isInternalServerError()); // Global handler returns 500 for
																					// UserAlreadyExists
	}
}
