package com.minimarket;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class MinimarketApplication {

	public static void main(String[] args) {
		SpringApplication.run(MinimarketApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(UsuarioRepository usuarioRepository,
									  RolRepository rolRepository,
									  PasswordEncoder passwordEncoder) {
		return args -> {

			// Crear roles
			Rol rolAdmin = new Rol();
			rolAdmin.setNombre("ROLE_ADMIN");
			rolRepository.save(rolAdmin);

			Rol rolEmpleado = new Rol();
			rolEmpleado.setNombre("ROLE_EMPLEADO");
			rolRepository.save(rolEmpleado);

			Rol rolCliente = new Rol();
			rolCliente.setNombre("ROLE_CLIENTE");
			rolRepository.save(rolCliente);

			// Crear usuarios
			Usuario admin = new Usuario();
			admin.setUsername("admin");
			admin.setPassword(passwordEncoder.encode("admin123"));
			admin.setRoles(Set.of(rolAdmin));
			usuarioRepository.save(admin);

			Usuario empleado = new Usuario();
			empleado.setUsername("empleado");
			empleado.setPassword(passwordEncoder.encode("empleado123"));
			empleado.setRoles(Set.of(rolEmpleado));
			usuarioRepository.save(empleado);

			Usuario cliente = new Usuario();
			cliente.setUsername("cliente");
			cliente.setPassword(passwordEncoder.encode("cliente123"));
			cliente.setRoles(Set.of(rolCliente));
			usuarioRepository.save(cliente);

			System.out.println("✅ Datos iniciales cargados correctamente");
		};
	}
}