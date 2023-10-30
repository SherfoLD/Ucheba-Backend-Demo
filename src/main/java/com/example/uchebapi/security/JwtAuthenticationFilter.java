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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final VkUserRepo vkUserRepo;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String vkId = "";
        Optional<VkUser> authUser = Optional.empty();

        try {
            vkId = jwtService.extractVkId(jwt);
            authUser = vkUserRepo.findByVkId(Integer.parseInt(vkId));
        } catch (ExpiredJwtException ex) {
            log.error("Access attempt with expired token");
        } catch (SignatureException | MalformedJwtException ex) {
            log.error("Access attempt with counterfeit token");
        }

        if (authUser.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    Integer.parseInt(vkId),
                    null,
                    Collections.singleton(new SimpleGrantedAuthority(vkId)) //Аутентификация проходит ТОЛЬКО если передается авторити в конструктор!!!
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
            logger.info("Successful login for user: " + vkId);
        }

        filterChain.doFilter(request, response);
    }
}