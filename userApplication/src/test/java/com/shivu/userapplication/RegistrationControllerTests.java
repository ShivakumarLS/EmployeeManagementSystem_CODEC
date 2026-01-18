package com.shivu.userapplication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.Model;

import com.shivu.userapplication.controller.RegistrationController;
import com.shivu.userapplication.model.ApplicationUser;
import com.shivu.userapplication.model.RegistrationDTO;
import com.shivu.userapplication.repository.UserRepository;
import com.shivu.userapplication.service.AuthenticationService;

@SpringBootTest
public class RegistrationControllerTests {
    @InjectMocks
    private RegistrationController registrationController;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @SuppressWarnings("deprecation")
    @BeforeEach
    public void setUp() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testShowRegistrationForm() {
        Model model = mock(Model.class);
        String result = registrationController.showRegistrationForm(model);
        assertEquals("registration", result);

        verify(model).addAttribute(eq("RegistrationDTOform"), any(RegistrationDTO.class));
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        Model model = mock(Model.class);
        RegistrationDTO user = new RegistrationDTO("username", "password", "email");

        when(userRepository.findByUsername("username")).thenReturn(java.util.Optional.empty());

        String result = registrationController.registerUser(user, model);

        assertEquals("redirect:/auth/register?success", result);
        verify(authenticationService).registerUser("username", "password", "email", null);
    }

    @Test
    public void testRegisterUser_UserExists() throws Exception {
        Model model = mock(Model.class);
        RegistrationDTO user = new RegistrationDTO("username", "password", "email");

        when(userRepository.findByUsername("username")).thenReturn(java.util.Optional.of(new ApplicationUser()));

        String result = registrationController.registerUser(user, model);

        assertEquals("redirect:/auth/register?userpresent", result);
        verify(authenticationService, never()).registerUser(anyString(), anyString(), anyString(), any());

    }
}
