package com.f1legends.modelo.carreras;

import com.f1legends.modelo.auto.Auto;
import com.f1legends.modelo.circuitos.Circuito;
import com.f1legends.patrones.estado.EstadoCarrera;
import com.f1legends.patrones.estado.EstadoInicio;
import com.f1legends.servicios.ConfiguracionCarrera;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Carrera {
    private int id;
    private Circuito circuito;
    private String fecha;
    private int vueltas;
    private String climaInicial;
    private List<Auto> autos;
    private EstadoCarrera estadoActual;

    public Carrera(int id, Circuito circuito, String fecha, int vueltas, String climaInicial) {
        this.id = id;
        this.circuito = circuito;
        this.fecha = fecha;
        this.vueltas = vueltas;
        this.climaInicial = climaInicial;
        this.autos = new ArrayList<>();
        this.estadoActual = new EstadoInicio();
    }

    public Carrera(ConfiguracionCarrera configuracionCarrera) {
        this.circuito = configuracionCarrera.getCircuito();
        this.vueltas = configuracionCarrera.getVueltas();
        this.climaInicial = configuracionCarrera.getClimaInicial();
        this.autos = new ArrayList<>();
        this.estadoActual = new EstadoInicio();
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
        estadoActual.abandonar(this);
    }

    public void cambiarEstado(EstadoCarrera nuevoEstado) {
        this.estadoActual = nuevoEstado;
    }

    public void agregarAuto(Auto auto) {
        autos.add(auto);
    }

    public void avanzarAutos(double deltaTiempo) {
        for (Auto auto : autos) {
            auto.avanzar(deltaTiempo);
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

    public int getId() { return id; }
    public Circuito getCircuito() { return circuito; }
    public String getFecha() { return fecha; }
    public int getVueltas() { return vueltas; }
    public String getClimaInicial() { return climaInicial; }
    public List<Auto> getAutos() { return autos; }
    public EstadoCarrera getEstadoActual() { return estadoActual; }
    public String getNombreEstado() { return estadoActual.getNombre(); }
}
