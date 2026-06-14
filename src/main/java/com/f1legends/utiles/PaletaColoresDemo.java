package com.f1legends.utiles;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

public class PaletaColoresDemo extends Application {
    // Variable estática para guardar el color elegido
    public static Color colorElegido;

    @Override
    public void start(Stage stage) {
        ColorPicker picker = new ColorPicker(Color.RED);

        picker.setOnAction(e -> {
            colorElegido = picker.getValue(); // guarda el color
            stage.close(); // cierra la ventana
        });

        VBox root = new VBox(picker);
        Scene scene = new Scene(root, 200, 100);
        stage.setScene(scene);
        stage.setTitle("Seleccionar color para escudería");
        stage.show();
    }

    // Este método lanza la aplicación JavaFX y devuelve el color elegido
    public static Color mostrarPicker() {
        // Lanza JavaFX (solo puede llamarse una vez por ejecución)
        Application.launch(PaletaColoresDemo.class);
        return colorElegido != null ? colorElegido : Color.GRAY; // valor por defecto si no se elige nada
    }
}

