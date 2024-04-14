package com.greenatom.noticeboards.service.impl;

import com.greenatom.noticeboards.exceptions.InvalidTokenException;
import com.greenatom.noticeboards.model.dto.JwtResponse;
import com.greenatom.noticeboards.model.enums.Role;
import com.greenatom.noticeboards.service.JwtTokenService;
import com.greenatom.noticeboards.service.UserService;
import com.greenatom.noticeboards.util.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.file.AccessDeniedException;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class JwtTokenServiceImpl implements JwtTokenService {
    private final JwtProperties properties;
    private final UserService userService;
    private Key key;
    private byte[] secret;
    @Autowired
    public JwtTokenServiceImpl(JwtProperties properties, UserService userService) {
        this.properties = properties;
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        this.secret = properties.getSecret().getBytes();
        this.key = Keys.hmacShaKeyFor(this.secret);
        //this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
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
        if (claims.isEmpty()){
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


    /*private Key signKey() {
        return Keys.hmacShaKeyFor(secret);
    }

    private String getLoginFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }*/
}
