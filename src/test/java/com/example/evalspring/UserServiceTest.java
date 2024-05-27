package com.example.evalspring;

import com.example.evalspring.dto.UserDTO;
import com.example.evalspring.dto.UserPrivateDTO;
import com.example.evalspring.exception.DataClownException;
import com.example.evalspring.exception.ObjectNotFoundException;
import com.example.evalspring.model.User;
import com.example.evalspring.repository.UserRepository;
import com.example.evalspring.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ObjectNotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    void testFindById_Success() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDTO userDTO = userService.findById(1L);

        assertNotNull(userDTO);
        assertEquals(1L, userDTO.getId());
        assertEquals("John Doe", userDTO.getName());
        assertEquals("john@example.com", userDTO.getEmail());
    }

    @Test
    void testCreateUser_EmailAlreadyExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
        UserPrivateDTO userDTO = new UserPrivateDTO();
        userDTO.setEmail("john@example.com");

        assertThrows(DataClownException.class, () -> userService.createUser(userDTO));
    }

    @Test
    void testCreateUser_Success() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserPrivateDTO userDTO = new UserPrivateDTO();
        userDTO.setName("John Doe");
        userDTO.setEmail("john@example.com");
        userDTO.setPassword("password");

        UserDTO createdUser = userService.createUser(userDTO);

        assertNotNull(createdUser);
        assertEquals(1L, createdUser.getId());
        assertEquals("John Doe", createdUser.getName());
        assertEquals("john@example.com", createdUser.getEmail());
    }

    @Test
    void testUpdateUser_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("john@example.com");

        assertThrows(ObjectNotFoundException.class, () -> userService.updateUser(1L, userDTO));
    }

    @Test
    void testUpdateUser_EmailAlreadyExists() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("John Doe");
        existingUser.setEmail("john@example.com");

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setName("Jane Doe");
        anotherUser.setEmail("jane@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(anotherUser));

        UserDTO userDTO = new UserDTO();
        userDTO.setName("Jane Doe");
        userDTO.setEmail("jane@example.com");

        assertThrows(DataClownException.class, () -> userService.updateUser(1L, userDTO));
    }

    @Test
    void testUpdateUser_Success() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("John Doe");
        existingUser.setEmail("john@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserDTO userDTO = new UserDTO();
        userDTO.setName("John Doe Updated");
        userDTO.setEmail("john_updated@example.com");

        UserDTO updatedUser = userService.updateUser(1L, userDTO);

        assertNotNull(updatedUser);
        assertEquals(1L, updatedUser.getId());
        assertEquals("John Doe Updated", updatedUser.getName());
        assertEquals("john_updated@example.com", updatedUser.getEmail());
    }

    @Test
    void testDeleteUser_UserNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(ObjectNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(userRepository).deleteById(anyLong());

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }
}
