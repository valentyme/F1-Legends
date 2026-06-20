package com.f1legends.servicios;

import com.f1legends.DAO.modeloDAO.UsuarioDAO;
import com.f1legends.DAO.modeloDAO.PilotoDAO;
import com.f1legends.modelo.Usuarios.Jugador;
import com.f1legends.modelo.Usuarios.Participante;
import com.f1legends.modelo.Piloto.Piloto;

import java.util.ArrayList;
import java.util.List;

public class ParticipanteService {
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private PilotoDAO pilotoDAO = new PilotoDAO();

    public List<Participante> crearParticipantes(List<Integer> idsUsuarios, List<Integer> idsPilotos) {
        List<Participante> participantes = new ArrayList<>();
        for (int i = 0; i < idsUsuarios.size(); i++) {
            // Buscar usuario y castear a Jugador
            int finalI = i;
            usuarioDAO.buscarPorId(idsUsuarios.get(i)).ifPresent(usuario -> {
                if (usuario instanceof Jugador jugador) {
                    Piloto piloto = pilotoDAO.obtenerPorId(idsPilotos.get(finalI));
                    participantes.add(new Participante(jugador, piloto));
                } else {
                    throw new IllegalArgumentException("El usuario con ID " + idsUsuarios.get(finalI) + " no es un jugador");
                }
            });
        }
        return participantes;
    }
}

