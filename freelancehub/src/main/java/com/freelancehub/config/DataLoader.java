package com.freelancehub.config;

import com.freelancehub.entities.Categoria;
import com.freelancehub.entities.ClientePerfil;
import com.freelancehub.entities.Habilidad;
import com.freelancehub.entities.ProfesionalPerfil;
import com.freelancehub.entities.Proyecto;
import com.freelancehub.entities.Usuario;
import com.freelancehub.enums.EstadoProyecto;
import com.freelancehub.enums.EstadoUsuario;
import com.freelancehub.enums.RolUsuario;
import com.freelancehub.repositories.CategoriaRepository;
import com.freelancehub.repositories.ClientePerfilRepository;
import com.freelancehub.repositories.HabilidadRepository;
import com.freelancehub.repositories.ProfesionalPerfilRepository;
import com.freelancehub.repositories.ProyectoRepository;
import com.freelancehub.repositories.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataLoader {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    public ApplicationRunner seed(UsuarioRepository usuarioRepository,
                                  ClientePerfilRepository clientePerfilRepository,
                                  ProfesionalPerfilRepository profesionalPerfilRepository,
                                  CategoriaRepository categoriaRepository,
                                  ProyectoRepository proyectoRepository,
                                  HabilidadRepository habilidadRepository,
                                  PasswordEncoder passwordEncoder) {
        return args -> {
            log.info("=== DataLoader: verificando datos semilla ===");

            usuarioRepository.findByEmail("admin@freelancehub.com")
                    .orElseGet(() -> {
                        Usuario u = new Usuario();
                        u.setEmail("admin@freelancehub.com");
                        u.setPassword(passwordEncoder.encode("Admin123!"));
                        u.setNombre("Administrador FreelanceHub");
                        u.setRol(RolUsuario.ADMIN);
                        u.setEstado(EstadoUsuario.ACTIVO);
                        Usuario saved = usuarioRepository.save(u);
                        log.info("ADMIN creado: {}", saved.getEmail());
                        return saved;
                    });

            Usuario cliente = usuarioRepository.findByEmail("cliente@test.com")
                    .orElseGet(() -> {
                        Usuario u = new Usuario();
                        u.setEmail("cliente@test.com");
                        u.setPassword(passwordEncoder.encode("Cliente123!"));
                        u.setNombre("Cliente Demo");
                        u.setTelefono("3000000001");
                        u.setRol(RolUsuario.CLIENTE);
                        u.setEstado(EstadoUsuario.ACTIVO);
                        Usuario saved = usuarioRepository.save(u);

                        ClientePerfil perfil = new ClientePerfil();
                        perfil.setUsuario(saved);
                        perfil.setEmpresa("Empresa Demo S.A.S.");
                        perfil.setRazonSocial("Empresa Demo Sociedad por Acciones Simplificada");
                        clientePerfilRepository.save(perfil);

                        log.info("CLIENTE demo creado: {}", saved.getEmail());
                        return saved;
                    });

            usuarioRepository.findByEmail("profesional@test.com")
                    .orElseGet(() -> {
                        Usuario u = new Usuario();
                        u.setEmail("profesional@test.com");
                        u.setPassword(passwordEncoder.encode("Profesional123!"));
                        u.setNombre("Profesional Demo");
                        u.setTelefono("3000000002");
                        u.setRol(RolUsuario.PROFESIONAL);
                        u.setEstado(EstadoUsuario.ACTIVO);
                        Usuario saved = usuarioRepository.save(u);

                        ProfesionalPerfil perfil = new ProfesionalPerfil();
                        perfil.setUsuario(saved);
                        perfil.setBiografia("Desarrollador full-stack con 5 años de experiencia.");
                        perfil.setTarifaPromedio(new BigDecimal("80000.00"));
                        perfil.setCertificaciones("Oracle Certified Java SE 21");
                        perfil.setAreaExperiencia("Desarrollo Web");
                        profesionalPerfilRepository.save(perfil);

                        log.info("PROFESIONAL demo creado: {}", saved.getEmail());
                        return saved;
                    });

            List<String[]> categoriasSemilla = List.of(
                    new String[]{"Desarrollo Web", "Construcción de sitios y aplicaciones web."},
                    new String[]{"Diseño Gráfico", "Diseño de marca, piezas visuales y branding."},
                    new String[]{"Marketing Digital", "SEO, SEM, redes sociales y campañas digitales."},
                    new String[]{"Redacción", "Redacción de contenidos, copywriting y traducción."},
                    new String[]{"Consultoría", "Asesoría técnica, estratégica y de negocios."}
            );

            for (String[] data : categoriasSemilla) {
                categoriaRepository.findByNombre(data[0]).orElseGet(() -> {
                    Categoria c = new Categoria();
                    c.setNombre(data[0]);
                    c.setDescripcion(data[1]);
                    c.setActivo(true);
                    Categoria saved = categoriaRepository.save(c);
                    log.info("Categoría creada: {}", saved.getNombre());
                    return saved;
                });
            }

            // ---- Habilidades ----
            List<String[]> habilidadesSemilla = List.of(
                    new String[]{"Java", "Lenguaje de programación orientado a objetos."},
                    new String[]{"Spring Boot", "Framework para construir aplicaciones Java basadas en Spring."},
                    new String[]{"React", "Biblioteca JavaScript para interfaces de usuario."},
                    new String[]{"Diseño UX", "Diseño de experiencia de usuario centrado en personas."},
                    new String[]{"SEO", "Optimización en motores de búsqueda."}
            );

            for (String[] data : habilidadesSemilla) {
                habilidadRepository.findByNombre(data[0]).orElseGet(() -> {
                    Habilidad h = new Habilidad();
                    h.setNombre(data[0]);
                    h.setDescripcion(data[1]);
                    h.setActivo(true);
                    Habilidad saved = habilidadRepository.save(h);
                    log.info("Habilidad creada: {}", saved.getNombre());
                    return saved;
                });
            }

            if (proyectoRepository.findByClienteId(cliente.getId()).isEmpty()) {
                Categoria webCat = categoriaRepository.findByNombre("Desarrollo Web").orElseThrow();
                Proyecto p = new Proyecto();
                p.setCliente(cliente);
                p.setCategoria(webCat);
                p.setTitulo("Landing page para startup");
                p.setDescripcion("Necesito una landing page responsive en HTML/CSS/JS con formulario de contacto.");
                p.setPresupuesto(new BigDecimal("1500000.00"));
                p.setPlazo(15);
                p.setEstado(EstadoProyecto.ABIERTO);
                proyectoRepository.save(p);
                log.info("Proyecto demo creado para cliente {}", cliente.getEmail());
            }

            log.info("=== DataLoader: datos semilla listos ===");
        };
    }
}
