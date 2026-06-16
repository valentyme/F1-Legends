package com.f1legends.vista;

import com.f1legends.DAO.modeloDAO.AutoDAO;
import com.f1legends.DAO.modeloDAO.CircuitoDAO;
import com.f1legends.DAO.modeloDAO.EscuderiaDAO;
import com.f1legends.DAO.modeloDAO.RankingGlobalDAO;
import com.f1legends.DAO.modeloDAO.UsuarioDAO;
import com.f1legends.controller.AutoController;
import com.f1legends.controller.CarreraController;
import com.f1legends.controller.EscuderiaController;
import com.f1legends.controller.PilotoController;
import com.f1legends.controller.RankingController;
import com.f1legends.controller.Sesion;
import com.f1legends.controller.UsuarioController;
import com.f1legends.modelo.Usuarios.Jugador;
import com.f1legends.modelo.Usuarios.Usuario;
import com.f1legends.patrones.facade.SistemaCarreraFacade;
import com.f1legends.servicios.AutoService;

import java.util.Scanner;

import static com.f1legends.vista.ConsoleUI.*;

/**
 * Lógica de consola extraída del Main original.
 * Se llama desde MainFX en un hilo separado para no bloquear JavaFX.
 */
public class MainConsola {

    public static void iniciar() {

        // ── DAOs ──────────────────────────────────────
        UsuarioDAO       usuarioDAO   = new UsuarioDAO();
        RankingGlobalDAO rankingDAO   = new RankingGlobalDAO();
        CircuitoDAO      circuitoDAO  = new CircuitoDAO();
        AutoDAO          autoDAO      = new AutoDAO();
        EscuderiaDAO     escuderiaDAO = new EscuderiaDAO();

        Scanner sc = new Scanner(System.in);

        // ── Controllers ───────────────────────────────
        UsuarioController usuarioController = new UsuarioController(usuarioDAO, rankingDAO, sc);
        RankingController rankingController = new RankingController(rankingDAO);
        CarreraController carreraController = new CarreraController(usuarioDAO, circuitoDAO, rankingController, sc);

        // ── Flujo principal ───────────────────────────
        cabecera();
        boolean corriendo = true;
        while (corriendo) {
            if (!Sesion.hayUsuarioLogueado()) {
                corriendo = menuPrincipal(sc, usuarioController);
            } else if (Sesion.esJugador()) {
                menuJugador(sc, usuarioController, carreraController);
            } else if (Sesion.esAdministrador()) {
                menuAdministrador(sc, usuarioController, autoDAO, escuderiaDAO);
            }
        }

        linea();
        System.out.println("  Gracias por jugar F1 Legends. ¡Hasta la próxima carrera!");
        linea();
    }

    // ════════════════════════════════════════════════
    // MENÚ PRINCIPAL (sin sesión)
    // ════════════════════════════════════════════════
    private static boolean menuPrincipal(Scanner sc, UsuarioController usuarioController) {
        linea();
        titulo("  F1 LEGENDS — MENÚ PRINCIPAL");
        linea();
        System.out.println("  [1] Iniciar sesión          (CU01)");
        System.out.println("  [2] Crear perfil             (CU02)");
        System.out.println("  [0] Salir");
        linea();
        System.out.print("  Opción: ");
        String op = sc.nextLine().trim();
        return switch (op) {
            case "1"  -> { usuarioController.cuIniciarSesion(); yield true; }
            case "2"  -> { usuarioController.cuCrearPerfil();   yield true; }
            case "0"  -> false;
            default   -> { msgError("Opción inválida."); yield true; }
        };
    }

    // ════════════════════════════════════════════════
    // MENÚ JUGADOR
    // ════════════════════════════════════════════════
    private static void menuJugador(Scanner sc,
                                    UsuarioController usuarioController,
                                    CarreraController carreraController) {
        Jugador j = Sesion.getJugadorActual();
        linea();
        titulo("  F1 LEGENDS — JUGADOR: " + j.getUsername().toUpperCase());
        linea();
        System.out.println("  [1] Jugar carrera");
        System.out.println("  [2] Ver ranking global       (CU03)");
        System.out.println("  [3] Modificar mi perfil      (CU05)");
        System.out.println("  [4] Eliminar mi cuenta       (CU06)");
        System.out.println("  [0] Cerrar sesión            (CU07)");
        linea();
        System.out.print("  Opción: ");
        switch (sc.nextLine().trim()) {
            case "1"  -> carreraController.flujoJugarCarrera(j);
            case "2"  -> usuarioController.cuVerRanking();
            case "3"  -> usuarioController.cuModificarPerfil();
            case "4"  -> usuarioController.cuEliminarPerfil(false);
            case "0"  -> usuarioController.cuCerrarSesion();
            default   -> msgError("Opción inválida.");
        }
    }

    // ════════════════════════════════════════════════
    // MENÚ ADMINISTRADOR
    // ════════════════════════════════════════════════
    private static void menuAdministrador(Scanner sc,
                                          UsuarioController usuarioController,
                                          AutoDAO autoDAO,
                                          EscuderiaDAO escuderiaDAO) {
        Usuario sesionActual = Sesion.getUsuarioActual();
        linea();
        titulo("  F1 LEGENDS — ADMINISTRADOR: " + sesionActual.getUsername().toUpperCase());
        linea();
        System.out.println("  [1] Ver ranking global         (CU03)");
        System.out.println("  [2] Eliminar perfil de usuario (CU06)");
        System.out.println("  [3] Listar todos los usuarios");
        System.out.println("  [4] Gestionar pilotos          (CU21)");
        System.out.println("  [5] Gestionar escuderías       (CU22)");
        System.out.println("  [6] Gestionar autos            (Factory Auto)");
        System.out.println("  [0] Cerrar sesión              (CU07)");
        linea();
        System.out.print("  Opción: ");
        switch (sc.nextLine().trim()) {
            case "1"  -> usuarioController.cuVerRanking();
            case "2"  -> usuarioController.cuEliminarPerfil(true);
            case "3"  -> usuarioController.listarUsuarios();
            case "4"  -> new PilotoController(new SistemaCarreraFacade(), autoDAO, sc).cuGestionarPilotos();
            case "5"  -> new EscuderiaController(new SistemaCarreraFacade(), escuderiaDAO, sc).cuGestionarEscuderias();
            case "6"  -> new AutoController(new AutoService(), escuderiaDAO, sc).cuGestionarAutos();
            case "0"  -> usuarioController.cuCerrarSesion();
            default   -> msgError("Opción inválida.");
        }
    }
}