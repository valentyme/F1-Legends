package com.f1legends.vista;

import com.f1legends.modelo.Auto;
import com.f1legends.modelo.Carrera;
import com.f1legends.modelo.CircuitoMonza;
import com.f1legends.modelo.Escuderia;
import com.f1legends.patrones.factory.FabricaAuto;
import com.f1legends.patrones.factory.TipoAuto;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class App extends Application {
    private static final int ANCHO = 950;
    private static final int ALTO = 650;

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(ANCHO, ALTO);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        FabricaAuto fabricaAuto = new FabricaAuto();
        Auto ferrari = fabricaAuto.crearAuto(TipoAuto.FERRARI);
        Auto mercedes = fabricaAuto.crearAuto(TipoAuto.MERCEDES);
        Auto redbull = fabricaAuto.crearAuto(TipoAuto.RED_BULL);

        Escuderia mclarenEsc = new Escuderia(4, "McLaren", Color.ORANGE);
        Escuderia alpineEsc = new Escuderia(5, "Alpine", Color.DEEPSKYBLUE);
        Auto mclaren = new Auto(4, "MCL60", 0.052, mclarenEsc);
        Auto alpine = new Auto(5, "A524", 0.050, alpineEsc);

        Carrera carrera = new Carrera(1, new CircuitoMonza(), "08/06/2026", 3, "Soleado");
        carrera.agregarAuto(ferrari);
        carrera.agregarAuto(mercedes);
        carrera.agregarAuto(redbull);
        carrera.agregarAuto(mclaren);
        carrera.agregarAuto(alpine);
        carrera.iniciar();

        Label estadoLabel = new Label();
        Label posicionesLabel = new Label();

        Button pausarButton = new Button("Pausar");
        Button reanudarButton = new Button("Reanudar");
        Button abandonarButton = new Button("Abandonar");

        pausarButton.setOnAction(event -> carrera.pausar());
        reanudarButton.setOnAction(event -> carrera.reanudar());
        abandonarButton.setOnAction(event -> carrera.abandonar());

        VBox panel = new VBox(12, estadoLabel, posicionesLabel, pausarButton, reanudarButton, abandonarButton);
        panel.setPadding(new Insets(16));
        panel.setPrefWidth(220);

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

                gc.setFill(Color.rgb(35, 125, 35));
                gc.fillRect(0, 0, ANCHO, ALTO);

                carrera.getCircuito().dibujar(gc);

                for (Auto auto : carrera.getAutos()) {
                    var pos = carrera.getCircuito().calcularPosicion(auto.getProgreso());
                    gc.setFill(auto.getEscuderia().getColor());
                    gc.fillOval(pos.x - 9, pos.y - 9, 18, 18);
                    gc.setStroke(Color.WHITE);
                    gc.setLineWidth(1.5);
                    gc.strokeOval(pos.x - 9, pos.y - 9, 18, 18);
                }

                estadoLabel.setText("Estado: " + carrera.getNombreEstado());
                posicionesLabel.setText(crearTextoPosiciones(carrera));
            }
        };
        timer.start();

        BorderPane root = new BorderPane();
        root.setCenter(canvas);
        root.setRight(panel);

        stage.setScene(new Scene(root, ANCHO + 220, ALTO));
        stage.setTitle("F1 Legends - Monza");
        stage.show();
    }

    private String crearTextoPosiciones(Carrera carrera) {
        StringBuilder texto = new StringBuilder("Posiciones\n");
        int posicion = 1;

        for (Auto auto : carrera.getPosiciones()) {
            texto.append(posicion)
                    .append(". ")
                    .append(auto.getModelo())
                    .append(" - vuelta ")
                    .append(auto.getVueltasCompletadas())
                    .append("/")
                    .append(carrera.getVueltas())
                    .append("\n");
            posicion++;
        }

        return texto.toString();
    }
}
