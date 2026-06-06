package com.f1legends.modelo;

public class Administrador extends Usuario {
    public Administrador(int id, String username, String password) {
        super(id, username, password);
    }

    public void gestionarSistema() {
        System.out.println("El administrador " + username + " gestiona el sistema.");
    }
}

