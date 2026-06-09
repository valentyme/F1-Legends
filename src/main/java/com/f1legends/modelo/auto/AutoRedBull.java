package com.f1legends.modelo.auto;

import com.f1legends.modelo.Escuderias.Escuderia;
import javafx.scene.paint.Color;

public class AutoRedBull extends Auto {
    public AutoRedBull() {
        super(3, "RB20", 0.058, new Escuderia(3, "Red Bull", Color.DARKBLUE));
    }
}
