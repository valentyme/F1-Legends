package com.f1legends.modelo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class CircuitoMonza extends Circuito {

    // ── Paleta ───────────────────────────────────────────────────────────
    private static final Color ASFALTO      = Color.rgb(42,  42,  46);
    private static final Color ASFALTO_MID  = Color.rgb(56,  56,  62);
    private static final Color KERB_ROJO    = Color.rgb(195, 28,  28);
    private static final Color KERB_BLANCO  = Color.rgb(235, 235, 235);
    private static final Color HIERBA_OSC   = Color.rgb(30,  88,  30);
    private static final Color HIERBA_CLAR  = Color.rgb(44, 108,  44);
    private static final Color META_BLANCA  = Color.WHITE;
    private static final Color META_NEGRA   = Color.rgb(15,  15,  15);

    public CircuitoMonza(int id, String nombre, String pais, int vueltas) {
        super(id, nombre, pais, vueltas);
    }

    public CircuitoMonza() {
        super(1, "Monza", "Italia", 53);
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

    // ── 1. Fondo: pasto con tablero de ajedrez suave ─────────────────────
    //   Alternamos dos verdes oscuros en franjas diagonales de 40px
    private void dibujarFondo(GraphicsContext gc) {
        int tileSize = 40;
        for (int col = 0; col * tileSize < 1080; col++) {
            for (int row = 0; row * tileSize < 620; row++) {
                boolean par = (col + row) % 2 == 0;
                gc.setFill(par ? HIERBA_OSC : HIERBA_CLAR);
                gc.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
            }
        }
    }

    // ── 2. Pit Lane (carril) ─────────────────────────────────────────────
    private void dibujarPitLaneCarril(GraphicsContext gc) {
        gc.setLineWidth(13);
        gc.setStroke(Color.rgb(70, 70, 78));
        trazarPitLane(gc);

        // muro exterior
        gc.setLineWidth(1.5);
        gc.setStroke(Color.rgb(215, 215, 215));
        gc.beginPath();
        gc.moveTo(695, 510);
        gc.bezierCurveTo(702, 510, 704, 527, 704, 527);
        gc.lineTo(526, 527);
        gc.bezierCurveTo(520, 527, 519, 510, 524, 510);
        gc.stroke();
    }

    private void trazarPitLane(GraphicsContext gc) {
        gc.beginPath();
        gc.moveTo(695, 504);
        gc.bezierCurveTo(702, 504, 703, 525, 703, 525);
        gc.lineTo(523, 525);
        gc.bezierCurveTo(519, 525, 519, 504, 524, 504);
        gc.stroke();
    }

    // ── 3. Kerbs ──────────────────────────────────────────────────────────
    private void dibujarKerbs(GraphicsContext gc) {
        gc.setLineWidth(32);
        gc.setLineDashes(16, 16);

        gc.setStroke(KERB_BLANCO);
        gc.setLineDashOffset(0);
        trazarCircuito(gc);

        gc.setStroke(KERB_ROJO);
        gc.setLineDashOffset(16);
        trazarCircuito(gc);

        gc.setLineDashes(0);
        gc.setLineDashOffset(0);
    }

    // ── 4. Asfalto ────────────────────────────────────────────────────────
    private void dibujarAsfalto(GraphicsContext gc) {
        gc.setLineWidth(24);
        gc.setStroke(ASFALTO);
        trazarCircuito(gc);

        gc.setLineWidth(16);
        gc.setStroke(ASFALTO_MID);
        trazarCircuito(gc);

        gc.setLineWidth(8);
        gc.setStroke(ASFALTO);
        trazarCircuito(gc);
    }

    // ── 5. Detalle asfalto ────────────────────────────────────────────────
    private void dibujarAsfaltoDetalle(GraphicsContext gc) {
        // rubber line
        gc.setLineWidth(3.5);
        gc.setStroke(Color.rgb(22, 22, 25, 0.50));
        gc.setLineDashes(26, 70);
        trazarCircuito(gc);
        gc.setLineDashes(0);

        // línea central
        gc.setLineWidth(1.0);
        gc.setStroke(Color.rgb(255, 255, 255, 0.48));
        gc.setLineDashes(9, 13);
        trazarCircuito(gc);
        gc.setLineDashes(0);
    }

    // ── 6. Detalle pit lane ───────────────────────────────────────────────
    private void dibujarPitLaneDetalle(GraphicsContext gc) {
        // línea interior discontinua
        gc.setLineWidth(1.2);
        gc.setStroke(Color.rgb(230, 230, 230, 0.85));
        gc.setLineDashes(6, 6);
        gc.beginPath();
        gc.moveTo(694, 510);
        gc.bezierCurveTo(699, 510, 700, 518, 700, 518);
        gc.lineTo(522, 518);
        gc.bezierCurveTo(520, 518, 520, 510, 523, 510);
        gc.stroke();
        gc.setLineDashes(0);

        // boxes
        gc.setFill(Color.rgb(72, 72, 80));
        gc.setStroke(Color.rgb(105, 105, 112));
        gc.setLineWidth(0.6);
        for (int i = 0; i < 14; i++) {
            double bx = 528 + i * 11.8;
            gc.fillRect(bx, 528, 10.5, 9);
            gc.strokeRect(bx, 528, 10.5, 9);
        }

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 7));
        gc.setFill(Color.rgb(255, 200, 0, 0.9));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("▼ PIT IN",  698, 516);
        gc.fillText("PIT OUT ▲", 510, 503);

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 6));
        gc.setFill(Color.rgb(255, 210, 60, 0.85));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("PIT LANE", 612, 537);
    }

    // ── 7. Línea de meta ─────────────────────────────────────────────────
    private void dibujarLineaMeta(GraphicsContext gc) {
        double metaX  = 636;
        double trackY1 = 488;
        double trackY2 = 513;
        double cellW  = 3.5, cellH = 3.5;
        int cols = 5;

        for (int col = 0; col < cols; col++) {
            int filas = (int) Math.ceil((trackY2 - trackY1) / cellH);
            for (int row = 0; row < filas; row++) {
                boolean blanco = (col + row) % 2 == 0;
                gc.setFill(blanco ? META_BLANCA : META_NEGRA);
                double cy = trackY1 + row * cellH;
                double ch = Math.min(cellH, trackY2 - cy);
                gc.fillRect(metaX + col * cellW, cy, cellW, ch);
            }
        }

        // mástil
        gc.setStroke(Color.rgb(230, 230, 230));
        gc.setLineWidth(1.5);
        double mastX = metaX + cols * cellW / 2.0;
        gc.strokeLine(mastX, trackY1 - 22, mastX, trackY1);

        // banderita
        double fw = 3.0, fh = 2.5;
        for (int fc = 0; fc < 5; fc++) {
            for (int fr = 0; fr < 3; fr++) {
                gc.setFill((fc + fr) % 2 == 0 ? META_BLANCA : META_NEGRA);
                gc.fillRect(mastX + 1 + fc * fw, trackY1 - 22 + fr * fh, fw, fh);
            }
        }
    }

    // ── 8. Panel info ─────────────────────────────────────────────────────
    private void dibujarInfoCircuito(GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 0, 0, 0.60));
        gc.fillRoundRect(60, 38, 152, 62, 8, 8);

        double fx = 64, fy = 42, fw2 = 14, fh2 = 10;
        gc.setFill(Color.rgb(0, 140, 69));   gc.fillRect(fx,         fy, fw2, fh2);
        gc.setFill(Color.WHITE);              gc.fillRect(fx + fw2,   fy, fw2, fh2);
        gc.setFill(Color.rgb(205, 33, 42));   gc.fillRect(fx + fw2*2, fy, fw2, fh2);

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("AUTODROMO NAZIONALE", 64, 65);
        gc.fillText("MONZA — ITALIA", 64, 77);

        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 8));
        gc.setFill(Color.rgb(255, 210, 50));
        gc.fillText("53 vueltas · 5.793 km", 64, 90);
    }

    // ════════════════════════════════════════════════════════════════════
    //  TRAZADO
    // ════════════════════════════════════════════════════════════════════
    private void trazarCircuito(GraphicsContext gc) {
        gc.beginPath();
        gc.moveTo(634, 500);
        gc.lineTo(410, 500);
        gc.bezierCurveTo(392, 500, 386, 484, 370, 484);
        gc.bezierCurveTo(354, 484, 348, 500, 328, 500);
        gc.lineTo(258, 500);
        gc.bezierCurveTo(136, 500, 74,  436, 106, 314);
        gc.bezierCurveTo(118, 262, 96,  252, 112, 232);
        gc.bezierCurveTo(128, 212, 116, 192, 128, 142);
        gc.bezierCurveTo(140, 80,  202, 50,  242, 102);
        gc.bezierCurveTo(264, 124, 272, 124, 292, 154);
        gc.lineTo(470, 372);
        gc.bezierCurveTo(492, 398, 514, 398, 534, 382);
        gc.bezierCurveTo(560, 362, 582, 382, 604, 392);
        gc.lineTo(762, 392);
        gc.bezierCurveTo(928, 392, 928, 508, 762, 508);
        gc.lineTo(634, 508);
        gc.stroke();
    }

    // ════════════════════════════════════════════════════════════════════
    //  PIT LANE — API pública para la lógica de carrera
    // ════════════════════════════════════════════════════════════════════

    public Punto getPuntoEntradaPit() { return new Punto(695, 504); }
    public Punto getPuntoSalidaPit()  { return new Punto(522, 504); }

    /** t ∈ [0,1]: recorre el pit lane de entrada a salida */
    public Punto calcularPosicionPitLane(double t) {
        if (t <= 0.5) {
            return calcularBezier(t / 0.5,
                    new Punto(695, 504), new Punto(695, 524),
                    new Punto(650, 524), new Punto(612, 524));
        } else {
            return calcularBezier((t - 0.5) / 0.5,
                    new Punto(612, 524), new Punto(570, 524),
                    new Punto(522, 524), new Punto(522, 504));
        }
    }

    /** Verdadero cuando el auto puede decidir entrar al pit */
    public boolean estaEnVentanaPit(double t) {
        return t >= 0.93 && t <= 0.99;
    }

    // ════════════════════════════════════════════════════════════════════
    //  VELOCIDAD CON VARIACIÓN ALEATORIA
    //
    //  La velocidad base de cada auto se inicializa en el constructor
    //  del auto con un factor aleatorio:
    //
    //    double factorVelocidad = 0.85 + Math.random() * 0.30;
    //    // rango: 0.85 (lento) .. 1.15 (rápido)
    //
    //  Luego, en cada tick de la simulación se aplica una micro-variación:
    //
    //    double variacion = 1.0 + (Math.random() - 0.5) * 0.04;
    //    double velocidadActual = velocidadBase * factorVelocidad * variacion;
    //
    //  El método de abajo devuelve el incremento de t para un auto dado
    //  su velocidadBase y su factorVelocidad personal.
    // ════════════════════════════════════════════════════════════════════

    /**
     * Calcula el incremento de t (posición en pista) para un frame.
     *
     * @param velocidadBase    velocidad nominal del auto (ej: 0.0018)
     * @param factorVelocidad  factor personal del piloto (0.85..1.15)
     * @return incremento de t a sumar en cada frame
     */
    public double calcularIncrementoT(double velocidadBase, double factorVelocidad) {
        double microVariacion = 1.0 + (Math.random() - 0.5) * 0.04;
        return velocidadBase * factorVelocidad * microVariacion;
    }

    /**
     * Genera un factor de velocidad aleatorio para inicializar un auto.
     * Llamar una sola vez por auto al crearlo.
     * Rango: 0.85 (más lento) a 1.15 (más rápido).
     */
    public static double generarFactorVelocidad() {
        return 0.85 + Math.random() * 0.30;
    }

    // ════════════════════════════════════════════════════════════════════
    //  POSICIÓN EN PISTA
    // ════════════════════════════════════════════════════════════════════
    @Override
    public Punto calcularPosicion(double t) {
        if      (t < 0.10) return interpolarLinea( t          / 0.10, new Punto(634, 500), new Punto(410, 500));
        else if (t < 0.14) return calcularBezier((t - 0.10) / 0.04,  new Punto(410, 500), new Punto(392, 500), new Punto(386, 484), new Punto(370, 484));
        else if (t < 0.18) return calcularBezier((t - 0.14) / 0.04,  new Punto(370, 484), new Punto(354, 484), new Punto(348, 500), new Punto(328, 500));
        else if (t < 0.22) return interpolarLinea((t - 0.18) / 0.04, new Punto(328, 500), new Punto(258, 500));
        else if (t < 0.38) return calcularBezier((t - 0.22) / 0.16,  new Punto(258, 500), new Punto(136, 500), new Punto(74,  436), new Punto(106, 314));
        else if (t < 0.44) return calcularBezier((t - 0.38) / 0.06,  new Punto(106, 314), new Punto(118, 262), new Punto(96,  252), new Punto(112, 232));
        else if (t < 0.50) return calcularBezier((t - 0.44) / 0.06,  new Punto(112, 232), new Punto(128, 212), new Punto(116, 192), new Punto(128, 142));
        else if (t < 0.58) return calcularBezier((t - 0.50) / 0.08,  new Punto(128, 142), new Punto(140, 80),  new Punto(202, 50),  new Punto(242, 102));
        else if (t < 0.64) return calcularBezier((t - 0.58) / 0.06,  new Punto(242, 102), new Punto(264, 124), new Punto(272, 124), new Punto(292, 154));
        else if (t < 0.76) return interpolarLinea((t - 0.64) / 0.12, new Punto(292, 154), new Punto(470, 372));
        else if (t < 0.81) return calcularBezier((t - 0.76) / 0.05,  new Punto(470, 372), new Punto(492, 398), new Punto(514, 398), new Punto(534, 382));
        else if (t < 0.86) return calcularBezier((t - 0.81) / 0.05,  new Punto(534, 382), new Punto(560, 362), new Punto(582, 382), new Punto(604, 392));
        else if (t < 0.91) return interpolarLinea((t - 0.86) / 0.05, new Punto(604, 392), new Punto(762, 392));
        else               return calcularBezier((t - 0.91) / 0.09,  new Punto(762, 392), new Punto(928, 392), new Punto(928, 508), new Punto(762, 508));
    }

    private Punto interpolarLinea(double t, Punto p0, Punto p1) {
        return new Punto(p0.x + t * (p1.x - p0.x), p0.y + t * (p1.y - p0.y));
    }

    private Punto calcularBezier(double t, Punto p0, Punto p1, Punto p2, Punto p3) {
        double u = 1 - t, tt = t * t, uu = u * u;
        double x = uu * u * p0.x + 3 * uu * t * p1.x + 3 * u * tt * p2.x + tt * t * p3.x;
        double y = uu * u * p0.y + 3 * uu * t * p1.y + 3 * u * tt * p2.y + tt * t * p3.y;
        return new Punto(x, y);
    }
}