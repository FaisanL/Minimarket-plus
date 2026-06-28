package com.minimarket;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Venta;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.impl.VentaServiceImpl;
@ExtendWith(MockitoExtension.class)
class VentaServiceImplTest {

    @Mock
    private VentaRepository ventaRepository;

    @InjectMocks
    private VentaServiceImpl ventaService;

    private Venta venta;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("clienteTest");
        usuario.setPassword("clave123");

        venta = new Venta();
        venta.setId(1L);
        venta.setUsuario(usuario);
        venta.setFecha(new Date());
    }
    @Test
    void findAll_deberiaRetornarListaDeVentas() {
        when(ventaRepository.findAll()).thenReturn(List.of(venta));

        List<Venta> resultado = ventaService.findAll();

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
    }

    @Test
    void findById_idExistente_deberiaRetornarVenta() {
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        Venta resultado = ventaService.findById(1L);

        assertNotNull(resultado);
        assertEquals("clienteTest", resultado.getUsuario().getUsername());
    }

    @Test
    void findById_idInexistente_deberiaRetornarNull() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        Venta resultado = ventaService.findById(99L);

        assertNull(resultado);
    }

    @Test
    void save_deberiaRegistrarVentaCorrectamente() {
        when(ventaRepository.save(venta)).thenReturn(venta);

        Venta resultado = ventaService.save(venta);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getUsuario().getId());
        verify(ventaRepository, times(1)).save(venta);
    }

    @Test
    void findByUsuarioId_deberiaRetornarVentasDelUsuario() {
        when(ventaRepository.findByUsuarioId(1L)).thenReturn(List.of(venta));

        List<Venta> resultado = ventaService.findByUsuarioId(1L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getUsuario().getId());
    }

    @Test
    void findByUsuarioId_sinVentas_deberiaRetornarListaVacia() {
        when(ventaRepository.findByUsuarioId(2L)).thenReturn(List.of());

        List<Venta> resultado = ventaService.findByUsuarioId(2L);

        assertTrue(resultado.isEmpty());
    }
    @Test
    void usuarioAdmin_deberiaTenerPermisoParaGenerarVenta() {
        Usuario admin = crearUsuarioConRol("ADMIN");

        boolean puedeVender = tienePermisoParaGenerarVenta(admin);

        assertTrue(puedeVender);
    }

    @Test
    void usuarioEmpleado_deberiaTenerPermisoParaGenerarVenta() {
        Usuario empleado = crearUsuarioConRol("EMPLEADO");

        boolean puedeVender = tienePermisoParaGenerarVenta(empleado);

        assertTrue(puedeVender);
    }

    @Test
    void usuarioCliente_deberiaTenerPermisoParaGenerarVenta() {
        Usuario cliente = crearUsuarioConRol("CLIENTE");

        boolean puedeVender = tienePermisoParaGenerarVenta(cliente);

        assertTrue(puedeVender);
    }

    @Test
    void usuarioCliente_noDeberiaTenerPermisoParaListarTodasLasVentas() {
        Usuario cliente = crearUsuarioConRol("CLIENTE");

        boolean puedeListar = tienePermisoParaListarVentas(cliente);

        assertFalse(puedeListar);
    }

    @Test
    void usuarioEmpleado_deberiaTenerPermisoParaListarTodasLasVentas() {
        Usuario empleado = crearUsuarioConRol("EMPLEADO");

        boolean puedeListar = tienePermisoParaListarVentas(empleado);

        assertTrue(puedeListar);
    }

    private Usuario crearUsuarioConRol(String nombreRol) {
        Rol rol = new Rol();
        rol.setNombre(nombreRol);

        Usuario usuario = new Usuario();
        usuario.setUsername("usuarioTest");
        usuario.setPassword("clave123");
        usuario.setRoles(Set.of(rol));
        return usuario;
    }

    private boolean tienePermisoParaGenerarVenta(Usuario usuario) {
        return usuario.getRoles().stream()
                .anyMatch(rol -> rol.getNombre().equals("ADMIN")
                        || rol.getNombre().equals("EMPLEADO")
                        || rol.getNombre().equals("CLIENTE"));
    }

    private boolean tienePermisoParaListarVentas(Usuario usuario) {
        return usuario.getRoles().stream()
                .anyMatch(rol -> rol.getNombre().equals("ADMIN") || rol.getNombre().equals("EMPLEADO"));
    }

}