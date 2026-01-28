package com.mansur.workflow.workflow_engine.security.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(
	        HttpServletRequest request,
	        HttpServletResponse response,
	        FilterChain filterChain)
	        throws ServletException, IOException {

	    String header = request.getHeader("Authorization");

	    System.out.println(">>> REQUEST URI: " + request.getRequestURI());
	    System.out.println(">>> Authorization Header: " + header);

	    if (header != null && header.startsWith("Bearer ")) {

	        String token = header.substring(7);

	        Claims claims = JwtUtil.parseToken(token);

	        String username = claims.getSubject();
	        String role = claims.get("role", String.class);

	        System.out.println(">>> JWT USER: " + username);
	        System.out.println(">>> JWT ROLE: " + role);

	        UsernamePasswordAuthenticationToken auth =
	                new UsernamePasswordAuthenticationToken(
	                        username,
	                        null,
	                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
	                );

	        SecurityContextHolder.getContext().setAuthentication(auth);
	        System.out.println(">>> Authentication set in SecurityContext");
	    } else {
	        System.out.println(">>> No valid Authorization header found");
	    }

	    filterChain.doFilter(request, response);
	}

}
