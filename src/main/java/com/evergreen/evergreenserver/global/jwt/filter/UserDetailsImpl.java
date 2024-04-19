package com.evergreen.evergreenserver.global.jwt.filter;


import com.evergreen.evergreenserver.domain.user.entity.User;
import java.util.Collection;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailsImpl implements UserDetails {

  @Getter
  private final User user;

  public UserDetailsImpl(User user) {
    this.user = user;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
//    UserRoleEnum role = user.getRole();
//    String authority = role.getAuthority();
//
//    SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
//    Collection<GrantedAuthority> authorities = new ArrayList<>();
//    authorities.add(simpleGrantedAuthority);

    return null;
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getLoginId();
  }

  @Override
  public boolean isAccountNonExpired() {
    return false;
  }

  @Override
  public boolean isAccountNonLocked() {
    return false;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return false;
  }

  @Override
  public boolean isEnabled() {
    return false;
  }
}