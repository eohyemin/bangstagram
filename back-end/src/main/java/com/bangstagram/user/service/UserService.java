package com.bangstagram.user.service;

import com.bangstagram.user.domain.model.user.User;
import com.bangstagram.user.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User join(String name, String email, String password) {
        User user = new User(name, email, passwordEncoder.encode(password));

        return save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        checkNotNull(email, "email must be provided.");

        return userRepository.findByEmail(email);
    }

    private User save(User user) {
        return userRepository.save(user);
    }
}