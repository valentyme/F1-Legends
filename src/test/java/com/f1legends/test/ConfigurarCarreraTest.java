package com.f1legends.test;

import com.f1legends.patrones.facade.SistemaCarreraFacade;
import com.f1legends.servicios.ConfiguracionCarrera;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurarCarreraTest {

    @Test
    public void testConfigurarCarreraValida() {
        SistemaCarreraFacade facade = new SistemaCarreraFacade();

        // Configuramos la carrera con 50 vueltas y clima "Soleado"
        facade.configurarCarrera(50, "Soleado");

        ConfiguracionCarrera config = facade.getConfiguracionCarrera();

        assertNotNull(config, "La configuración no debería ser null");
        assertEquals(50, config.getVueltas(), "Las vueltas deberían coincidir");
        assertEquals("Soleado", config.getClimaInicial(), "El clima debería coincidir");
    }
}
