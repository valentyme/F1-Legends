package com.f1legends.patrones.estrategias;

import com.f1legends.modelo.Piloto;

public class EstrategiaConservadora implements EstrategiaConduccion {
    @Override
    public double calcularRendimiento(Piloto piloto, double condicionesPista) {
        // rendimiento más bajo pero estable
        return piloto.getHabilidad() * 0.85 * condicionesPista;
    }

    @Override
    public String getNombre() {
        return "Conservadora";
    }
}