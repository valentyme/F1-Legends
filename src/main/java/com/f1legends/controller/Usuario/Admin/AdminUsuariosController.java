package com.f1legends.controller.Usuario.Admin;

import com.f1legends.DAO.modeloDAO.RankingGlobalDAO;
import com.f1legends.DAO.modeloDAO.UsuarioDAO;
import com.f1legends.controller.Usuario.Sesion;
import com.f1legends.controller.Usuario.UsuarioController;
import com.f1legends.modelo.Usuarios.Usuario;
import com.f1legends.vista.FxRouter;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.util.List;

public class AdminUsuariosController {
    private final UsuarioController usuarioController = new UsuarioController(
            new UsuarioDAO(),
            new RankingGlobalDAO()
    );

    @FXML private TableView<UsuarioRow> usuariosTable;
    @FXML private TableColumn<UsuarioRow, Number> idColumn;
    @FXML private TableColumn<UsuarioRow, String> usernameColumn;
    @FXML private TableColumn<UsuarioRow, String> rolColumn;
    @FXML private TableColumn<UsuarioRow, String> registroColumn;
    @FXML private Label estadoLabel;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(cell -> new ReadOnlyIntegerWrapper(cell.getValue().id()));
        usernameColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().username()));
        rolColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().rol()));
        registroColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().fechaRegistro()));
        cargarUsuarios();
    }

    @FXML
    private void cargarUsuarios() {
        usuariosTable.getItems().clear();
        List<Usuario> usuarios = usuarioController.obtenerUsuarios();
        for (Usuario usuario : usuarios) {
            usuariosTable.getItems().add(new UsuarioRow(
                    usuario.getId(),
                    usuario.getUsername(),
                    Sesion.getRol(usuario),
                    usuario.getFechaRegistro()
            ));
        }
        estadoLabel.setText("Usuarios cargados: " + usuarios.size());
    }

    @FXML
    private void eliminarSeleccionado() {
        UsuarioRow seleccionado = usuariosTable.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Seleccioná un usuario para eliminar.");
            return;
        }

        if (Sesion.getUsuarioActual().getId() == seleccionado.id()) {
            mostrarError("No podés eliminar el admin actualmente logueado.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar usuario");
        alert.setHeaderText("Eliminar a " + seleccionado.username());
        alert.setContentText("También se eliminará su historial de ranking.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            usuarioController.eliminarPerfil(seleccionado.id());
            cargarUsuarios();
            mostrarOk("Usuario eliminado correctamente.");
        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void volver() throws IOException {
        FxRouter.showAdminHome();
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

    private record UsuarioRow(int id, String username, String rol, String fechaRegistro) {
    }
}