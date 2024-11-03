package com.mhkcode.institution.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}


//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
//
//    private final JwtUtil jwtUtil;
//    private final UserDetailsService userDetailsService;
//
//    @Autowired
//    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
//        this.jwtUtil = jwtUtil;
//        this.userDetailsService = userDetailsService;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        final String authHeader = request.getHeader("Authorization");
//        logger.debug("Auth header: {}", authHeader);
//
//        // Skip authentication for permitted URLs
//        String requestURI = request.getRequestURI();
//        if (shouldNotFilter(request)) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            logger.debug("No valid auth header found");
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            return;
//        }
//
//        try {
//            String jwt = authHeader.substring(7);
//            String username = jwtUtil.extractUsername(jwt);
//            String role = jwtUtil.extractRole(jwt);
//
//            logger.debug("Extracted username: {}", username);
//            logger.debug("Extracted role: {}", role);
//
//            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
//
//                logger.debug("Loaded user details: {}", userDetails);
//
//                if (jwtUtil.validateToken(jwt, userDetails)) {
//                    Collection<? extends GrantedAuthority> authorities =
//                            Collections.singletonList(new SimpleGrantedAuthority(role));
//
//                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                            userDetails,
//                            null,
//                            authorities
//                    );
//
//                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(authToken);
//                    logger.debug("Authentication set in context");
//                }
//            }
//        } catch (Exception e) {
//            logger.error("Authentication error: ", e);
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            return;
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    public boolean shouldNotFilter(HttpServletRequest request) {
//        String path = request.getRequestURI();
//        return path.startsWith("/login") ||
//                path.startsWith("/register") ||
//                path.startsWith("/forgot-password") ||
//                path.startsWith("/reset-password") ||
//                path.startsWith("/admin-dashboard") ||
//                path.startsWith("/agent-dashboard") ||
//                path.startsWith("/institution-dashboard") ||
//                path.startsWith("/users") ||
//                path.startsWith("/images") ||
//                path.startsWith("/css") ||
//                path.startsWith("/js");
//    }
//}