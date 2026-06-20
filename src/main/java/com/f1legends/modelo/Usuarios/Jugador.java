package com.f1legends.modelo.Usuarios;

import java.util.ArrayList;

public class Jugador extends Usuario {

    public Jugador(int id, String username, String password, String fechaRegistro) {
        super(id, username, password, "Jugador", fechaRegistro);
    }

    @Override
    public String[] accionesDisponibles() {
        return new String[]{
                "CU01 - Iniciar sesión",
                "CU02 - Crear perfil",
                "CU03 - Ver ranking global",
                "CU05 - Modificar perfil",
                "CU06 - Eliminar su propio perfil",
                "CU07 - Cerrar sesión"
        };
    }


    @Override
    public String toString() {
        return "[Jugador] " + super.toString();
    }
}
