package com.f1legends.controller.Usuario.Admin;

import com.f1legends.DAO.modeloDAO.AutoDAO;
import com.f1legends.DAO.modeloDAO.EscuderiaDAO;
import com.f1legends.controller.Objetos.AutoController;
import com.f1legends.controller.Objetos.EscuderiaController;
import com.f1legends.controller.Objetos.PilotoController;
import com.f1legends.modelo.Escuderias.Escuderia;
import com.f1legends.modelo.Piloto;
import com.f1legends.modelo.auto.Auto;
import com.f1legends.patrones.facade.SistemaCarreraFacade;
import com.f1legends.patrones.factory.TipoAuto;
import com.f1legends.servicios.AutoService;
import com.f1legends.utiles.ColorUtil;
import com.f1legends.vista.FxRouter;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.List;

public class AdminGestionDatosController {
    private final AutoDAO autoDAO = new AutoDAO();
    private final EscuderiaDAO escuderiaDAO = new EscuderiaDAO();
    private final SistemaCarreraFacade facade = new SistemaCarreraFacade();
    private final PilotoController pilotoController = new PilotoController(facade, autoDAO);
    private final EscuderiaController escuderiaController = new EscuderiaController(facade, escuderiaDAO);
    private final AutoController autoController = new AutoController(new AutoService(), escuderiaDAO);
    private static final String AUTO_REFERENCIADO = "No se puede eliminar el auto porque hay pilotos asociados. "
            + "Primero modificá o eliminá esos pilotos.";

    @FXML private TableView<Piloto> pilotosTable;
    @FXML private TableColumn<Piloto, Number> pilotoIdColumn;
    @FXML private TableColumn<Piloto, String> pilotoNombreColumn;
    @FXML private TableColumn<Piloto, Number> pilotoHabilidadColumn;
    @FXML private TableColumn<Piloto, Number> pilotoEscuderiaColumn;
    @FXML private TextField pilotoNombreField;
    @FXML private Spinner<Integer> pilotoHabilidadSpinner;
    @FXML private ComboBox<Escuderia> pilotoEscuderiaCombo;

    @FXML private TableView<Escuderia> escuderiasTable;
    @FXML private TableColumn<Escuderia, Number> escuderiaIdColumn;
    @FXML private TableColumn<Escuderia, String> escuderiaNombreColumn;
    @FXML private TableColumn<Escuderia, String> escuderiaColorColumn;
    @FXML private TextField escuderiaNombreField;
    @FXML private TextField escuderiaColorField;

    @FXML private TableView<Auto> autosTable;
    @FXML private TableColumn<Auto, Number> autoIdColumn;
    @FXML private TableColumn<Auto, String> autoModeloColumn;
    @FXML private TableColumn<Auto, Number> autoVelocidadColumn;
    @FXML private TableColumn<Auto, String> autoTipoColumn;
    @FXML private TableColumn<Auto, String> autoEscuderiaColumn;
    @FXML private TextField autoModeloField;
    @FXML private TextField autoVelocidadField;
    @FXML private ComboBox<Escuderia> autoEscuderiaCombo;
    @FXML private ComboBox<TipoAuto> autoTipoCombo;

    @FXML private Label estadoLabel;

    @FXML
    private void initialize() {
        configurarColumnas();
        configurarFormularios();
        cargarTodo();
    }

    @FXML
    private void guardarPiloto() {
        Escuderia escuderia = pilotoEscuderiaCombo.getValue();
        if (escuderia == null || pilotoNombreField.getText().isBlank()) {
            mostrarError("Completá nombre y escudería del piloto.");
            return;
        }
        if (autoDAO.obtenerPorEscuderiaId(escuderia.getId()) == null) {
            mostrarError("La escudería elegida necesita al menos un auto cargado.");
            return;
        }
        pilotoController.altaPiloto(pilotoNombreField.getText().trim(), pilotoHabilidadSpinner.getValue(), escuderia.getId());
        cargarTodo();
        limpiarPiloto();
        mostrarOk("Piloto guardado.");
    }

    @FXML
    private void modificarPiloto() {
        Piloto seleccionado = pilotosTable.getSelectionModel().getSelectedItem();
        Escuderia escuderia = pilotoEscuderiaCombo.getValue();
        if (seleccionado == null || escuderia == null || pilotoNombreField.getText().isBlank()) {
            mostrarError("Seleccioná un piloto y completá el formulario.");
            return;
        }
        pilotoController.modificarPiloto(seleccionado.getId(), pilotoNombreField.getText().trim(), pilotoHabilidadSpinner.getValue(), escuderia.getId());
        cargarTodo();
        mostrarOk("Piloto modificado.");
    }

    @FXML
    private void eliminarPiloto() {
        Piloto seleccionado = pilotosTable.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Seleccioná un piloto.");
            return;
        }
        pilotoController.bajaPiloto(seleccionado.getId());
        cargarTodo();
        limpiarPiloto();
        mostrarOk("Piloto eliminado.");
    }

    @FXML
    private void guardarEscuderia() {
        if (escuderiaNombreField.getText().isBlank() || escuderiaColorField.getText().isBlank()) {
            mostrarError("Completá nombre y color.");
            return;
        }
        escuderiaController.altaEscuderia(escuderiaNombreField.getText().trim(), Color.web(escuderiaColorField.getText().trim()));
        cargarTodo();
        limpiarEscuderia();
        mostrarOk("Escudería guardada.");
    }

    @FXML
    private void modificarEscuderia() {
        Escuderia seleccionada = escuderiasTable.getSelectionModel().getSelectedItem();
        if (seleccionada == null || escuderiaNombreField.getText().isBlank()) {
            mostrarError("Seleccioná una escudería y completá el formulario.");
            return;
        }
        escuderiaController.modificarEscuderia(seleccionada.getId(), escuderiaNombreField.getText().trim(), escuderiaColorField.getText().trim());
        cargarTodo();
        mostrarOk("Escudería modificada.");
    }

    @FXML
    private void eliminarEscuderia() {
        Escuderia seleccionada = escuderiasTable.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Seleccioná una escudería.");
            return;
        }
        escuderiaController.bajaEscuderia(seleccionada.getId());
        cargarTodo();
        limpiarEscuderia();
        mostrarOk("Escudería eliminada junto a sus autos y pilotos.");
    }

    @FXML
    private void guardarAuto() {
        Escuderia escuderia = autoEscuderiaCombo.getValue();
        if (escuderia == null || autoTipoCombo.getValue() == null || autoModeloField.getText().isBlank()) {
            mostrarError("Completá modelo, escudería y reglamento.");
            return;
        }
        try {
            autoController.altaAuto(autoModeloField.getText().trim(), leerVelocidad(), escuderia.getId(), autoTipoCombo.getValue());
            cargarTodo();
            limpiarAuto();
            mostrarOk("Auto guardado.");
        } catch (RuntimeException e) {
            mostrarError(mensajeAuto(e));
        }
    }

    @FXML
    private void modificarAuto() {
        Auto seleccionado = autosTable.getSelectionModel().getSelectedItem();
        Escuderia escuderia = autoEscuderiaCombo.getValue();
        if (seleccionado == null || escuderia == null || autoTipoCombo.getValue() == null || autoModeloField.getText().isBlank()) {
            mostrarError("Seleccioná un auto y completá el formulario.");
            return;
        }
        try {
            autoController.modificarAuto(seleccionado.getId(), autoModeloField.getText().trim(), leerVelocidad(), escuderia.getId(), autoTipoCombo.getValue());
            cargarTodo();
            mostrarOk("Auto modificado.");
        } catch (RuntimeException e) {
            mostrarError(mensajeAuto(e));
        }
    }

    @FXML
    private void eliminarAuto() {
        Auto seleccionado = autosTable.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Seleccioná un auto.");
            return;
        }
        try {
            autoController.bajaAuto(seleccionado.getId());
            cargarTodo();
            limpiarAuto();
            mostrarOk("Auto eliminado.");
        } catch (RuntimeException e) {
            mostrarError(mensajeAuto(e));
        }
    }

    @FXML
    private void volver() throws IOException {
        FxRouter.showAdminHome();
    }

    private void configurarColumnas() {
        pilotoIdColumn.setCellValueFactory(cell -> new ReadOnlyIntegerWrapper(cell.getValue().getId()));
        pilotoNombreColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getNombre()));
        pilotoHabilidadColumn.setCellValueFactory(cell -> new ReadOnlyIntegerWrapper(cell.getValue().getHabilidad()));
        pilotoEscuderiaColumn.setCellValueFactory(cell -> new ReadOnlyIntegerWrapper(cell.getValue().getEscuderiaId()));

        escuderiaIdColumn.setCellValueFactory(cell -> new ReadOnlyIntegerWrapper(cell.getValue().getId()));
        escuderiaNombreColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getNombre()));
        escuderiaColorColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(ColorUtil.toHex(cell.getValue().getColor())));

        autoIdColumn.setCellValueFactory(cell -> new ReadOnlyIntegerWrapper(cell.getValue().getId()));
        autoModeloColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getModelo()));
        autoVelocidadColumn.setCellValueFactory(cell -> new ReadOnlyDoubleWrapper(cell.getValue().getVelocidadBase()));
        autoTipoColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getTipoAuto().name()));
        autoEscuderiaColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                cell.getValue().getEscuderia() == null ? "" : cell.getValue().getEscuderia().getNombre()
        ));
    }

    private void configurarFormularios() {
        pilotoHabilidadSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 80));
        autoTipoCombo.getItems().setAll(TipoAuto.values());
        pilotoEscuderiaCombo.setVisibleRowCount(8);
        autoEscuderiaCombo.setVisibleRowCount(8);
        autoTipoCombo.setVisibleRowCount(4);

        StringConverter<Escuderia> escuderiaConverter = new StringConverter<>() {
            @Override
            public String toString(Escuderia escuderia) {
                return escuderia == null ? "" : escuderia.getNombre();
            }

            @Override
            public Escuderia fromString(String string) {
                return null;
            }
        };
        pilotoEscuderiaCombo.setConverter(escuderiaConverter);
        autoEscuderiaCombo.setConverter(escuderiaConverter);
        pilotoEscuderiaCombo.setCellFactory(combo -> crearEscuderiaCell());
        pilotoEscuderiaCombo.setButtonCell(crearEscuderiaCell());
        autoEscuderiaCombo.setCellFactory(combo -> crearEscuderiaCell());
        autoEscuderiaCombo.setButtonCell(crearEscuderiaCell());
        autoTipoCombo.setCellFactory(combo -> crearTipoAutoCell());
        autoTipoCombo.setButtonCell(crearTipoAutoCell());

        pilotosTable.getSelectionModel().selectedItemProperty().addListener((obs, anterior, piloto) -> completarPiloto(piloto));
        escuderiasTable.getSelectionModel().selectedItemProperty().addListener((obs, anterior, escuderia) -> completarEscuderia(escuderia));
        autosTable.getSelectionModel().selectedItemProperty().addListener((obs, anterior, auto) -> completarAuto(auto));
    }

    private ListCell<Escuderia> crearEscuderiaCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Escuderia escuderia, boolean empty) {
                super.updateItem(escuderia, empty);
                setText(empty || escuderia == null ? "" : escuderia.getNombre());
            }
        };
    }

    private ListCell<TipoAuto> crearTipoAutoCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(TipoAuto tipoAuto, boolean empty) {
                super.updateItem(tipoAuto, empty);
                setText(empty || tipoAuto == null ? "" : tipoAuto.name());
            }
        };
    }

    private void cargarTodo() {
        List<Escuderia> escuderias = escuderiaController.consultarEscuderias();
        pilotosTable.getItems().setAll(pilotoController.obtenerTodos());
        escuderiasTable.getItems().setAll(escuderias);
        autosTable.getItems().setAll(autoController.consultarAutos());
        pilotoEscuderiaCombo.getItems().setAll(escuderias);
        autoEscuderiaCombo.getItems().setAll(escuderias);
        estadoLabel.setText("Datos actualizados.");
    }

    private void completarPiloto(Piloto piloto) {
        if (piloto == null) return;
        pilotoNombreField.setText(piloto.getNombre());
        pilotoHabilidadSpinner.getValueFactory().setValue(piloto.getHabilidad());
        pilotoEscuderiaCombo.getItems().stream()
                .filter(e -> e.getId() == piloto.getEscuderiaId())
                .findFirst()
                .ifPresent(pilotoEscuderiaCombo::setValue);
    }

    private void completarEscuderia(Escuderia escuderia) {
        if (escuderia == null) return;
        escuderiaNombreField.setText(escuderia.getNombre());
        escuderiaColorField.setText(ColorUtil.toHex(escuderia.getColor()));
    }

    private void completarAuto(Auto auto) {
        if (auto == null) return;
        autoModeloField.setText(auto.getModelo());
        autoVelocidadField.setText(String.valueOf(auto.getVelocidadBase()));
        autoTipoCombo.setValue(auto.getTipoAuto());
        if (auto.getEscuderia() != null) {
            autoEscuderiaCombo.getItems().stream()
                    .filter(e -> e.getId() == auto.getEscuderia().getId())
                    .findFirst()
                    .ifPresent(autoEscuderiaCombo::setValue);
        }
    }

    private double leerVelocidad() {
        try {
            return Double.parseDouble(autoVelocidadField.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La velocidad base debe ser un número. Ejemplo: 300 o 0.055.");
        }
    }

    private String mensajeAuto(RuntimeException e) {
        Throwable actual = e;
        while (actual != null) {
            String mensaje = actual.getMessage();
            if (mensaje != null && mensaje.contains("FOREIGN KEY constraint failed")) {
                return AUTO_REFERENCIADO;
            }
            actual = actual.getCause();
        }
        return e.getMessage() == null ? "No se pudo completar la operación del auto." : e.getMessage();
    }

    private void limpiarPiloto() {
        pilotoNombreField.clear();
        pilotoHabilidadSpinner.getValueFactory().setValue(80);
    }

    private void limpiarEscuderia() {
        escuderiaNombreField.clear();
        escuderiaColorField.setText("#E53935");
    }

    private void limpiarAuto() {
        autoModeloField.clear();
        autoVelocidadField.clear();
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