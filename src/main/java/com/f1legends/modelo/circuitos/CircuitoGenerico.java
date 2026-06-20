package com.f1legends.modelo.circuitos;

import com.f1legends.modelo.Punto;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class CircuitoGenerico extends Circuito {

    // ── Paleta de Colores Estándar ───────────────────────────────────────
    private static final Color ASFALTO      = Color.rgb(45,  45,  50);
    private static final Color ASFALTO_MID  = Color.rgb(60,  60,  66);
    private static final Color KERB_ROJO    = Color.rgb(200, 30,  30);
    private static final Color KERB_BLANCO  = Color.rgb(240, 240, 240);
    private static final Color HIERBA_OSC   = Color.rgb(35,  95,  35);
    private static final Color HIERBA_CLAR  = Color.rgb(50, 115,  50);
    private static final Color META_BLANCA  = Color.WHITE;
    private static final Color META_NEGRA   = Color.rgb(20,  20,  20);

    public CircuitoGenerico(int id, String nombre, String pais, int vueltas) {
        super(id, nombre, pais, vueltas);
    }

    public CircuitoGenerico() {
        super(99, "Circuito de Pruebas", "Genérico", 10);
    }

    // ════════════════════════════════════════════════════════════════════
    //  DIBUJO PRINCIPAL
    // ════════════════════════════════════════════════════════════════════
    @Override
    public void dibujar(GraphicsContext gc) {
        gc.save();
        dibujarFondo(gc);
        dibujarPitLaneCarril(gc);
        dibujarKerbs(gc);
        dibujarAsfalto(gc);
        dibujarAsfaltoDetalle(gc);
        dibujarPitLaneDetalle(gc);
        dibujarLineaMeta(gc);
        dibujarInfoCircuito(gc);
        gc.restore();
    }

    private void dibujarFondo(GraphicsContext gc) {
        int tile = 40;
        for (int col = 0; col * tile < 1080; col++) {
            for (int row = 0; row * tile < 700; row++) {
                gc.setFill((col + row) % 2 == 0 ? HIERBA_OSC : HIERBA_CLAR);
                gc.fillRect(col * tile, row * tile, tile, tile);
            }
        }
    }

    private void dibujarKerbs(GraphicsContext gc) {
        gc.setLineWidth(30);
        gc.setLineDashes(14, 14);
        gc.setStroke(KERB_BLANCO); gc.setLineDashOffset(0);  trazarCircuito(gc);
        gc.setStroke(KERB_ROJO);   gc.setLineDashOffset(14); trazarCircuito(gc);
        gc.setLineDashes(0); gc.setLineDashOffset(0);
    }

    private void dibujarAsfalto(GraphicsContext gc) {
        gc.setLineWidth(22); gc.setStroke(ASFALTO);     trazarCircuito(gc);
        gc.setLineWidth(14); gc.setStroke(ASFALTO_MID); trazarCircuito(gc);
        gc.setLineWidth(6);  gc.setStroke(ASFALTO);     trazarCircuito(gc);
    }

    private void dibujarAsfaltoDetalle(GraphicsContext gc) {
        gc.setLineWidth(2.5);
        gc.setStroke(Color.rgb(255, 255, 255, 0.4));
        gc.setLineDashes(10, 15); trazarCircuito(gc); gc.setLineDashes(0);
    }

    private void dibujarLineaMeta(GraphicsContext gc) {
        // Situada en la recta principal inferior (X: 500 a 525, Y: 550)
        double metaY = 539, trackX1 = 500, trackX2 = 528;
        double cellW = 3.5, cellH = 3.5;
        int filas = 5;

        for (int fila = 0; fila < filas; fila++) {
            int cols = (int) Math.ceil((trackX2 - trackX1) / cellW);
            for (int col = 0; col < cols; col++) {
                gc.setFill((col + fila) % 2 == 0 ? META_BLANCA : META_NEGRA);
                double cx = trackX1 + col * cellW;
                gc.fillRect(cx, metaY + fila * cellH, Math.min(cellW, trackX2 - cx), cellH);
            }
        }
    }

    private void dibujarInfoCircuito(GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 0, 0, 0.65));
        gc.fillRoundRect(60, 38, 160, 62, 8, 8);

        // Bandera genérica de cuadros (Racing Flag) en el panel
        double fx = 68, fy = 44, fw = 20, fh = 12;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 5; c++) {
                gc.setFill((r + c) % 2 == 0 ? Color.WHITE : Color.BLACK);
                gc.fillRect(fx + c * 4, fy + r * 4, 4, 4);
            }
        }

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("TEST TRACK", 64, 71);
        gc.fillText("GENERICO", 64, 83);

        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 8));
        gc.setFill(Color.rgb(255, 210, 50));
        gc.fillText("10 vueltas · 4.250 km", 64, 94);
    }

    // ── Trazado Geométrico de la Pista ───────────────────────────────────
    private void trazarCircuito(GraphicsContext gc) {
        gc.beginPath();
        // Recta principal inferior — Flujo de izquierda a derecha
        gc.moveTo(250, 550);
        gc.lineTo(750, 550);

        // Curva amplia derecha (subiendo por el este)
        gc.bezierCurveTo(870, 550, 870, 150, 750, 150);

        // Recta superior de regreso (hacia la izquierda)
        gc.lineTo(600, 150);

        // Sección Técnica: Chicane central en S (Bézier)
        gc.bezierCurveTo(530, 150, 520, 280, 450, 280);
        gc.bezierCurveTo(380, 280, 370, 150, 300, 150);

        // Cierre de la recta superior izquierda
        gc.lineTo(250, 150);

        // Curva amplia izquierda para volver a la recta de meta (bajando)
        gc.bezierCurveTo(130, 150, 130, 550, 250, 550);

        gc.stroke();
    }

    // ── Pit Lane (carril) ────────────────────────────────────────────────
    private void dibujarPitLaneCarril(GraphicsContext gc) {
        // Ubicado de forma segura en paralelo debajo de la recta de meta (Y: 550 -> Pit en Y: 580)
        gc.setLineWidth(10);
        gc.setStroke(Color.rgb(65, 65, 72));
        gc.beginPath();
        gc.moveTo(300, 580);
        gc.lineTo(700, 580);
        gc.stroke();

        // Muro de contención de boxes
        gc.setLineWidth(2.0);
        gc.setStroke(Color.rgb(210, 210, 215));
        gc.beginPath();
        gc.moveTo(310, 566);
        gc.lineTo(690, 566);
        gc.stroke();
    }

    private void dibujarPitLaneDetalle(GraphicsContext gc) {
        // Línea discontinua reglamentaria de boxes
        gc.setLineWidth(1.0);
        gc.setStroke(Color.rgb(255, 255, 255, 0.7));
        gc.setLineDashes(4, 4);
        gc.beginPath();
        gc.moveTo(310, 579);
        gc.lineTo(690, 579);
        gc.stroke();
        gc.setLineDashes(0);

        // Cajones de Garajes (Abajo del todo, Y: 592)
        gc.setFill(Color.rgb(75, 75, 85));
        gc.setStroke(Color.rgb(105, 105, 115));
        gc.setLineWidth(0.6);
        for (int i = 0; i < 15; i++) {
            double bx = 325 + i * 23;
            gc.fillRect(bx, 590, 16, 12);
            gc.strokeRect(bx, 590, 16, 12);
        }

        // Señalizaciones
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 7));
        gc.setFill(Color.rgb(255, 200, 0));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("▶ PIT IN", 270, 565);
        gc.fillText("PIT OUT ▶", 710, 565);
    }

    // ── API Pública del Pit Lane ─────────────────────────────────────────
    public Punto getPuntoEntradaPit() { return new Punto(300, 550); }
    public Punto getPuntoSalidaPit()  { return new Punto(700, 550); }

    public Punto calcularPosicionPitLane(double t) {
        if (t <= 0.10) {
            return bezier(t / 0.10, p(300, 550), p(302, 555), p(298, 580), p(300, 580));
        } else if (t <= 0.90) {
            return interp((t - 0.10) / 0.80, p(300, 580), p(700, 580));
        } else {
            return bezier((t - 0.90) / 0.10, p(700, 580), p(702, 580), p(698, 555), p(700, 550));
        }
    }

    public boolean estaEnVentanaPit(double t) {
        // El auto está terminando la curva de reincorporación (justo antes de boxes)
        return t >= 0.94 || t <= 0.03;
    }

    // ── Simulación matemática de posiciones (Segmentación temporal) ──────
    @Override
    public Punto calcularPosicion(double t) {
        // 1. Recta de meta principal
        if      (t < 0.20) return interp(t / 0.20,           p(250,550), p(750,550));
            // 2. Gran curva Este (derecha)
        else if (t < 0.40) return bezier((t - 0.20) / 0.20,  p(750,550), p(870,550), p(870,150), p(750,150));
            // 3. Recta superior previo a chicane
        else if (t < 0.47) return interp((t - 0.40) / 0.07,  p(750,150), p(600,150));
            // 4. Chicane entrada (S)
        else if (t < 0.57) return bezier((t - 0.47) / 0.10,  p(600,150), p(530,150), p(520,280), p(450,280));
            // 5. Chicane salida (S)
        else if (t < 0.67) return bezier((t - 0.57) / 0.10,  p(450,280), p(380,280), p(370,150), p(300,150));
            // 6. Recta superior post chicane
        else if (t < 0.74) return interp((t - 0.67) / 0.07,  p(300,150), p(250,150));
            // 7. Gran curva Oeste (izquierda) para cerrar vuelta
        else               return bezier((t - 0.74) / 0.26,  p(250,150), p(130,150), p(130,550), p(250,550));
    }

    // ── Helpers ──────────────────────────────────────────────────────────
    private Punto p(double x, double y) { return new Punto(x, y); }
    private Punto interp(double t, Punto a, Punto b) {
        return new Punto(a.x + t * (b.x - a.x), a.y + t * (b.y - a.y));
    }
    private Punto bezier(double t, Punto p0, Punto p1, Punto p2, Punto p3) {
        double u = 1 - t, tt = t * t, uu = u * u;
        return new Punto(uu * u * p0.x + 3 * uu * t * p1.x + 3 * u * tt * p2.x + tt * t * p3.x,
                uu * u * p0.y + 3 * uu * t * p1.y + 3 * u * tt * p2.y + tt * t * p3.y);
    }
}