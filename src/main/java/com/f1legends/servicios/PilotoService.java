package com.f1legends.servicios;

import com.f1legends.modelo.Piloto.Piloto;
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
        Piloto piloto = dao.obtenerPorId(id);
        if (piloto == null) {
            throw new IllegalArgumentException("Piloto no encontrado con ID: " + id);
        }
        return piloto;
    }
    public List<Piloto> listarPilotos() {
        return dao.obtenerTodos();
    }

    public List<Piloto> obtenerParticipantes(List<Integer> idsPilotos) {
        List<Piloto> lista = new ArrayList<>();
        for (Integer id : idsPilotos) {
            Piloto piloto = dao.obtenerPorId(id);
            if (piloto != null) {
                lista.add(piloto);
            }
        }
        return lista;
    }
    public void gestionarPilotos(String operacion, Piloto piloto) {
        switch (operacion.toLowerCase()) {
            case "alta":
                dao.insertar(piloto);
                break;
            case "baja":
                dao.eliminar(piloto.getId());
                break;
            case "modificacion":
                dao.actualizar(piloto);
                break;
            case "consulta":
                Piloto encontrado = dao.obtenerPorId(piloto.getId());
                if (encontrado != null) {
                    System.out.println("Piloto encontrado: " + encontrado.getNombre());
                } else {
                    System.out.println("No se encontró piloto con ID: " + piloto.getId());
                }
                break;
            default:
                throw new IllegalArgumentException("Operación no válida en gestionarPilotos");
        }


    }
}
