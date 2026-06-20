package com.f1legends.controller;

import com.f1legends.DAO.modeloDAO.AutoDAO;
import com.f1legends.DAO.modeloDAO.CircuitoDAO;
import com.f1legends.DAO.modeloDAO.CircuitoDTO;
import com.f1legends.DAO.modeloDAO.PilotoDAO;
import com.f1legends.DAO.modeloDAO.RankingGlobalDAO;
import com.f1legends.DAO.modeloDAO.UsuarioDAO;
import com.f1legends.modelo.Piloto;
import com.f1legends.modelo.Usuarios.Jugador;
import com.f1legends.modelo.Usuarios.Participante;
import com.f1legends.modelo.auto.Auto;
import com.f1legends.modelo.carreras.Carrera;
import com.f1legends.patrones.estrategias.EstrategiaAgresiva;
import com.f1legends.patrones.estrategias.EstrategiaConduccion;
import com.f1legends.patrones.estrategias.EstrategiaConservadora;
import com.f1legends.patrones.estrategias.EstrategiaEquilibrada;
import com.f1legends.patrones.facade.SistemaCarreraFacade;
import com.f1legends.patrones.factory.TipoAuto;
import com.f1legends.vista.CarreraFXWindow;
import com.f1legends.vista.FxRouter;
import com.f1legends.vista.MainFX;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Vista de preparación de carrera para el modo Multijugador Local.
 * Equivalente JavaFX al antiguo flujo de consola (CarreraController#flujoMultijugador),
 * ya eliminado:
 *   - El jugador principal (sesión actual) elige piloto y auto.
 *   - Se pueden agregar jugadores adicionales ya registrados en la BD,
 *     cada uno con su propio piloto y auto (sin repetir jugador ni piloto).
 *   - Si faltan participantes para llegar al mínimo de grilla, se
 *     completa con CPUs (mismo criterio que singleplayer).
 */
public class PrepararMultijugadorController {

    private static final int MINIMO_PARTICIPANTES = 6;

    private final AutoDAO autoDAO = new AutoDAO();
    private final CircuitoDAO circuitoDAO = new CircuitoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final UsuarioController usuarioController = new UsuarioController(usuarioDAO, new RankingGlobalDAO());
    private final PreparadorGrilla preparadorGrilla = new PreparadorGrilla();

    @FXML private ComboBox<Piloto> pilotoPrincipalCombo;
    @FXML private ComboBox<TipoAuto> reglamentacionCombo;
    @FXML private ComboBox<Auto> autoPrincipalCombo;
    @FXML private VBox jugadoresAdicionalesBox;
    @FXML private Button agregarJugadorButton;
    @FXML private ComboBox<CircuitoDTO> circuitoCombo;
    @FXML private ComboBox<String> estrategiaCombo;
    @FXML private ComboBox<String> climaCombo;
    @FXML private Spinner<Integer> vueltasSpinner;
    @FXML private Label resumenLabel;
    @FXML private Label estadoLabel;

    /** Una fila de la UI = un jugador adicional con su piloto y su auto. */
    private final List<FilaJugador> filasAdicionales = new ArrayList<>();

    @FXML
    private void initialize() {
        configurarCombos();
        cargarDatos();
        actualizarEstadoBotonAgregar();
    }

    @FXML
    private void agregarJugador() {
        Jugador jugadorPrincipal = Sesion.getJugadorActual();

        List<Jugador> jugadoresDisponibles = usuarioController.obtenerJugadores().stream()
                .filter(j -> j.getId() != jugadorPrincipal.getId())
                .filter(j -> filasAdicionales.stream().noneMatch(f -> f.jugadorSeleccionado(j)))
                .toList();

        List<Piloto> pilotosDisponibles = PilotoDAO.obtenerTodos().stream()
                .filter(p -> pilotoPrincipalCombo.getValue() == null || p.getId() != pilotoPrincipalCombo.getValue().getId())
                .filter(p -> filasAdicionales.stream().noneMatch(f -> f.pilotoSeleccionado(p)))
                .toList();

        if (jugadoresDisponibles.isEmpty()) {
            mostrarError("No hay más jugadores registrados disponibles para agregar.");
            return;
        }
        if (pilotosDisponibles.isEmpty()) {
            mostrarError("No hay más pilotos disponibles para asignar.");
            return;
        }

        FilaJugador fila = new FilaJugador(this);
        filasAdicionales.add(fila);
        jugadoresAdicionalesBox.getChildren().add(fila.getNodo());
        fila.cargarJugadores(jugadoresDisponibles);
        actualizarEstadoBotonAgregar();
        actualizarResumen();
    }

    private void quitarJugador(FilaJugador fila) {
        filasAdicionales.remove(fila);
        jugadoresAdicionalesBox.getChildren().remove(fila.getNodo());
        actualizarEstadoBotonAgregar();
        actualizarResumen();
    }

    @FXML
    private void iniciarCarrera() {
        Piloto pilotoPrincipal = pilotoPrincipalCombo.getValue();
        Auto autoPrincipal = autoPrincipalCombo.getValue();
        TipoAuto reglamentacion = reglamentacionCombo.getValue();
        CircuitoDTO circuito = circuitoCombo.getValue();
        Jugador jugadorPrincipal = Sesion.getJugadorActual();

        if (pilotoPrincipal == null || autoPrincipal == null || circuito == null || reglamentacion == null) {
            mostrarError("Elegí tu piloto, reglamentación, auto y circuito para iniciar.");
            return;
        }

        List<Participante> participantesAdicionales = new ArrayList<>();
        for (FilaJugador fila : filasAdicionales) {
            Jugador jugador = fila.getJugador();
            Piloto piloto = fila.getPiloto();
            Auto auto = fila.getAuto();
            if (jugador == null || piloto == null || auto == null) {
                mostrarError("Completá jugador, piloto y auto en cada fila agregada (o quitá la fila).");
                return;
            }
            participantesAdicionales.add(new Participante(
                    jugador,
                    piloto,
                    copiarAutoParaCarrera(auto, jugador.getId(), piloto.getNombre())
            ));
        }

        Set<Integer> pilotosUsados = new HashSet<>();
        pilotosUsados.add(pilotoPrincipal.getId());
        for (Participante p : participantesAdicionales) {
            if (!pilotosUsados.add(p.getPiloto().getId())) {
                mostrarError("Hay un piloto repetido entre los participantes.");
                return;
            }
        }
        Set<Integer> jugadoresUsados = new HashSet<>();
        jugadoresUsados.add(jugadorPrincipal.getId());
        for (Participante p : participantesAdicionales) {
            if (!jugadoresUsados.add(p.getJugador().getId())) {
                mostrarError("Hay un jugador repetido entre los participantes.");
                return;
            }
        }

        try {
            SistemaCarreraFacade facade = new SistemaCarreraFacade();
            facade.getConfiguracionCarrera().setJugadorPrincipal(jugadorPrincipal);
            facade.seleccionarModoJuego("Multijugador Local");
            facade.seleccionarPiloto(pilotoPrincipal.getId());
            facade.seleccionarEstrategiaConduccion(crearEstrategia());

            List<Participante> participantes = new ArrayList<>();
            participantes.add(new Participante(
                    jugadorPrincipal,
                    pilotoPrincipal,
                    copiarAutoParaCarrera(autoPrincipal, jugadorPrincipal.getId(), pilotoPrincipal.getNombre())
            ));
            participantes.addAll(participantesAdicionales);
            facade.getConfiguracionCarrera().setParticipantes(participantes);

            if (!completarGrillaConAutosDisponibles(facade, reglamentacion, MINIMO_PARTICIPANTES)) {
                mostrarError("Necesitás al menos " + MINIMO_PARTICIPANTES + " pilotos con auto de su escudería para esa reglamentación.");
                return;
            }

            facade.seleccionarCircuito(circuito.getId());
            facade.configurarCarrera(vueltasSpinner.getValue(), climaCombo.getValue());

            Carrera carrera = facade.iniciarCarrera();
            if (!preparadorGrilla.prepararAutosParticipantes(carrera, facade)) {
                mostrarError("No se pudo preparar la grilla de la carrera.");
                return;
            }
            carrera.iniciar();

            CarreraFXWindow ventana = new CarreraFXWindow(
                    carrera,
                    jugadorPrincipal,
                    facade.getConfiguracionCarrera().getParticipantes(),
                    new RankingController(new RankingGlobalDAO())
            );
            ventana.mostrar(MainFX.primaryStage);
            mostrarOk("Carrera multijugador iniciada en ventana de simulación.");
        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void volver() throws IOException {
        FxRouter.showJugadorHome();
    }

    private void configurarCombos() {
        estrategiaCombo.getItems().setAll("Agresiva", "Equilibrada", "Conservadora");
        estrategiaCombo.setValue("Equilibrada");

        climaCombo.getItems().setAll("Soleado", "Nublado", "Lluvioso");
        climaCombo.setValue("Soleado");

        vueltasSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 3));

        pilotoPrincipalCombo.setVisibleRowCount(8);
        reglamentacionCombo.setVisibleRowCount(4);
        autoPrincipalCombo.setVisibleRowCount(8);
        circuitoCombo.setVisibleRowCount(8);
        estrategiaCombo.setVisibleRowCount(4);
        climaCombo.setVisibleRowCount(4);

        pilotoPrincipalCombo.setConverter(pilotoConverter());
        pilotoPrincipalCombo.setCellFactory(combo -> pilotoCell());

        reglamentacionCombo.getItems().setAll(TipoAuto.values());
        reglamentacionCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(TipoAuto tipoAuto) {
                return tipoAuto == null ? "" : tipoAuto.name();
            }

            @Override
            public TipoAuto fromString(String string) {
                return null;
            }
        });
        reglamentacionCombo.setCellFactory(combo -> new ListCell<>() {
            @Override
            protected void updateItem(TipoAuto tipoAuto, boolean empty) {
                super.updateItem(tipoAuto, empty);
                setText(empty || tipoAuto == null ? "" : tipoAuto.name());
            }
        });

        autoPrincipalCombo.setConverter(autoConverter());
        autoPrincipalCombo.setCellFactory(combo -> autoCell());

        circuitoCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(CircuitoDTO circuito) {
                return circuito == null ? "" : circuito.getNombre() + " - " + circuito.getPais();
            }

            @Override
            public CircuitoDTO fromString(String string) {
                return null;
            }
        });
        circuitoCombo.setCellFactory(combo -> new ListCell<>() {
            @Override
            protected void updateItem(CircuitoDTO circuito, boolean empty) {
                super.updateItem(circuito, empty);
                setText(empty || circuito == null ? "" : circuito.getNombre() + " - " + circuito.getPais());
            }
        });

        pilotoPrincipalCombo.valueProperty().addListener((obs, anterior, piloto) -> {
            cargarAutosPrincipal();
            actualizarEstadoBotonAgregar();
        });
        reglamentacionCombo.valueProperty().addListener((obs, anterior, reglamentacion) -> cargarAutosPrincipal());
        circuitoCombo.valueProperty().addListener((obs, anterior, circuito) -> ajustarVueltas(circuito));
    }

    private void cargarDatos() {
        pilotoPrincipalCombo.getItems().setAll(PilotoDAO.obtenerTodos());
        circuitoCombo.getItems().setAll(circuitoDAO.obtenerTodos());
        seleccionarPrimerReglamentoConAutos();
        if (!pilotoPrincipalCombo.getItems().isEmpty()) {
            pilotoPrincipalCombo.getSelectionModel().selectFirst();
        }
        if (!circuitoCombo.getItems().isEmpty()) {
            circuitoCombo.getSelectionModel().selectFirst();
        }
        actualizarResumen();
    }

    private void seleccionarPrimerReglamentoConAutos() {
        for (TipoAuto tipoAuto : TipoAuto.values()) {
            if (cantidadPilotosCompatibles(tipoAuto) >= MINIMO_PARTICIPANTES) {
                reglamentacionCombo.setValue(tipoAuto);
                cargarAutosPrincipal();
                return;
            }
        }
        if (!reglamentacionCombo.getItems().isEmpty()) {
            reglamentacionCombo.getSelectionModel().selectFirst();
            cargarAutosPrincipal();
        }
    }

    private long cantidadPilotosCompatibles(TipoAuto reglamentacion) {
        return PilotoDAO.obtenerTodos().stream()
                .filter(piloto -> autoDeEscuderiaCompatible(piloto, reglamentacion) != null)
                .count();
    }

    private void cargarAutosPrincipal() {
        Piloto piloto = pilotoPrincipalCombo.getValue();
        TipoAuto reglamentacion = reglamentacionCombo.getValue();
        autoPrincipalCombo.getItems().setAll(autosDelPilotoPorReglamentacion(piloto, reglamentacion));
        if (!autoPrincipalCombo.getItems().isEmpty()) {
            autoPrincipalCombo.getSelectionModel().selectFirst();
        } else {
            autoPrincipalCombo.setValue(null);
        }
        actualizarResumen();
    }

    List<Auto> autosDelPilotoPorReglamentacion(Piloto piloto, TipoAuto reglamentacion) {
        if (piloto == null || reglamentacion == null) {
            return List.of();
        }
        return autoDAO.obtenerTodos().stream()
                .filter(auto -> auto.getTipoAuto() == reglamentacion)
                .filter(auto -> auto.getEscuderia() != null
                        && auto.getEscuderia().getId() == piloto.getEscuderiaId())
                .toList();
    }

    TipoAuto getReglamentacionSeleccionada() {
        return reglamentacionCombo.getValue();
    }

    StringConverter<Piloto> pilotoConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(Piloto piloto) {
                return piloto == null ? "" : piloto.getNombre() + " - habilidad " + piloto.getHabilidad();
            }

            @Override
            public Piloto fromString(String string) {
                return null;
            }
        };
    }

    ListCell<Piloto> pilotoCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Piloto piloto, boolean empty) {
                super.updateItem(piloto, empty);
                setText(empty || piloto == null ? "" : piloto.getNombre() + " - habilidad " + piloto.getHabilidad());
            }
        };
    }

    StringConverter<Auto> autoConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(Auto auto) {
                return auto == null ? "" : auto.getModelo() + " - " + auto.getTipoAuto();
            }

            @Override
            public Auto fromString(String string) {
                return null;
            }
        };
    }

    ListCell<Auto> autoCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Auto auto, boolean empty) {
                super.updateItem(auto, empty);
                setText(empty || auto == null ? "" : auto.getModelo() + " - " + auto.getTipoAuto());
            }
        };
    }

    StringConverter<Jugador> jugadorConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(Jugador jugador) {
                return jugador == null ? "" : jugador.getUsername();
            }

            @Override
            public Jugador fromString(String string) {
                return null;
            }
        };
    }

    ListCell<Jugador> jugadorCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Jugador jugador, boolean empty) {
                super.updateItem(jugador, empty);
                setText(empty || jugador == null ? "" : jugador.getUsername());
            }
        };
    }

    /** Pilotos restantes para una fila adicional: excluye el del jugador principal y los ya usados en otras filas. */
    List<Piloto> pilotosDisponiblesPara(FilaJugador filaActual) {
        Piloto principal = pilotoPrincipalCombo.getValue();
        return PilotoDAO.obtenerTodos().stream()
                .filter(p -> principal == null || p.getId() != principal.getId())
                .filter(p -> filasAdicionales.stream()
                        .filter(f -> f != filaActual)
                        .noneMatch(f -> f.pilotoSeleccionado(p)))
                .toList();
    }

    private void actualizarEstadoBotonAgregar() {
        Jugador jugadorPrincipal = Sesion.getJugadorActual();
        boolean hayJugadores = usuarioController.obtenerJugadores().stream()
                .anyMatch(j -> j.getId() != jugadorPrincipal.getId()
                        && filasAdicionales.stream().noneMatch(f -> f.jugadorSeleccionado(j)));
        boolean hayPilotos = pilotosDisponiblesPara(null).size() > 0;
        agregarJugadorButton.setDisable(!hayJugadores || !hayPilotos);
    }

    private void ajustarVueltas(CircuitoDTO circuito) {
        int max = circuito == null ? 10 : Math.max(1, circuito.getVueltas());
        int valor = Math.min(vueltasSpinner.getValue(), max);
        vueltasSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, max, valor));
        actualizarResumen();
    }

    private EstrategiaConduccion crearEstrategia() {
        return switch (estrategiaCombo.getValue()) {
            case "Agresiva" -> new EstrategiaAgresiva();
            case "Conservadora" -> new EstrategiaConservadora();
            default -> new EstrategiaEquilibrada();
        };
    }

    private Auto copiarAutoParaCarrera(Auto auto, int idCarrera, String pilotoNombre) {
        return new Auto(
                idCarrera,
                pilotoNombre + " / " + auto.getModelo(),
                velocidadParaSimulacion(auto.getVelocidadBase()),
                auto.getEscuderia(),
                auto.getTipoAuto()
        );
    }

    private double velocidadParaSimulacion(double velocidadBase) {
        if (velocidadBase > 1.0) {
            return velocidadBase / 5000.0;
        }
        return velocidadBase;
    }

    private boolean completarGrillaConAutosDisponibles(SistemaCarreraFacade facade,
                                                       TipoAuto reglamentacion, int minimoParticipantes) {
        List<Participante> participantes = facade.getConfiguracionCarrera().getParticipantes();
        if (participantes == null || minimoParticipantes < 2) {
            return false;
        }

        Set<Integer> pilotosUsados = new HashSet<>();
        for (Participante participante : participantes) {
            pilotosUsados.add(participante.getPiloto().getId());
        }

        List<Piloto> pilotosCpu = PilotoDAO.obtenerTodos().stream()
                .filter(piloto -> !pilotosUsados.contains(piloto.getId()))
                .filter(piloto -> autoDeEscuderiaCompatible(piloto, reglamentacion) != null)
                .sorted(Comparator.comparingInt(Piloto::getHabilidad).reversed())
                .toList();

        if (pilotosCpu.size() + participantes.size() < minimoParticipantes) {
            return false;
        }

        int cpuId = -1;
        for (Piloto pilotoCpu : pilotosCpu) {
            if (participantes.size() >= minimoParticipantes) {
                break;
            }
            Auto autoCpu = autoDeEscuderiaCompatible(pilotoCpu, reglamentacion);
            if (autoCpu == null) {
                continue;
            }
            Jugador jugadorCpu = new Jugador(cpuId, "CPU - " + pilotoCpu.getNombre(), "", "CPU");
            participantes.add(new Participante(
                    jugadorCpu,
                    pilotoCpu,
                    copiarAutoParaCarrera(autoCpu, cpuId, pilotoCpu.getNombre())
            ));
            cpuId--;
        }

        return participantes.size() >= minimoParticipantes;
    }

    private Auto autoDeEscuderiaCompatible(Piloto piloto, TipoAuto reglamentacion) {
        if (piloto == null || reglamentacion == null) {
            return null;
        }
        return autoDAO.obtenerTodosPorEscuderiaId(piloto.getEscuderiaId()).stream()
                .filter(auto -> auto.getTipoAuto() == reglamentacion)
                .findFirst()
                .orElse(null);
    }

    void actualizarResumen() {
        CircuitoDTO circuito = circuitoCombo.getValue();
        String circuitoTexto = circuito == null ? "sin circuito" : circuito.getNombre();
        resumenLabel.setText("Participantes humanos: " + (1 + filasAdicionales.size())
                + " | Circuito: " + circuitoTexto);
        actualizarEstadoBotonAgregar();
    }

    void mostrarError(String mensaje) {
        estadoLabel.getStyleClass().removeAll("message-ok");
        if (!estadoLabel.getStyleClass().contains("message-error")) {
            estadoLabel.getStyleClass().add("message-error");
        }
        estadoLabel.setText(mensaje);
    }

    void mostrarOk(String mensaje) {
        estadoLabel.getStyleClass().removeAll("message-error");
        if (!estadoLabel.getStyleClass().contains("message-ok")) {
            estadoLabel.getStyleClass().add("message-ok");
        }
        estadoLabel.setText(mensaje);
    }

    /**
     * Fila de UI para un jugador adicional: combo de jugador (BD),
     * combo de piloto y combo de auto, más un botón para quitarla.
     * Se construye en código (no FXML) porque la cantidad de filas es dinámica.
     */
    private static final class FilaJugador {
        private final PrepararMultijugadorController owner;
        private final HBox nodo;
        private final ComboBox<Jugador> jugadorCombo = new ComboBox<>();
        private final ComboBox<Piloto> pilotoCombo = new ComboBox<>();
        private final ComboBox<Auto> autoCombo = new ComboBox<>();
        private final Button quitarButton = new Button("Quitar");

        private FilaJugador(PrepararMultijugadorController owner) {
            this.owner = owner;

            jugadorCombo.setPromptText("Jugador");
            pilotoCombo.setPromptText("Piloto");
            autoCombo.setPromptText("Auto");

            jugadorCombo.setConverter(owner.jugadorConverter());
            jugadorCombo.setCellFactory(combo -> owner.jugadorCell());
            pilotoCombo.setConverter(owner.pilotoConverter());
            pilotoCombo.setCellFactory(combo -> owner.pilotoCell());
            autoCombo.setConverter(owner.autoConverter());
            autoCombo.setCellFactory(combo -> owner.autoCell());

            jugadorCombo.setMaxWidth(Double.MAX_VALUE);
            pilotoCombo.setMaxWidth(Double.MAX_VALUE);
            autoCombo.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(jugadorCombo, Priority.ALWAYS);
            HBox.setHgrow(pilotoCombo, Priority.ALWAYS);
            HBox.setHgrow(autoCombo, Priority.ALWAYS);

            quitarButton.getStyleClass().add("secondary-small-button");
            quitarButton.setOnAction(e -> owner.quitarJugador(this));

            pilotoCombo.getItems().setAll(owner.pilotosDisponiblesPara(this));
            pilotoCombo.valueProperty().addListener((obs, anterior, piloto) -> {
                cargarAutos();
                owner.actualizarEstadoBotonAgregar();
                owner.actualizarResumen();
            });
            jugadorCombo.valueProperty().addListener((obs, anterior, jugador) -> {
                owner.actualizarEstadoBotonAgregar();
                owner.actualizarResumen();
            });

            nodo = new HBox(10, jugadorCombo, pilotoCombo, autoCombo, quitarButton);
            nodo.setPadding(new Insets(4, 0, 4, 0));
        }

        void cargarJugadores(List<Jugador> jugadoresDisponibles) {
            jugadorCombo.getItems().setAll(jugadoresDisponibles);
            if (!jugadorCombo.getItems().isEmpty()) {
                jugadorCombo.getSelectionModel().selectFirst();
            }
            if (!pilotoCombo.getItems().isEmpty()) {
                pilotoCombo.getSelectionModel().selectFirst();
            }
        }

        private void cargarAutos() {
            TipoAuto reglamentacion = owner.getReglamentacionSeleccionada();
            autoCombo.getItems().setAll(owner.autosDelPilotoPorReglamentacion(pilotoCombo.getValue(), reglamentacion));
            if (!autoCombo.getItems().isEmpty()) {
                autoCombo.getSelectionModel().selectFirst();
            } else {
                autoCombo.setValue(null);
            }
        }

        boolean jugadorSeleccionado(Jugador jugador) {
            return jugadorCombo.getValue() != null && jugadorCombo.getValue().getId() == jugador.getId();
        }

        boolean pilotoSeleccionado(Piloto piloto) {
            return pilotoCombo.getValue() != null && pilotoCombo.getValue().getId() == piloto.getId();
        }

        Jugador getJugador() {
            return jugadorCombo.getValue();
        }

        Piloto getPiloto() {
            return pilotoCombo.getValue();
        }

        Auto getAuto() {
            return autoCombo.getValue();
        }

        HBox getNodo() {
            return nodo;
        }
    }
}