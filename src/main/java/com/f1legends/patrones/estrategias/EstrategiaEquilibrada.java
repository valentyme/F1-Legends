package com.f1legends.patrones.estrategias;

import com.f1legends.modelo.Piloto;

public class EstrategiaEquilibrada implements EstrategiaConduccion {
    @Override
    public double calcularRendimiento(Piloto piloto, double condicionesPista) {
        return piloto.getHabilidad() * 1.0 * condicionesPista;
    }

    @Override
    public String getNombre() {
        return "Equilibrada";
    }
}