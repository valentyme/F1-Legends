package com.f1legends.vista;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Punto de entrada JavaFX.
 * Lanza el flujo de consola en un hilo aparte para no bloquear
 * el hilo de JavaFX, y le pasa el Stage primario a CarreraFXWindow
 * cuando se necesita mostrar la carrera.
 */
public class MainFX extends Application {

    // Stage compartido entre el hilo FX y el hilo de consola
    public static volatile Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        stage.setTitle("F1 Legends");
        // No mostramos nada todavía; la consola maneja la navegación

        // Correr la lógica de consola en un hilo separado
        Thread hiloConsola = new Thread(() -> {
            try {
                MainConsola.iniciar(); // ← ver paso 3
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Platform.exit();
            }
        });
        hiloConsola.setDaemon(true);
        hiloConsola.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}