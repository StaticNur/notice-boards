package com.greenatom.noticeboards.service;

import com.greenatom.noticeboards.model.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {


    void insertNewUser(User user);

    Optional<User> findByUsername(String login);

}
