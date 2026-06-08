package com.f1legends.patrones;

import com.f1legends.modelo.*;
import com.f1legends.servicios.*;


import java.util.List;

public class SistemaCarreraFacade {

    private ConfiguracionCarrera configuracionCarrera;
    private PilotoService pilotoService;
    private EscuderiaService escuderiaService;
    private CircuitoService circuitoService;
    private ModoJuegoService modoJuegoService;

    public SistemaCarreraFacade() {
        this.pilotoService = new PilotoService();
        this.escuderiaService = new EscuderiaService();
        this.circuitoService = new CircuitoService();
        this.modoJuegoService = new ModoJuegoService();
        this.configuracionCarrera = new ConfiguracionCarrera();
    }

    // CU08 - Seleccionar modo de juego
    public void seleccionarModoJuego(String modo) {
        modoJuegoService.seleccionarModo(modo);
        configuracionCarrera.setModoJuego(modoJuegoService.getModoSeleccionado());
    }
    public ConfiguracionCarrera getConfiguracionCarrera() {
        return configuracionCarrera;
    }

    // CU09 - Seleccionar piloto
    public void seleccionarPiloto(int pilotoId) {
        Piloto piloto = pilotoService.seleccionarPiloto(pilotoId); // validación y búsqueda
        configuracionCarrera.setPilotoSeleccionado(piloto);        // almacenamiento en configuración
    }


   // CU10 - Seleccionar participantes
    //public void seleccionarParticipantes(List<Integer> idsPilotos) {
      //  List<Piloto> participantes = pilotoService.obtenerParticipantes(idsPilotos);
        //configuracionCarrera.setParticipantes(participantes);
    //}
    //falta usuarios para que se puede hacer


    // CU11 - Seleccionar circuito
    public void seleccionarCircuito(int circuitoId) {
        Circuito circuito = circuitoService.obtenerCircuito(circuitoId); // devuelve un Circuito concreto
        configuracionCarrera.setCircuito(circuito); // ahora sí funciona
    }


    // CU12 - Configurar carrera
    public void configurarCarrera(int vueltas, String clima) {
        configuracionCarrera.setVueltas(vueltas);
        configuracionCarrera.setClimaInicial(clima);
    }

    // CU21 - Gestionar pilotos
    public void gestionarPilotos(Piloto piloto) {
        //pilotoService.gestionarPiloto(piloto);
    }

    // CU22 - Gestionar escuderías
    public void gestionarEscuderias(Escuderia escuderia) {
      //  escuderiaService.gestionarEscuderia(escuderia);
    }

    // CU23 - Administrar configuraciones
    public void administrarConfiguraciones(ConfiguracionCarrera config) {
        this.configuracionCarrera = config;
    }

    // Método final para iniciar la carrera
    public Carrera iniciarCarrera() {
        return new Carrera(configuracionCarrera);
    }
}
