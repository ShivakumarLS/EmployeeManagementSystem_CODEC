package com.shivu.userapplication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import com.shivu.userapplication.controller.AdminController;
import com.shivu.userapplication.model.ApplicationUser;
import com.shivu.userapplication.model.Department;
import com.shivu.userapplication.model.Role;
import com.shivu.userapplication.repository.RoleRepository;
import com.shivu.userapplication.repository.UserRepository;
import com.shivu.userapplication.exception.ResourceNotFoundException;
import com.shivu.userapplication.exception.UserNotFoundException;

@SpringBootTest
public class AdminControllerTests {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    private AdminController adminController;

    @Mock
    RoleRepository roleRepository;

    @SuppressWarnings("deprecation")
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testHelloAdminController() throws Exception {

        AdminController mockAdmin = mock(AdminController.class);
        when(mockAdmin.helloAdminController()).thenReturn("Admin level access");
        assertEquals("Admin level access", adminController.helloAdminController());
    }

    @Test
    public void testGetUsers() throws Exception {
        List<ApplicationUser> users = new ArrayList<>();
        Department dept = new Department(0, "mockdept");
        Role role = new Role(1, "mockrole");
        Set<Role> roles = new HashSet<>(Set.of(role));
        ApplicationUser user1 = new ApplicationUser("mockname1", "mockpass", roles,
                dept, "mockemail@gmail.com", null);
        ApplicationUser user2 = new ApplicationUser("mockname2", "mockpass", roles,
                dept, "mockemail@gmail.com", null);
        ApplicationUser user3 = new ApplicationUser("mockname3", "mockpass", roles,
                dept, "mockemail@gmail.com", null);
        users.add(user1);
        users.add(user2);
        users.add(user3);
        when(userRepository.findAll()).thenReturn(users);
        List<ApplicationUser> actual = adminController.getUsers();
        assertEquals(users, actual);
    }

    @Test
    public void testGetUsersByDepartment() throws Exception {
        Department dept = new Department(0, "mockdept");
        Role role = new Role(1, "mockrole");
        Set<Role> roles = new HashSet<>(Set.of(role));
        ApplicationUser user1 = new ApplicationUser("mockname1", "mockpass", roles,
                dept, "mockemail@gmail.com", null);
        ApplicationUser user2 = new ApplicationUser("mockname2", "mockpass", roles,
                dept, "mockemail@gmail.com", null);
        ApplicationUser user3 = new ApplicationUser("mockname3", "mockpass", roles,
                dept, "mockemail@gmail.com", null);
        ArrayList<ApplicationUser> users = new ArrayList<>(List.of(user1, user2, user3));
        when(userRepository.findAllByDepartmentName(anyString())).thenReturn(users);
        assertEquals(users, adminController.getUsersByDepartment("department"));
    }

    @Test
    public void testGetUserById() throws Exception {

        Department dept = new Department(0, "mockdept");
        Role role = new Role(1, "mockrole");
        Set<Role> mockRole = new HashSet<>(Set.of(role));
        ApplicationUser mockUser = mock(ApplicationUser.class);
        ApplicationUser user = new ApplicationUser("mockname", "mockpass",
                mockRole, dept, "mockemail@gmail.com", null);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        ApplicationUser actual = adminController.getuserById("mockname");
        assertEquals(user, actual);
        assertNotEquals(user, mockUser);
    }

    @Test
    public void testDeleteUserById_UserNotFound() {
        when(userRepository.findByUsername("nonexistentusername")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> adminController.deleteUserById("nonexistentusername"));
    }

    @Test
    public void testDeleteUserById() throws Exception {
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(new ApplicationUser()));
        Boolean actual = adminController.deleteUserById("username");
        assertTrue(actual);
    }

    @Test
    public void testDeleteUsers() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            adminController.deleteUsers();
        });
        assertEquals("No users record found to delete", exception.getMessage());
    }

    @Test
    public void testUpdate() throws Exception {
        ApplicationUser user = mock(ApplicationUser.class);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        ApplicationUser actual = adminController.update("username", user);
        assertEquals(user, actual);
    }

}
