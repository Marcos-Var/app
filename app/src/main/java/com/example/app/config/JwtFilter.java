package com.example.app.config;

import com.example.app.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Extraer el header "Authorization"
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;
        
        // Verificar si el header tiene el formato: "Bearer TOKEN"
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Quitar "Bearer "
            try {
                email = jwtUtil.extractEmail(token);
            } catch (Exception e) {
                // Token inválido o expirado
                System.out.println("Token inválido: " + e.getMessage());
            }
        }
        
        // Si hay un email válido y el usuario no está autenticado aún
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Verificar que el token sea válido
            if (jwtUtil.isTokenValid(token, email)) {
                
                // Crear un objeto de autenticación para Spring Security
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(email, null, null);
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Autenticar al usuario en el contexto de Spring Security
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
    
}
