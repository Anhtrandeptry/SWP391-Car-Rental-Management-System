package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.repository.UserRepository;
import fpt.swp391.carrentalsystem.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository
                .findByEmailOrPhoneNumber(email, email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + email)
                );

        return new CustomUserDetails(user);
    }

}


