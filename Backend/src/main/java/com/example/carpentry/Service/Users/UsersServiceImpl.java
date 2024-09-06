package com.example.carpentry.Service.Users;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.carpentry.Enum.Roles;
import com.example.carpentry.Jwt.JwtUtils;
import com.example.carpentry.Model.Users.Role;
import com.example.carpentry.Model.Users.Users;
import com.example.carpentry.Repository.UsersRepository.RoleRepository;
import com.example.carpentry.Repository.UsersRepository.UsersRepository;
import com.example.carpentry.Request.LoggedUser;
import com.example.carpentry.Request.SignUpRequest;
import com.example.carpentry.Security.UserDetails.UserDetailsImplementation;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public Collection<Users> getAll() {
        return usersRepository.findAll();
    }

    @Override
    public Optional<Users> getUser(Long id) {
        return usersRepository.findById(id);
    }

    @Override
    public Optional<Users> getByName(String username) {
        return usersRepository.findByUsername(username);
    }

    @Override
    public ResponseEntity<?> createUser(SignUpRequest signUpRequest) {
        if (getByName(signUpRequest.getUsername()).isPresent()) {
            return new ResponseEntity<>("Użytkownik o podanej nazwie już istnieje", HttpStatus.BAD_REQUEST);
        }
        try {
            Users user = new Users(signUpRequest.getUsername(), passwordEncoder.encode(signUpRequest.getPassword()));

            Set<Role> roles = new HashSet<>();
            if (signUpRequest.getRoles().isEmpty()) {
                Role userRole = roleRepository.findByName(Roles.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(userRole);
            } else {
                signUpRequest.getRoles().forEach(role -> {
                    switch (role) {
                        case "admin":
                            Role adminRole = roleRepository.findByName(Roles.ROLE_ADMIN)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(adminRole);

                            break;
                        case "mod":
                            Role modRole = roleRepository.findByName(Roles.ROLE_MODERATOR)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(modRole);

                            break;
                        default:
                            Role userRole = roleRepository.findByName(Roles.ROLE_USER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(userRole);
                    }
                });
            }
            user.setRoles(roles);
            usersRepository.save(user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> loginUser(Users user) {
        if (!getByName(user.getUsername()).isPresent()) {
            return new ResponseEntity<>("Użytkownik o podanej nazwie nie istnieje", HttpStatus.BAD_REQUEST);
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImplementation userDetails = (UserDetailsImplementation) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        String jwtCookie = jwtUtils.generateJwtToken(userDetails);

        LoggedUser loggedUser = new LoggedUser(userDetails.getId(), userDetails.getUsername(), roles,
                System.currentTimeMillis(), jwtCookie);

        return new ResponseEntity<>(loggedUser, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> changeUsername(String newUsername, Long id) {
        Users findUser = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono uzytkownika"));
        if (findUser.getUsername().equals(newUsername)) {
            return new ResponseEntity<>("Podane nazwy są takie same", HttpStatus.BAD_REQUEST);
        }

        if (newUsername.isEmpty()) {
            return new ResponseEntity<>("Nazwa nie może być pusta", HttpStatus.BAD_REQUEST);
        }

        if (usersRepository.findByUsername(newUsername).isPresent()) {
            return new ResponseEntity<>("Taka nazwa już istnieje", HttpStatus.BAD_REQUEST);
        }

        findUser.setUsername(newUsername);
        usersRepository.save(findUser);
        return new ResponseEntity<>("Nazwa zmieniona pomyślnie na: " + findUser.getUsername(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> changePassword(String confirmOldPass, String newPass, Long id) {
        Users findUser = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono uzytkownika"));
        if (confirmOldPass.isEmpty() || newPass.isEmpty()) {
            return new ResponseEntity<>("Wszystkie pola muszą być uzupełnione", HttpStatus.BAD_REQUEST);
        }
        if (!checkConfirmPassword(confirmOldPass, findUser.getPassword())) {
            return new ResponseEntity<>("Podano błędne stare hasło", HttpStatus.BAD_REQUEST);
        }
        if (confirmOldPass.equals(newPass)) {
            return new ResponseEntity<>("Hasła są takie same", HttpStatus.BAD_REQUEST);
        }

        String encodedNewPass = passwordEncoder.encode(newPass);
        findUser.setPassword(encodedNewPass);
        usersRepository.save(findUser);
        return new ResponseEntity<>("Hasło zmienione pomyślnie", HttpStatus.OK);

    }

    public boolean checkConfirmPassword(String confirmOldPass, String oldPass) {
        if (!passwordEncoder.matches(confirmOldPass, oldPass)) {
            return false;
        } else {
            return true;
        }

    }

    @Override
    public ResponseEntity<?> deleteRole(Long id, String role) {
        Users findUser = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono uzytkownika"));
        Set<Role> roleList = findUser.getRoles();
        Set<String> availableRoleString = Set.of("ROLE_USER", "ROLE_MODERATOR", "ROLE_ADMIN");
        boolean result = roleList.stream().anyMatch(userRole -> userRole.getName().toString().equals(role));


        if (roleList.size() > 1) {
            if (!availableRoleString.contains(role)) {
                return new ResponseEntity<>("Rola w zapytaniu nie pasuje do istniejących ról", HttpStatus.BAD_REQUEST);
            }
            if (!result) {
                return new ResponseEntity<>("Użytkownik nie posiada takiej roli", HttpStatus.BAD_REQUEST);
            }
            findUser.setRoles(newRoles(roleList, role));
            usersRepository.save(findUser);
            return new ResponseEntity<>("Rola została usunięta", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Użytkownik musi mięc conajmniej jedną role", HttpStatus.BAD_REQUEST);
        }

    }

    public Set<Role> newRoles(Set<Role> tempRoleList, String role) {
        List<Role> tempList = new ArrayList<>();
        tempRoleList.forEach(userRole -> {
            if (!userRole.getName().toString().equals(role)) {
                tempList.add(userRole);
            }
        });

        tempRoleList.removeAll(tempRoleList);
        tempList.forEach(userRole -> {
            tempRoleList.add(userRole);
        });
        return tempRoleList;
    }

    @Override
    public ResponseEntity<?> deleteUser(Long id) {
        Users findUser = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono uzytkownika"));
        usersRepository.deleteById(findUser.getId());
        return new ResponseEntity<>("Użytkownik został usunięty", HttpStatus.OK);

    }
}
