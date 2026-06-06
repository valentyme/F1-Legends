package com.f1legends.vista;

import com.f1legends.modelo.Auto;
import com.f1legends.modelo.CircuitoMonza;
import com.f1legends.modelo.Escuderia;
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

        // Escuderías con colores oficiales
        Escuderia ferrariEsc = new Escuderia(1, "Ferrari", Color.RED);
        Escuderia mercedesEsc = new Escuderia(2, "Mercedes", Color.SILVER);
        Escuderia redbullEsc = new Escuderia(3, "Red Bull", Color.DARKBLUE);
        Escuderia mclarenEsc = new Escuderia(4, "McLaren", Color.ORANGE);
        Escuderia alpineEsc = new Escuderia(5, "Alpine", Color.DEEPSKYBLUE);

        // Autos vinculados a sus escuderías
        Auto ferrari   = new Auto(1, "SF-24", 0.055, ferrariEsc);
        Auto mercedes  = new Auto(2, "W15",   0.051, mercedesEsc);
        Auto redbull   = new Auto(3, "RB20",  0.058, redbullEsc);
        Auto mclaren   = new Auto(4, "MCL60", 0.052, mclarenEsc);
        Auto alpine    = new Auto(5, "A524",  0.050, alpineEsc);

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

                // Dibujar autos con el color de su escudería
                for (Auto auto : carrera.getAutos()) {
                    var pos = carrera.getCircuito().calcularPosicion(auto.getProgreso());
                    gc.setFill(auto.getEscuderia().getColor());
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
