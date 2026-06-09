package com.f1legends.patrones.factory;

import com.f1legends.modelo.Auto;
import com.f1legends.modelo.AutoFerrari;
import com.f1legends.modelo.AutoMercedes;
import com.f1legends.modelo.AutoRedBull;

public class FabricaAuto {
    public Auto crearAuto(TipoAuto tipoAuto) {
        return switch (tipoAuto) {
            case FERRARI -> new AutoFerrari();
            case MERCEDES -> new AutoMercedes();
            case RED_BULL -> new AutoRedBull();
        };
    }
}
