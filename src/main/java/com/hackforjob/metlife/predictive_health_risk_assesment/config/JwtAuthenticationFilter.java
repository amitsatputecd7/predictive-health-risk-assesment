package com.hackforjob.metlife.predictive_health_risk_assesment.config;

import com.hackforjob.metlife.predictive_health_risk_assesment.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        // Check if the Authorization header is present and starts with "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                email = jwtUtil.getEmailFromToken(jwt);
            } catch (Exception e) {
                log.warn("Unable to get JWT Token or JWT Token has expired", e);
            }
        }

        // If email is present and no authentication is set in SecurityContext
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Check if token is valid
            if (jwtUtil.validateToken(jwt, email)) {
                // Create authentication token
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Set authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("Authentication set for user: {}", email);
            }
        }

        filterChain.doFilter(request, response);
    }
}