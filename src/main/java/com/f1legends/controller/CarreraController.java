package com.f1legends.controller;

import com.f1legends.DAO.modeloDAO.AutoDAO;
import com.f1legends.DAO.modeloDAO.CircuitoDAO;
import com.f1legends.DAO.modeloDAO.CircuitoDTO;
import com.f1legends.DAO.modeloDAO.PilotoDAO;
import com.f1legends.DAO.modeloDAO.UsuarioDAO;
import com.f1legends.modelo.Piloto;
import com.f1legends.modelo.Usuarios.Jugador;
import com.f1legends.modelo.Usuarios.Participante;
import com.f1legends.modelo.Usuarios.Usuario;
import com.f1legends.modelo.auto.Auto;
import com.f1legends.modelo.carreras.Carrera;
import com.f1legends.modelo.carreras.Ranking;
import com.f1legends.modelo.circuitos.Circuito;
import com.f1legends.patrones.estrategias.EstrategiaAgresiva;
import com.f1legends.patrones.estrategias.EstrategiaConduccion;
import com.f1legends.patrones.estrategias.EstrategiaConservadora;
import com.f1legends.patrones.estrategias.EstrategiaEquilibrada;
import com.f1legends.patrones.facade.SistemaCarreraFacade;
import com.f1legends.patrones.observer.EstadisticaObserver;
import com.f1legends.vista.CarreraFXWindow;
import com.f1legends.vista.MainFX;
import javafx.application.Platform;
import java.util.concurrent.CountDownLatch;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

import static com.f1legends.vista.ConsoleUI.*;

/**
 * Controlador del flujo completo de juego: selección de modo, piloto,
 * estrategia, circuito, configuración, y simulación de la carrera.
 * Agrupa CU08, CU09, CU11, CU12 y la simulación.
 */
public class CarreraController {

    private final UsuarioDAO usuarioDAO;
    private final CircuitoDAO circuitoDAO;
    private final AutoDAO autoDAO;
    private final RankingController rankingController;
    private final Scanner sc;

    public CarreraController(UsuarioDAO usuarioDAO, CircuitoDAO circuitoDAO,
                             RankingController rankingController, Scanner sc) {
        this.usuarioDAO = usuarioDAO;
        this.circuitoDAO = circuitoDAO;
        this.autoDAO = new AutoDAO();
        this.rankingController = rankingController;
        this.sc = sc;
    }

    // ════════════════════════════════════════════════
    // FLUJO PRINCIPAL
    // ════════════════════════════════════════════════
    public void flujoJugarCarrera(Jugador jugador) {
        SistemaCarreraFacade facade = new SistemaCarreraFacade();
        facade.getConfiguracionCarrera().setJugadorPrincipal(jugador);

        subtitulo("PREPARANDO CARRERA");

        String modo = seleccionarModoJuego(facade);
        if (modo == null) return;

        if ("Singleplayer".equals(modo)) {
            Piloto pilotoElegido = seleccionarPilotoSingleplayer(facade);
            if (pilotoElegido == null) return;
            seleccionarEstrategia(facade);
            if (!seleccionarAutosParticipantes(facade)) return;
            if (!completarGrillaConCpu(facade, 6)) return;

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
            seleccionarEstrategia(facade);
            if (!seleccionarAutosParticipantes(facade)) return;
            if (!completarGrillaConCpu(facade, 6)) return;

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
    // CU08 — SELECCIONAR MODO DE JUEGO
    // ════════════════════════════════════════════════
    public String seleccionarModoJuego(SistemaCarreraFacade facade) {
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

    // ════════════════════════════════════════════════
    // CU09 — SELECCIONAR PILOTO (Singleplayer)
    // ════════════════════════════════════════════════
    public Piloto seleccionarPilotoSingleplayer(SistemaCarreraFacade facade) {
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

        Jugador jugadorPrincipal = (Jugador) Sesion.getUsuarioActual();

        if (facade.getConfiguracionCarrera().getJugadorPrincipal() == null) {
            facade.getConfiguracionCarrera().setJugadorPrincipal(jugadorPrincipal);
        }

        List<Participante> participantes = new ArrayList<>();
        participantes.add(new Participante(jugadorPrincipal, pilotoElegido));
        facade.getConfiguracionCarrera().setParticipantes(participantes);

        msgOk("Piloto seleccionado: " + pilotoElegido.getNombre());
        return pilotoElegido;
    }

    public int pedirIdPiloto(List<Piloto> pilotos) {
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

    public Piloto validarPilotoEnLista(int idPiloto, List<Piloto> pilotos) {
        return pilotos.stream()
                .filter(p -> p.getId() == idPiloto)
                .findFirst()
                .orElse(null);
    }

    // ════════════════════════════════════════════════
    // CU11 — SELECCIONAR CIRCUITO
    // ════════════════════════════════════════════════
    public CircuitoDTO seleccionarCircuitoDTO() {
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

    public Circuito aplicarCircuito(SistemaCarreraFacade facade, CircuitoDTO dto) {
        facade.seleccionarCircuito(dto.getId());
        Circuito circuito = facade.getConfiguracionCarrera().getCircuito();
        if (circuito == null) {
            msgError("Error al cargar el circuito desde la configuración.");
            return null;
        }
        msgOk("Circuito: " + circuito.getNombre() + " — " + circuito.getPais());
        return circuito;
    }

    // ════════════════════════════════════════════════
    // CU12 — CONFIGURAR CARRERA
    // ════════════════════════════════════════════════
    public void configurarCarrera(SistemaCarreraFacade facade, int maxVueltas) {
        subtitulo("CU12 — CONFIGURAR CARRERA");
        int vueltas = pedirVueltas(maxVueltas);
        String clima = pedirClima();
        facade.configurarCarrera(vueltas, clima);
    }

    public int pedirVueltas(int maxVueltas) {
        System.out.print("\n  Número de vueltas [1-" + maxVueltas + "]: ");
        int vueltas;
        try {
            vueltas = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            vueltas = 3;
        }
        return Math.max(1, Math.min(vueltas, maxVueltas));
    }

    public String pedirClima() {
        System.out.println("  Clima: [1] Soleado  [2] Lluvioso  [3] Nublado");
        System.out.print("  Opción: ");
        return switch (sc.nextLine().trim()) {
            case "2" -> "Lluvioso";
            case "3" -> "Nublado";
            default  -> "Soleado";
        };
    }

    // ════════════════════════════════════════════════
    // Estrategia de conducción
    // ════════════════════════════════════════════════
    public void seleccionarEstrategia(SistemaCarreraFacade facade) {
        System.out.println("Seleccioná estrategia de conducción:");
        System.out.println("  [1] Agresiva");
        System.out.println("  [2] Equilibrada");
        System.out.println("  [3] Conservadora");
        System.out.print("  Opción: ");
        String op = sc.nextLine().trim();

        EstrategiaConduccion estrategia = switch (op) {
            case "1" -> new EstrategiaAgresiva();
            case "2" -> new EstrategiaEquilibrada();
            case "3" -> new EstrategiaConservadora();
            default  -> null;
        };

        if (estrategia != null) {
            boolean asignada = facade.seleccionarEstrategiaConduccion(estrategia);
            if (asignada) {
                System.out.println("✅ Estrategia seleccionada: " + estrategia.getNombre());
            } else {
                System.out.println("⚠ No hay piloto seleccionado. No se pudo asignar la estrategia.");
            }
        } else {
            System.out.println("⚠ Opción inválida. Se usará estrategia por defecto.");
        }
    }

    public boolean seleccionarAutosParticipantes(SistemaCarreraFacade facade) {
        List<Participante> participantes = facade.getConfiguracionCarrera().getParticipantes();
        if (participantes == null || participantes.isEmpty()) {
            msgError("No hay participantes para seleccionar autos.");
            return false;
        }

        subtitulo("SELECCIONAR AUTO");
        for (Participante participante : participantes) {
            if (participante == null || participante.getJugador() == null || participante.getPiloto() == null) {
                msgError("Hay un participante incompleto. No se puede seleccionar auto.");
                return false;
            }

            Auto auto = seleccionarAutoParaPiloto(participante.getPiloto(), participante.getJugador());
            if (auto == null) {
                return false;
            }

            participante.setAuto(copiarAutoParaCarrera(
                    auto,
                    participante.getJugador().getId(),
                    participante.getPiloto().getNombre()
            ));
            msgOk(participante.getJugador().getUsername() + " corre con " + auto.getModelo()
                    + " - " + auto.getTipoAuto());
        }
        return true;
    }

    private Auto seleccionarAutoParaPiloto(Piloto piloto, Jugador jugador) {
        List<Auto> autos = autoDAO.obtenerTodosPorEscuderiaId(piloto.getEscuderiaId());
        if (autos.isEmpty()) {
            msgError("La escuderia del piloto " + piloto.getNombre() + " no tiene autos cargados.");
            return null;
        }

        System.out.println("\n  " + jugador.getUsername() + " - Piloto: " + piloto.getNombre());
        System.out.println("  AUTOS DISPONIBLES:");
        for (int i = 0; i < autos.size(); i++) {
            Auto auto = autos.get(i);
            System.out.printf("  [%d] %-20s %-18s Velocidad: %.3f%n",
                    i + 1,
                    auto.getModelo(),
                    auto.getTipoAuto(),
                    auto.getVelocidadBase());
        }

        System.out.print("  Selecciona el auto: ");
        try {
            int opcion = Integer.parseInt(sc.nextLine().trim());
            if (opcion < 1 || opcion > autos.size()) {
                msgError("No existe un auto con esa opcion.");
                return null;
            }
            return autos.get(opcion - 1);
        } catch (NumberFormatException e) {
            msgError("Opcion invalida.");
            return null;
        }
    }

    // ════════════════════════════════════════════════
    // MULTIJUGADOR
    // ════════════════════════════════════════════════
    public boolean completarGrillaConCpu(SistemaCarreraFacade facade, int minimoParticipantes) {
        List<Participante> participantes = facade.getConfiguracionCarrera().getParticipantes();
        if (participantes == null) {
            msgError("No hay participantes para completar la grilla.");
            return false;
        }

        Set<Integer> pilotosElegidos = new HashSet<>();
        for (Participante participante : participantes) {
            if (participante != null && participante.getPiloto() != null) {
                pilotosElegidos.add(participante.getPiloto().getId());
            }
        }

        List<Piloto> candidatos = PilotoDAO.obtenerTodos().stream()
                .filter(piloto -> !pilotosElegidos.contains(piloto.getId()))
                .toList();

        int cpuId = -1;
        for (Piloto pilotoCpu : candidatos) {
            if (participantes.size() >= minimoParticipantes) {
                break;
            }

            Auto autoCpu = elegirAutoCpu(pilotoCpu, cpuId);
            if (autoCpu == null) {
                continue;
            }

            Jugador jugadorCpu = new Jugador(cpuId, "CPU - " + pilotoCpu.getNombre(), "", "CPU");
            participantes.add(new Participante(jugadorCpu, pilotoCpu, autoCpu));
            pilotosElegidos.add(pilotoCpu.getId());
            cpuId--;
        }

        if (participantes.size() < 2) {
            msgError("No hay suficientes pilotos con autos cargados para disputar una carrera.");
            return false;
        }

        if (participantes.size() < minimoParticipantes) {
            msgError("La grilla tiene solo " + participantes.size()
                    + " participantes. Carga mas pilotos/autos para una carrera completa.");
            return false;
        }

        msgOk("Rivales CPU cargados desde SQLite. Grilla: " + participantes.size() + " autos.");
        return true;
    }

    private Auto elegirAutoCpu(Piloto piloto, int idCarrera) {
        return autoDAO.obtenerTodosPorEscuderiaId(piloto.getEscuderiaId()).stream()
                .max(Comparator.comparingDouble(Auto::getVelocidadBase))
                .map(auto -> copiarAutoParaCarrera(auto, idCarrera, piloto.getNombre()))
                .orElse(null);
    }

    private Auto copiarAutoParaCarrera(Auto auto, int idCarrera, String pilotoNombre) {
        return new Auto(
                idCarrera,
                pilotoNombre + " / " + auto.getModelo(),
                auto.getVelocidadBase(),
                auto.getEscuderia(),
                auto.getTipoAuto()
        );
    }

    public boolean flujoMultijugador(SistemaCarreraFacade facade, Jugador jugadorPrincipal) {
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

    public Jugador validarJugadorAdicional(int idUsuario, Jugador jugadorPrincipal, List<Integer> idsYaElegidos) {
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

    public Piloto validarPilotoAdicional(int idPiloto, int idPilotoPrincipal, List<Integer> idsPilotosYaElegidos) {
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

    public int leerEntero(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            msgError("Número inválido.");
            return -1;
        }
    }

    // ════════════════════════════════════════════════
    // SIMULACIÓN DE CARRERA
    // ════════════════════════════════════════════════
    /*
    public void simularCarrera(Jugador jugador, Carrera carrera, Piloto pilotoElegido, SistemaCarreraFacade facade) {
        if (carrera == null) {
            msgError("No se pudo iniciar la carrera.");
            return;
        }

        linea();
        subtitulo("SIMULANDO CARRERA");
        mostrarCabeceraCarrera(carrera);

        carrera.agregarObservador(new VistaCarreraObserver());
        carrera.agregarObservador(new EstadisticaObserver());

        if (!prepararAutosParticipantes(carrera, facade)) {
            return;
        }

        carrera.iniciar();
        System.out.println("  Semáforo apagado... luces encendidas... ¡VERDE!\n");

        simularVueltas(carrera);
        mostrarResultados(carrera, jugador, facade);
    }
    */

    public void simularCarrera(Jugador jugador, Carrera carrera, Piloto pilotoElegido,
                               SistemaCarreraFacade facade) {
        if (carrera == null) { msgError("No se pudo iniciar la carrera."); return; }

        linea();
        subtitulo("INICIANDO CARRERA EN PANTALLA");
        mostrarCabeceraCarrera(carrera);

        // Agregar observers (el de estadísticas sigue funcionando igual)
        carrera.agregarObservador(new EstadisticaObserver());

        if (!prepararAutosParticipantes(carrera, facade)) return;

        carrera.iniciar();
        System.out.println("  Abriendo ventana JavaFX... ¡Buena carrera!\n");

        // ── Abrir ventana JavaFX y esperar ────────────────────────────────────
        CarreraFXWindow ventana = new CarreraFXWindow(carrera);
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            ventana.mostrar(MainFX.primaryStage);
            latch.countDown();
        });

        try {
            // Esperar a que se abra (el mostrar() retorna inmediato)
            latch.await();
            // Esperar a que se cierre la ventana antes de mostrar resultados
            ventana.esperarCierre();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // ─────────────────────────────────────────────────────────────────────

        System.out.println("  Carrera finalizada. Calculando resultados...\n");
        mostrarResultados(carrera, jugador, facade);
    }
    public void mostrarCabeceraCarrera(Carrera carrera) {
        System.out.println("  *** CARRERA EN "
                + carrera.getCircuito().getNombre().toUpperCase()
                + " — " + carrera.getVueltas() + " VUELTAS — "
                + carrera.getClimaInicial().toUpperCase() + " ***");
        linea();
    }

    public boolean prepararAutosParticipantes(Carrera carrera, SistemaCarreraFacade facade) {
        List<Participante> participantes = facade.getConfiguracionCarrera().getParticipantes();

        if (participantes == null || participantes.isEmpty()) {
            msgError("No hay participantes registrados en la carrera.");
            return false;
        }

        for (Participante p : participantes) {
            if (p == null) {
                msgError("Se encontró un participante nulo; se omite.");
                return false;
            }
            if (p.getJugador() == null) {
                msgError("Participante sin jugador asignado; se omite.");
                return false;
            }
            if (p.getPiloto() == null) {
                msgError("Participante sin piloto asignado (jugador: "
                        + p.getJugador().getUsername() + "); se omite.");
                return false;
            }
            if (p.getAuto() == null) {
                msgError("Participante sin auto seleccionado (jugador: "
                        + p.getJugador().getUsername() + ").");
                return false;
            }

            Auto auto = p.getAuto();

            // Aplicar el efecto de la estrategia de conducción del piloto (Strategy).
            double condicionesPista = obtenerCondicionesPista(carrera.getClimaInicial());
            double factorHabilidad = 0.85 + (p.getPiloto().getHabilidad() / 100.0) * 0.30;
            double rendimientoBase = p.getPiloto().getHabilidad() * condicionesPista;
            double rendimientoConEstrategia = p.getPiloto().calcularRendimiento(condicionesPista);
            if (rendimientoBase > 0) {
                auto.setFactorEstrategia(factorHabilidad * (rendimientoConEstrategia / rendimientoBase));
            } else {
                auto.setFactorEstrategia(factorHabilidad);
            }

            carrera.agregarAuto(auto);
        }
        return true;
    }

    /**
     * Traduce el clima inicial de la carrera a un factor de condiciones
     * de pista usado por las estrategias de conducción (Strategy):
     * climas adversos penalizan más a estrategias agresivas.
     */
    private double obtenerCondicionesPista(String clima) {
        if (clima == null) return 1.0;
        return switch (clima) {
            case "Lluvioso" -> 0.7;
            case "Nublado"  -> 0.9;
            default         -> 1.0; // Soleado u otro
        };
    }

    public void simularVueltas(Carrera carrera) {
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

    public void mostrarResultados(Carrera carrera, Jugador jugador, SistemaCarreraFacade facade) {
        List<Auto> posiciones = carrera.getPosiciones();
        Auto autoJugador = obtenerAutoJugador(facade, jugador);
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
            boolean esJugador = autoJugador != null && a == autoJugador;
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
            rankingController.sumarPuntos(jugador.getId(), ranking.getPuntosTotales());
            msgOk("Puntos sumados al ranking global.");
        }

        System.out.println();
        System.out.print("  Presioná ENTER para continuar...");
        sc.nextLine();
    }

    private Auto obtenerAutoJugador(SistemaCarreraFacade facade, Jugador jugador) {
        List<Participante> participantes = facade.getConfiguracionCarrera().getParticipantes();
        if (participantes == null) {
            return null;
        }

        return participantes.stream()
                .filter(p -> p.getJugador() != null && p.getJugador().getId() == jugador.getId())
                .map(Participante::getAuto)
                .findFirst()
                .orElse(null);
    }
}
