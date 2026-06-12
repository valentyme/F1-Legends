package com.f1legends.servicios;

import com.f1legends.modelo.Usuarios.Jugador;
import com.f1legends.modelo.Usuarios.Participante;
import com.f1legends.modelo.circuitos.Circuito;
import com.f1legends.modelo.Piloto;

import java.util.ArrayList;
import java.util.List;

public class ConfiguracionCarrera {
    private String modoJuego;
    private Piloto pilotoSeleccionado;
    private List<Participante> participantes;
    private Circuito circuito;
    private int vueltas;
    private String climaInicial;
    private Jugador jugadorPrincipal;   // ahora privado con setter

    public ConfiguracionCarrera() {
        this.participantes = new ArrayList<>();
    }

    public ConfiguracionCarrera(String modoJuego, Piloto pilotoSeleccionado,
                                List<Participante> participantes, Circuito circuito,
                                int vueltas, String climaInicial) {
        this.modoJuego = modoJuego;
        this.pilotoSeleccionado = pilotoSeleccionado;
        this.participantes = participantes;
        this.circuito = circuito;
        this.vueltas = vueltas;
        this.climaInicial = climaInicial;
    }

    // ── Getters / Setters ─────────────────────────
    public String getModoJuego()                        { return modoJuego; }
    public void   setModoJuego(String modoJuego)        { this.modoJuego = modoJuego; }

    public Piloto getPilotoSeleccionado()               { return pilotoSeleccionado; }
    public void   setPilotoSeleccionado(Piloto p)       { this.pilotoSeleccionado = p; }

    public List<Participante> getParticipantes()        { return participantes; }
    public void setParticipantes(List<Participante> p)  { this.participantes = p; }

    public Circuito getCircuito()                       { return circuito; }
    public void     setCircuito(Circuito circuito)      { this.circuito = circuito; }

    public int  getVueltas()                            { return vueltas; }
    public void setVueltas(int vueltas)                 { this.vueltas = vueltas; }

    public String getClimaInicial()                     { return climaInicial; }
    public void   setClimaInicial(String c)             { this.climaInicial = c; }

    public Jugador getJugadorPrincipal()                { return jugadorPrincipal; }

    /**
     * Registra el jugador principal en la sesión de configuración.
     * NO crea ningún Participante aquí — eso ocurre cuando el piloto
     * ya está elegido (ver SistemaCarreraFacade.seleccionarPiloto).
     */
    public void setJugadorPrincipal(Jugador jugador)    { this.jugadorPrincipal = jugador; }

    public void agregarParticipante(Participante p) {
        if (participantes == null) participantes = new ArrayList<>();
        participantes.add(p);
    }

    public boolean estaCompleta() {
        return modoJuego != null && pilotoSeleccionado != null &&
                circuito != null && vueltas > 0 && climaInicial != null;
    }
}
