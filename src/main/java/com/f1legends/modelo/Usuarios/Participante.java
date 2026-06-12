package com.f1legends.modelo.Usuarios;

import com.f1legends.modelo.Piloto;

public class Participante {
    private Jugador jugador;
    private Piloto piloto;

    public Participante(Jugador jugador, Piloto piloto) {
        this.jugador = jugador;
        this.piloto = piloto;
    }

    public Jugador getJugador() { return jugador; }
    public Piloto getPiloto() { return piloto; }
}
