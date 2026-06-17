package com.f1legends.controller;

import com.f1legends.modelo.Usuarios.Jugador;
import com.f1legends.vista.FxRouter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class JugadorHomeController {
    @FXML private Label usuarioLabel;
    @FXML private Label detalleLabel;
    @FXML private Label estadoLabel;

    @FXML
    private void initialize() {
        Jugador jugador = Sesion.getJugadorActual();
        usuarioLabel.setText(jugador.getUsername());
        detalleLabel.setText("Jugador desde " + jugador.getFechaRegistro());
        estadoLabel.setText("Elegí una acción para continuar.");
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
    private void abrirPrepararCarrera() throws IOException {
        FxRouter.showPrepararCarrera();
    }

    @FXML
    private void abrirMultijugador() throws IOException {
        FxRouter.showPrepararMultijugador();
    }

    @FXML
    private void abrirPerfil() throws IOException {
        FxRouter.showPerfilJugador();
    }

    @FXML
    private void cerrarSesion() throws IOException {
        FxRouter.showAuth();
    }
}