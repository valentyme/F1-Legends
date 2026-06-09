package com.f1legends.test;


import com.f1legends.patrones.facade.SistemaCarreraFacade;
import com.f1legends.modelo.circuitos.Circuito;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SeleccionarCircuitoTest {

    @Test
    public void testSeleccionarCircuitoValido() {
        SistemaCarreraFacade facade = new SistemaCarreraFacade();

        // Seleccionamos el circuito con ID 4 (Monaco en tu dataset)
        facade.seleccionarCircuito(4);

        Circuito circuitoSeleccionado = facade.getConfiguracionCarrera().getCircuito();

        assertNotNull(circuitoSeleccionado, "El circuito no debería ser null");
        assertEquals(4, circuitoSeleccionado.getId(), "El ID del circuito debería ser 4");
        assertTrue(circuitoSeleccionado.getNombre().contains("Monaco"),
                "El nombre debería contener 'Monaco'");

    }
}
