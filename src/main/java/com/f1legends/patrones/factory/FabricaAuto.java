package com.f1legends.patrones.factory;

import com.f1legends.modelo.auto.Auto;
import com.f1legends.modelo.auto.AutoFerrari;
import com.f1legends.modelo.auto.AutoMercedes;
import com.f1legends.modelo.auto.AutoRedBull;

public class FabricaAuto {
    public Auto crearAuto(TipoAuto tipoAuto) {
        return switch (tipoAuto) {
            case FERRARI -> new AutoFerrari();
            case MERCEDES -> new AutoMercedes();
            case RED_BULL -> new AutoRedBull();
        };
    }
}
