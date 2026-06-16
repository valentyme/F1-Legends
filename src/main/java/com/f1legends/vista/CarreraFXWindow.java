package com.f1legends.vista;
import com.f1legends.modelo.auto.Auto;
import com.f1legends.modelo.carreras.Carrera;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Ventana JavaFX que visualiza cualquier Carrera ya configurada.
 * Se puede abrir desde el flujo de consola (MainFX) o desde una
 * pantalla de configuración JavaFX en el futuro.
 *
 * Uso:
 *   CarreraFXWindow ventana = new CarreraFXWindow(carrera, jugador, facade);
 *   ventana.mostrarYEsperar(ownerStage); // bloquea hasta que la carrera termine
 */
public class CarreraFXWindow {

    private static final int ANCHO_CANVAS = 950;
    private static final int ALTO_CANVAS  = 650;
    private static final int ANCHO_PANEL  = 260;

    private final Carrera carrera;

    // Latch para que el hilo que llama pueda esperar a que cierre la ventana
    private final CountDownLatch latch = new CountDownLatch(1);

    // Log de eventos para mostrar en pantalla (observer)
    private final List<String> logEventos = new ArrayList<>();
    private static final int MAX_LOG = 10;

    //Para mostrar eventos visualmente
    private final List<EventoVisual> eventosVisuales = new ArrayList<>();
    private final Object lockEventos = new Object();

    public CarreraFXWindow(Carrera carrera) {
        this.carrera = carrera;
    }

    /**
     * Abre la ventana y bloquea el hilo llamador hasta que se cierre.
     * Debe llamarse desde el hilo de JavaFX (Platform.runLater) o desde
     * MainFX que ya corre en el Application thread.
     */
    public void mostrar(Stage owner) {
        Stage stage = new Stage();
        if (owner != null) {
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
        }
        stage.setTitle("F1 Legends — " + carrera.getCircuito().getNombre()
                + " (" + carrera.getVueltas() + " vueltas)");
        stage.setOnCloseRequest(e -> latch.countDown());

        // ── Canvas principal ──────────────────────────
        Canvas canvas = new Canvas(ANCHO_CANVAS, ALTO_CANVAS);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // ── Panel lateral ─────────────────────────────
        // ── Estilos base ──────────────────────────────
        String estiloPanel = "-fx-background-color: #0d0d0d;";

        String estiloTitulo = """
        -fx-font-family: 'Segoe UI';
        -fx-font-size: 11px;
        -fx-font-weight: bold;
        -fx-text-fill: #cc0000;
        -fx-padding: 6 0 2 0;
        """;

        String estiloValor = """
        -fx-font-family: 'Segoe UI';
        -fx-font-size: 12px;
        -fx-text-fill: #ffffff;
        """;

        String estiloPosiciones = """
        -fx-font-family: 'Courier New';
        -fx-font-size: 11px;
        -fx-text-fill: #ffffff;
        -fx-line-spacing: 2;
        """;

        String estiloEventos = """
        -fx-font-family: 'Courier New';
        -fx-font-size: 10px;
        -fx-text-fill: #aaaaaa;
        -fx-line-spacing: 2;
        """;

        String estiloSeparador = """
        -fx-text-fill: #333333;
        -fx-font-size: 10px;
        """;

    // ── Labels ────────────────────────────────────
        Label tituloEstado  = new Label("ESTADO");
        Label lblEstado     = new Label("Inicio");
        Label tituloClima   = new Label("CLIMA");
        Label lblClima      = new Label(carrera.getClimaInicial());
        Label tituloPosiciones = new Label("POSICIONES");
        Label lblPos        = new Label();
        Label tituloEventos = new Label("EVENTOS RECIENTES");
        Label lblEventos    = new Label();
        Label sep1 = new Label("────────────────────");
        Label sep2 = new Label("────────────────────");
        Label sep3 = new Label("────────────────────");

        tituloEstado.setStyle(estiloTitulo);
        tituloClima.setStyle(estiloTitulo);
        tituloPosiciones.setStyle(estiloTitulo);
        tituloEventos.setStyle(estiloTitulo);

        lblEstado.setStyle(estiloValor);
        lblClima.setStyle(estiloValor);
        lblPos.setStyle(estiloPosiciones);
        lblEventos.setStyle(estiloEventos);

        sep1.setStyle(estiloSeparador);
        sep2.setStyle(estiloSeparador);
        sep3.setStyle(estiloSeparador);

        lblEventos.setWrapText(true);
        lblPos.setWrapText(true);

        // ── Botones ───────────────────────────────────
        Button btnPausar    = new Button("⏸  PAUSAR");
        Button btnReanudar  = new Button("▶  REANUDAR");
        Button btnAbandonar = new Button("✖  ABANDONAR");

        String estiloBtnBase = """
        -fx-font-family: 'Segoe UI';
        -fx-font-size: 11px;
        -fx-font-weight: bold;
        -fx-cursor: hand;
        -fx-background-radius: 4;
        -fx-padding: 8 12 8 12;
        """;

        btnPausar.setStyle(estiloBtnBase + """
        -fx-background-color: #1a1a1a;
        -fx-text-fill: #ffffff;
        -fx-border-color: #444444;
        -fx-border-radius: 4;
        -fx-border-width: 1;
        """);

        btnReanudar.setStyle(estiloBtnBase + """
        -fx-background-color: #1a1a1a;
        -fx-text-fill: #ffffff;
        -fx-border-color: #444444;
        -fx-border-radius: 4;
        -fx-border-width: 1;
        """);

        btnAbandonar.setStyle(estiloBtnBase + """
        -fx-background-color: #3a0000;
        -fx-text-fill: #ff4444;
        -fx-border-color: #cc0000;
        -fx-border-radius: 4;
        -fx-border-width: 1;
        """);

        btnPausar.setMaxWidth(Double.MAX_VALUE);
        btnReanudar.setMaxWidth(Double.MAX_VALUE);
        btnAbandonar.setMaxWidth(Double.MAX_VALUE);

        btnPausar.setOnAction(e -> carrera.pausar());
        btnReanudar.setOnAction(e -> carrera.reanudar());
        btnAbandonar.setOnAction(e -> {
            carrera.abandonar();
            stage.close();
            latch.countDown();
        });

        // ── Ensamblado del panel ──────────────────────
        VBox panel = new VBox(6,
                tituloEstado, lblEstado,
                tituloClima,  lblClima,
                sep1,
                tituloPosiciones, lblPos,
                sep2,
                tituloEventos, lblEventos,
                sep3,
                btnPausar, btnReanudar, btnAbandonar
        );
        panel.setPadding(new Insets(16, 14, 16, 14));
        panel.setPrefWidth(ANCHO_PANEL);
        panel.setStyle(estiloPanel);


        // ── Observer: captura eventos y los convierte en visuales ──────────────
        carrera.agregarObservador((evento, c) -> {
            // Log de texto (panel lateral)
            String linea = "[V" + evento.getVuelta() + "] " + evento.getDescripcion();
            logEventos.add(0, linea);
            if (logEventos.size() > MAX_LOG) logEventos.remove(logEventos.size() - 1);

            // Evento visual en canvas
            crearEventoVisual(evento.getTipo(), evento.getDescripcion(), c);
        });

        // ── Layout ────────────────────────────────────
        BorderPane root = new BorderPane();
        root.setCenter(canvas);
        root.setRight(panel);
        root.setStyle("-fx-background-color: #1a1a2e;");

        stage.setScene(new Scene(root, ANCHO_CANVAS + ANCHO_PANEL, ALTO_CANVAS));
        stage.show();

        // ── AnimationTimer ────────────────────────────────────────────────────
        AnimationTimer timer = new AnimationTimer() {
            private long ultimaActualizacion = 0;

            @Override
            public void handle(long ahora) {
                if (ultimaActualizacion == 0) { ultimaActualizacion = ahora; return; }

                double dt = (ahora - ultimaActualizacion) / 1_000_000_000.0;
                ultimaActualizacion = ahora;

                carrera.actualizar(dt);

                // ── Fondo ──
                gc.setFill(Color.rgb(20, 30, 20));
                gc.fillRect(0, 0, ANCHO_CANVAS, ALTO_CANVAS);

                // ── Circuito ──
                carrera.getCircuito().dibujar(gc);

                // ── Autos ──
                for (Auto auto : carrera.getAutos()) {
                    dibujarAuto(gc, auto, carrera);
                }

                // ── Eventos visuales flotantes ──
                List<EventoVisual> aEliminar = new ArrayList<>();
                for (EventoVisual ev : eventosVisuales) {
                    ev.tiempoRestante -= dt;
                    if (ev.tiempoRestante <= 0) {
                        aEliminar.add(ev);
                    } else {
                        dibujarEventoVisual(gc, ev);
                    }
                }
                eventosVisuales.removeAll(aEliminar);

                // ── Panel ──
                lblEstado.setText("Estado: " + carrera.getNombreEstado());
                lblClima.setText("Clima: " + iconClima(carrera.getClimaActual())
                        + " " + carrera.getClimaActual());
                lblPos.setText(textoPosturas(carrera));
                lblEventos.setText(String.join("\n", logEventos));

                if (carrera.estaCompletada()) {
                    stop();
                    Platform.runLater(() -> {
                        stage.setTitle(stage.getTitle() + " — FINALIZADA ✓");
                        btnPausar.setDisable(true);
                        btnReanudar.setDisable(true);
                        latch.countDown();
                    });
                }
            }
        };
        timer.start();
    }

    /** Espera a que la ventana se cierre (para uso desde hilo no-FX). */
    public void esperarCierre() throws InterruptedException {
        latch.await();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String textoPosturas(Carrera carrera) {
        StringBuilder sb = new StringBuilder("Posiciones:\n");
        int pos = 1;
        for (Auto a : carrera.getPosiciones()) {
            String estado = a.isFueraCarrera() ? " [OUT]" : "";
            sb.append(String.format("%d. %-22s V%d/%d%s\n",
                    pos++,
                    cortarNombre(a.getModelo(), 22),
                    a.getVueltasCompletadas(),
                    carrera.getVueltas(),
                    estado));
        }
        return sb.toString();
    }

    private String cortarNombre(String nombre, int max) {
        return nombre.length() <= max ? nombre : nombre.substring(0, max - 1) + "…";
    }

    private void dibujarAuto(GraphicsContext gc, Auto auto, Carrera carrera) {
        // Autos eliminados: no se dibujan
        if (auto.isFueraCarrera()) return;

        var pos = carrera.getCircuito().calcularPosicion(auto.getProgreso());

        // Color base del auto
        Color colorBase = auto.getEscuderia().getColor();

        // Si está detenido en boxes: color atenuado + ícono pit
        boolean enBoxes = auto.estaDetenido(); // necesitás este getter (ver abajo)

        gc.setFill(enBoxes ? colorBase.darker().darker() : colorBase);
        gc.fillOval(pos.x - 9, pos.y - 9, 18, 18);

        gc.setStroke(enBoxes ? Color.YELLOW : Color.WHITE);
        gc.setLineWidth(enBoxes ? 2.5 : 1.5);
        gc.strokeOval(pos.x - 9, pos.y - 9, 18, 18);

        // Ícono de boxes encima
        if (enBoxes) {
            gc.setFill(Color.YELLOW);
            gc.setFont(javafx.scene.text.Font.font(11));
            gc.fillText("🔧", pos.x - 6, pos.y - 12);
        }

        // Nombre
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(9));
        String etiqueta = auto.getModelo().contains(" / ")
                ? auto.getModelo().split(" / ")[0]
                : auto.getModelo();
        gc.fillText(etiqueta, pos.x + 11, pos.y + 4);
    }

    private void dibujarEventoVisual(GraphicsContext gc, EventoVisual ev) {
        double alpha = Math.min(1.0, ev.tiempoRestante / 0.5); // fade out en últimos 0.5s

        gc.save();
        gc.setGlobalAlpha(alpha);

        // Fondo del cartel según tipo
        Color fondo = switch (ev.tipo) {
            case ACCIDENTE      -> Color.rgb(200, 30, 30, 0.85);
            case BOXES          -> Color.rgb(200, 160, 0, 0.85);
            case ADELANTAMIENTO -> Color.rgb(30, 120, 200, 0.85);
            case CLIMA          -> Color.rgb(80, 80, 200, 0.85);
            case FINALIZACION   -> Color.rgb(30, 180, 30, 0.85);
        };

        String icono = switch (ev.tipo) {
            case ACCIDENTE      -> "💥";
            case BOXES          -> "🔧";
            case ADELANTAMIENTO -> "⚡";
            case CLIMA          -> "🌦";
            case FINALIZACION   -> "🏁";
        };

        // Tamaño del cartel
        double ancho = 260;
        double alto  = 36;
        double rx = ev.x - ancho / 2;
        double ry = ev.y - alto / 2;

        gc.setFill(fondo);
        gc.fillRoundRect(rx, ry, ancho, alto, 10, 10);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeRoundRect(rx, ry, ancho, alto, 10, 10);

        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("SansSerif", javafx.scene.text.FontWeight.BOLD, 12));
        gc.fillText(icono + " " + cortar(ev.texto, 34), rx + 10, ry + 24);

        gc.restore();
    }

    private void crearEventoVisual(String tipo, String descripcion, Carrera c) {
        // Posición: eventos graves al centro, el resto arriba
        EventoVisual.Tipo tipoVisual = switch (tipo) {
            case "ACCIDENTE"      -> EventoVisual.Tipo.ACCIDENTE;
            case "BOXES"          -> EventoVisual.Tipo.BOXES;
            case "ADELANTAMIENTO" -> EventoVisual.Tipo.ADELANTAMIENTO;
            case "CAMBIO_CLIMA"   -> EventoVisual.Tipo.CLIMA;
            case "FINALIZACION"   -> EventoVisual.Tipo.FINALIZACION;
            default -> null;
        };

        if (tipoVisual == null) return;

        // Posición en canvas: apilados verticalmente para que no se pisen
        double baseY = 60 + (eventosVisuales.size() % 5) * 46.0;
        double cx    = ANCHO_CANVAS / 2.0;

        double duracion = switch (tipoVisual) {
            case ACCIDENTE   -> 4.0;
            case FINALIZACION -> 6.0;
            default          -> 2.5;
        };

        eventosVisuales.add(new EventoVisual(tipoVisual, descripcion, cx, baseY, duracion));
    }

    private String iconClima(String clima) {
        return switch (clima) {
            case "Lluvioso" -> "🌧";
            case "Nublado"  -> "☁";
            default         -> "☀";
        };
    }

    private String cortar(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }
}