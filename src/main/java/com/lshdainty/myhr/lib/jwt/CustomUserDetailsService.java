package com.lshdainty.myhr.lib.jwt;

import com.lshdainty.myhr.domain.User;
import com.lshdainty.myhr.repository.UserRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepositoryImpl userRepositoryImpl;

    @Override
    public UserDetails loadUserByUsername(String userNo) throws UsernameNotFoundException {
        User user = userRepositoryImpl.findById(Long.valueOf(userNo));

        if (!Objects.isNull(user)) {
            return new CustomUserDetails(user);
        }

        return null;
    }
}
