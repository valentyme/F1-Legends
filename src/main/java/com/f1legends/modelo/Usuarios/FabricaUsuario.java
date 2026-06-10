package com.f1legends.modelo.Usuarios;

//import f1.modelo.Administrador;
//import f1.modelo.Jugador;
//import f1.modelo.Usuario;

/**
 * Fábrica que instancia el tipo de usuario correcto según el rol.
 *
 * Patrón: Factory Method
 * SOLID: OCP — agregar un nuevo rol solo requiere añadir un case aquí.
 *        SRP — única responsabilidad: construir usuarios.
 * GRASP: Creator — responsable de la creación de Jugador y Administrador.
 *        Low Coupling — el resto del sistema usa Usuario (interfaz del dominio).
 */
public class FabricaUsuario {

    // Clase utilitaria — no se instancia
    private FabricaUsuario() {}

    /**
     * Crea el subtipo correcto de Usuario según el rol recibido.
     *
     * @param id            id de la base de datos (0 si aún no persiste)
     * @param username      nombre de usuario
     * @param password      contraseña (ya hasheada o en texto, según implementación)
     * @param rol           "Jugador" | "Administrador"
     * @param fechaRegistro fecha en formato ISO (puede ser null)
     * @return instancia concreta de Usuario
     * @throws IllegalArgumentException si el rol no es reconocido
     */
    public static Usuario crear(int id, String username, String password,
                                String rol, String fechaRegistro) {
        return switch (rol) {
            case "Jugador"        -> new Jugador(id, username, password, fechaRegistro);
            case "Administrador"  -> new Administrador(id, username, password, fechaRegistro);
            default -> throw new IllegalArgumentException("Rol desconocido: " + rol);
        };
    }

    /**
     * Sobrecarga de conveniencia para crear un usuario nuevo (sin id asignado aún).
     */
    public static Usuario crear(String username, String password, String rol, String fechaRegistro) {
        return crear(0, username, password, rol, fechaRegistro);
    }
}
