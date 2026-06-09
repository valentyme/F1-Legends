package com.f1legends.modelo.Usuarios;

public class Usuario {
    protected int id;
    protected String username;
    protected String password;

    public Usuario(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public void login() {
        System.out.println(username + " ha iniciado sesión.");
    }
}
