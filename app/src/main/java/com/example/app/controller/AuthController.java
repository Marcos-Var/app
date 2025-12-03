package com.example.app.controller;

import com.example.app.config.JwtUtil;
import com.example.app.dto.AuthRequest;
import com.example.app.dto.AuthResponse;
import com.example.app.dto.RegisterRequest;
import com.example.app.model.User;
import com.example.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    // POST /api/auth/register - Registrar nuevo usuario
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Verificar si el email ya existe
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("El email ya está registrado");
        }
        
        // Crear nuevo usuario
        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        // Encriptar la contraseña antes de guardarla
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // Guardar en la base de datos
        userRepository.save(user);
        
        // Generar token JWT
        String token = jwtUtil.generateToken(user.getEmail());
        
        // Devolver respuesta con el token
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(token, user.getEmail(), user.getName()));
    }
    
    // POST /api/auth/login - Iniciar sesión
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        // Buscar usuario por email
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        
        if (userOptional.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Email o contraseña incorrectos");
        }
        
        User user = userOptional.get();
        
        // Verificar la contraseña
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Email o contraseña incorrectos");
        }
        
        // Generar token JWT
        String token = jwtUtil.generateToken(user.getEmail());
        
        // Devolver respuesta con el token
        return ResponseEntity.ok(new AuthResponse(token, user.getEmail(), user.getName()));
    }
}
