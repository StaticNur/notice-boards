package com.greenatom.noticeboards.service.impl;

import com.greenatom.noticeboards.exceptions.AuthorizeException;
import com.greenatom.noticeboards.exceptions.InvalidInputException;
import com.greenatom.noticeboards.model.dto.JwtResponse;
import com.greenatom.noticeboards.model.entity.User;
import com.greenatom.noticeboards.model.enums.Role;
import com.greenatom.noticeboards.service.JwtTokenService;
import com.greenatom.noticeboards.service.SecurityService;
import com.greenatom.noticeboards.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class SecurityServiceImpl implements SecurityService {
    private final UserService userService;
    private final JwtTokenService tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityServiceImpl(UserService userService,
                               JwtTokenServiceImpl tokenProvider,
                               PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User register(String userName, String password, String role) {
        Role roleEnum;
        try {
            roleEnum = Role.valueOf(role.trim());
        }catch (RuntimeException e){
            throw new InvalidInputException("Invalid role. Must be ADMIN or USER");
        }
        Optional<User> byUsername = userService.findByUsername(userName);
        if(byUsername.isEmpty()){
            User user = User.builder()
                    .username(userName)
                    .password(passwordEncoder.encode(password))
                    .active(true)
                    .role(roleEnum)
                    .created(ZonedDateTime.now())
                    .build();

            userService.insertNewUser(user);
            return user;
        }else {
            throw new InvalidInputException("Пользователь с таким username уже существует, введите что-то другое!");
        }
    }

    @Override
    public JwtResponse authorization(String login, String password) {
        Optional<User> optionalUser = userService.findByUsername(login);
        if (optionalUser.isEmpty()) {
            throw new AuthorizeException("There is no user with this login in the database.");
        }

        User user = optionalUser.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthorizeException("The password for this login is incorrect.");
        }

        String accessToken = tokenProvider.createAccessToken(login, optionalUser.get().getRole());
        String refreshToken = tokenProvider.createRefreshToken(login, optionalUser.get().getRole());
        try {
            Authentication authentication = tokenProvider.authentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AccessDeniedException e) {
            throw new AuthorizeException("Access denied!.");
        }
        return new JwtResponse(login, accessToken, refreshToken);
    }

}
