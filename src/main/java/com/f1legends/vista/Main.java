package com.f1legends.vista;

import com.f1legends.DAO.modeloDAO.CircuitoDAO;
import com.f1legends.DAO.modeloDAO.CircuitoDTO;
import com.f1legends.DAO.modeloDAO.PilotoDAO;
import com.f1legends.DAO.modeloDAO.RankingGlobalDAO;
import com.f1legends.DAO.modeloDAO.UsuarioDAO;
import com.f1legends.modelo.Piloto;
import com.f1legends.modelo.Usuarios.Administrador;
import com.f1legends.modelo.Usuarios.Jugador;
import com.f1legends.modelo.Usuarios.Usuario;
import com.f1legends.modelo.auto.Auto;
import com.f1legends.modelo.carreras.Carrera;
import com.f1legends.modelo.carreras.Ranking;
import com.f1legends.modelo.Escuderias.Escuderia;
import com.f1legends.patrones.circuitoFactory.CircuitoFactory;
import com.f1legends.patrones.factory.FabricaAuto;
import com.f1legends.patrones.factory.TipoAuto;
import com.f1legends.modelo.circuitos.Circuito;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Punto de entrada del programa F1 Legends.
 * Vista de consola que cubre: CU01, CU02, CU03, CU05, CU06, CU07
 * y el flujo de juego (CU08–CU12) para el rol Jugador.
 */
public class Main {

    // ── DAOs ──────────────────────────────────────
    private static final UsuarioDAO     usuarioDAO     = new UsuarioDAO();
    private static final RankingGlobalDAO rankingDAO   = new RankingGlobalDAO();
    private static final PilotoDAO      pilotoDAO      = new PilotoDAO();
    private static final CircuitoDAO    circuitoDAO    = new CircuitoDAO();

    // ── Sesión activa ─────────────────────────────
    private static Usuario sesionActual = null;

    private static final Scanner sc = new Scanner(System.in);

    // ════════════════════════════════════════════════
    // MAIN
    // ════════════════════════════════════════════════
    public static void main(String[] args) {
        cabecera();
        boolean corriendo = true;
        while (corriendo) {
            if (sesionActual == null) {
                corriendo = menuPrincipal();
            } else if (sesionActual instanceof Jugador) {
                menuJugador();
            } else if (sesionActual instanceof Administrador) {
                menuAdministrador();
            }
        }
        linea();
        System.out.println("  Gracias por jugar F1 Legends. ¡Hasta la próxima carrera!");
        linea();
    }

    // ════════════════════════════════════════════════
    // MENÚ PRINCIPAL (sin sesión)
    // ════════════════════════════════════════════════
    private static boolean menuPrincipal() {
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
            case "1"  -> { cuIniciarSesion();  yield true; }
            case "2"  -> { cuCrearPerfil();    yield true; }
            case "0"  -> false;
            default   -> { msgError("Opción inválida."); yield true; }
        };
    }

    // ════════════════════════════════════════════════
    // MENÚ JUGADOR
    // ════════════════════════════════════════════════
    private static void menuJugador() {
        Jugador j = (Jugador) sesionActual;
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
            case "1"  -> flujoJugarCarrera(j);
            case "2"  -> cuVerRanking();
            case "3"  -> cuModificarPerfil();
            case "4"  -> cuEliminarPerfil(false);
            case "0"  -> cuCerrarSesion();
            default   -> msgError("Opción inválida.");
        }
    }

    // ════════════════════════════════════════════════
    // MENÚ ADMINISTRADOR
    // ════════════════════════════════════════════════
    private static void menuAdministrador() {
        linea();
        titulo("  F1 LEGENDS — ADMINISTRADOR: " + sesionActual.getUsername().toUpperCase());
        linea();
        System.out.println("  [1] Ver ranking global       (CU03)");
        System.out.println("  [2] Eliminar perfil de usuario (CU06)");
        System.out.println("  [3] Listar todos los usuarios");
        System.out.println("  [0] Cerrar sesión            (CU07)");
        linea();
        System.out.print("  Opción: ");
        switch (sc.nextLine().trim()) {
            case "1"  -> cuVerRanking();
            case "2"  -> cuEliminarPerfil(true);
            case "3"  -> listarUsuarios();
            case "0"  -> cuCerrarSesion();
            default   -> msgError("Opción inválida.");
        }
    }

    // ════════════════════════════════════════════════
    // CU01 — INICIAR SESIÓN
    // ════════════════════════════════════════════════
    private static void cuIniciarSesion() {
        subtitulo("CU01 — INICIAR SESIÓN");
        System.out.print("  Username : ");
        String user = sc.nextLine().trim();
        System.out.print("  Password : ");
        String pass = sc.nextLine().trim();

        Optional<Usuario> resultado = usuarioDAO.buscarPorUsername(user);

        if (resultado.isEmpty()) {
            msgError("Usuario no encontrado.");
            return;
        }
        Usuario u = resultado.get();
        if (!u.getPassword().equals(pass)) {
            msgError("Contraseña incorrecta.");
            return;
        }
        sesionActual = u;
        u.login();
        msgOk("Bienvenido/a, " + u.getUsername() + "  [" + getRol(u) + "]");
    }

    // ════════════════════════════════════════════════
    // CU02 — CREAR PERFIL
    // ════════════════════════════════════════════════
    private static void cuCrearPerfil() {
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
            usuarioDAO.crear(user, pass, rol);
            msgOk("Perfil creado correctamente: " + user + " [" + rol + "]");
        } catch (RuntimeException e) {
            msgError("No se pudo crear el perfil: " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════
    // CU03 — VER RANKING GLOBAL
    // ════════════════════════════════════════════════
    private static void cuVerRanking() {
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

    // ════════════════════════════════════════════════
    // CU05 — MODIFICAR PERFIL (solo Jugador)
    // ════════════════════════════════════════════════
    private static void cuModificarPerfil() {
        subtitulo("CU05 — MODIFICAR PERFIL");
        System.out.println("  Dejá en blanco para conservar el valor actual.");
        System.out.print("  Nuevo username [actual: " + sesionActual.getUsername() + "] : ");
        String nuevoUser = sc.nextLine().trim();
        System.out.print("  Nueva password : ");
        String nuevoPass = sc.nextLine().trim();

        if (nuevoUser.isBlank() && nuevoPass.isBlank()) {
            System.out.println("  Sin cambios.");
            return;
        }
        String userFinal = nuevoUser.isBlank()  ? sesionActual.getUsername() : nuevoUser;
        String passFinal = nuevoPass.isBlank()  ? sesionActual.getPassword() : nuevoPass;

        try {
            usuarioDAO.actualizar(sesionActual.getId(), userFinal, passFinal);
            // Actualizar objeto en sesión
            sesionActual.setUsername(userFinal);
            sesionActual.setPassword(passFinal);
            msgOk("Perfil actualizado correctamente.");
        } catch (RuntimeException e) {
            msgError("Error al actualizar: " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════
    // CU06 — ELIMINAR PERFIL
    // ════════════════════════════════════════════════
    private static void cuEliminarPerfil(boolean esAdmin) {
        subtitulo("CU06 — ELIMINAR PERFIL");
        int idObjetivo;

        if (esAdmin) {
            listarUsuarios();
            System.out.print("  ID del usuario a eliminar: ");
            try { idObjetivo = Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { msgError("ID inválido."); return; }
        } else {
            // Jugador solo puede eliminar su propia cuenta
            idObjetivo = sesionActual.getId();
            System.out.print("  ¿Seguro que querés eliminar tu cuenta? [S/N]: ");
            if (!sc.nextLine().trim().equalsIgnoreCase("S")) {
                System.out.println("  Operación cancelada.");
                return;
            }
        }

        try {
            usuarioDAO.eliminar(idObjetivo);
            msgOk("Usuario id=" + idObjetivo + " eliminado junto a su historial de ranking.");
            // Si eliminó su propia cuenta, cerrar sesión
            if (sesionActual != null && sesionActual.getId() == idObjetivo) {
                sesionActual = null;
            }
        } catch (RuntimeException e) {
            msgError("Error al eliminar: " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════
    // CU07 — CERRAR SESIÓN
    // ════════════════════════════════════════════════
    private static void cuCerrarSesion() {
        subtitulo("CU07 — CERRAR SESIÓN");
        msgOk("Sesión cerrada. ¡Hasta la próxima, " + sesionActual.getUsername() + "!");
        sesionActual = null;
    }

    // ════════════════════════════════════════════════
    // FLUJO DE JUEGO — Jugador
    // ════════════════════════════════════════════════
    private static void flujoJugarCarrera(Jugador jugador) {
        subtitulo("PREPARANDO CARRERA");

        // CU09 — Seleccionar piloto
        List<Piloto> pilotos = PilotoDAO.obtenerTodos();
        if (pilotos.isEmpty()) { msgError("No hay pilotos disponibles en la BD."); return; }

        System.out.println("  PILOTOS DISPONIBLES:");
        for (Piloto p : pilotos) {
            System.out.printf("  [%2d] %-25s  Habilidad: %d%n", p.getId(), p.getNombre(), p.getHabilidad());
        }
        System.out.print("  Seleccioná el ID de tu piloto: ");
        int pilotoId;
        try { pilotoId = Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { msgError("ID inválido."); return; }

        Piloto pilotoElegido = PilotoDAO.obtenerPorId(pilotoId);
        if (pilotoElegido == null) { msgError("Piloto no encontrado."); return; }
        msgOk("Piloto seleccionado: " + pilotoElegido.getNombre());

        // CU11 — Seleccionar circuito
        List<CircuitoDTO> circuitos = circuitoDAO.obtenerTodos();
        if (circuitos.isEmpty()) { msgError("No hay circuitos en la BD."); return; }

        System.out.println("\n  CIRCUITOS DISPONIBLES:");
        for (CircuitoDTO c : circuitos) {
            System.out.printf("  [%2d] %-35s  %s  (%d vueltas)%n",
                    c.getId(), c.getNombre(), c.getPais(), c.getVueltas());
        }
        System.out.print("  Seleccioná el ID del circuito: ");
        int circuitoId;
        try { circuitoId = Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { msgError("ID inválido."); return; }

        CircuitoDTO circuitoDTO = circuitoDAO.obtenerPorId(circuitoId);
        if (circuitoDTO == null) { msgError("Circuito no encontrado."); return; }
        Circuito circuito = CircuitoFactory.crear(circuitoDTO);
        msgOk("Circuito: " + circuitoDTO.getNombre() + " — " + circuitoDTO.getPais());

        // CU12 — Configurar vueltas y clima
        System.out.print("\n  Número de vueltas [1-" + circuitoDTO.getVueltas() + "]: ");
        int vueltas;
        try { vueltas = Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { vueltas = 3; }
        vueltas = Math.max(1, Math.min(vueltas, circuitoDTO.getVueltas()));

        System.out.println("  Clima: [1] Soleado  [2] Lluvioso  [3] Nublado");
        System.out.print("  Opción: ");
        String clima = switch (sc.nextLine().trim()) {
            case "2" -> "Lluvioso";
            case "3" -> "Nublado";
            default  -> "Soleado";
        };

        // Armar la carrera con los autos del dataset
        simularCarrera(jugador, pilotoElegido, circuito, circuitoDTO.getNombre(), vueltas, clima);
    }

    /**
     * Simula la carrera en consola usando la lógica real de Carrera + Auto.
     * Al terminar genera el Ranking y suma puntos al RankingGlobal.
     */
    private static void simularCarrera(Jugador jugador, Piloto pilotoElegido,
                                       Circuito circuito, String nombreCircuito,
                                       int vueltas, String clima) {
        linea();
        System.out.println("  *** CARRERA EN " + nombreCircuito.toUpperCase() + " — " + vueltas + " VUELTAS — " + clima.toUpperCase() + " ***");
        linea();

        // Autos rivales hardcodeados igual que en App.java
        FabricaAuto fabricaAuto = new FabricaAuto();
        Auto ferrari  = fabricaAuto.crearAuto(TipoAuto.FERRARI);
        Auto mercedes = fabricaAuto.crearAuto(TipoAuto.MERCEDES);
        Auto redbull  = fabricaAuto.crearAuto(TipoAuto.RED_BULL);
        Escuderia mclarenEsc = new Escuderia(4, "McLaren", Color.ORANGE);
        Auto mclaren  = new Auto(4, "MCL60", 0.052, mclarenEsc);

        // Auto del jugador basado en la habilidad del piloto elegido
        double velJugador = 0.046 + (pilotoElegido.getHabilidad() / 100.0) * 0.014;
        Escuderia escJugador = new Escuderia(99, jugador.getUsername(), Color.RED);
        Auto autoJugador = new Auto(99, pilotoElegido.getNombre(), velJugador, escJugador);

        Carrera carrera = new Carrera(1, circuito, java.time.LocalDate.now().toString(), vueltas, clima);
        carrera.agregarAuto(autoJugador);
        carrera.agregarAuto(ferrari);
        carrera.agregarAuto(mercedes);
        carrera.agregarAuto(redbull);
        carrera.agregarAuto(mclaren);
        carrera.iniciar();

        System.out.println("  Semáforo apagado... luces encendidas... ¡VERDE!");
        System.out.println();

        // Simulación por ticks hasta completar las vueltas
        final double DT = 0.05;
        int tick = 0;
        int ultimaVueltaMostrada = -1;

        while (!carrera.estaCompletada()) {
            carrera.actualizar(DT);
            tick++;

            // Mostrar progreso cada vuelta que complete el líder
            int vueltaLider = carrera.getPosiciones().get(0).getVueltasCompletadas();
            if (vueltaLider > ultimaVueltaMostrada && vueltaLider > 0) {
                ultimaVueltaMostrada = vueltaLider;
                System.out.printf("  Vuelta %d/%d — Líder: %s%n",
                        vueltaLider, vueltas,
                        carrera.getPosiciones().get(0).getModelo());
            }

            // Guardia de seguridad (evita loop infinito si circuito no tiene lógica)
            if (tick > 200_000) break;
        }

        // Resultados finales
        List<Auto> posiciones = carrera.getPosiciones();
        linea();
        System.out.println("  *** BANDERA A CUADROS ***");
        linea();
        System.out.printf("  %-5s %-25s %s%n", "POS", "PILOTO / AUTO", "VUELTAS");
        linea();

        int[] tablaPuntos = {25, 18, 15, 12, 10};
        int posicionJugador = -1;
        int puntosGanados   = 0;

        for (int i = 0; i < posiciones.size(); i++) {
            Auto a = posiciones.get(i);
            boolean esJugador = a.getId() == 99;
            String marca = esJugador ? " ← TÚ" : "";
            System.out.printf("  %-5s %-25s %d vueltas%s%n",
                    (i + 1) + "º", a.getModelo(), a.getVueltasCompletadas(), marca);

            if (esJugador) {
                posicionJugador = i + 1;
                puntosGanados   = (i < tablaPuntos.length) ? tablaPuntos[i] : 1;
            }
        }

        // Generar objeto Ranking (tal como está definido en el proyecto)
        Ranking ranking = new Ranking();
        ranking.setPosicion(posicionJugador);
        ranking.setPuntosTotales(puntosGanados);

        linea();
        System.out.printf("  Posición final : %dº%n",   ranking.getPosicion());
        System.out.printf("  Puntos ganados : %d pts%n", ranking.getPuntosTotales());

        // Guardar puntos en RankingGlobal
        if (ranking.getPuntosTotales() > 0) {
            rankingDAO.sumarPuntos(jugador.getId(), ranking.getPuntosTotales());
            msgOk("Puntos sumados al ranking global.");
        }

        System.out.println();
        System.out.print("  Presioná ENTER para continuar...");
        sc.nextLine();
    }

    // ════════════════════════════════════════════════
    // UTILIDADES
    // ════════════════════════════════════════════════

    private static void listarUsuarios() {
        subtitulo("LISTA DE USUARIOS");
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        if (usuarios.isEmpty()) { System.out.println("  Sin usuarios registrados."); return; }
        System.out.printf("  %-5s %-25s %-15s %s%n", "ID", "USERNAME", "ROL", "REGISTRO");
        linea();
        for (Usuario u : usuarios) {
            System.out.printf("  %-5d %-25s %-15s %s%n",
                    u.getId(), u.getUsername(), getRol(u),
                    (u instanceof Jugador j) ? j.getFechaRegistro() : "—");
        }
    }

    private static String getRol(Usuario u) {
        return (u instanceof Administrador) ? "Administrador" : "Jugador";
    }

    private static void cabecera() {
        System.out.println();
        linea();
        System.out.println("  ███████╗ ██╗      ██╗      ███████╗  ██████╗  ███████╗  ███╗  ██╗  ██████╗  ███████╗");
        System.out.println("  ██╔════╝ ██║      ██║      ██╔════╝ ██╔════╝  ██╔════╝  ████╗ ██║  ██╔══██╗ ██╔════╝");
        System.out.println("  █████╗   ██║      ██║      █████╗   ██║  ███╗ █████╗    ██╔██╗██║  ██║  ██║ ███████╗");
        System.out.println("  ██╔══╝   ██║      ██║      ██╔══╝   ██║   ██║ ██╔══╝    ██║╚████║  ██║  ██║ ╚════██║");
        System.out.println("  ██║      ███████╗ ███████╗ ███████╗ ╚██████╔╝ ███████╗  ██║ ╚███║  ██████╔╝ ███████║");
        System.out.println("  ╚═╝      ╚══════╝ ╚══════╝ ╚══════╝  ╚═════╝  ╚══════╝  ╚═╝  ╚══╝  ╚═════╝  ╚══════╝");
        System.out.println("                              F1 LEGENDS  v1.0  — Desarrollo de Software");
        linea();
    }

    private static void linea() {
        System.out.println("  " + "─".repeat(78));
    }

    private static void titulo(String texto) {
        System.out.println(texto);
    }

    private static void subtitulo(String texto) {
        System.out.println();
        System.out.println("  ▶ " + texto);
        System.out.println("  " + "·".repeat(50));
    }

    private static void msgOk(String msg) {
        System.out.println("  ✔ " + msg);
    }

    private static void msgError(String msg) {
        System.out.println("  ✘ ERROR: " + msg);
    }
}