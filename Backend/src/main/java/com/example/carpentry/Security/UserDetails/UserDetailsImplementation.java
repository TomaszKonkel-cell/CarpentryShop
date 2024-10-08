package com.example.carpentry.Security.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.carpentry.Model.Users.Users;

public class UserDetailsImplementation implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;
  
    private String username;

    private String password;
  
    private Collection<? extends GrantedAuthority> authorities;
  
    public UserDetailsImplementation(Long id, String username, String password,
    Collection<? extends GrantedAuthority> authorities) {
      this.id = id;
      this.username = username;
      this.password = password;
      this.authorities = authorities;
    }

    
  
    public static UserDetailsImplementation build(Users user) {
  
      List<GrantedAuthority> authorities = user.getRoles().stream()
        .map(role -> new SimpleGrantedAuthority(role.getName().name()))
        .collect(Collectors.toList());
        
      return new UserDetailsImplementation(
          user.getId(), 
          user.getUsername(),
          user.getPassword(),
          authorities);
    }
  
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      return authorities;
    }
  
    public Long getId() {
      return id;
    }

  
    @Override
    public String getPassword() {
      return password;
    }
  
    @Override
    public String getUsername() {
      return username;
    }
  
    @Override
    public boolean isAccountNonExpired() {
      return true;
    }
  
    @Override
    public boolean isAccountNonLocked() {
      return true;
    }
  
    @Override
    public boolean isCredentialsNonExpired() {
      return true;
    }
  
    @Override
    public boolean isEnabled() {
      return true;
    }
  
}
