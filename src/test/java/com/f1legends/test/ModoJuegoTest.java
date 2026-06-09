package com.f1legends.test;


import com.f1legends.patrones.facade.SistemaCarreraFacade;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ModoJuegoTest {

    @Test
    public void testSeleccionarModoJuegoValido() {
        SistemaCarreraFacade facade = new SistemaCarreraFacade();

        // Act: seleccionar un modo válido
        facade.seleccionarModoJuego("Singleplayer");

        // Assert: verificar que se guardó en ConfiguracionCarrera
        String modoJuego = facade.getConfiguracionCarrera().getModoJuego();
        assertEquals("Singleplayer", modoJuego);
    }

    @Test
    public void testSeleccionarModoJuegoInvalido() {
        SistemaCarreraFacade facade = new SistemaCarreraFacade();

        // Act & Assert: debe lanzar excepción si el modo no existe
        assertThrows(IllegalArgumentException.class, () -> {
            facade.seleccionarModoJuego("ModoInventado");
        });
    }
}

