package com.greenatom.noticeboards.controller.security;

import com.greenatom.noticeboards.model.dto.*;
import com.greenatom.noticeboards.model.entity.User;
import com.greenatom.noticeboards.service.SecurityService;
import com.greenatom.noticeboards.util.GeneratorResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authorization Controller", description = "Authentication API")
public class SecurityController {
    private final SecurityService securityService;
    private final GeneratorResponseMessage generatorResponseMessage;

    @Autowired
    public SecurityController(SecurityService securityService,
                              GeneratorResponseMessage generatorResponseMessage) {
        this.securityService = securityService;
        this.generatorResponseMessage = generatorResponseMessage;
    }

    @Operation(summary = "New User Registration")
    @PostMapping("/registration")
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationDTO registerRequest,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<CustomFieldError> customFieldErrors = generatorResponseMessage.generateErrorMessage(bindingResult);
            return ResponseEntity.badRequest().body(customFieldErrors);
        } else {
            User registered = securityService.register(registerRequest.userName(),
                    registerRequest.password(),
                    registerRequest.role());
            return ResponseEntity.ok(new SuccessResponse("User with login "
                                                         + registered.getUsername() + " successfully created."));
        }
    }

    @Operation(summary = "login")
    @PostMapping("/login")
    public ResponseEntity<?> signIn(@RequestBody @Valid SignInDTO signInDTO,
                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<CustomFieldError> customFieldErrors = generatorResponseMessage.generateErrorMessage(bindingResult);
            return ResponseEntity.badRequest().body(customFieldErrors);
        } else {
            JwtResponse authenticated = securityService.authorization(signInDTO.username(), signInDTO.password());
            return ResponseEntity.ok(authenticated);
        }
    }
}