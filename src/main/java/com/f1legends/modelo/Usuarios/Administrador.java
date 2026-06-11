package com.f1legends.modelo.Usuarios;
/**
 * Representa un usuario con rol Administrador.
 *
 * Patrón: Factory Method (producto concreto)
 * SOLID: OCP — extiende Usuario sin modificarlo.
 * GRASP: Low Coupling — no conoce persistencia.
 */
public class Administrador extends Usuario {

    public Administrador(int id, String username, String password, String fechaRegistro) {
        super(id, username, password, "Administrador", fechaRegistro);
    }

    @Override
    public String[] accionesDisponibles() {
        return new String[]{
                "CU01 - Iniciar sesión",
                "CU02 - Crear perfil",
                "CU03 - Ver ranking global",
                "CU06 - Eliminar cualquier perfil",
                "CU07 - Cerrar sesión"
        };
    }

    @Override
    public String toString() {
        return "[Administrador] " + super.toString();
    }
}


