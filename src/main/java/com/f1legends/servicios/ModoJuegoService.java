package com.f1legends.servicios;

import java.util.Arrays;
import java.util.List;

public class ModoJuegoService {

    private String modoSeleccionado;
    private final List<String> modosDisponibles = Arrays.asList("Singleplayer", "Multijugador Local");

    // Seleccionar modo de juego
    public void seleccionarModo(String modo) {
        if (modosDisponibles.contains(modo)) {
            this.modoSeleccionado = modo;
            System.out.println("Modo de juego seleccionado: " + modo);
        } else {
            throw new IllegalArgumentException("Modo de juego no válido: " + modo);
        }
    }

    // Obtener el modo actual
    public String getModoSeleccionado() {
        return modoSeleccionado;
    }

    // Listar modos disponibles
    public List<String> getModosDisponibles() {
        return modosDisponibles;
    }
}
