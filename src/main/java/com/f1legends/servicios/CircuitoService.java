package com.f1legends.servicios;

import com.f1legends.DAO.modeloDAO.CircuitoDAO;
import com.f1legends.DAO.modeloDAO.CircuitoDTO;
import com.f1legends.patrones.CircuitoFactory;
import com.f1legends.modelo.Circuito;
import java.util.List;
import java.util.stream.Collectors;

public class CircuitoService {
    private CircuitoDAO dao = new CircuitoDAO();

    // Obtener un circuito por ID
    public Circuito obtenerCircuito(int id) {
        CircuitoDTO dto = dao.obtenerPorId(id);
        return dto != null ? CircuitoFactory.crear(dto) : null;
    }

    // Obtener un circuito por nombre
    public Circuito obtenerCircuitoPorNombre(String nombre) {
        CircuitoDTO dto = dao.obtenerPorNombre(nombre);
        return dto != null ? CircuitoFactory.crear(dto) : null;
    }

    // Listar todos los circuitos disponibles
    public List<Circuito> listarCircuitos() {
        List<CircuitoDTO> dtos = dao.obtenerTodos();
        return dtos.stream()
                .map(CircuitoFactory::crear)
                .collect(Collectors.toList());
    }

}


