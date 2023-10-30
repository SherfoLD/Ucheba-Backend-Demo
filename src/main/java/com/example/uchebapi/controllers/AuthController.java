package com.example.uchebapi.controllers;

import com.example.uchebapi.dtos.AuthDto;
import com.example.uchebapi.security.AuthenticationResponse;
import com.example.uchebapi.security.JwtService;
import com.example.uchebapi.services.AuthenticationService;
import com.example.uchebapi.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController()
@RequestMapping("/api/user")
@Tag(name = "Authenticate")
public class AuthController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public AuthController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "Authenticate endpoint. Returning JWT (FOR PROD WITH HEADERS)")
    @GetMapping("authenticate")
    public AuthDto authenticate(HttpServletResponse response, @RequestHeader Map<String, String> request) {
        AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);
        response.addHeader("Set-Cookie", "access_token=" + authenticationResponse.getToken());

        Integer vkId = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userService.login(vkId, authenticationResponse.getToken());
    }
}