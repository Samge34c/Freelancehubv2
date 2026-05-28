package com.freelancehub.services;

import com.freelancehub.controllers.dtos.auth.AuthResponse;
import com.freelancehub.controllers.dtos.auth.LoginRequest;
import com.freelancehub.controllers.dtos.auth.RegisterClientRequest;
import com.freelancehub.controllers.dtos.auth.RegisterProfessionalRequest;
import com.freelancehub.entities.ClientePerfil;
import com.freelancehub.entities.ProfesionalPerfil;
import com.freelancehub.entities.Usuario;
import com.freelancehub.enums.EstadoUsuario;
import com.freelancehub.enums.RolUsuario;
import com.freelancehub.exception.BusinessException;
import com.freelancehub.repositories.ClientePerfilRepository;
import com.freelancehub.repositories.ProfesionalPerfilRepository;
import com.freelancehub.repositories.UsuarioRepository;
import com.freelancehub.config.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final ClientePerfilRepository clientePerfilRepository;
    private final ProfesionalPerfilRepository profesionalPerfilRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository,
                       ClientePerfilRepository clientePerfilRepository,
                       ProfesionalPerfilRepository profesionalPerfilRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.clientePerfilRepository = clientePerfilRepository;
        this.profesionalPerfilRepository = profesionalPerfilRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse registerClient(RegisterClientRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El correo electrónico ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombre(request.getNombre());
        usuario.setTelefono(request.getTelefono());
        usuario.setRol(RolUsuario.CLIENTE);
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuario = usuarioRepository.save(usuario);

        ClientePerfil perfil = new ClientePerfil();
        perfil.setUsuario(usuario);
        perfil.setEmpresa(request.getEmpresa());
        perfil.setRazonSocial(request.getRazonSocial());
        clientePerfilRepository.save(perfil);

        String token = jwtService.generateToken(usuario);
        return new AuthResponse(token, usuario.getId(), usuario.getEmail(),
                usuario.getNombre(), usuario.getRol());
    }

    @Transactional
    public AuthResponse registerProfessional(RegisterProfessionalRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El correo electrónico ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombre(request.getNombre());
        usuario.setTelefono(request.getTelefono());
        usuario.setRol(RolUsuario.PROFESIONAL);
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuario = usuarioRepository.save(usuario);

        ProfesionalPerfil perfil = new ProfesionalPerfil();
        perfil.setUsuario(usuario);
        perfil.setBiografia(request.getBiografia());
        perfil.setTarifaPromedio(request.getTarifaPromedio());
        perfil.setCertificaciones(request.getCertificaciones());
        perfil.setAreaExperiencia(request.getAreaExperiencia());
        profesionalPerfilRepository.save(perfil);

        String token = jwtService.generateToken(usuario);
        return new AuthResponse(token, usuario.getId(), usuario.getEmail(),
                usuario.getNombre(), usuario.getRol());
    }

    public AuthResponse login(LoginRequest request) {
        // Verificación previa: si el usuario existe pero está bloqueado o inactivo,
        // devolvemos un mensaje claro (también lo cubre AuthenticationProvider).
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElse(null);
        if (usuario != null) {
            if (usuario.getEstado() == EstadoUsuario.BLOQUEADO) {
                throw new LockedException("La cuenta está bloqueada");
            }
            if (usuario.getEstado() == EstadoUsuario.INACTIVO) {
                throw new DisabledException("La cuenta está inactiva");
            }
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        // Si llega aquí, el usuario es válido y está ACTIVO.
        Usuario authenticated = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Usuario no encontrado tras autenticación"));

        String token = jwtService.generateToken(authenticated);
        return new AuthResponse(token, authenticated.getId(), authenticated.getEmail(),
                authenticated.getNombre(), authenticated.getRol());
    }
}
