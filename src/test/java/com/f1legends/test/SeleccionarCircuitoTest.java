package com.f1legends.test;


import com.f1legends.patrones.SistemaCarreraFacade;
import com.f1legends.modelo.Circuito;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SeleccionarCircuitoTest {

    @Test
    public void testSeleccionarCircuitoValido() {
        SistemaCarreraFacade facade = new SistemaCarreraFacade();

        // Seleccionamos un circuito que sabemos existe en la BD (ej: ID 3 = Mónaco, según tu dataset)
        facade.seleccionarCircuito(4);

        Circuito circuitoSeleccionado = facade.getConfiguracionCarrera().getCircuito();

        assertNotNull(circuitoSeleccionado, "El circuito no debería ser null");
        assertEquals(3, circuitoSeleccionado.getId(), "El ID del circuito debería ser 3");
        assertEquals("Monaco", circuitoSeleccionado.getNombre(), "El nombre debería coincidir");
    }
}
