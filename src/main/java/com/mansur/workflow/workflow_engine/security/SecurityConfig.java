package com.mansur.workflow.workflow_engine.security;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.mansur.workflow.workflow_engine.security.jwt.JwtAuthFilter;

@Configuration
public class SecurityConfig {
	
	@Bean
	public JwtAuthFilter jwtAuthFilter() {
	    return new JwtAuthFilter();
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

	    http
	        .csrf(csrf -> csrf.disable())
	        .anonymous(anonymous -> anonymous.disable()) // 🚨 KEY FIX
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers("/auth/**", "/h2-console/**").permitAll()
	            .requestMatchers("/requests/**").authenticated()
	            .anyRequest().authenticated()
	        )
	        .addFilterBefore(
	            jwtAuthFilter(),
	            AuthorizationFilter.class
	        )
	        .headers(headers -> headers
	            .frameOptions(frame -> frame.disable())
	        );

	    return http.build();
	}



}
