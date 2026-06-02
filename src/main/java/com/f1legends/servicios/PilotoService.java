package com.f1legends.servicios;

import com.f1legends.modelo.Piloto;
import com.f1legends.DAO.modeloDAO.PilotoDAO;

public class PilotoService {
    private PilotoDAO dao = new PilotoDAO();

    public Piloto buscarPiloto(String nombre) {
        return dao.obtenerPorNombre(nombre);
    }
}

