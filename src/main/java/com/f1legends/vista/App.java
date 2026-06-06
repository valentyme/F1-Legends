package com.f1legends.vista;

import com.f1legends.modelo.Auto;
import com.f1legends.modelo.CircuitoMonza;
import com.f1legends.servicios.CarreraService;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Arrays;

public class App extends Application {
    private static final int ANCHO = 950;
    private static final int ALTO = 650;
    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(ANCHO, ALTO);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Autos con colores oficiales
        Auto ferrari   = new Auto("Ferrari",   0.055, Color.RED);
        Auto mercedes  = new Auto("Mercedes",  0.051, Color.SILVER);   // plateado
        Auto redbull   = new Auto("Red Bull",  0.058, Color.DARKBLUE);
        Auto mclaren   = new Auto("McLaren",   0.052, Color.ORANGE);
        Auto alpine    = new Auto("Alpine",    0.050, Color.DEEPSKYBLUE);

        CarreraService carrera = new CarreraService(
                new CircuitoMonza(),
                Arrays.asList(ferrari, mercedes, redbull, mclaren, alpine)
        );

        AnimationTimer timer = new AnimationTimer() {
            private long ultimaActualizacion = 0;

            @Override
            public void handle(long ahora) {
                if (ultimaActualizacion == 0) {
                    ultimaActualizacion = ahora;
                    return;
                }
                double deltaTiempo = (ahora - ultimaActualizacion) / 1_000_000_000.0;
                ultimaActualizacion = ahora;

                carrera.actualizar(deltaTiempo);

                // Fondo verde
                gc.setFill(Color.rgb(35, 125, 35));
                gc.fillRect(0, 0, ANCHO, ALTO);

                // Dibujar circuito
                carrera.getCircuito().dibujar(gc);

                // Dibujar autos con su color
                for (Auto auto : carrera.getAutos()) {
                    var pos = carrera.getCircuito().calcularPosicion(auto.getProgreso());
                    gc.setFill(auto.getColor());
                    gc.fillOval(pos.x - 9, pos.y - 9, 18, 18);
                    gc.setStroke(Color.WHITE);
                    gc.setLineWidth(1.5);
                    gc.strokeOval(pos.x - 9, pos.y - 9, 18, 18);
                }
            }
        };
        timer.start();

        stage.setScene(new Scene(new StackPane(canvas), ANCHO, ALTO));
        stage.setTitle("F1 Legends - Monza");
        stage.show();
    }

}
