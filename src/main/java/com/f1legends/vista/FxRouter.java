package com.f1legends.vista;

import com.f1legends.controller.Sesion;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public final class FxRouter {
    private static final String CSS = "/com/f1legends/vista/styles/f1-legends.css";
    private static Stage stage;

    private FxRouter() {
    }

    public static void init(Stage primaryStage) {
        stage = primaryStage;
    }

    public static void showAuth() throws IOException {
        Sesion.cerrarSesion();
        setScene("/com/f1legends/vista/auth-view.fxml", "F1 Legends - Iniciar sesion");
    }

    public static void showJugadorHome() throws IOException {
        setScene("/com/f1legends/vista/jugador-home.fxml", "F1 Legends - Jugador");
    }

    public static void showAdminHome() throws IOException {
        setScene("/com/f1legends/vista/admin-home.fxml", "F1 Legends - Administrador");
    }

    public static void showRanking() throws IOException {
        setScene("/com/f1legends/vista/ranking-view.fxml", "F1 Legends - Ranking");
    }

    public static void showPerfilJugador() throws IOException {
        setScene("/com/f1legends/vista/perfil-jugador.fxml", "F1 Legends - Mi perfil");
    }

    public static void showPrepararCarrera() throws IOException {
        setScene("/com/f1legends/vista/preparar-carrera.fxml", "F1 Legends - Preparar carrera");
    }

    /** Navega a la pantalla de configuración de carrera Multijugador Local. */
    public static void showPrepararMultijugador() throws IOException {
        setScene("/com/f1legends/vista/preparar-multijugador.fxml", "F1 Legends - Multijugador Local");
    }

    public static void showAdminUsuarios() throws IOException {
        setScene("/com/f1legends/vista/admin-usuarios.fxml", "F1 Legends - Usuarios");
    }

    public static void showAdminGestionDatos() throws IOException {
        setScene("/com/f1legends/vista/admin-gestion-datos.fxml", "F1 Legends - Gestion de datos");
    }

    public static void volverAlHome() throws IOException {
        if (Sesion.esAdministrador()) {
            showAdminHome();
        } else {
            showJugadorHome();
        }
    }

    private static void setScene(String fxml, String title) throws IOException {
        if (stage == null) {
            throw new IllegalStateException("FxRouter no fue inicializado con un Stage.");
        }

        FXMLLoader loader = new FXMLLoader(FxRouter.class.getResource(fxml));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1100, 720);
        scene.getStylesheets().add(FxRouter.class.getResource(CSS).toExternalForm());
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }
}