package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.request.RegisterRequest;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.mapper.UserMapper;
import fpt.swp391.carrentalsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void register(RegisterRequest request) {

        User user = UserMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);

        userRepository.save(user);
    }
}
