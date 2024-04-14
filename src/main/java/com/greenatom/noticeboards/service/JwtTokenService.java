package com.greenatom.noticeboards.service;

import com.greenatom.noticeboards.model.dto.JwtResponse;
import com.greenatom.noticeboards.model.enums.Role;
import org.springframework.security.core.Authentication;

import java.nio.file.AccessDeniedException;
import java.util.List;


public interface JwtTokenService {


    String createAccessToken(String login, Role role);


    String createRefreshToken(String login, Role role);

    JwtResponse refreshUserTokens(String refreshToken) throws AccessDeniedException;

    Authentication authentication(String token) throws AccessDeniedException;

    boolean validateToken(String token);
    Role extractRoles(String token);
    String extractUserName(String token);
}
