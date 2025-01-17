package com.itsuda.perfume.security;

import com.itsuda.perfume.domain.type.EProvider;
import com.itsuda.perfume.domain.type.ERole;
import com.itsuda.perfume.repository.UserRepository;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
@Setter(AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomUserDetails implements OAuth2User, UserDetails {
    private final Long id;
    private final EProvider provider;
    private final String password;
    private final ERole role;
    private final Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public static CustomUserDetails create(UserRepository.UserSecurityForm user, Map<String, Object> attributes) {
        CustomUserDetails userPrincipal = create(user);
        userPrincipal.setAttributes(attributes);

        return userPrincipal;
    }

    public static CustomUserDetails create(UserRepository.UserSecurityForm user) {
        return new CustomUserDetails(
                user.getId(),
                EProvider.valueOf(user.getProvider()),
                "password",
                user.getRole(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getRoleCode())
                ));
    }

    // ------------------------------------
    /*
     * OAuth2User
     */

    @Override
    public String getName() {
        return id.toString();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    /*
     * UserDetails
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return id.toString();
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