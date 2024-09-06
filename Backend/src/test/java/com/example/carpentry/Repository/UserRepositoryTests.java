
package com.example.carpentry.Repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import com.example.carpentry.Enum.Roles;
import com.example.carpentry.Model.Users.Role;
import com.example.carpentry.Model.Users.Users;
import com.example.carpentry.Repository.UsersRepository.RoleRepository;
import com.example.carpentry.Repository.UsersRepository.UsersRepository;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserRepositoryTests {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RoleRepository roleRepository;

    public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
    }

    @BeforeEach
    public void setup() {

        if (roleRepository.count() == 0) {
            Role newRoleAdmin = new Role(Roles.ROLE_ADMIN);
            Role newRoleUser = new Role(Roles.ROLE_USER);
            Role newRoleMod = new Role(Roles.ROLE_MODERATOR);
            roleRepository.save(newRoleAdmin);
            roleRepository.save(newRoleUser);
            roleRepository.save(newRoleMod);
        }

    }

    @Test
    @Order(1)
    @Rollback(value = false)
    public void saveUserTest() {
        Set<Role> roles = new HashSet<>();
        Role adminRole = roleRepository.findByName(Roles.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(adminRole);
        Users user = new Users("JestemAdmin", passwordEncoder().encode("Admin123!@"));
        user.setRoles(roles);
        usersRepository.save(user);

        assertThat(user.getId()).isGreaterThan(0);
    }

    @Test
    @Order(2)
    public void getUserByIdTest() {
        Users findUser = usersRepository.findById(1L).get();

        assertThat(findUser.getId()).isEqualTo(1L);
    }

    @Test
    @Order(3)
    public void getUserByUsernameTest() {
        Users findUser = usersRepository.findByUsername("JestemAdmin").get();

        assertThat(findUser.getUsername()).isEqualTo("JestemAdmin");
    }

    @Test
    @Order(4)
    public void getListOfUsersTest() {
        Collection<Users> userList = usersRepository.findAll();

        assertThat(userList.size()).isGreaterThan(0);

    }

    @Test
    @Order(5)
    @Rollback(value = false)
    public void updateUserTest() {
        Users findUserById = usersRepository.findById(1L).get();
        findUserById.setUsername("JestemKonkel");
        Users userUpdated = usersRepository.save(findUserById);

        assertThat(userUpdated.getUsername()).isEqualTo("JestemKonkel");

    }

    @Test
    @Order(6)
    @Rollback(value = false)
    public void deleteUserTest() {
        usersRepository.deleteById(1L);
        Optional<Users> userOptional = usersRepository.findById(1L);

        assertThat(userOptional).isEmpty();
    }

}
