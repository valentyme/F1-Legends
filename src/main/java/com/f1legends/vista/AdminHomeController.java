package com.f1legends.vista;

import com.f1legends.controller.Sesion;
import com.f1legends.modelo.Usuarios.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class AdminHomeController {
    @FXML private Label usuarioLabel;
    @FXML private Label detalleLabel;
    @FXML private Label estadoLabel;

    @FXML
    private void initialize() {
        Usuario admin = Sesion.getUsuarioActual();
        usuarioLabel.setText(admin.getUsername());
        detalleLabel.setText("Panel de administración");
        estadoLabel.setText("Elegí una gestión para continuar.");
    }

    @FXML
    private void mostrarProximamente() {
        estadoLabel.setText("Esta pantalla se conecta en la próxima parte.");
    }

    @FXML
    private void abrirRanking() throws IOException {
        FxRouter.showRanking();
    }

    @FXML
    private void abrirUsuarios() throws IOException {
        FxRouter.showAdminUsuarios();
    }

    @FXML
    private void abrirGestionDatos() throws IOException {
        FxRouter.showAdminGestionDatos();
    }

    @FXML
    private void cerrarSesion() throws IOException {
        FxRouter.showAuth();
    }
}
