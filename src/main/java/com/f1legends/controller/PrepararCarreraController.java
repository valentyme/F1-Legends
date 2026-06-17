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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrepararCarreraController {
    private final AutoDAO autoDAO = new AutoDAO();
    private final CircuitoDAO circuitoDAO = new CircuitoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final CarreraController carreraController = new CarreraController(
            usuarioDAO,
            circuitoDAO,
            new RankingController(new RankingGlobalDAO()),
            null
    );

    @FXML private ComboBox<Piloto> pilotoCombo;
    @FXML private ComboBox<TipoAuto> reglamentacionCombo;
    @FXML private ComboBox<Auto> autoCombo;
    @FXML private ComboBox<CircuitoDTO> circuitoCombo;
    @FXML private ComboBox<String> estrategiaCombo;
    @FXML private ComboBox<String> climaCombo;
    @FXML private Spinner<Integer> vueltasSpinner;
    @FXML private Label resumenLabel;
    @FXML private Label estadoLabel;

    @FXML
    private void initialize() {
        configurarCombos();
        cargarDatos();
    }

    @FXML
    private void iniciarCarrera() {
        Piloto piloto = pilotoCombo.getValue();
        TipoAuto reglamentacion = reglamentacionCombo.getValue();
        Auto auto = autoCombo.getValue();
        CircuitoDTO circuito = circuitoCombo.getValue();

        if (piloto == null || auto == null || circuito == null) {
            mostrarError("Elegí piloto, reglamentación, auto y circuito para iniciar.");
            return;
        }
        if (reglamentacion == null || auto.getTipoAuto() != reglamentacion) {
            mostrarError("El auto elegido no corresponde a la reglamentación seleccionada.");
            return;
        }

        try {
            SistemaCarreraFacade facade = new SistemaCarreraFacade();
            Jugador jugador = Sesion.getJugadorActual();
            facade.getConfiguracionCarrera().setJugadorPrincipal(jugador);
            facade.seleccionarModoJuego("Singleplayer");
            facade.seleccionarPiloto(piloto.getId());
            facade.seleccionarEstrategiaConduccion(crearEstrategia());
            List<Participante> participantes = new ArrayList<>();
            participantes.add(new Participante(
                    jugador,
                    piloto,
                    copiarAutoParaCarrera(auto, jugador.getId(), piloto.getNombre())
            ));
            facade.getConfiguracionCarrera().setParticipantes(participantes);

            if (!completarGrillaConAutosDisponibles(facade, auto, reglamentacion, 6)) {
                mostrarError("Necesitás 6 pilotos con auto de su escudería para esa reglamentación.");
                return;
            }

            facade.seleccionarCircuito(circuito.getId());
            facade.configurarCarrera(vueltasSpinner.getValue(), climaCombo.getValue());

            Carrera carrera = facade.iniciarCarrera();
            if (!carreraController.prepararAutosParticipantes(carrera, facade)) {
                mostrarError("No se pudo preparar la grilla de la carrera.");
                return;
            }
            carrera.iniciar();

            CarreraFXWindow ventana = new CarreraFXWindow(
                    carrera,
                    jugador,
                    facade.getConfiguracionCarrera().getParticipantes(),
                    new RankingController(new RankingGlobalDAO())
            );
            ventana.mostrar(MainFX.primaryStage);
            mostrarOk("Carrera iniciada en ventana de simulación.");
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
        pilotoCombo.setVisibleRowCount(8);
        reglamentacionCombo.setVisibleRowCount(4);
        autoCombo.setVisibleRowCount(8);
        circuitoCombo.setVisibleRowCount(8);
        estrategiaCombo.setVisibleRowCount(4);
        climaCombo.setVisibleRowCount(4);

        pilotoCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Piloto piloto) {
                return piloto == null ? "" : piloto.getNombre() + " - habilidad " + piloto.getHabilidad();
            }

            @Override
            public Piloto fromString(String string) {
                return null;
            }
        });
        pilotoCombo.setCellFactory(combo -> new ListCell<>() {
            @Override
            protected void updateItem(Piloto piloto, boolean empty) {
                super.updateItem(piloto, empty);
                setText(empty || piloto == null ? "" : piloto.getNombre() + " - habilidad " + piloto.getHabilidad());
            }
        });
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
        autoCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Auto auto) {
                return auto == null ? "" : auto.getModelo() + " - " + auto.getTipoAuto();
            }

            @Override
            public Auto fromString(String string) {
                return null;
            }
        });
        autoCombo.setCellFactory(combo -> new ListCell<>() {
            @Override
            protected void updateItem(Auto auto, boolean empty) {
                super.updateItem(auto, empty);
                setText(empty || auto == null ? "" : auto.getModelo() + " - " + auto.getTipoAuto());
            }
        });
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

        pilotoCombo.valueProperty().addListener((obs, anterior, piloto) -> cargarAutosDelPiloto());
        reglamentacionCombo.valueProperty().addListener((obs, anterior, reglamentacion) -> cargarAutosPorReglamentacion(reglamentacion));
        circuitoCombo.valueProperty().addListener((obs, anterior, circuito) -> ajustarVueltas(circuito));
    }

    private void cargarDatos() {
        pilotoCombo.getItems().setAll(PilotoDAO.obtenerTodos());
        circuitoCombo.getItems().setAll(circuitoDAO.obtenerTodos());
        seleccionarPrimerReglamentoConAutos();
        if (!pilotoCombo.getItems().isEmpty()) {
            pilotoCombo.getSelectionModel().selectFirst();
        }
        if (!circuitoCombo.getItems().isEmpty()) {
            circuitoCombo.getSelectionModel().selectFirst();
        }
        actualizarResumen();
    }

    private void seleccionarPrimerReglamentoConAutos() {
        for (TipoAuto tipoAuto : TipoAuto.values()) {
            if (cantidadPilotosCompatibles(tipoAuto) >= 6) {
                reglamentacionCombo.setValue(tipoAuto);
                cargarAutosPorReglamentacion(tipoAuto);
                return;
            }
        }
        if (!reglamentacionCombo.getItems().isEmpty()) {
            reglamentacionCombo.getSelectionModel().selectFirst();
            cargarAutosPorReglamentacion(reglamentacionCombo.getValue());
        }
    }

    private long cantidadPilotosCompatibles(TipoAuto reglamentacion) {
        return PilotoDAO.obtenerTodos().stream()
                .filter(piloto -> autoDeEscuderiaCompatible(piloto, reglamentacion) != null)
                .count();
    }

    private void cargarAutosPorReglamentacion(TipoAuto reglamentacion) {
        cargarAutosDelPiloto();
    }

    private void cargarAutosDelPiloto() {
        Piloto piloto = pilotoCombo.getValue();
        TipoAuto reglamentacion = reglamentacionCombo.getValue();
        autoCombo.getItems().setAll(autosDelPilotoPorReglamentacion(piloto, reglamentacion));
        if (!autoCombo.getItems().isEmpty()) {
            autoCombo.getSelectionModel().selectFirst();
        } else {
            autoCombo.setValue(null);
        }
        actualizarResumen();
    }

    private List<Auto> autosDelPilotoPorReglamentacion(Piloto piloto, TipoAuto reglamentacion) {
        if (piloto == null || reglamentacion == null) {
            return List.of();
        }
        return autoDAO.obtenerTodos().stream()
                .filter(auto -> auto.getTipoAuto() == reglamentacion)
                .filter(auto -> auto.getEscuderia() != null
                        && auto.getEscuderia().getId() == piloto.getEscuderiaId())
                .toList();
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

    private boolean completarGrillaConAutosDisponibles(SistemaCarreraFacade facade, Auto autoJugador,
                                                       TipoAuto reglamentacion, int minimoParticipantes) {
        List<Participante> participantes = facade.getConfiguracionCarrera().getParticipantes();
        if (participantes == null) {
            return false;
        }
        if (minimoParticipantes < 2) {
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
            if (autoCpu == null || autoCpu.getId() == autoJugador.getId()) {
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

    private void actualizarResumen() {
        CircuitoDTO circuito = circuitoCombo.getValue();
        String circuitoTexto = circuito == null ? "sin circuito" : circuito.getNombre();
        resumenLabel.setText("Autos de la escudería para esa reglamentación: "
                + autoCombo.getItems().size() + " | Circuito: " + circuitoTexto);
    }

    private void mostrarError(String mensaje) {
        estadoLabel.getStyleClass().removeAll("message-ok");
        if (!estadoLabel.getStyleClass().contains("message-error")) {
            estadoLabel.getStyleClass().add("message-error");
        }
        estadoLabel.setText(mensaje);
    }

    private void mostrarOk(String mensaje) {
        estadoLabel.getStyleClass().removeAll("message-error");
        if (!estadoLabel.getStyleClass().contains("message-ok")) {
            estadoLabel.getStyleClass().add("message-ok");
        }
        estadoLabel.setText(mensaje);
    }
}
