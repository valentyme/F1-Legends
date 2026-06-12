package com.f1legends.servicios;

import com.f1legends.modelo.Piloto;
import com.f1legends.DAO.modeloDAO.PilotoDAO;

import java.util.ArrayList;
import java.util.List;

public class PilotoService {
    private PilotoDAO dao = new PilotoDAO();

    // Buscar piloto por nombre
    public Piloto buscarPiloto(String nombre) {
        return dao.obtenerPorNombre(nombre);
    }

    // Buscar piloto por ID
    public Piloto buscarPilotoPorId(int id) {
        return dao.obtenerPorId(id);
    }
    // Seleccionar piloto por ID

    public Piloto seleccionarPiloto(int id) {
        Piloto piloto = dao.obtenerPorId(id); // ✅ uso del DAO instanciado
        if (piloto == null) {
            throw new IllegalArgumentException("Piloto no encontrado con ID: " + id);
        }
        return piloto;
    }

    // Listar todos los pilotos
    public List<Piloto> listarPilotos() {
        return PilotoDAO.obtenerTodos();
    }
    public List<Piloto> obtenerParticipantes(List<Integer> idsPilotos) {
        List<Piloto> lista = new ArrayList<>();
        for (Integer id : idsPilotos) {
            Piloto piloto = PilotoDAO.obtenerPorId(id);
            if (piloto != null) {
                lista.add(piloto);
            }
        }
        return lista;
    }

}
