package com.f1legends.utiles;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class PaletaColoresDemo {

    private static boolean inicializado = false;

    private static void inicializarJavaFX() {
        if (!inicializado) {
            Platform.startup(() -> {});
            inicializado = true;
        }
    }

    public static Color mostrarPicker() {
        inicializarJavaFX();

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Color> colorSeleccionado =
                new AtomicReference<>(Color.GRAY);

        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);

            ColorPicker picker = new ColorPicker(Color.RED);

            picker.setOnAction(e -> {
                colorSeleccionado.set(picker.getValue());
                stage.close();
            });

            stage.setOnHidden(e -> latch.countDown());

            VBox root = new VBox(10);
            root.getChildren().add(picker);

            Scene scene = new Scene(root, 250, 100);

            stage.setTitle("Seleccionar color para escudería");
            stage.setScene(scene);
            stage.show();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return colorSeleccionado.get();
    }
}