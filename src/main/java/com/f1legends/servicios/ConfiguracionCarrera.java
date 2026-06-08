package com.f1legends.servicios;

import com.f1legends.modelo.Circuito;
import com.f1legends.modelo.Jugador;
import com.f1legends.modelo.Piloto;

import java.util.List;

public class ConfiguracionCarrera {
    private String modoJuego;
    private Piloto pilotoSeleccionado;       // CU09
    private List<Jugador> participantes;     // CU10
    private Circuito circuito;
    private int vueltas;
    private String climaInicial;

    // Constructor vacío
    public ConfiguracionCarrera() {}

    // Constructor completo (útil para tests o inicialización rápida)
    public ConfiguracionCarrera(String modoJuego, Piloto pilotoSeleccionado,
                                List<Jugador> participantes, Circuito circuito,
                                int vueltas, String climaInicial) {
        this.modoJuego = modoJuego;
        this.pilotoSeleccionado = pilotoSeleccionado;
        this.participantes = participantes;
        this.circuito = circuito;
        this.vueltas = vueltas;
        this.climaInicial = climaInicial;
    }
    // Getters y Setters
    public String getModoJuego() {
        return modoJuego;
    }

    public void setModoJuego(String modoJuego) {
        this.modoJuego = modoJuego;
    }

    public Piloto getPilotoSeleccionado() {
        return pilotoSeleccionado;
    }

    public void setPilotoSeleccionado(Piloto pilotoSeleccionado) {
        this.pilotoSeleccionado = pilotoSeleccionado;
    }

    public List<Jugador> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<Jugador> participantes) {
        this.participantes = participantes;
    }

    public Circuito getCircuito() {
        return circuito;
    }

    public void setCircuito(Circuito circuito) {
        this.circuito = circuito;
    }

    public int getVueltas() {
        return vueltas;
    }

    public void setVueltas(int vueltas) {
        this.vueltas = vueltas;
    }

    public String getClimaInicial() {
        return climaInicial;
    }

    public void setClimaInicial(String climaInicial) {
        this.climaInicial = climaInicial;
    }
}
