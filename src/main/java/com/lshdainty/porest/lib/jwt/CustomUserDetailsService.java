package com.lshdainty.porest.lib.jwt;

import com.lshdainty.porest.user.domain.User;
import com.lshdainty.porest.user.repository.UserRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepositoryImpl userRepositoryImpl;
    private final MessageSource ms;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Optional<User> user = userRepositoryImpl.findById(userId);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + userId);
        }

        return new CustomUserDetails(user.get());
    }
}