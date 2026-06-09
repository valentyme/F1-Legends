package com.f1legends.patrones.circuitoFactory;

import com.f1legends.DAO.modeloDAO.CircuitoDTO;
import com.f1legends.modelo.circuitos.Circuito;
import com.f1legends.modelo.circuitos.CircuitoGenerico;
import com.f1legends.modelo.circuitos.CircuitoMonza;
import com.f1legends.modelo.circuitos.CircuitoSilverstone;

public class CircuitoFactory {
    public static Circuito crear(CircuitoDTO dto) {
        switch (dto.getNombre().toLowerCase()) {
            case "monza":
                return new CircuitoMonza(dto.getId(), dto.getNombre(), dto.getPais(), dto.getVueltas());
            case "silverstone":
                return new CircuitoSilverstone(dto.getId(), dto.getNombre(), dto.getPais(), dto.getVueltas());
            default:
                return new CircuitoGenerico(dto.getId(), dto.getNombre(), dto.getPais(), dto.getVueltas());
        }
    }
}
