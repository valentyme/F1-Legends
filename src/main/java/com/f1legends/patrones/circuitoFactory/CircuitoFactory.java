package com.f1legends.patrones.circuitoFactory;

import com.f1legends.DAO.modeloDAO.CircuitoDTO;
import com.f1legends.modelo.circuitos.Circuito;
import com.f1legends.modelo.circuitos.CircuitoGenerico;
import com.f1legends.modelo.circuitos.CircuitoMonaco;
import com.f1legends.modelo.circuitos.CircuitoMonza;
import com.f1legends.modelo.circuitos.CircuitoSilverstone;
import com.f1legends.modelo.circuitos.CircuitoSpa;
import com.f1legends.modelo.circuitos.CircuitoSuzuka;

public class CircuitoFactory {
    public static Circuito crear(CircuitoDTO dto) {
        String nombre = normalizar(dto.getNombre());
        if (nombre.contains("monza")) {
            return new CircuitoMonza(dto.getId(), dto.getNombre(), dto.getPais(), dto.getVueltas());
        }
        if (nombre.contains("silverstone")) {
            return new CircuitoSilverstone(dto.getId(), dto.getNombre(), dto.getPais(), dto.getVueltas());
        }
        if (nombre.contains("spa")) {
            return new CircuitoSpa(dto.getId(), dto.getNombre(), dto.getPais(), dto.getVueltas());
        }
        if (nombre.contains("monaco")) {
            return new CircuitoMonaco(dto.getId(), dto.getNombre(), dto.getPais(), dto.getVueltas());
        }
        if (nombre.contains("suzuka")) {
            return new CircuitoSuzuka(dto.getId(), dto.getNombre(), dto.getPais(), dto.getVueltas());
        }
        return new CircuitoGenerico(dto.getId(), dto.getNombre(), dto.getPais(), dto.getVueltas());
    }

    private static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
    }
}
