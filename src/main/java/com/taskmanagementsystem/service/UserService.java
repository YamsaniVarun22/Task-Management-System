package com.taskmanagementsystem.service;

import com.taskmanagementsystem.dto.UserDto;
import com.taskmanagementsystem.exception.UserAlreadyExistException;
import com.taskmanagementsystem.exception.UserNotFoundException;
import com.taskmanagementsystem.mapper.UserMapper;
import com.taskmanagementsystem.model.User;
import com.taskmanagementsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String saveUser(User user){
        boolean exists = userRepository.existsByUserName(user.getUserName());
        if(exists){
            throw new UserAlreadyExistException("User already exist with this username : "+ user.getUserName());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User createdUser = userRepository.save(user);
        return "User " + createdUser.getUserName() + " registered successfully.";
    }

    public UserDto getUserByUserId(long id){
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with userId : " + id));
        return UserMapper.toDTO(user);
    }

    public UserDto getUserByUserName(String userName) {
        User user = userRepository.findByUserName(userName);
        if (user == null) {
            throw new UserNotFoundException("User not found with the username : " + userName);
        }
        return UserMapper.toDTO(user);
    }

    public List<UserDto> getAllUsers(){
        return UserMapper.toDTOList(userRepository.findAll());
    }

    public UserDto updateUser(long id, User inputUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with userId : " + id));
        if (userRepository.existsByUserName(inputUser.getUserName())
                && !user.getUserName().equals(inputUser.getUserName())) {
            throw new UserAlreadyExistException("Username already exists.");
        }
        user.setUserName(inputUser.getUserName());
        user.setRole(inputUser.getRole());
        user.setPassword(passwordEncoder.encode(inputUser.getPassword()));

        return UserMapper.toDTO(userRepository.save(user));
    }

    public String deleteUser(long id){

        if(!userRepository.existsById(id)){
            throw new UserNotFoundException("User not found with userId : " + id);
        }
        userRepository.deleteById(id);
        return "Successfully removed user";
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUserName())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
