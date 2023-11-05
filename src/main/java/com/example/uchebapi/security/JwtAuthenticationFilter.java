package com.example.uchebapi.security;

import com.example.uchebapi.domain.VkUser;
import com.example.uchebapi.repos.VkUserRepo;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final VkUserRepo vkUserRepo;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        try {
            Integer vkId = Integer.parseInt(jwtService.extractVkId(jwt));
            Optional<VkUser> authUser = vkUserRepo.findByVkId(vkId);

            if (authUser.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
                SecurityContextHolder.getContext().setAuthentication(
                        new PreAuthenticatedAuthenticationToken(
                                vkId,
                                null,
                                List.of(new SimpleGrantedAuthority("AppUser")) //рандомный авторити
                        ) //Аутентификация проходит ТОЛЬКО если передается авторити в конструктор
                );
                log.info("User: " + vkId + " was authenticated");
            }

        } catch (ExpiredJwtException ex) {
            log.error("Access attempt with expired token");
        } catch (SignatureException | MalformedJwtException ex) {
            log.error("Access attempt with counterfeit token");
        }

        filterChain.doFilter(request, response);
    }
}