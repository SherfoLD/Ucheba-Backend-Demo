package com.example.uchebapi.services;

import com.example.uchebapi.domain.GroupUser;
import com.example.uchebapi.domain.VkUser;
import com.example.uchebapi.repos.GroupUserRepo;
import com.example.uchebapi.repos.VkUserRepo;
import com.example.uchebapi.security.JwtService;
import com.example.uchebapi.security.VkStartupParametersResolver;
import com.example.uchebapi.security.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final VkUserRepo vkUserRepo;
    private final GroupUserRepo groupUserRepo;
    private final JwtService jwtService;

    public AuthenticationResponse authenticate(Map<String, String> request) {
        VkStartupParametersResolver vkStartupParametersResolver = new VkStartupParametersResolver(request);

        if (!vkStartupParametersResolver.isStartupParametersCorrect()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Startup parameters are incorrect");
        }
        if (!vkStartupParametersResolver.isStartupParametersNotExpired())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Startup parameters are expired");

        Integer vkId = vkStartupParametersResolver.getVkId();
        Optional<VkUser> user = vkUserRepo.findByVkId(vkId);
        if (user.isEmpty()) {
            user = Optional.of(
                    VkUser.builder()
                            .vkId(vkId)
                            .build());
            vkUserRepo.save(user.get());
        }

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        vkId,
                        null
                )
        );
        log.info("Successful login for user: " + vkId);
        var jwt = jwtService.generateToken(vkId.toString());

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