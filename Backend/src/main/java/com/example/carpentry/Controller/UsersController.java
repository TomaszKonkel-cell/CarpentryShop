package com.example.carpentry.Controller;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.carpentry.Model.Users.Users;
import com.example.carpentry.Request.SignUpRequest;
import com.example.carpentry.Service.Users.UsersServiceImpl;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UsersController {

  @Autowired
  UsersServiceImpl usersService;


  @GetMapping("/get")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public Collection<Users> getAll() {
    return usersService.getAll();
  }

  @GetMapping("/details")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public Optional<Users> getDetails(@RequestParam Long id) {
    return usersService.getUser(id);
  }

  @PostMapping("/signup")
  public ResponseEntity<?> signUpUser(@Valid @ModelAttribute SignUpRequest signUpRequest) {
    return usersService.createUser(signUpRequest);
  }

  @PostMapping("/signin")
  public ResponseEntity<?> loginUser(@Valid @ModelAttribute Users user) {
    return usersService.loginUser(user);
  }

  @PutMapping("/changeUsername")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<?> changeUsername(@RequestParam String username, @RequestParam Long id) {
    return usersService.changeUsername(username, id);
  }

  @PutMapping("/changePassword")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<?> changePassword(@RequestParam String oldPass, @RequestParam String newPass, @RequestParam Long id) {
    return usersService.changePassword(oldPass, newPass, id);
  }

  @DeleteMapping("/deleteRole")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<?> deleteRole(@RequestParam Long id, @RequestParam String role) {
    return usersService.deleteRole(id, role);
  }

  @DeleteMapping("/delete")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<?> deleteUser(@RequestParam Long id) {
    return usersService.deleteUser(id);

  }

}
