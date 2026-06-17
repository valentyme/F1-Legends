package com.f1legends.modelo.auto;

import com.f1legends.modelo.Escuderias.Escuderia;
import com.f1legends.patrones.factory.TipoAuto;

public class AutoReglamento2022 extends Auto {
    public AutoReglamento2022(int id, String modelo, double velocidadBase, Escuderia escuderia) {
        super(id, modelo, velocidadBase, escuderia, TipoAuto.REGLAMENTO_2022);
    }
}
