package com.example.carpentry.Service.Users;

import java.util.Collection;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.example.carpentry.Model.Users.Users;
import com.example.carpentry.Request.SignUpRequest;

public interface UsersService {

    public Optional<Users> getByName(String username);

    public ResponseEntity<?> createUser(SignUpRequest signUpRequest);

    public ResponseEntity<?> loginUser(Users user);

    public Collection<Users> getAll();

    public Optional<Users> getUser(Long id);

    public ResponseEntity<?> deleteUser(Long id);

    public ResponseEntity<?> changeUsername(String username, Long id);

    public ResponseEntity<?> changePassword(String confirmPass, String newPass, Long id);

    public ResponseEntity<?> deleteRole(Long id, String role);

    

}
