package io.degeus.recipeappapi.service;

import io.degeus.recipeappapi.domain.User;
import io.degeus.recipeappapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> byUsernameIgnoreCase = userRepository.findOneByUsernameIgnoreCase(username);
        if (byUsernameIgnoreCase.isPresent()) {
            return byUsernameIgnoreCase.get();
        }
        throw new UsernameNotFoundException("user not found!");
    }
}
