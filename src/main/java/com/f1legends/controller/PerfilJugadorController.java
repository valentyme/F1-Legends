package com.f1legends.controller;

import com.f1legends.DAO.modeloDAO.RankingGlobalDAO;
import com.f1legends.DAO.modeloDAO.UsuarioDAO;
import com.f1legends.modelo.Usuarios.Usuario;
import com.f1legends.vista.FxRouter;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class PerfilJugadorController {
    private final UsuarioController usuarioController = new UsuarioController(
            new UsuarioDAO(),
            new RankingGlobalDAO()
    );

    @FXML private Label usuarioActualLabel;
    @FXML private Label fechaRegistroLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label estadoLabel;

    @FXML
    private void initialize() {
        Usuario usuario = Sesion.getUsuarioActual();
        usuarioActualLabel.setText(usuario.getUsername());
        fechaRegistroLabel.setText("Registro: " + usuario.getFechaRegistro());
        usernameField.setText(usuario.getUsername());
    }

    @FXML
    private void guardarCambios() {
        try {
            usuarioController.modificarPerfil(usernameField.getText().trim(), passwordField.getText().trim());
            usuarioActualLabel.setText(Sesion.getUsuarioActual().getUsername());
            passwordField.clear();
            mostrarOk("Perfil actualizado correctamente.");
        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void eliminarCuenta() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar cuenta");
        alert.setHeaderText("Vas a eliminar tu cuenta");
        alert.setContentText("Esta acción también borra tu historial de ranking.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        usuarioController.eliminarPerfil(Sesion.getUsuarioActual().getId());
        FxRouter.showAuth();
    }

    @FXML
    private void volver() throws IOException {
        FxRouter.showJugadorHome();
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
