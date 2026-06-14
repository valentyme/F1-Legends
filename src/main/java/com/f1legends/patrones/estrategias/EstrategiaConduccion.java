package com.f1legends.patrones.estrategias;

import com.f1legends.modelo.Piloto;

public interface EstrategiaConduccion {
    double calcularRendimiento(Piloto piloto, double condicionesPista);
    String getNombre();
}