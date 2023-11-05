package com.example.uchebapi.services;

import com.example.uchebapi.domain.GroupUser;
import com.example.uchebapi.repos.GroupUserRepo;
import com.example.uchebapi.security.AuthenticationResponse;
import com.example.uchebapi.security.JwtService;
import com.example.uchebapi.security.VkStartupParametersResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final VkStartupParametersResolver vkStartupParametersResolver;
    private final GroupUserRepo groupUserRepo;
    private final JwtService jwtService;

    public AuthenticationResponse authenticate(Map<String, String> request) {
        Integer vkId = vkStartupParametersResolver.getVkId(request);
        if (vkId == -1)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid startup parameters");

        SecurityContextHolder.getContext().setAuthentication(
                new PreAuthenticatedAuthenticationToken(
                        vkId,
                        null,
                        List.of(new SimpleGrantedAuthority("AppUser"))
                )
        );

        log.info("Authentication token was generated for user: " + vkId);
        String jwt = jwtService.generateToken(vkId.toString());

        return new AuthenticationResponse(jwt);
    }

    public Integer getPrivilegeLevel(UUID groupId) {
        Optional<GroupUser> groupUser = groupUserRepo.findByGroup_IdAndUser_VkId(groupId, getVkId());
        if (groupUser.isEmpty()) {
            return null;
        }

        return groupUser.get().getRole().ordinal();
    }

    public Integer getVkId() {

        return (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}