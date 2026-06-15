package com.f1legends.modelo.carreras;

import com.f1legends.modelo.auto.Auto;
import com.f1legends.modelo.circuitos.Circuito;
import com.f1legends.patrones.estado.EstadoCarrera;
import com.f1legends.patrones.estado.EstadoFinalizada;
import com.f1legends.patrones.estado.EstadoInicio;
import com.f1legends.patrones.observer.ObservableCarrera;
import com.f1legends.patrones.observer.ObservadorCarrera;
import com.f1legends.servicios.ConfiguracionCarrera;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Carrera implements ObservableCarrera {
    private int id;
    private Circuito circuito;
    private String fecha;
    private int vueltas;
    private String climaInicial;
    private List<Auto> autos;
    private EstadoCarrera estadoActual;
    private String modoJuego;
    private final List<ObservadorCarrera> observadores;

    public Carrera(int id, Circuito circuito, String fecha, int vueltas, String climaInicial) {
        this.id = id;
        this.circuito = circuito;
        this.fecha = fecha;
        this.vueltas = vueltas;
        this.climaInicial = climaInicial;
        this.autos = new ArrayList<>();
        this.estadoActual = new EstadoInicio();
        this.observadores = new ArrayList<>();
    }

    public Carrera(ConfiguracionCarrera configuracionCarrera) {
        this.circuito = configuracionCarrera.getCircuito();
        this.vueltas = configuracionCarrera.getVueltas();
        this.climaInicial = configuracionCarrera.getClimaInicial();
        this.modoJuego = configuracionCarrera.getModoJuego();
        this.fecha = java.time.LocalDate.now().toString();
        this.autos = new ArrayList<>();
        this.estadoActual = new EstadoInicio();
        this.observadores = new ArrayList<>();
    }

    public void iniciar() {
        estadoActual.iniciar(this);
    }

    public void actualizar(double deltaTiempo) {
        estadoActual.actualizar(this, deltaTiempo);
    }

    public void pausar() {
        estadoActual.pausar(this);
    }

    public void reanudar() {
        estadoActual.reanudar(this);
    }

    public void abandonar() {
        notificarEvento("ABANDONO", "La carrera fue abandonada.");
        estadoActual.abandonar(this);
    }

    public void cambiarEstado(EstadoCarrera nuevoEstado) {
        this.estadoActual = nuevoEstado;
        notificarEvento("CAMBIO_ESTADO", "Estado de carrera: " + nuevoEstado.getNombre());
        if (nuevoEstado instanceof EstadoFinalizada) {
            notificarEvento("FINALIZACION", "La carrera finalizo.");
        }
    }

    public void agregarAuto(Auto auto) {
        autos.add(auto);
        notificarEvento("AUTO_AGREGADO", "Auto agregado a la carrera: " + auto.getModelo());
    }

    public void avanzarAutos(double deltaTiempo) {
        for (Auto auto : autos) {
            int vueltasAntes = auto.getVueltasCompletadas();
            auto.avanzar(deltaTiempo);
            if (auto.getVueltasCompletadas() > vueltasAntes) {
                notificarEvento(
                        "VUELTA_COMPLETADA",
                        auto.getModelo() + " completo la vuelta " + auto.getVueltasCompletadas(),
                        auto.getVueltasCompletadas()
                );
            }
        }
    }

    public boolean estaCompletada() {
        return autos.stream().anyMatch(auto -> auto.getVueltasCompletadas() >= vueltas);
    }

    public List<Auto> getPosiciones() {
        return autos.stream()
                .sorted(Comparator
                        .comparingInt(Auto::getVueltasCompletadas)
                        .thenComparingDouble(Auto::getProgreso)
                        .reversed())
                .toList();
    }

    @Override
    public void agregarObservador(ObservadorCarrera observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    @Override
    public void eliminarObservador(ObservadorCarrera observador) {
        observadores.remove(observador);
    }

    @Override
    public void notificarObservadores(EventoCarrera evento) {
        for (ObservadorCarrera observador : List.copyOf(observadores)) {
            observador.actualizar(evento, this);
        }
    }

    public void notificarEvento(String tipo, String descripcion) {
        notificarEvento(tipo, descripcion, getVueltaActual());
    }

    public void notificarEvento(String tipo, String descripcion, int vuelta) {
        notificarObservadores(new EventoCarrera(tipo, descripcion, vuelta));
    }

    private int getVueltaActual() {
        return autos.stream()
                .mapToInt(Auto::getVueltasCompletadas)
                .max()
                .orElse(0);
    }

    public int getId() { return id; }
    public Circuito getCircuito() { return circuito; }
    public String getFecha() { return fecha; }
    public int getVueltas() { return vueltas; }
    public String getClimaInicial() { return climaInicial; }
    public List<Auto> getAutos() { return autos; }
    public EstadoCarrera getEstadoActual() { return estadoActual; }
    public String getNombreEstado() { return estadoActual.getNombre(); }
    public String getModoJuego() { return modoJuego; }
}
