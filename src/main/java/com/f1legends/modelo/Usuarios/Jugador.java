package com.f1legends.modelo.Usuarios;

public class Jugador extends Usuario {
    private String fechaRegistro;
    private int puntosTotales;

    public Jugador(int id, String username, String password, String fechaRegistro) {
        super(id, username, password);
        this.fechaRegistro = fechaRegistro;
        this.puntosTotales = 0;
    }

    public void obtenerEstadisticas() {
        System.out.println("Estadísticas de " + username + ": " + puntosTotales + " puntos.");
    }
}
