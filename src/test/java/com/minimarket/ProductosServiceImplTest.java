package com.minimarket;

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

import com.minimarket.entity.Categoria;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.service.impl.ProductoServiceImpl;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Abarrotes");

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Arroz 1kg");
        producto.setPrecio(1200.0);
        producto.setStock(50);
        producto.setCategoria(categoria);
    }
    @Test
    void findAll_deberiaRetornarListaDeProductos() {
        when(productoRepository.findAll()).thenReturn(List.of(producto));

        List<Producto> resultado = productoService.findAll();

        assertEquals(1, resultado.size());
        assertEquals("Arroz 1kg", resultado.get(0).getNombre());
    }

    @Test
    void findById_idExistente_deberiaRetornarProducto() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Producto resultado = productoService.findById(1L);

        assertNotNull(resultado);
        assertEquals(1200.0, resultado.getPrecio());
    }

    @Test
    void findById_idInexistente_deberiaRetornarNull() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        Producto resultado = productoService.findById(99L);

        assertNull(resultado);
    }

    @Test
    void save_deberiaPersistirProductoCorrectamente() {
        when(productoRepository.save(producto)).thenReturn(producto);

        Producto resultado = productoService.save(producto);

        assertNotNull(resultado);
        assertEquals("Arroz 1kg", resultado.getNombre());
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    void deleteById_deberiaInvocarAlRepositorio() {
        doNothing().when(productoRepository).deleteById(1L);

        productoService.deleteById(1L);

        verify(productoRepository, times(1)).deleteById(1L);
    }
     @Test
    void usuarioAdmin_deberiaTenerPermisoParaModificarProducto() {
        Usuario admin = crearUsuarioConRol("ADMIN");

        boolean puedeModificar = tienePermisoParaModificarProducto(admin);

        assertTrue(puedeModificar);
    }

    @Test
    void usuarioEmpleado_deberiaTenerPermisoParaModificarProducto() {
        Usuario empleado = crearUsuarioConRol("EMPLEADO");

        boolean puedeModificar = tienePermisoParaModificarProducto(empleado);

        assertTrue(puedeModificar);
    }

    @Test
    void usuarioCliente_noDeberiaTenerPermisoParaModificarProducto() {
        Usuario cliente = crearUsuarioConRol("CLIENTE");

        boolean puedeModificar = tienePermisoParaModificarProducto(cliente);

        assertFalse(puedeModificar);
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

    private boolean tienePermisoParaModificarProducto(Usuario usuario) {
        return usuario.getRoles().stream()
                .anyMatch(rol -> rol.getNombre().equals("ADMIN") || rol.getNombre().equals("EMPLEADO"));
    }

}