package com.f1legends.modelo.Usuarios;

import com.f1legends.modelo.Piloto.Piloto;
import com.f1legends.modelo.auto.Auto;

public class Participante {
    private Jugador jugador;
    private Piloto piloto;
    private Auto auto;

    public Participante(Jugador jugador, Piloto piloto) {
        this(jugador, piloto, null);
    }

    public Participante(Jugador jugador, Piloto piloto, Auto auto) {
        this.jugador = jugador;
        this.piloto = piloto;
        this.auto = auto;
    }

    public Jugador getJugador() { return jugador; }
    public Piloto getPiloto() { return piloto; }
    public Auto getAuto() { return auto; }
    public void setAuto(Auto auto) { this.auto = auto; }
}
