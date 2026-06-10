package com.f1legends.modelo.Usuarios;

/**
 * Entidad base que representa un usuario del sistema.
 *
 * SOLID: OCP — clase abierta a extensión (Jugador / Administrador),
 *              cerrada a modificación.
 * GRASP: Low Coupling — no conoce SQL ni ninguna capa de persistencia.
 */
public abstract class Usuario {

    private int    id;
    private String username;
    private String password;
    private String rol;
    private String fechaRegistro;

    // ──────────────────────────────────────────────
    // Constructor
    // ──────────────────────────────────────────────
    public Usuario(int id, String username, String password, String rol, String fechaRegistro) {
        this.id            = id;
        this.username      = username;
        this.password      = password;
        this.rol           = rol;
        this.fechaRegistro = fechaRegistro;
    }

    // ──────────────────────────────────────────────
    // Método polimórfico obligatorio
    // ──────────────────────────────────────────────
    /** Devuelve las acciones disponibles para este tipo de usuario. */
    public abstract String[] accionesDisponibles();

    // ──────────────────────────────────────────────
    // Getters y Setters
    // ──────────────────────────────────────────────
    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }

    public String getUsername()             { return username; }
    public void setUsername(String u)       { this.username = u; }

    public String getPassword()             { return password; }
    public void setPassword(String p)       { this.password = p; }

    public String getRol()                  { return rol; }
    public void setRol(String rol)          { this.rol = rol; }

    public String getFechaRegistro()        { return fechaRegistro; }
    public void setFechaRegistro(String f)  { this.fechaRegistro = f; }

    public void login() {
        System.out.println(username + " ha iniciado sesión.");
    }

    @Override
    public String toString() {
        return String.format("Usuario{id=%d, username='%s', rol='%s', fechaRegistro='%s'}",
                id, username, rol, fechaRegistro);
    }
}
