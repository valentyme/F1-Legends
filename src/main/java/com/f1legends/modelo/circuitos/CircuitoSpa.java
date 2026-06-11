package com.f1legends.modelo.circuitos;

import com.f1legends.modelo.Punto;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class CircuitoSpa extends Circuito {

    // ── Paleta ───────────────────────────────────────────────────────────
    private static final Color ASFALTO      = Color.rgb(42,  42,  46);
    private static final Color ASFALTO_MID  = Color.rgb(56,  56,  62);
    private static final Color KERB_ROJO    = Color.rgb(195, 28,  28);
    private static final Color KERB_BLANCO  = Color.rgb(235, 235, 235);
    private static final Color HIERBA_OSC   = Color.rgb(22,  78,  22);   // verde belga más oscuro
    private static final Color HIERBA_CLAR  = Color.rgb(34,  96,  34);
    private static final Color META_BLANCA  = Color.WHITE;
    private static final Color META_NEGRA   = Color.rgb(15,  15,  15);

    public CircuitoSpa(int id, String nombre, String pais, int vueltas) {
        super(id, nombre, pais, vueltas);
    }

    public CircuitoSpa() {
        super(2, "Spa-Francorchamps", "Bélgica", 44);
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
    //  El pit lane de Spa corre en paralelo a la recta de meta (zona inferior izquierda)
    private void dibujarPitLaneCarril(GraphicsContext gc) {
        gc.setLineWidth(13);
        gc.setStroke(Color.rgb(70, 70, 78));
        trazarPitLane(gc);

        // muro exterior
        gc.setLineWidth(1.5);
        gc.setStroke(Color.rgb(215, 215, 215));
        gc.beginPath();
        gc.moveTo(182, 430);
        gc.bezierCurveTo(182, 437, 196, 440, 196, 440);
        gc.lineTo(370, 440);
        gc.bezierCurveTo(378, 440, 380, 430, 375, 430);
        gc.stroke();
    }

    private void trazarPitLane(GraphicsContext gc) {

        gc.beginPath();

        gc.moveTo(470,540);

        gc.lineTo(440,565);
        gc.lineTo(250,565);

        gc.bezierCurveTo(
                220,565,
                220,560,
                220,550);

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
        gc.setLineWidth(3.5);
        gc.setStroke(Color.rgb(22, 22, 25, 0.50));
        gc.setLineDashes(26, 70);
        trazarCircuito(gc);
        gc.setLineDashes(0);

        gc.setLineWidth(1.0);
        gc.setStroke(Color.rgb(255, 255, 255, 0.48));
        gc.setLineDashes(9, 13);
        trazarCircuito(gc);
        gc.setLineDashes(0);
    }

    // ── 6. Detalle pit lane ───────────────────────────────────────────────
    private void dibujarPitLaneDetalle(GraphicsContext gc) {

        // Línea interior pit
        gc.setLineWidth(1.2);
        gc.setStroke(Color.rgb(230, 230, 230, 0.85));
        gc.setLineDashes(6, 6);

        gc.beginPath();
        gc.moveTo(450, 565);
        gc.lineTo(250, 565);
        gc.stroke();

        gc.setLineDashes(0);

        // Boxes
        gc.setFill(Color.rgb(72, 72, 80));
        gc.setStroke(Color.rgb(105, 105, 112));
        gc.setLineWidth(0.6);

        for (int i = 0; i < 14; i++) {

            double bx = 270 + i * 12;

            gc.fillRect(bx, 575, 10, 9);
            gc.strokeRect(bx, 575, 10, 9);
        }

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 7));
        gc.setFill(Color.rgb(255, 200, 0, 0.9));
        gc.setTextAlign(TextAlignment.LEFT);

        gc.fillText("▶ PIT IN", 455, 558);
        gc.fillText("PIT OUT ◀", 205, 558);

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 6));
        gc.setFill(Color.rgb(255, 210, 60, 0.85));
        gc.setTextAlign(TextAlignment.CENTER);

        gc.fillText("PIT LANE", 350, 587);
    }
    // ── 7. Línea de meta ─────────────────────────────────────────────────
    //  La línea de meta de Spa está al inicio de la recta de Kemmel (parte inferior izquierda)
    private void dibujarLineaMeta(GraphicsContext gc) {
        double metaX = 295;
        double trackY1 = 528;
        double trackY2 = 552;
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
        gc.fillRoundRect(60, 38, 168, 62, 8, 8);

        // Bandera de Bélgica (negro, amarillo, rojo — franjas verticales)
        double fx = 64, fy = 42, fw2 = 14, fh2 = 10;
        gc.setFill(Color.rgb(0, 0, 0));        gc.fillRect(fx,         fy, fw2, fh2);
        gc.setFill(Color.rgb(255, 205, 0));    gc.fillRect(fx + fw2,   fy, fw2, fh2);
        gc.setFill(Color.rgb(205, 33, 42));    gc.fillRect(fx + fw2*2, fy, fw2, fh2);

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("CIRCUIT DE SPA-", 64, 65);
        gc.fillText("FRANCORCHAMPS — BÉLGICA", 64, 77);

        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 8));
        gc.setFill(Color.rgb(255, 210, 50));
        gc.fillText("44 vueltas · 7.004 km", 64, 90);
    }

    private void trazarCircuito(GraphicsContext gc) {

        gc.beginPath();

        // META
        gc.moveTo(300, 540);

        // Recta principal
        gc.lineTo(220, 540);

        // La Source
        gc.bezierCurveTo(
                140, 540,
                140, 470,
                210, 450);

        // Eau Rouge
        gc.bezierCurveTo(
                250, 430,
                280, 360,
                320, 280);

        // Raidillon
        gc.bezierCurveTo(
                340, 230,
                360, 180,
                390, 140);

        // Recta Kemmel
        gc.lineTo(860, 90);

        // Les Combes
        gc.bezierCurveTo(
                930, 95,
                950, 150,
                900, 200);

        // Malmedy
        gc.bezierCurveTo(
                860, 240,
                820, 270,
                780, 300);

        // Rivage
        gc.bezierCurveTo(
                740, 340,
                730, 390,
                690, 420);

        // Pouhon
        gc.bezierCurveTo(
                620, 470,
                540, 470,
                470, 430);

        // Fagnes
        gc.bezierCurveTo(
                430, 400,
                430, 340,
                500, 300);

        // Stavelot
        gc.bezierCurveTo(
                580, 260,
                700, 260,
                800, 320);

        // Blanchimont
        gc.bezierCurveTo(
                920, 400,
                900, 520,
                700, 550);

        // Llegada a Bus Stop
        gc.bezierCurveTo(
                620, 560,
                540, 560,
                460, 550);

        // Bus Stop
        gc.bezierCurveTo(
                400, 545,
                350, 560,
                320, 530);

        // Recta final
        gc.bezierCurveTo(
                300, 515,
                300, 525,
                300, 540);

        gc.stroke();
    }
    // ════════════════════════════════════════════════════════════════════
    //  PIT LANE — API pública para la lógica de carrera
    // ════════════════════════════════════════════════════════════════════

    public Punto getPuntoEntradaPit() {
        return new Punto(470,540);
    }

    public Punto getPuntoSalidaPit() {
        return new Punto(220,550);
    }
    /** t ∈ [0,1]: recorre el pit lane de entrada a salida */
    public Punto calcularPosicionPitLane(double t) {
        // Pit lane recto de derecha a izquierda
        double x = 374 - t * (374 - 184);
        double y = 424;
        return new Punto(x, y);
    }

    /** Verdadero cuando el auto puede decidir entrar al pit */
    public boolean estaEnVentanaPit(double t) {
        return t >= 0.93 && t <= 0.99;
    }

    // ════════════════════════════════════════════════════════════════════
    //  VELOCIDAD CON VARIACIÓN ALEATORIA
    // ════════════════════════════════════════════════════════════════════

    /**
     * Calcula el incremento de t (posición en pista) para un frame.
     *
     * @param velocidadBase    velocidad nominal del auto (ej: 0.0016)
     * @param factorVelocidad  factor personal del piloto (0.85..1.15)
     * @return incremento de t a sumar en cada frame
     */
    public double calcularIncrementoT(double velocidadBase, double factorVelocidad) {
        double microVariacion = 1.0 + (Math.random() - 0.5) * 0.04;
        return velocidadBase * factorVelocidad * microVariacion;
    }

    /**
     * Genera un factor de velocidad aleatorio para inicializar un auto.
     * Rango: 0.85 (más lento) a 1.15 (más rápido).
     */
    public static double generarFactorVelocidad() {
        return 0.85 + Math.random() * 0.30;
    }

    // ════════════════════════════════════════════════════════════════════
    //  POSICIÓN EN PISTA
    //
    //  El trazado se divide en 20 segmentos que siguen el orden
    //  de los puntos en trazarCircuito().
    // ════════════════════════════════════════════════════════════════════
    @Override
    public Punto calcularPosicion(double t) {

        if(t < 0.08)
            return interpolarLinea(
                    t/0.08,
                    new Punto(300,540),
                    new Punto(220,540));

        else if(t < 0.16)
            return calcularBezier(
                    (t-0.08)/0.08,
                    new Punto(220,540),
                    new Punto(140,540),
                    new Punto(140,470),
                    new Punto(210,450));

        else if(t < 0.24)
            return calcularBezier(
                    (t-0.16)/0.08,
                    new Punto(210,450),
                    new Punto(250,430),
                    new Punto(280,360),
                    new Punto(320,280));

        else if(t < 0.30)
            return calcularBezier(
                    (t-0.24)/0.06,
                    new Punto(320,280),
                    new Punto(340,230),
                    new Punto(360,180),
                    new Punto(390,140));

        else if(t < 0.44)
            return interpolarLinea(
                    (t-0.30)/0.14,
                    new Punto(390,140),
                    new Punto(860,90));

        else if(t < 0.52)
            return calcularBezier(
                    (t-0.44)/0.08,
                    new Punto(860,90),
                    new Punto(930,95),
                    new Punto(950,150),
                    new Punto(780,300));

        else if(t < 0.62)
            return calcularBezier(
                    (t-0.52)/0.10,
                    new Punto(780,300),
                    new Punto(740,340),
                    new Punto(730,390),
                    new Punto(690,420));

        else if(t < 0.72)
            return calcularBezier(
                    (t-0.62)/0.10,
                    new Punto(690,420),
                    new Punto(620,470),
                    new Punto(540,470),
                    new Punto(470,430));

        else if(t < 0.80)
            return calcularBezier(
                    (t-0.72)/0.08,
                    new Punto(470,430),
                    new Punto(430,400),
                    new Punto(430,340),
                    new Punto(500,300));

        else if(t < 0.88)
            return calcularBezier(
                    (t-0.80)/0.08,
                    new Punto(500,300),
                    new Punto(580,260),
                    new Punto(700,260),
                    new Punto(800,320));

        else if(t < 0.95)
            return calcularBezier(
                    (t-0.88)/0.07,
                    new Punto(800,320),
                    new Punto(920,400),
                    new Punto(900,520),
                    new Punto(700,550));

        return calcularBezier(
                (t-0.95)/0.05,
                new Punto(700,550),
                new Punto(500,560),
                new Punto(350,560),
                new Punto(300,540));
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