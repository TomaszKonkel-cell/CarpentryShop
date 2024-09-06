package com.example.carpentry.Services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.carpentry.Enum.Roles;
import com.example.carpentry.Model.Users.Role;
import com.example.carpentry.Model.Users.Users;
import com.example.carpentry.Repository.UsersRepository.RoleRepository;
import com.example.carpentry.Repository.UsersRepository.UsersRepository;
import com.example.carpentry.Request.SignUpRequest;
import com.example.carpentry.Service.Users.UsersServiceImpl;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsersServiceTests {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @InjectMocks
    private UsersServiceImpl usersService;

    private Users user;
    private SignUpRequest userData = new SignUpRequest();

    private Set<String> rolesStrEmpty = new HashSet<>();
    private Set<String> rolesStrMultiple = new HashSet<>();
    private Set<Role> listOfRole = new HashSet<>();

    Optional<Role> roleUser;
    Optional<Role> roleAdmin;
    Optional<Role> roleMod;

    @BeforeEach
    public void setup() {
        user = new Users("JestemKonkel", "Admin123!@");
        user.setRoles(listOfRole);

        userData.setUsername("JestemKonkel");
        userData.setPassword("Konkel123!@");

        rolesStrMultiple.add("admin");
        rolesStrMultiple.add("mod");

        roleUser = Optional.ofNullable(new Role(Roles.ROLE_USER));
        roleAdmin = Optional.ofNullable(new Role(Roles.ROLE_ADMIN));
        roleMod = Optional.ofNullable(new Role(Roles.ROLE_MODERATOR));

        listOfRole.add(roleAdmin.get());
        listOfRole.add(roleMod.get());

    }

    @Test
    @Order(1)
    public void createUser_withDefaultRole_thenCreateUserWithDefaultRole() {
        userData.setRoles(rolesStrEmpty);
        given(roleRepository.findByName(Roles.ROLE_USER)).willReturn(roleUser);

        ResponseEntity<?> response = usersService.createUser(userData);
        Users createdUser = (Users) response.getBody();

        assertThat(response.getStatusCode().toString()).isEqualTo("201 CREATED");
        assertThat(createdUser.getRoles()).isEqualTo(Set.of(roleUser.get()));
    }

    @Test
    @Order(2)
    public void createUser_withMultipleRoles_thenCreateUserWithSetOfRoles() {
        userData.setRoles(rolesStrMultiple);
        given(roleRepository.findByName(Roles.ROLE_ADMIN)).willReturn(roleAdmin);
        given(roleRepository.findByName(Roles.ROLE_MODERATOR)).willReturn(roleMod);

        ResponseEntity<?> response = usersService.createUser(userData);
        Users createdUser = (Users) response.getBody();

        assertThat(response.getStatusCode().toString()).isEqualTo("201 CREATED");
        assertThat(createdUser.getRoles()).isEqualTo(Set.of(roleAdmin.get(), roleMod.get()));
    }

    @Test
    @Order(3)
    public void createUser_withExistUsername_thenResponseIsBadRequestWithCustomMessage() {
        given(usersRepository.findByUsername("JestemKonkel")).willReturn(Optional.of(user));

        ResponseEntity<?> response = usersService.createUser(userData);

        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(response.getBody()).isEqualTo("Użytkownik o podanej nazwie już istnieje");
    }

    @Test
    @Order(3)
    public void createUser_thenCheckThatPasswordIsEncoded() {
        given(roleRepository.findByName(Roles.ROLE_USER)).willReturn(roleUser);
        given(passwordEncoder.encode(userData.getPassword())).willReturn(encoder.encode(userData.getPassword()));

        Users createdUser = (Users) usersService.createUser(userData).getBody();

        assertThat(createdUser.getPassword()).contains("$2a$10");
        assertThat(passwordEncoder.matches(createdUser.getPassword(), userData.getPassword()));
    }

    @Test
    @Order(4)
    public void changeUsername_WithGoodParameters_thenChangeUsernameAndStatusIsOK() {
        given(usersRepository.findById(user.getId())).willReturn(Optional.of(user));

        ResponseEntity<?> response = usersService.changeUsername("JestemKonkelNew", user.getId());

        assertThat(user.getUsername()).isEqualTo("JestemKonkelNew");
        assertThat(response.getBody()).isEqualTo("Nazwa zmieniona pomyślnie na: JestemKonkelNew");
        assertThat(response.getStatusCode().toString()).isEqualTo("200 OK");

    }

    @Test
    @Order(5)
    public void changeUsername_WithBadUserId_thenThrowExceptionWithCustomMessage() throws Exception {

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> {
                    usersService.changeUsername("JestemKonkelNew", user.getId());
                });

        assertThat(ex.getMessage()).isEqualTo("Nie znaleziono uzytkownika");

    }

    @Test
    @Order(6)
    public void changeUsername_WithRepeatedUsername_thenResponseIsBadRequestWithCustomMessage() {
        given(usersRepository.findById(user.getId())).willReturn(Optional.of(user));

        ResponseEntity<?> response = usersService.changeUsername("JestemKonkel", user.getId());

        assertThat(response.getBody()).isEqualTo("Podane nazwy są takie same");
        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");

    }

    @Test
    @Order(7)
    public void changeUsername_WithEmptyUsername_thenResponseIsBadRequestWithCustomMessage() {
        given(usersRepository.findById(user.getId())).willReturn(Optional.of(user));

        ResponseEntity<?> response = usersService.changeUsername("", user.getId());

        assertThat(response.getBody()).isEqualTo("Nazwa nie może być pusta");
        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");

    }

    @Test
    @Order(8)
    public void changeUsername_WithAnotherUserUsername_thenResponseIsBadRequestWithCustomMessage() {
        given(usersRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(usersRepository.findByUsername(any())).willReturn(Optional.of(new Users("JestemAdmin", "Admin123!@")));

        ResponseEntity<?> response = usersService.changeUsername("JestemAdmin", user.getId());

        assertThat(response.getBody()).isEqualTo("Taka nazwa już istnieje");
        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");

    }

    @Test
    @Order(9)
    public void changePassword_WithBadUserId_thenThrowExceptionWithCustomMessage() throws Exception {

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> {
                    usersService.changePassword(user.getPassword(), "Konkelone123!@", user.getId());
                });

        assertThat(ex.getMessage()).isEqualTo("Nie znaleziono uzytkownika");

    }

    @Test
    @Order(10)
    public void changePassword_WithEmptyConfirmPassOrNewPass_thenResponseIsBadRequestWithCustomMessage() {
        given(usersRepository.findById(user.getId())).willReturn(Optional.of(user));

        String responseStatusCodeAsString = usersService.changePassword("","", user.getId()).getStatusCode().toString();
        ResponseEntity<?> responseEmptyAll = usersService.changePassword("","", user.getId());
        ResponseEntity<?> responseEmptyConfirmOldPass = usersService.changePassword("","Konkelone123!@", user.getId());
        ResponseEntity<?> responseEmptyNewPass = usersService.changePassword("Admin123!@","", user.getId());

        assertThat(responseEmptyAll.getBody()).isEqualTo("Wszystkie pola muszą być uzupełnione");
        assertThat(responseEmptyConfirmOldPass.getBody()).isEqualTo("Wszystkie pola muszą być uzupełnione");
        assertThat(responseEmptyNewPass.getBody()).isEqualTo("Wszystkie pola muszą być uzupełnione");
        assertThat(responseStatusCodeAsString).isEqualTo("400 BAD_REQUEST");

    }

    @Test
    @Order(11)
    public void changePassword_WithBadConfirmOldPass_thenResponseIsBadRequestWithCustomMessage() {
        given(usersRepository.findById(user.getId())).willReturn(Optional.of(user));

        ResponseEntity<?> response = usersService.changePassword("Admin123","Konkelone123!@", user.getId());
 
        assertThat(response.getBody()).isEqualTo("Podano błędne stare hasło");
        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");

    }

    @Test
    @Order(12)
    public void changePassword_WithNewPassEqualToConfirmOldPass_thenResponseIsBadRequestWithCustomMessage() {
        given(usersRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(passwordEncoder.matches("Admin123!@", user.getPassword())).willReturn(true);

        ResponseEntity<?> response = usersService.changePassword("Admin123!@","Admin123!@", user.getId());

        assertThat(response.getBody()).isEqualTo("Hasła są takie same");
        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");

    }

    @Test
    @Order(13)
    public void changePassword_WithAllGoodParameters_thenResponseIsOKWithCustomMessage() {
        String originalPassword = user.getPassword();
        given(usersRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(passwordEncoder.matches("Admin123!@", user.getPassword())).willReturn(true);

        ResponseEntity<?> response = usersService.changePassword("Admin123!@","Konkelone123!@", user.getId());

        assertThat(originalPassword).isNotEqualTo(user.getPassword());
        assertThat(response.getBody()).isEqualTo("Hasło zmienione pomyślnie");
        assertThat(response.getStatusCode().toString()).isEqualTo("200 OK");

    }

    @Test
    @Order(14)
    public void newRoles_shouldFromOriginalSetDeleteGivenRole() {

    Set<Role> newRole = usersService.newRoles(listOfRole, "ROLE_MODERATOR");

    assertThat(newRole).contains(roleAdmin.get());
    assertThat(newRole.size()).isEqualTo(1);
    }

    @Test
    @Order(15)
    public void deleteRole_WithBadUserId_thenThrowExceptionWithCustomMessage() throws Exception {

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> {
                    usersService.deleteRole(user.getId(), "ROLE_MODERATOR");
                });

        assertThat(ex.getMessage()).isEqualTo("Nie znaleziono uzytkownika");

    }

    @Test
    @Order(16)
    public void deleteRole_WithNotExistingRole_thenResponseIsBadRequestWithCustomMessage() {
        given(usersRepository.findById(user.getId())).willReturn(Optional.of(user));

        ResponseEntity<?> response = usersService.deleteRole(user.getId(), "ROLE_NOTHING");

        assertThat(response.getBody()).isEqualTo("Rola w zapytaniu nie pasuje do istniejących ról");
        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");

    }

    @Test
    @Order(17)
    public void deleteRole_WithNotMatchingRoleForUser_thenResponseIsBadRequestWithCustomMessage() {
        given(usersRepository.findById(user.getId())).willReturn(Optional.of(user));

        ResponseEntity<?> response = usersService.deleteRole(user.getId(), "ROLE_USER");
 
        assertThat(response.getBody()).isEqualTo("Użytkownik nie posiada takiej roli");
        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");

    }

    @Test
    @Order(18)
    public void deleteRole_WithGoodData_thenResponseIsOKWithCustomMessage() {
        Set<Role> originalRoles = new HashSet<>();
        originalRoles.add(roleAdmin.get());
        originalRoles.add(roleMod.get());
        given(usersRepository.findById(user.getId())).willReturn(Optional.of(user));

        ResponseEntity<?> response = usersService.deleteRole(user.getId(), "ROLE_ADMIN");

        assertThat(originalRoles).isNotEqualTo(user.getRoles());
        assertThat(response.getBody()).isEqualTo("Rola została usunięta");
        assertThat(response.getStatusCode().toString()).isEqualTo("200 OK");

    }

    @Test
    @Order(19)
    public void deleteRole_WithOnlyOneRoleForUser_thenResponseIsBadRequestWithCustomMessage() {
        listOfRole.removeAll(listOfRole);
        listOfRole.add(roleAdmin.get());
        given(usersRepository.findById(user.getId())).willReturn(Optional.of(user));

        ResponseEntity<?> response = usersService.deleteRole(user.getId(), "ROLE_ADMIN");
 
        assertThat(response.getBody()).isEqualTo("Użytkownik musi mięc conajmniej jedną role");
        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");

    }

    @Test
    @Order(20)
    public void deleteUser_WithBadUserId_thenResponseIsBadRequestWithCustomMessage() {

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> {
                    usersService.deleteUser(user.getId());
                });

        assertThat(ex.getMessage()).isEqualTo("Nie znaleziono uzytkownika");

    }

    @Test
    @Order(21)
    public void deleteUser_WithGoodData_thenResponseIsOKWithCustomMessage() {
        given(usersRepository.findById(user.getId())).willReturn(Optional.of(user));

        ResponseEntity<?> response = usersService.deleteUser(user.getId());
 
        assertThat(response.getBody()).isEqualTo("Użytkownik został usunięty");
        assertThat(response.getStatusCode().toString()).isEqualTo("200 OK");

    }

}
