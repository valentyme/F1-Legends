package com.f1legends.modelo.auto;

import com.f1legends.modelo.Escuderias.Escuderia;
import javafx.scene.paint.Color;

public class AutoFerrari extends Auto {
    public AutoFerrari() {
        super(1, "SF-24", 0.055, new Escuderia(1, "Ferrari", Color.RED));
    }
}
