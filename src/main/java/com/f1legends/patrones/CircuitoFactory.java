package com.f1legends.patrones;

import com.f1legends.DAO.modeloDAO.CircuitoDTO;
import com.f1legends.modelo.*;

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
