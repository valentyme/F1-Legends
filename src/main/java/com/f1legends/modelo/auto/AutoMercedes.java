package com.f1legends.modelo.auto;

import com.f1legends.modelo.Escuderias.Escuderia;
import javafx.scene.paint.Color;

public class AutoMercedes extends Auto {
    public AutoMercedes() {
        super(2, "W15", 0.051, new Escuderia(2, "Mercedes", Color.SILVER));
    }
}
