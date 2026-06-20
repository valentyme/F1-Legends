package com.f1legends.controller.Usuario;

import com.f1legends.DAO.modeloDAO.RankingGlobalDAO;
import com.f1legends.DAO.modeloDAO.UsuarioDAO;
import com.f1legends.modelo.Usuarios.Usuario;
import com.f1legends.vista.FxRouter;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Optional;

public class AuthController {
    private final UsuarioController usuarioController = new UsuarioController(
            new UsuarioDAO(),
            new RankingGlobalDAO()
    );

    @FXML private TextField loginUsuarioField;
    @FXML private PasswordField loginPasswordField;
    @FXML private Label loginMensajeLabel;
    @FXML private TextField registroUsuarioField;
    @FXML private PasswordField registroPasswordField;
    @FXML private ChoiceBox<String> registroRolChoice;
    @FXML private Label registroMensajeLabel;

    @FXML
    private void initialize() {
        registroRolChoice.getItems().setAll("Jugador", "Administrador");
        registroRolChoice.setValue("Jugador");
    }

    @FXML
    private void iniciarSesion() throws IOException {
        limpiarMensajes();
        String username = loginUsuarioField.getText().trim();
        String password = loginPasswordField.getText().trim();

        if (username.isBlank() || password.isBlank()) {
            mostrarError(loginMensajeLabel, "Completá usuario y contraseña.");
            return;
        }

        try {
            Optional<Usuario> usuario = usuarioController.iniciarSesion(username, password);
            if (usuario.isEmpty()) {
                mostrarError(loginMensajeLabel, "Usuario o contraseña incorrectos.");
                return;
            }

            if (Sesion.esAdministrador()) {
                FxRouter.showAdminHome();
            } else {
                FxRouter.showJugadorHome();
            }
        } catch (RuntimeException e) {
            mostrarError(loginMensajeLabel, e.getMessage());
        }
    }

    @FXML
    private void crearPerfil() {
        limpiarMensajes();
        String username = registroUsuarioField.getText().trim();
        String password = registroPasswordField.getText().trim();
        String rol = registroRolChoice.getValue();

        if (username.isBlank() || password.isBlank()) {
            mostrarError(registroMensajeLabel, "Completá usuario y contraseña.");
            return;
        }

        try {
            usuarioController.crearPerfil(username, password, rol);
            registroUsuarioField.clear();
            registroPasswordField.clear();
            mostrarOk(registroMensajeLabel, "Perfil creado. Ya podés iniciar sesión.");
        } catch (RuntimeException e) {
            mostrarError(registroMensajeLabel, e.getMessage());
        }
    }

    private void limpiarMensajes() {
        loginMensajeLabel.setText("");
        registroMensajeLabel.setText("");
        loginMensajeLabel.getStyleClass().removeAll("message-error", "message-ok");
        registroMensajeLabel.getStyleClass().removeAll("message-error", "message-ok");
    }

    private void mostrarError(Label label, String mensaje) {
        label.getStyleClass().removeAll("message-ok");
        if (!label.getStyleClass().contains("message-error")) {
            label.getStyleClass().add("message-error");
        }
        label.setText(mensaje);
    }

    private void mostrarOk(Label label, String mensaje) {
        label.getStyleClass().removeAll("message-error");
        if (!label.getStyleClass().contains("message-ok")) {
            label.getStyleClass().add("message-ok");
        }
        label.setText(mensaje);
    }
}