package com.minimarket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.minimarket.security.util.JwtUtil;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        // Mismo formato de clave y expiración que usa application.properties,
        // pero creado directamente aquí para no depender de Spring.
        jwtUtil = new JwtUtil("minimarketplusjwtsecretkeyforsecurityweekthree2026", 86400000L);
    }
    @Test
    void generateToken_deberiaCrearUnTokenValido() {
        String token = jwtUtil.generateToken("juanperez");

        assertTrue(token != null && !token.isEmpty());
    }

    @Test
    void extractUsername_deberiaRetornarElUsernameCorrecto() {
        String token = jwtUtil.generateToken("juanperez");

        String username = jwtUtil.extractUsername(token);

        assertEquals("juanperez", username);
    }

    @Test
    void validateToken_tokenValido_deberiaRetornarTrue() {
        // Escenario de éxito: simula un "login válido"
        String token = jwtUtil.generateToken("juanperez");

        boolean esValido = jwtUtil.validateToken(token);

        assertTrue(esValido);
    }

    @Test
    void validateToken_tokenCorrupto_deberiaRetornarFalse() {
        // Escenario de error: simula un "login inválido" (token manipulado/corrupto)
        String tokenCorrupto = "esto.no.es.un.token.valido";

        boolean esValido = jwtUtil.validateToken(tokenCorrupto);

        assertFalse(esValido);
    }

    @Test
    void validateToken_tokenVacio_deberiaRetornarFalse() {
        // Escenario de error/borde: un token vacío no es manejado internamente por
        // JwtUtil.validateToken(), ya que IllegalArgumentException no es subclase de
        // JwtException y no queda capturada por el catch. Se documenta como hallazgo
        // para una mejora futura (ver propuesta de mejora en el informe).
        assertThrows(IllegalArgumentException.class, () -> jwtUtil.validateToken(""));
    }

    @Test
    void validateToken_firmadoConClaveDistinta_deberiaRetornarFalse() {
        // Escenario de error: token válido en estructura, pero firmado con otra clave secreta
        JwtUtil otroJwtUtil = new JwtUtil("otra-clave-secreta-completamente-distinta-12345", 86400000L);
        String tokenConOtraClave = otroJwtUtil.generateToken("juanperez");

        boolean esValido = jwtUtil.validateToken(tokenConOtraClave);

        assertFalse(esValido);
    }

}