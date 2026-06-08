package com.f1legends.test;

import com.f1legends.DAO.modeloDAO.PilotoDAO;
import com.f1legends.patrones.SistemaCarreraFacade;
import com.f1legends.modelo.Piloto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


    public class SeleccionarPilotoTest {
        @Test
        public void testDAOOscarPiastri() throws Exception {
            PilotoDAO dao = new PilotoDAO();
            Piloto piastri = dao.obtenerPorNombre("Oscar Piastri");
            assertNotNull(piastri);
            assertEquals(8, piastri.getId());
        }


        @Test
        public void testSeleccionarPilotoValido() {
            SistemaCarreraFacade facade = new SistemaCarreraFacade();

            facade.seleccionarPiloto(8); // ID real en tu BD

            Piloto pilotoSeleccionado = facade.getConfiguracionCarrera().getPilotoSeleccionado();
            assertNotNull(pilotoSeleccionado);
            assertEquals(8, pilotoSeleccionado.getId());
            assertEquals("Oscar Piastri", pilotoSeleccionado.getNombre());
        }

    }


