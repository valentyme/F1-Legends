package com.f1legends.controller;

import com.f1legends.DAO.modeloDAO.RankingGlobalDAO;
import com.f1legends.DAO.modeloDAO.UsuarioDAO;
import com.f1legends.controller.Sesion;
import com.f1legends.modelo.Usuarios.Jugador;
import com.f1legends.modelo.Usuarios.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static com.f1legends.vista.ConsoleUI.*;

/**
 * Controlador de los casos de uso relacionados al usuario:
 * login, registro, ranking, modificación y eliminación de perfil.
 *
 * La lógica de validación/negocio queda separada de la entrada/salida
 * por consola para poder reusarla desde JavaFX (los métodos devuelven
 * resultados/booleans en vez de leer directo de Scanner cuando es posible).
 */
public class UsuarioController {

    private final UsuarioDAO usuarioDAO;
    private final RankingGlobalDAO rankingDAO;
    private final Scanner sc;

    public UsuarioController(UsuarioDAO usuarioDAO, RankingGlobalDAO rankingDAO, Scanner sc) {
        this.usuarioDAO = usuarioDAO;
        this.rankingDAO = rankingDAO;
        this.sc = sc;
    }

    // ════════════════════════════════════════════════
    // CU01 — INICIAR SESIÓN
    // ════════════════════════════════════════════════
    public void cuIniciarSesion() {
        subtitulo("CU01 — INICIAR SESIÓN");
        System.out.print("  Username : ");
        String user = sc.nextLine().trim();
        System.out.print("  Password : ");
        String pass = sc.nextLine().trim();

        Optional<Usuario> resultado = iniciarSesion(user, pass);

        if (resultado.isEmpty()) {
            msgError("Usuario no encontrado o contraseña incorrecta.");
            return;
        }
        Usuario u = resultado.get();
        msgOk("Bienvenido/a, " + u.getUsername() + "  [" + Sesion.getRol(u) + "]");
    }

    /**
     * Lógica pura de autenticación, reutilizable desde JavaFX.
     * Si las credenciales son correctas, deja la sesión activa y devuelve el usuario.
     */
    public Optional<Usuario> iniciarSesion(String user, String pass) {
        Optional<Usuario> resultado = usuarioDAO.buscarPorUsername(user);
        if (resultado.isEmpty()) return Optional.empty();

        Usuario u = resultado.get();
        if (!u.getPassword().equals(pass)) return Optional.empty();

        Sesion.setUsuarioActual(u);
        u.login();
        return Optional.of(u);
    }

    // ════════════════════════════════════════════════
    // CU02 — CREAR PERFIL
    // ════════════════════════════════════════════════
    public void cuCrearPerfil() {
        subtitulo("CU02 — CREAR PERFIL");
        System.out.print("  Username  : ");
        String user = sc.nextLine().trim();
        if (user.isBlank()) { msgError("El username no puede estar vacío."); return; }

        System.out.print("  Password  : ");
        String pass = sc.nextLine().trim();
        if (pass.isBlank()) { msgError("La contraseña no puede estar vacía."); return; }

        System.out.print("  Rol [J = Jugador / A = Administrador] : ");
        String rolInput = sc.nextLine().trim().toUpperCase();
        String rol = switch (rolInput) {
            case "J" -> "Jugador";
            case "A" -> "Administrador";
            default  -> null;
        };
        if (rol == null) { msgError("Rol inválido."); return; }

        try {
            crearPerfil(user, pass, rol);
            msgOk("Perfil creado correctamente: " + user + " [" + rol + "]");
        } catch (RuntimeException e) {
            msgError("No se pudo crear el perfil: " + e.getMessage());
        }
    }

    /** Lógica pura de creación de perfil, reutilizable desde JavaFX. */
    public void crearPerfil(String user, String pass, String rol) {
        usuarioDAO.crear(user, pass, rol);
    }

    // ════════════════════════════════════════════════
    // CU03 — VER RANKING GLOBAL
    // ════════════════════════════════════════════════
    public void cuVerRanking() {
        subtitulo("CU03 — RANKING GLOBAL");
        List<RankingGlobalDAO.EntradaRanking> lista = rankingDAO.obtenerRankingGlobal();

        if (lista.isEmpty()) {
            System.out.println("  Sin resultados aún. ¡Jugá tu primera carrera!");
            return;
        }

        System.out.printf("  %-5s %-25s %s%n", "POS", "PILOTO / USUARIO", "PUNTOS");
        linea();
        String[] medallas = {"🥇", "🥈", "🥉"};
        for (int i = 0; i < lista.size(); i++) {
            RankingGlobalDAO.EntradaRanking e = lista.get(i);
            String pos = (i < 3) ? medallas[i] : "  " + (i + 1) + ".";
            System.out.printf("  %-5s %-25s %d pts%n", pos, e.username, e.puntaje);
        }
    }

    public List<RankingGlobalDAO.EntradaRanking> obtenerRankingGlobal() {
        return rankingDAO.obtenerRankingGlobal();
    }

    // ════════════════════════════════════════════════
    // CU05 — MODIFICAR PERFIL (solo Jugador)
    // ════════════════════════════════════════════════
    public void cuModificarPerfil() {
        subtitulo("CU05 — MODIFICAR PERFIL");
        Usuario sesionActual = Sesion.getUsuarioActual();
        System.out.println("  Dejá en blanco para conservar el valor actual.");
        System.out.print("  Nuevo username [actual: " + sesionActual.getUsername() + "] : ");
        String nuevoUser = sc.nextLine().trim();
        System.out.print("  Nueva password : ");
        String nuevoPass = sc.nextLine().trim();

        if (nuevoUser.isBlank() && nuevoPass.isBlank()) {
            System.out.println("  Sin cambios.");
            return;
        }

        try {
            modificarPerfil(nuevoUser, nuevoPass);
            msgOk("Perfil actualizado correctamente.");
        } catch (RuntimeException e) {
            msgError("Error al actualizar: " + e.getMessage());
        }
    }

    /**
     * Lógica pura de modificación de perfil del usuario en sesión.
     * Strings vacíos/blank mantienen el valor actual.
     */
    public void modificarPerfil(String nuevoUser, String nuevoPass) {
        Usuario sesionActual = Sesion.getUsuarioActual();
        String userFinal = nuevoUser.isBlank() ? sesionActual.getUsername() : nuevoUser;
        String passFinal = nuevoPass.isBlank() ? sesionActual.getPassword() : nuevoPass;

        usuarioDAO.actualizar(sesionActual.getId(), userFinal, passFinal);
        sesionActual.setUsername(userFinal);
        sesionActual.setPassword(passFinal);
    }

    // ════════════════════════════════════════════════
    // CU06 — ELIMINAR PERFIL
    // ════════════════════════════════════════════════
    public void cuEliminarPerfil(boolean esAdmin) {
        subtitulo("CU06 — ELIMINAR PERFIL");
        int idObjetivo;

        if (esAdmin) {
            listarUsuarios();
            System.out.print("  ID del usuario a eliminar: ");
            try { idObjetivo = Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { msgError("ID inválido."); return; }
        } else {
            idObjetivo = Sesion.getUsuarioActual().getId();
            System.out.print("  ¿Seguro que querés eliminar tu cuenta? [S/N]: ");
            if (!sc.nextLine().trim().equalsIgnoreCase("S")) {
                System.out.println("  Operación cancelada.");
                return;
            }
        }

        try {
            eliminarPerfil(idObjetivo);
            msgOk("Usuario id=" + idObjetivo + " eliminado junto a su historial de ranking.");
        } catch (RuntimeException e) {
            msgError("Error al eliminar: " + e.getMessage());
        }
    }

    /**
     * Lógica pura de eliminación de usuario. Si el usuario eliminado es
     * el que está en sesión, cierra la sesión.
     */
    public void eliminarPerfil(int idObjetivo) {
        usuarioDAO.eliminar(idObjetivo);
        Usuario sesionActual = Sesion.getUsuarioActual();
        if (sesionActual != null && sesionActual.getId() == idObjetivo) {
            Sesion.cerrarSesion();
        }
    }

    // ════════════════════════════════════════════════
    // CU07 — CERRAR SESIÓN
    // ════════════════════════════════════════════════
    public void cuCerrarSesion() {
        subtitulo("CU07 — CERRAR SESIÓN");
        msgOk("Sesión cerrada. ¡Hasta la próxima, " + Sesion.getUsuarioActual().getUsername() + "!");
        Sesion.cerrarSesion();
    }

    // ════════════════════════════════════════════════
    // Listado de usuarios
    // ════════════════════════════════════════════════
    public void listarUsuarios() {
        subtitulo("LISTA DE USUARIOS");
        List<Usuario> usuarios = obtenerUsuarios();
        if (usuarios.isEmpty()) { System.out.println("  Sin usuarios registrados."); return; }
        System.out.printf("  %-5s %-25s %-15s %s%n", "ID", "USERNAME", "ROL", "REGISTRO");
        linea();
        for (Usuario u : usuarios) {
            System.out.printf("  %-5d %-25s %-15s %s%n",
                    u.getId(), u.getUsername(), Sesion.getRol(u),
                    (u instanceof Jugador j) ? j.getFechaRegistro() : "—");
        }
    }

    public List<Usuario> obtenerUsuarios() {
        return usuarioDAO.listarTodos();
    }

    public List<Jugador> obtenerJugadores() {
        return usuarioDAO.listarJugadores();
    }

    public Optional<Usuario> buscarPorId(int id) {
        return usuarioDAO.buscarPorId(id);
    }
}