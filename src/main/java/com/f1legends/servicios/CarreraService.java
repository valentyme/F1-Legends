package com.f1legends.servicios;

import com.f1legends.modelo.Auto;
import com.f1legends.modelo.Circuito;

import java.util.List;

public class CarreraService {
    private Circuito circuito;
    private List<Auto> autos;

    public CarreraService(Circuito circuito, List<Auto> autos) {
        this.circuito = circuito;
        this.autos = autos;
    }

    public void actualizar(double deltaTiempo) {
        for (Auto auto : autos) {
            auto.avanzar(deltaTiempo);
        }
    }

    public Circuito getCircuito() { return circuito; }
    public List<Auto> getAutos() { return autos; }
}

