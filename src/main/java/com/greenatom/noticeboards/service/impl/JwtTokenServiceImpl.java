package com.greenatom.noticeboards.service.impl;

import com.greenatom.noticeboards.exceptions.InvalidTokenException;
import com.greenatom.noticeboards.model.dto.JwtResponse;
import com.greenatom.noticeboards.model.enums.Role;
import com.greenatom.noticeboards.service.JwtTokenService;
import com.greenatom.noticeboards.service.UserService;
import com.greenatom.noticeboards.util.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {
    private final JwtProperties properties;
    private final UserService userService;
    private Key key;

    @Autowired
    public JwtTokenServiceImpl(JwtProperties properties, UserService userService) {
        this.properties = properties;
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes());
    }

    @Override
    public String createAccessToken(String login, Role role) {
        Claims claims = Jwts.claims().setSubject(login);
        claims.put("role", role.name());
        Date now = new Date();
        Date validity = new Date(now.getTime() + properties.getAccess());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
    }

    @Override
    public String createRefreshToken(String login, Role role) {
        Claims claims = Jwts.claims().setSubject(login);
        claims.put("role", role.name());
        Date now = new Date();
        Date validity = new Date(now.getTime() + properties.getRefresh());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
    }

    @Override
    public JwtResponse refreshUserTokens(String refreshToken) throws AccessDeniedException {
        if (!validateToken(refreshToken)) {
            throw new AccessDeniedException("Access denied!");
        }

        String login = extractUserName(refreshToken);
        Role role = extractRoles(refreshToken);

        return new JwtResponse(login, createAccessToken(login, role), createRefreshToken(login, role));
    }

    @Override
    public Authentication authentication(String token) {
        String username = extractUserName(token);
        UserDetails userDetails = userService.loadUserByUsername(username);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public String extractUserName(String token) {
        Optional<Claims> claims = extractAllClaims(token);
        if (claims.isEmpty() || claims.get().getSubject() == null) {
            throw new InvalidTokenException("Invalid token.");
        }
        return claims.get().getSubject();
    }

    @Override
    public Role extractRoles(String token) {
        Optional<Claims> claims = extractAllClaims(token);
        if (claims.isEmpty()) {
            throw new InvalidTokenException("Invalid token.");
        }
        String roleStr = claims.get().get("role", String.class);
        try {
            return Role.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException(e.getMessage());
        }
    }

    private Optional<Claims> extractAllClaims(String token) {
        try {
            return Optional.ofNullable(Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody());
        } catch (MalformedJwtException e) {
            throw new InvalidTokenException("Malformed JWT token." + e.getMessage());
        }
    }

}
