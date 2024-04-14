package com.greenatom.noticeboards.service.impl;

import com.greenatom.noticeboards.exceptions.AuthorizeException;
import com.greenatom.noticeboards.model.entity.User;
import com.greenatom.noticeboards.repository.UserRepository;
import com.greenatom.noticeboards.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void insertNewUser(User user) {
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String userName) {
        return userRepository.findByUsername(userName);
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(login);
        if (optionalUser.isEmpty()) {
            throw new AuthorizeException("There is no user with this login in the database.");
        }
        User user = optionalUser.get();
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList()
        );
    }
}
