package com.f1legends.patrones.facade;

import com.f1legends.modelo.*;
import com.f1legends.modelo.Escuderias.Escuderia;
import com.f1legends.modelo.Usuarios.Jugador;
import com.f1legends.modelo.Usuarios.Participante;
import com.f1legends.modelo.carreras.Carrera;
import com.f1legends.modelo.circuitos.Circuito;
import com.f1legends.patrones.estrategias.EstrategiaConduccion;
import com.f1legends.servicios.*;

import java.util.List;

public class SistemaCarreraFacade {

    private ConfiguracionCarrera configuracionCarrera;
    private PilotoService        pilotoService;
    private EscuderiaService     escuderiaService;
    private CircuitoService      circuitoService;
    private ModoJuegoService     modoJuegoService;
    private ParticipanteService  participanteService;

    public SistemaCarreraFacade() {
        this.pilotoService       = new PilotoService();
        this.escuderiaService    = new EscuderiaService();
        this.circuitoService     = new CircuitoService();
        this.modoJuegoService    = new ModoJuegoService();
        this.participanteService = new ParticipanteService();
        this.configuracionCarrera = new ConfiguracionCarrera();
    }

    public ConfiguracionCarrera getConfiguracionCarrera() {
        return configuracionCarrera;
    }

    // ── CU08 ─────────────────────────────────────────
    public void seleccionarModoJuego(String modo) {
        modoJuegoService.seleccionarModo(modo);
        configuracionCarrera.setModoJuego(modoJuegoService.getModoSeleccionado());
    }

    // ── CU09 ─────────────────────────────────────────
    public void seleccionarPiloto(int pilotoId) {
        Piloto piloto = pilotoService.seleccionarPiloto(pilotoId);
        configuracionCarrera.setPilotoSeleccionado(piloto);

        Jugador jugadorPrincipal = configuracionCarrera.getJugadorPrincipal();
        if (jugadorPrincipal != null && piloto != null) {
            // Reemplazar la lista de participantes con el principal ya completo
            configuracionCarrera.setParticipantes(
                    List.of(new Participante(jugadorPrincipal, piloto))
            );
        }

    }

    // ── CU10 ─────────────────────────────────────────

    public void seleccionarParticipantes(List<Integer> idsUsuarios, List<Integer> idsPilotos) {
        List<Participante> adicionales = participanteService.crearParticipantes(idsUsuarios, idsPilotos);
        // Agregar a los participantes existentes (no reemplazar)
        for (Participante p : adicionales) {
            configuracionCarrera.agregarParticipante(p);
        }
    }

    // ── CU11 ─────────────────────────────────────────
    public void seleccionarCircuito(int circuitoId) {
        Circuito circuito = circuitoService.obtenerCircuito(circuitoId);
        configuracionCarrera.setCircuito(circuito);
    }

    // ── CU12 ─────────────────────────────────────────
    public void configurarCarrera(int vueltas, String clima) {
        configuracionCarrera.setVueltas(vueltas);
        configuracionCarrera.setClimaInicial(clima);
    }

    // ── Admin ─────────────────────────────────────────

    // ── CU21 ─────────────────────────────────────────
    public void gestionarPilotos(String operacion, Piloto piloto) {
        pilotoService.gestionarPilotos(operacion, piloto);
    }

    // ── CU22 ─────────────────────────────────────────
    // Métodos directos para gestionar escuderías
    public void altaEscuderia(String nombre, String colorHex) {
        escuderiaService.altaEscuderia(nombre, colorHex);
    }

    public void bajaEscuderia(int id) {
        escuderiaService.bajaEscuderia(id);
    }

    public void modificarEscuderia(int id, String nombre, String colorHex) {
        escuderiaService.modificarEscuderia(id, nombre, colorHex);
    }

    public List<Escuderia> consultarEscuderias() {
        return escuderiaService.consultarEscuderias();
    }

    // ── CUXX ─────────────────────────────────────────

    public boolean seleccionarEstrategiaConduccion(EstrategiaConduccion estrategia) {
        Piloto piloto = configuracionCarrera.getPilotoSeleccionado();
        if (piloto == null) {
            return false;
        }
        piloto.setEstrategiaConduccion(estrategia);
        return true;
    }

    // ── Inicio carrera ────────────────────────────────
    public Carrera iniciarCarrera() {
        return new Carrera(configuracionCarrera);
    }
}