package com.f1legends.patrones.estrategias;

import com.f1legends.modelo.Piloto.Piloto;

// Estrategia agresiva: mayor riesgo, mayor velocidad
public class EstrategiaAgresiva implements EstrategiaConduccion {
    @Override
    public double calcularRendimiento(Piloto piloto, double condicionesPista) {
        // rendimiento alto pero sensible a condiciones adversas
        return piloto.getHabilidad() * 1.2 * condicionesPista;
    }

    @Override
    public String getNombre() {
        return "Agresiva";
    }
}