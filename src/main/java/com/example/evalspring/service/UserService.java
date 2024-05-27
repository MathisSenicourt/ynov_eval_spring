package com.example.evalspring.service;

import com.example.evalspring.dto.UserDTO;
import com.example.evalspring.dto.UserPrivateDTO;
import com.example.evalspring.exception.DataClownException;
import com.example.evalspring.exception.ObjectNotFoundException;
import com.example.evalspring.model.User;
import com.example.evalspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserDTO findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("User not found with id " + id));
        return convertToDTO(user);
    }

    public UserDTO createUser(UserPrivateDTO userPrivateDTO) {
        if (userRepository.findByEmail(userPrivateDTO.getEmail()).isPresent()) {
            throw new DataClownException("Email already exists");
        }
        User user = convertToPrivateEntity(userPrivateDTO);
        user = userRepository.save(user);
        return convertToDTO(user);
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("User not found with id " + id));

        if (!existingUser.getEmail().equals(userDTO.getEmail()) && userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new DataClownException("Email already exists");
        }

        existingUser.setName(userDTO.getName());
        existingUser.setEmail(userDTO.getEmail());
        existingUser = userRepository.save(existingUser);
        return convertToDTO(existingUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ObjectNotFoundException("User not found with id " + id);
        }
        userRepository.deleteById(id);
    }

    public List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private User convertToEntity(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        return user;
    }

    private User convertToPrivateEntity(UserPrivateDTO userPrivateDTO) {
        User user = new User();
        user.setName(userPrivateDTO.getName());
        user.setEmail(userPrivateDTO.getEmail());
        user.setPassword(userPrivateDTO.getPassword());
        return user;
    }

    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }
}
