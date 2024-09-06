package com.example.carpentry.Service.StartUp;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.carpentry.Enum.Roles;
import com.example.carpentry.Model.Users.Role;
import com.example.carpentry.Model.Users.Users;
import com.example.carpentry.Repository.UsersRepository.RoleRepository;
import com.example.carpentry.Repository.UsersRepository.UsersRepository;

@Component
public class OnApplicationStart implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (roleRepository.count() == 0) {
            Role newRoleAdmin = new Role(Roles.ROLE_ADMIN);
            Role newRoleUser = new Role(Roles.ROLE_USER);
            Role newRoleMod = new Role(Roles.ROLE_MODERATOR);
            roleRepository.save(newRoleAdmin);
            roleRepository.save(newRoleUser);
            roleRepository.save(newRoleMod);
        }

        if (usersRepository.count() == 0) {
            Set<Role> roles = new HashSet<>();
            Role adminRole = roleRepository.findByName(Roles.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(adminRole);
            Users user = new Users("JestemAdmin", passwordEncoder.encode("Admin123!@"));
            user.setRoles(roles);
            usersRepository.save(user);
        }
    }
}
