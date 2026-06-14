package com.f1legends.vista;

import com.f1legends.DAO.modeloDAO.*;
import com.f1legends.modelo.Piloto;
import com.f1legends.modelo.Usuarios.Administrador;
import com.f1legends.modelo.Usuarios.Jugador;
import com.f1legends.modelo.Usuarios.Participante;
import com.f1legends.modelo.Usuarios.Usuario;
import com.f1legends.modelo.auto.Auto;
import com.f1legends.modelo.carreras.Carrera;
import com.f1legends.modelo.carreras.Ranking;
import com.f1legends.modelo.Escuderias.Escuderia;
import com.f1legends.patrones.circuitoFactory.CircuitoFactory;
import com.f1legends.patrones.fabricaEscuderia.FabricaEscuderia;
import com.f1legends.patrones.facade.SistemaCarreraFacade;
import com.f1legends.patrones.factory.FabricaAuto;
import com.f1legends.patrones.factory.TipoAuto;
import com.f1legends.modelo.circuitos.Circuito;
import com.f1legends.utiles.PaletaColoresDemo;
import javafx.scene.paint.Color;

import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;


public class Main {

    // ── DAOs ──────────────────────────────────────
    private static final UsuarioDAO       usuarioDAO  = new UsuarioDAO();
    private static final RankingGlobalDAO rankingDAO  = new RankingGlobalDAO();
    private static final PilotoDAO        pilotoDAO   = new PilotoDAO();
    private static final CircuitoDAO      circuitoDAO = new CircuitoDAO();

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
        System.out.println("  [4] Gestionar pilotos        (CU21)");
        System.out.println("  [5] Gestionar escuderías (CU22)");
        System.out.println("  [0] Cerrar sesión            (CU07)");
        linea();
        System.out.print("  Opción: ");
        switch (sc.nextLine().trim()) {
            case "1"  -> cuVerRanking();
            case "2"  -> cuEliminarPerfil(true);
            case "3"  -> listarUsuarios();
            case "4"  -> cuGestionarPilotos();
            case "5"  -> cuGestionarEscuderias();
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
        String userFinal = nuevoUser.isBlank() ? sesionActual.getUsername() : nuevoUser;
        String passFinal = nuevoPass.isBlank() ? sesionActual.getPassword() : nuevoPass;

        try {
            usuarioDAO.actualizar(sesionActual.getId(), userFinal, passFinal);
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
    // CU21 — Gestion Pilotos
    // ════════════════════════════════════════════════
    private static void cuGestionarPilotos() {
        SistemaCarreraFacade sistemaFacade = new SistemaCarreraFacade();
        AutoDAO autoDAO = new AutoDAO(); // DAO para buscar autos

        linea();
        titulo("  GESTIONAR PILOTOS (CU21)");
        linea();
        System.out.println("  [1] Alta de piloto");
        System.out.println("  [2] Baja de piloto");
        System.out.println("  [3] Modificar piloto");
        System.out.println("  [4] Consultar piloto");
        System.out.print("  Opción: ");
        String opcion = sc.nextLine().trim();

        Piloto piloto;
        switch (opcion) {
            case "1" -> {
                System.out.print("Nombre del piloto: ");
                String nombre = sc.nextLine();
                System.out.print("Habilidad: ");
                int habilidad = Integer.parseInt(sc.nextLine());
                mostrarEscuderias();
                System.out.print("ID de escudería: ");
                int escuderiaId = Integer.parseInt(sc.nextLine());

                // Buscar el auto asociado a la escudería
                Auto auto = autoDAO.obtenerPorEscuderiaId(escuderiaId);
                int autoId = auto.getId();
                piloto = new Piloto(0, nombre, habilidad, escuderiaId, autoId);
                sistemaFacade.gestionarPilotos("alta", piloto);

                mostrarPiloto(piloto);
            }
            case "2" -> {
                mostrarPilotos(); // mostrar antes de eliminar
                System.out.print("ID del piloto a eliminar: ");
                int id = Integer.parseInt(sc.nextLine());
                piloto = new Piloto(id, "", 0, 0, 0);
                sistemaFacade.gestionarPilotos("baja", piloto);
            }

            case "3" -> {
                mostrarPilotos(); // mostrar antes de modificar
                System.out.print("ID del piloto a modificar: ");
                int id = Integer.parseInt(sc.nextLine());
                System.out.print("Nuevo nombre: ");
                String nombre = sc.nextLine();
                System.out.print("Nueva habilidad: ");
                int habilidad = Integer.parseInt(sc.nextLine());
                System.out.print("Nuevo ID de escudería: ");
                int escuderiaId = Integer.parseInt(sc.nextLine());

                Auto auto = autoDAO.obtenerPorEscuderiaId(escuderiaId);
                int autoId = auto.getId();

                piloto = new Piloto(id, nombre, habilidad, escuderiaId, autoId);
                sistemaFacade.gestionarPilotos("modificacion", piloto);
            }

            case "4" -> {
                System.out.print("ID del piloto a consultar: ");
                int id = Integer.parseInt(sc.nextLine());
                piloto = new Piloto(id, "", 0, 0, 0);
                sistemaFacade.gestionarPilotos("consulta", piloto);
            }
            default -> msgError("Opción inválida.");
        }
    }

    private static void mostrarPilotos() {
        List<Piloto> pilotos = PilotoDAO.obtenerTodos(); // usa el método estático del DAO
        linea();
        titulo("  LISTA DE PILOTOS");
        linea();
        for (Piloto p : pilotos) {
            System.out.println("ID: " + p.getId() +
                    " | Nombre: " + p.getNombre() +
                    " | Habilidad: " + p.getHabilidad() +
                    " | Escudería: " + p.getEscuderiaId() +
                    " | Auto: " + p.getAutoId());
        }
        linea();
    }

    private static void mostrarPiloto(Piloto piloto) {
        linea();
        titulo("  PILOTO REGISTRADO");
        linea();
        System.out.println("ID: " + piloto.getId() +
                " | Nombre: " + piloto.getNombre() +
                " | Habilidad: " + piloto.getHabilidad() +
                " | Escudería: " + piloto.getEscuderiaId() +
                " | Auto: " + piloto.getAutoId());
        linea();
    }
    private static void mostrarEscuderias() {
        List<Escuderia> escuderias = EscuderiaDAO.obtenerTodos(); // usa tu DAO de escuderías
        linea();
        titulo("  LISTA DE ESCUDERÍAS");
        linea();
        for (Escuderia e : escuderias) {
            System.out.println("ID: " + e.getId() +
                    " | Nombre: " + e.getNombre() +
                    " | Color: " + e.getColor());
        }
        linea();
    }

    // ════════════════════════════════════════════════
    // CU22 — Gestion Escuderias
    // ════════════════════════════════════════════════
    private static void cuGestionarEscuderias() {
        // Similar a cuGestionarPilotos, pero usando EscuderiaDAO
        linea();
        titulo("  GESTIONAR ESCUDERÍAS (CU22)");
        linea();
        System.out.println("  [1] Alta de escudería");
        System.out.println("  [2] Baja de escudería");
        System.out.println("  [3] Modificar escudería");
        System.out.println("  [4] Consultar escudería");
        System.out.print("  Opción: ");
        String opcion = sc.nextLine().trim();

        EscuderiaDAO escuderiaDAO = new EscuderiaDAO();

        switch (opcion) {
            case "1" -> {
                System.out.print("Nombre: ");
                String nombre = sc.nextLine();
                Color color = PaletaColoresDemo.mostrarPicker();
                Escuderia escuderia = new Escuderia(0, nombre, color);
                escuderiaDAO.insertar(escuderia);

                System.out.println("Escudería registrada: " + nombre);
            }
            case "2" -> {
                mostrarEscuderias();
                System.out.print("ID de escudería a eliminar: ");
                int id = Integer.parseInt(sc.nextLine());
                escuderiaDAO.eliminar(id);
                System.out.println("Escudería eliminada.");
            }
            case "3" -> {
                mostrarEscuderias();
                System.out.print("ID de escudería a modificar: ");
                int id = Integer.parseInt(sc.nextLine());
                System.out.print("Nuevo nombre: ");
                String nombre = sc.nextLine();
                System.out.print("Nuevo color (hex #RRGGBB): ");
                String colorHex = sc.nextLine().trim();

                try {
                    Escuderia escuderia = new Escuderia(id, nombre, javafx.scene.paint.Color.web(colorHex));
                    escuderiaDAO.actualizar(escuderia); // usar actualizar, no insertar
                    System.out.println("✅ Escudería actualizada.");
                } catch (IllegalArgumentException e) {
                    System.out.println("⚠ Color inválido. Debe ser en formato #RRGGBB (ej: #FF0000).");
                }
            }

            case "4" -> {
                mostrarEscuderias();
            }
            default -> msgError("Opción inválida.");
        }
    }

    // ════════════════════════════════════════════════
    // FLUJO DE JUEGO — Jugador
    // ════════════════════════════════════════════════


    private static String seleccionarModoJuego(SistemaCarreraFacade facade) {
        subtitulo("CU08 — SELECCIONAR MODO DE JUEGO");
        System.out.println("  [1] Singleplayer");
        System.out.println("  [2] Multijugador Local");
        System.out.print("  Opción: ");
        String op = sc.nextLine().trim();

        switch (op) {
            case "1" -> {
                facade.seleccionarModoJuego("Singleplayer");
                msgOk("Modo seleccionado: Singleplayer");
                return "Singleplayer";
            }
            case "2" -> {
                facade.seleccionarModoJuego("Multijugador Local");
                msgOk("Modo seleccionado: Multijugador Local");
                return "Multijugador Local";
            }
            default -> {
                msgError("Opción inválida.");
                return null;
            }
        }
    }


    private static int pedirIdPiloto(List<Piloto> pilotos) {
        System.out.println("  PILOTOS DISPONIBLES:");
        for (Piloto p : pilotos) {
            System.out.printf("  [%2d] %-25s  Habilidad: %d%n",
                    p.getId(), p.getNombre(), p.getHabilidad());
        }
        System.out.print("  Seleccioná el ID de tu piloto: ");
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            msgError("ID inválido.");
            return -1;
        }
    }

    private static Piloto validarPilotoEnLista(int idPiloto, List<Piloto> pilotos) {
        return pilotos.stream()
                .filter(p -> p.getId() == idPiloto)
                .findFirst()
                .orElse(null);
    }

    private static Piloto seleccionarPilotoSingleplayer(SistemaCarreraFacade facade) {
        subtitulo("CU09 — SELECCIONAR PILOTO");

        List<Piloto> pilotos = PilotoDAO.obtenerTodos();
        if (pilotos.isEmpty()) {
            msgError("No hay pilotos disponibles en la BD.");
            return null;
        }

        int idPiloto = pedirIdPiloto(pilotos);
        if (idPiloto == -1) return null;

        Piloto pilotoElegido = validarPilotoEnLista(idPiloto, pilotos);
        if (pilotoElegido == null) {
            msgError("No existe un piloto con ese ID.");
            return null;
        }

        facade.seleccionarPiloto(idPiloto);


        Jugador jugadorPrincipal = (Jugador) sesionActual;

        if (facade.getConfiguracionCarrera().getJugadorPrincipal() == null) {
            facade.getConfiguracionCarrera().setJugadorPrincipal(jugadorPrincipal);
        }

        List<Participante> participantes = new ArrayList<>();
        participantes.add(new Participante(jugadorPrincipal, pilotoElegido));
        facade.getConfiguracionCarrera().setParticipantes(participantes);

        msgOk("Piloto seleccionado: " + pilotoElegido.getNombre());
        return pilotoElegido;
    }


    private static CircuitoDTO seleccionarCircuitoDTO() {
        subtitulo("CU11 — SELECCIONAR CIRCUITO");

        List<CircuitoDTO> circuitos = circuitoDAO.obtenerTodos();
        if (circuitos.isEmpty()) {
            msgError("No hay circuitos en la BD.");
            return null;
        }

        System.out.println("\n  CIRCUITOS DISPONIBLES:");
        for (CircuitoDTO c : circuitos) {
            System.out.printf("  [%2d] %-35s  %s  (%d vueltas)%n",
                    c.getId(), c.getNombre(), c.getPais(), c.getVueltas());
        }

        System.out.print("  Seleccioná el ID del circuito: ");
        int circuitoId;
        try {
            circuitoId = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            msgError("ID inválido.");
            return null;
        }

        CircuitoDTO elegido = circuitos.stream()
                .filter(c -> c.getId() == circuitoId)
                .findFirst()
                .orElse(null);

        if (elegido == null) {
            msgError("No existe un circuito con ese ID.");
            return null;
        }

        return elegido;
    }
    private static Circuito aplicarCircuito(SistemaCarreraFacade facade, CircuitoDTO dto) {
        facade.seleccionarCircuito(dto.getId());
        Circuito circuito = facade.getConfiguracionCarrera().getCircuito();
        if (circuito == null) {
            msgError("Error al cargar el circuito desde la configuración.");
            return null;
        }
        msgOk("Circuito: " + circuito.getNombre() + " — " + circuito.getPais());
        return circuito;
    }

    private static int pedirVueltas(int maxVueltas) {
        System.out.print("\n  Número de vueltas [1-" + maxVueltas + "]: ");
        int vueltas;
        try {
            vueltas = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            vueltas = 3;
        }
        return Math.max(1, Math.min(vueltas, maxVueltas));
    }

    private static String pedirClima() {
        System.out.println("  Clima: [1] Soleado  [2] Lluvioso  [3] Nublado");
        System.out.print("  Opción: ");
        return switch (sc.nextLine().trim()) {
            case "2" -> "Lluvioso";
            case "3" -> "Nublado";
            default  -> "Soleado";
        };
    }

    private static void configurarCarrera(SistemaCarreraFacade facade, int maxVueltas) {
        subtitulo("CU12 — CONFIGURAR CARRERA");
        int vueltas = pedirVueltas(maxVueltas);
        String clima = pedirClima();
        facade.configurarCarrera(vueltas, clima);
    }

    // ── Multijugador ─────────────────────────────────────────────────────────

    private static Jugador validarJugadorAdicional(int idUsuario,
                                                   Jugador jugadorPrincipal,
                                                   List<Integer> idsYaElegidos) {
        Optional<Usuario> opt = usuarioDAO.buscarPorId(idUsuario);
        if (opt.isEmpty() || !(opt.get() instanceof Jugador)) {
            msgError("No existe un jugador con ese ID.");
            return null;
        }
        if (idUsuario == jugadorPrincipal.getId()) {
            msgError("Ese usuario ya es el jugador principal. Elegí otro.");
            return null;
        }
        if (idsYaElegidos.contains(idUsuario)) {
            msgError("Ese usuario ya fue elegido. Elegí otro.");
            return null;
        }
        return (Jugador) opt.get();
    }


    private static Piloto validarPilotoAdicional(int idPiloto,
                                                 int idPilotoPrincipal,
                                                 List<Integer> idsPilotosYaElegidos) {
        Piloto piloto = PilotoDAO.obtenerPorId(idPiloto);
        if (piloto == null) {
            msgError("No existe un piloto con ese ID.");
            return null;
        }
        if (idPiloto == idPilotoPrincipal) {
            msgError("Ese piloto ya está asignado al jugador principal. Elegí otro.");
            return null;
        }
        if (idsPilotosYaElegidos.contains(idPiloto)) {
            msgError("Ese piloto ya fue elegido por otro jugador. Elegí otro.");
            return null;
        }
        return piloto;
    }


    private static int leerEntero(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            msgError("Número inválido.");
            return -1;
        }
    }

    private static boolean flujoMultijugador(SistemaCarreraFacade facade, Jugador jugadorPrincipal) {
        subtitulo("MODO MULTIJUGADOR LOCAL");

        // Paso 1: piloto del jugador principal
        List<Piloto> todosLosPilotos = PilotoDAO.obtenerTodos();
        if (todosLosPilotos.isEmpty()) {
            msgError("No hay pilotos disponibles en la BD.");
            return false;
        }

        int idPilotoPrincipal = pedirIdPiloto(todosLosPilotos);
        if (idPilotoPrincipal == -1) return false;

        Piloto pilotoPrincipal = validarPilotoEnLista(idPilotoPrincipal, todosLosPilotos);
        if (pilotoPrincipal == null) {
            msgError("No existe un piloto con ese ID.");
            return false;
        }

        facade.seleccionarPiloto(idPilotoPrincipal);


        if (facade.getConfiguracionCarrera().getJugadorPrincipal() == null) {
            facade.getConfiguracionCarrera().setJugadorPrincipal(jugadorPrincipal);
        }

        msgOk("Piloto asignado al jugador principal: " + pilotoPrincipal.getNombre());

        // Paso 2: mostrar jugadores disponibles
        List<Jugador> jugadoresDisponibles = usuarioDAO.listarJugadores();
        if (jugadoresDisponibles.isEmpty()) {
            msgError("No hay jugadores adicionales registrados. No se puede iniciar multijugador.");
            return false;
        }

        System.out.println("\n  JUGADORES DISPONIBLES:");
        for (Jugador j : jugadoresDisponibles) {
            System.out.printf("  [%d] %-20s%n", j.getId(), j.getUsername());
        }

        int cantidad = leerEntero("\n  ¿Cuántos jugadores adicionales participarán? ");
        if (cantidad <= 0) {
            msgError("Debe haber al menos 1 jugador adicional.");
            return false;
        }

        List<Integer> idsUsuarios  = new ArrayList<>();
        List<Integer> idsPilotos   = new ArrayList<>();


        for (int i = 0; i < cantidad; ) {
            System.out.printf("\n  Jugador adicional %d/%d%n", i + 1, cantidad);

            int idUsuario = leerEntero("  ID Usuario: ");
            if (idUsuario == -1) continue;

            Jugador jugadorAdicional = validarJugadorAdicional(idUsuario, jugadorPrincipal, idsUsuarios);
            if (jugadorAdicional == null) continue;

            int idPiloto = leerEntero("  ID Piloto: ");
            if (idPiloto == -1) continue;

            Piloto pilotoAdicional = validarPilotoAdicional(idPiloto, idPilotoPrincipal, idsPilotos);
            if (pilotoAdicional == null) continue;

            idsUsuarios.add(idUsuario);
            idsPilotos.add(idPiloto);
            i++;
        }

        List<Participante> participantes = new ArrayList<>();
        participantes.add(new Participante(jugadorPrincipal, pilotoPrincipal));
        facade.getConfiguracionCarrera().setParticipantes(participantes);

        facade.seleccionarParticipantes(idsUsuarios, idsPilotos);

        msgOk("Participantes adicionales asignados correctamente.");
        return true;
    }


    private static void flujoJugarCarrera(Jugador jugador) {
        SistemaCarreraFacade facade = new SistemaCarreraFacade();

        facade.getConfiguracionCarrera().setJugadorPrincipal(jugador);

        subtitulo("PREPARANDO CARRERA");

        String modo = seleccionarModoJuego(facade);
        if (modo == null) return;

        if ("Singleplayer".equals(modo)) {
            Piloto pilotoElegido = seleccionarPilotoSingleplayer(facade);
            if (pilotoElegido == null) return;

            CircuitoDTO dto = seleccionarCircuitoDTO();
            if (dto == null) return;

            Circuito circuito = aplicarCircuito(facade, dto);
            if (circuito == null) return;

            configurarCarrera(facade, dto.getVueltas());

            Carrera carrera = facade.iniciarCarrera();
            simularCarrera(jugador, carrera, pilotoElegido, facade);

        } else if ("Multijugador Local".equals(modo)) {
            boolean ok = flujoMultijugador(facade, jugador);
            if (!ok) return;

            CircuitoDTO dto = seleccionarCircuitoDTO();
            if (dto == null) return;

            Circuito circuito = aplicarCircuito(facade, dto);
            if (circuito == null) return;

            configurarCarrera(facade, dto.getVueltas());

            Carrera carrera = facade.iniciarCarrera();
            Piloto pilotoPrincipal = facade.getConfiguracionCarrera().getPilotoSeleccionado();
            simularCarrera(jugador, carrera, pilotoPrincipal, facade);
        }
    }

    // ════════════════════════════════════════════════
    // SIMULACIÓN DE CARRERA
    // ════════════════════════════════════════════════

    private static void simularCarrera(Jugador jugador, Carrera carrera,
                                       Piloto pilotoElegido, SistemaCarreraFacade facade) {
        if (carrera == null) {
            msgError("No se pudo iniciar la carrera.");
            return;
        }

        linea();
        subtitulo("SIMULANDO CARRERA");
        mostrarCabeceraCarrera(carrera);

        prepararAutosBase(carrera);
        prepararAutosParticipantes(carrera, facade);

        carrera.iniciar();
        System.out.println("  Semáforo apagado... luces encendidas... ¡VERDE!\n");

        simularVueltas(carrera);
        mostrarResultados(carrera, jugador);
    }

    private static void mostrarCabeceraCarrera(Carrera carrera) {
        System.out.println("  *** CARRERA EN "
                + carrera.getCircuito().getNombre().toUpperCase()
                + " — " + carrera.getVueltas() + " VUELTAS — "
                + carrera.getClimaInicial().toUpperCase() + " ***");
        linea();
    }

    private static void prepararAutosBase(Carrera carrera) {
        FabricaAuto fabricaAuto = new FabricaAuto();
        FabricaEscuderia fabricaEscuderia = new FabricaEscuderia();

        Auto ferrari  = fabricaAuto.crearAuto(TipoAuto.FERRARI);
        ferrari.setEscuderia(fabricaEscuderia.crearEscuderia(1));

        Auto mercedes = fabricaAuto.crearAuto(TipoAuto.MERCEDES);
        mercedes.setEscuderia(fabricaEscuderia.crearEscuderia(2));

        Auto redbull  = fabricaAuto.crearAuto(TipoAuto.RED_BULL);
        redbull.setEscuderia(fabricaEscuderia.crearEscuderia(3));

        Auto mclaren  = new Auto(4, "MCL60", 0.052, fabricaEscuderia.crearEscuderia(4));
        Auto alpine   = new Auto(5, "A524",  0.050, fabricaEscuderia.crearEscuderia(5));

        carrera.agregarAuto(ferrari);
        carrera.agregarAuto(mercedes);
        carrera.agregarAuto(redbull);
        carrera.agregarAuto(mclaren);
        carrera.agregarAuto(alpine);
    }

    private static void prepararAutosParticipantes(Carrera carrera, SistemaCarreraFacade facade) {
        List<Participante> participantes = facade.getConfiguracionCarrera().getParticipantes();

        if (participantes == null || participantes.isEmpty()) {
            msgError("No hay participantes registrados en la carrera.");
            return;
        }

        for (Participante p : participantes) {
            // Validación defensiva para evitar NullPointerException
            if (p == null) {
                msgError("Se encontró un participante nulo; se omite.");
                continue;
            }
            if (p.getJugador() == null) {
                msgError("Participante sin jugador asignado; se omite.");
                continue;
            }
            if (p.getPiloto() == null) {
                msgError("Participante sin piloto asignado (jugador: "
                        + p.getJugador().getUsername() + "); se omite.");
                continue;
            }

            double velocidad = 0.046 + (p.getPiloto().getHabilidad() / 100.0) * 0.014;
            Escuderia esc = new Escuderia(
                    p.getJugador().getId(),
                    p.getJugador().getUsername(),
                    Color.BLUE
            );
            Auto auto = new Auto(
                    p.getJugador().getId(),
                    p.getPiloto().getNombre(),
                    velocidad,
                    esc
            );
            carrera.agregarAuto(auto);
        }
    }

    private static void simularVueltas(Carrera carrera) {
        final double DT = 0.05;
        int tick = 0;
        int ultimaVueltaMostrada = -1;

        while (!carrera.estaCompletada()) {
            carrera.actualizar(DT);
            tick++;

            int vueltaLider = carrera.getPosiciones().get(0).getVueltasCompletadas();
            if (vueltaLider > ultimaVueltaMostrada && vueltaLider > 0) {
                ultimaVueltaMostrada = vueltaLider;
                System.out.printf("  Vuelta %d/%d — Líder: %s%n",
                        vueltaLider, carrera.getVueltas(),
                        carrera.getPosiciones().get(0).getModelo());
            }

            if (tick > 200_000) break; // seguridad
        }
    }

    private static void mostrarResultados(Carrera carrera, Jugador jugador) {
        List<Auto> posiciones = carrera.getPosiciones();
        linea();
        System.out.println("  *** BANDERA A CUADROS ***");
        linea();
        System.out.printf("  %-5s %-25s %s%n", "POS", "PILOTO / AUTO", "VUELTAS");
        linea();

        int[] tablaPuntos     = {25, 18, 15, 12, 10};
        int posicionJugador   = -1;
        int puntosGanados     = 0;

        for (int i = 0; i < posiciones.size(); i++) {
            Auto a = posiciones.get(i);
            boolean esJugador = (a.getId() == jugador.getId());
            String marca = esJugador ? " ← TÚ" : "";
            System.out.printf("  %-5s %-25s %d vueltas%s%n",
                    (i + 1) + "º", a.getModelo(), a.getVueltasCompletadas(), marca);

            if (esJugador) {
                posicionJugador = i + 1;
                puntosGanados   = (i < tablaPuntos.length) ? tablaPuntos[i] : 1;
            }
        }

        Ranking ranking = new Ranking();
        ranking.setPosicion(posicionJugador);
        ranking.setPuntosTotales(puntosGanados);

        linea();
        System.out.printf("  Posición final : %dº%n", ranking.getPosicion());
        System.out.printf("  Puntos ganados : %d pts%n", ranking.getPuntosTotales());

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