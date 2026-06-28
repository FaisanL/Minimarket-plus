package com.minimarket;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.service.impl.UsuarioServiceImpl;
@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        Rol rolCliente = new Rol();
        rolCliente.setNombre("CLIENTE");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("juanperez");
        usuario.setPassword("clave123");
        usuario.setRoles(Set.of(rolCliente));
    }
    @Test
    void findAll_deberiaRetornarListaDeUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<Usuario> resultado = usuarioService.findAll();

        assertEquals(1, resultado.size());
        assertEquals("juanperez", resultado.get(0).getUsername());
    }

    @Test
    void findById_idExistente_deberiaRetornarUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals("juanperez", resultado.get().getUsername());
    }

    @Test
    void findById_idInexistente_deberiaRetornarOptionalVacio() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.findById(99L);

        assertFalse(resultado.isPresent());
    }

    @Test
    void findByUsername_usernameExistente_deberiaRetornarUsuario() {
        when(usuarioRepository.findByUsername("juanperez")).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.findByUsername("juanperez");

        assertTrue(resultado.isPresent());
        assertEquals("juanperez", resultado.get().getUsername());
    }

    @Test
    void findByUsername_usernameInexistente_deberiaRetornarOptionalVacio() {
        when(usuarioRepository.findByUsername("noexiste")).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.findByUsername("noexiste");

        assertFalse(resultado.isPresent());
    }

    @Test
    void save_deberiaPersistirUsuarioCorrectamente() {
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario resultado = usuarioService.save(usuario);

        assertNotNull(resultado);
        assertEquals("juanperez", resultado.getUsername());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void deleteById_deberiaInvocarAlRepositorio() {
        doNothing().when(usuarioRepository).deleteById(1L);

        usuarioService.deleteById(1L);

        verify(usuarioRepository, times(1)).deleteById(1L);
    }
}