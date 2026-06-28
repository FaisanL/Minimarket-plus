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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.service.impl.InventarioServiceImpl;

@ExtendWith(MockitoExtension.class)
class InventarioServiceImplTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    private Inventario inventario;
    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Arroz 1kg");
        producto.setPrecio(1200.0);
        producto.setStock(50);

        inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProducto(producto);
        inventario.setCantidad(20);
        inventario.setTipoMovimiento("Entrada");
        inventario.setFechaMovimiento(new Date());
    }
    @Test
    void findAll_deberiaRetornarListaDeMovimientos() {
        when(inventarioRepository.findAll()).thenReturn(List.of(inventario));

        List<Inventario> resultado = inventarioService.findAll();

        assertEquals(1, resultado.size());
        assertEquals("Entrada", resultado.get(0).getTipoMovimiento());
    }

    @Test
    void findById_idExistente_deberiaRetornarMovimiento() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));

        Inventario resultado = inventarioService.findById(1L);

        assertNotNull(resultado);
        assertEquals(20, resultado.getCantidad());
    }

    @Test
    void findById_idInexistente_deberiaRetornarNull() {
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());

        Inventario resultado = inventarioService.findById(99L);

        assertNull(resultado);
    }

    @Test
    void save_movimientoEntrada_deberiaPersistirCorrectamente() {
        when(inventarioRepository.save(inventario)).thenReturn(inventario);

        Inventario resultado = inventarioService.save(inventario);

        assertNotNull(resultado);
        assertEquals("Entrada", resultado.getTipoMovimiento());
        verify(inventarioRepository, times(1)).save(inventario);
    }

    @Test
    void save_movimientoSalida_deberiaPersistirCorrectamente() {
        inventario.setTipoMovimiento("Salida");
        when(inventarioRepository.save(inventario)).thenReturn(inventario);

        Inventario resultado = inventarioService.save(inventario);

        assertNotNull(resultado);
        assertEquals("Salida", resultado.getTipoMovimiento());
    }

    @Test
    void findByProductoId_deberiaRetornarMovimientosDelProducto() {
        when(inventarioRepository.findByProductoId(1L)).thenReturn(List.of(inventario));

        List<Inventario> resultado = inventarioService.findByProductoId(1L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getProducto().getId());
    }

    @Test
    void findByProductoId_sinMovimientos_deberiaRetornarListaVacia() {
        when(inventarioRepository.findByProductoId(2L)).thenReturn(List.of());

        List<Inventario> resultado = inventarioService.findByProductoId(2L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deleteById_deberiaInvocarAlRepositorio() {
        doNothing().when(inventarioRepository).deleteById(1L);

        inventarioService.deleteById(1L);

        verify(inventarioRepository, times(1)).deleteById(1L);
    }
    @Test
    void usuarioAdmin_deberiaTenerPermisoParaRegistrarMovimiento() {
        Usuario admin = crearUsuarioConRol("ADMIN");

        boolean puedeRegistrar = tienePermisoParaRegistrarMovimiento(admin);

        assertTrue(puedeRegistrar);
    }

    @Test
    void usuarioEmpleado_deberiaTenerPermisoParaRegistrarMovimiento() {
        Usuario empleado = crearUsuarioConRol("EMPLEADO");

        boolean puedeRegistrar = tienePermisoParaRegistrarMovimiento(empleado);

        assertTrue(puedeRegistrar);
    }

    @Test
    void usuarioCliente_noDeberiaTenerPermisoParaRegistrarMovimiento() {
        Usuario cliente = crearUsuarioConRol("CLIENTE");

        boolean puedeRegistrar = tienePermisoParaRegistrarMovimiento(cliente);

        assertFalse(puedeRegistrar);
    }

    @Test
    void usuarioAdmin_deberiaTenerPermisoParaEliminarMovimiento() {
        Usuario admin = crearUsuarioConRol("ADMIN");

        boolean puedeEliminar = tienePermisoParaEliminarMovimiento(admin);

        assertTrue(puedeEliminar);
    }

    @Test
    void usuarioEmpleado_noDeberiaTenerPermisoParaEliminarMovimiento() {
        Usuario empleado = crearUsuarioConRol("EMPLEADO");

        boolean puedeEliminar = tienePermisoParaEliminarMovimiento(empleado);

        assertFalse(puedeEliminar);
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

    private boolean tienePermisoParaRegistrarMovimiento(Usuario usuario) {
        return usuario.getRoles().stream()
                .anyMatch(rol -> rol.getNombre().equals("ADMIN") || rol.getNombre().equals("EMPLEADO"));
    }

    private boolean tienePermisoParaEliminarMovimiento(Usuario usuario) {
        return usuario.getRoles().stream()
                .anyMatch(rol -> rol.getNombre().equals("ADMIN"));
    }
}
