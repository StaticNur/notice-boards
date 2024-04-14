package com.greenatom.noticeboards.controller.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenatom.noticeboards.exceptions.AuthorizeException;
import com.greenatom.noticeboards.exceptions.InvalidTokenException;
import com.greenatom.noticeboards.model.dto.ExceptionResponse;
import com.greenatom.noticeboards.model.enums.Role;
import com.greenatom.noticeboards.service.JwtTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final ObjectMapper jacksonMapper;

    @Autowired
    public JwtAuthFilter(JwtTokenService jwtTokenService, ObjectMapper jacksonMapper) {
        this.jwtTokenService = jwtTokenService;
        this.jacksonMapper = jacksonMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7).trim(); // Удаление пробелов в конце

            if (!jwt.isEmpty()) {
                try {
                    username = ((UserDetails) jwtTokenService.authentication(jwt).getPrincipal()).getUsername();
                } catch (ExpiredJwtException exception) {
                    sendResponse(response, "The token's lifetime has expired.");
                    return;
                } catch (InvalidTokenException | AuthorizeException e) {
                    sendResponse(response, e.getMessage());
                    return;
                }
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                Role role = jwtTokenService.extractRoles(jwt);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.singleton(new SimpleGrantedAuthority(role.name()))
                );
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } catch (RuntimeException e) {
                sendResponse(response, e.getMessage());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }


    private void sendResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        ExceptionResponse exceptionResponse = new ExceptionResponse(message);
        String jsonResponse = jacksonMapper.writeValueAsString(exceptionResponse);
        response.getWriter().write(jsonResponse);
    }
}
