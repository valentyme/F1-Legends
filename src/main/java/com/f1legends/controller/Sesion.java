package com.f1legends.controller;

import com.f1legends.modelo.Usuarios.Administrador;
import com.f1legends.modelo.Usuarios.Jugador;
import com.f1legends.modelo.Usuarios.Usuario;


public class Sesion {

    private static Usuario usuarioActual = null;

    private Sesion() {}

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static boolean hayUsuarioLogueado() {
        return usuarioActual != null;
    }

    public static boolean esAdministrador() {
        return usuarioActual instanceof Administrador;
    }

    public static boolean esJugador() {
        return usuarioActual instanceof Jugador;
    }

    public static Jugador getJugadorActual() {
        return (Jugador) usuarioActual;
    }

    public static String getRol(Usuario u) {
        return (u instanceof Administrador) ? "Administrador" : "Jugador";
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }
}