package com.f1legends.modelo.circuitos;

import com.f1legends.modelo.Punto;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class CircuitoSilverstone extends Circuito {

    // ── Paleta ───────────────────────────────────────────────────────────
    private static final Color ASFALTO      = Color.rgb(42,  42,  46);
    private static final Color ASFALTO_MID  = Color.rgb(56,  56,  62);
    private static final Color KERB_ROJO    = Color.rgb(195, 28,  28);
    private static final Color KERB_BLANCO  = Color.rgb(235, 235, 235);
    private static final Color HIERBA_OSC   = Color.rgb(30,  88,  30);
    private static final Color HIERBA_CLAR  = Color.rgb(44, 108,  44);
    private static final Color META_BLANCA  = Color.WHITE;
    private static final Color META_NEGRA   = Color.rgb(15,  15,  15);

    public CircuitoSilverstone(int id, String nombre, String pais, int vueltas) {
        super(id, nombre, pais, vueltas);
    }

    public CircuitoSilverstone() {
        super(2, "Silverstone", "Reino Unido", 52);
    }

    // ════════════════════════════════════════════════════════════════════
    //  DIBUJO PRINCIPAL
    // ════════════════════════════════════════════════════════════════════
    @Override
    public void dibujar(GraphicsContext gc) {
        gc.save();
        dibujarFondo(gc);
        dibujarPitLaneCarril(gc);   // ← NUEVO (antes de kerbs para que quede debajo)
        dibujarKerbs(gc);
        dibujarAsfalto(gc);
        dibujarAsfaltoDetalle(gc);
        dibujarPitLaneDetalle(gc);  // ← NUEVO (encima del asfalto)
        dibujarLineaMeta(gc);
        dibujarInfoCircuito(gc);
        gc.restore();
    }

    // ── 1. Fondo ─────────────────────────────────────────────────────────
    private void dibujarFondo(GraphicsContext gc) {
        int tileSize = 40;
        for (int col = 0; col * tileSize < 1080; col++) {
            for (int row = 0; row * tileSize < 700; row++) {
                boolean par = (col + row) % 2 == 0;
                gc.setFill(par ? HIERBA_OSC : HIERBA_CLAR);
                gc.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
            }
        }
    }

    // ── 2. Pit Lane (carril) ─────────────────────────────────────────────
    // El pit lane corre paralelo a la recta de meta (y=172), desplazado
    // hacia abajo (y=195). Va desde Wing Entrance (~x=540) hasta la salida
    // (~x=750), curvando suavemente en ambos extremos para conectar con la pista.
    private void dibujarPitLaneCarril(GraphicsContext gc) {
        gc.setLineWidth(13);
        gc.setStroke(Color.rgb(70, 70, 78));
        trazarPitLane(gc);

        // muro exterior (lado inferior del pit lane)
        gc.setLineWidth(1.5);
        gc.setStroke(Color.rgb(215, 215, 215));
        gc.beginPath();
        gc.moveTo(542, 207);
        gc.bezierCurveTo(542, 207, 539, 210, 539, 210);
        gc.lineTo(751, 210);
        gc.bezierCurveTo(754, 210, 754, 207, 751, 207);
        gc.stroke();
    }

    private void trazarPitLane(GraphicsContext gc) {
        // El pit lane va paralelo a la recta de meta (y=172) pero a y≈195
        // Entrada: desde x≈750 curva suave hacia abajo
        // Salida:  en x≈540 curva suave de regreso a la pista
        gc.beginPath();
        gc.moveTo(750, 172);
        gc.bezierCurveTo(758, 172, 762, 195, 750, 195);
        gc.lineTo(542, 195);
        gc.bezierCurveTo(530, 195, 534, 172, 542, 172);
        gc.stroke();
    }

    // ── 3. Kerbs ─────────────────────────────────────────────────────────
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

    // ── 4. Asfalto ───────────────────────────────────────────────────────
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

    // ── 5. Detalle asfalto ───────────────────────────────────────────────
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
        // línea interior discontinua
        gc.setLineWidth(1.2);
        gc.setStroke(Color.rgb(230, 230, 230, 0.85));
        gc.setLineDashes(6, 6);
        gc.beginPath();
        gc.moveTo(748, 188);
        gc.bezierCurveTo(753, 188, 754, 195, 750, 195);
        gc.lineTo(542, 195);
        gc.bezierCurveTo(538, 195, 539, 188, 544, 188);
        gc.stroke();
        gc.setLineDashes(0);

        // boxes (a lo largo del pit lane, debajo de la recta)
        gc.setFill(Color.rgb(72, 72, 80));
        gc.setStroke(Color.rgb(105, 105, 112));
        gc.setLineWidth(0.6);
        for (int i = 0; i < 14; i++) {
            double bx = 547 + i * 12.5;
            gc.fillRect(bx, 197, 11, 9);
            gc.strokeRect(bx, 197, 11, 9);
        }

        // etiquetas PIT IN / PIT OUT
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 7));
        gc.setFill(Color.rgb(255, 200, 0, 0.9));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("▼ PIT IN",  733, 188);
        gc.fillText("PIT OUT ▲", 510, 183);

        // etiqueta central
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 6));
        gc.setFill(Color.rgb(255, 210, 60, 0.85));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("PIT LANE", 646, 206);
    }

    // ── 7. Línea de meta ─────────────────────────────────────────────────
    private void dibujarLineaMeta(GraphicsContext gc) {
        double metaY  = 175;
        double trackX1 = 578;
        double trackX2 = 606;
        double cellW  = 3.5, cellH = 3.5;
        int filas = 5;

        for (int fila = 0; fila < filas; fila++) {
            int cols = (int) Math.ceil((trackX2 - trackX1) / cellW);
            for (int col = 0; col < cols; col++) {
                boolean blanco = (col + fila) % 2 == 0;
                gc.setFill(blanco ? META_BLANCA : META_NEGRA);
                double cx = trackX1 + col * cellW;
                double cw = Math.min(cellW, trackX2 - cx);
                gc.fillRect(cx, metaY + fila * cellH, cw, cellH);
            }
        }

        // Mástil
        gc.setStroke(Color.rgb(230, 230, 230));
        gc.setLineWidth(1.5);
        double mastY = metaY + filas * cellH / 2.0;
        gc.strokeLine(trackX1 - 1, mastY - 22, trackX1 - 1, mastY);

        // Banderita
        double fw = 2.5, fh = 3.0;
        for (int fc = 0; fc < 3; fc++) {
            for (int fr = 0; fr < 5; fr++) {
                gc.setFill((fc + fr) % 2 == 0 ? META_BLANCA : META_NEGRA);
                gc.fillRect(trackX1 - 1 + fc * fw, mastY - 22 + fr * fh, fw, fh);
            }
        }
    }

    // ── 8. Panel info ─────────────────────────────────────────────────────
    private void dibujarInfoCircuito(GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 0, 0, 0.60));
        gc.fillRoundRect(60, 38, 170, 62, 8, 8);

        double fx = 64, fy = 42, fw = 21, fh = 11;
        gc.setFill(Color.rgb(1, 33, 105));
        gc.fillRect(fx, fy, fw, fh);

        gc.save();
        gc.beginPath();
        gc.rect(fx, fy, fw, fh);
        gc.clip();

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2.5);
        gc.strokeLine(fx, fy, fx + fw, fy + fh);
        gc.strokeLine(fx, fy + fh, fx + fw, fy);

        gc.setStroke(Color.rgb(200, 16, 46));
        gc.setLineWidth(1.0);
        gc.strokeLine(fx, fy, fx + fw, fy + fh);
        gc.strokeLine(fx, fy + fh, fx + fw, fy);
        gc.restore();

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3.5);
        gc.strokeLine(fx + fw / 2.0, fy, fx + fw / 2.0, fy + fh);
        gc.strokeLine(fx, fy + fh / 2.0, fx + fw, fy + fh / 2.0);

        gc.setStroke(Color.rgb(200, 16, 46));
        gc.setLineWidth(2.0);
        gc.strokeLine(fx + fw / 2.0, fy, fx + fw / 2.0, fy + fh);
        gc.strokeLine(fx, fy + fh / 2.0, fx + fw, fy + fh / 2.0);

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFill(Color.WHITE);
        gc.fillText("SILVERSTONE CIRCUIT", 64, 65);
        gc.fillText("GRAN BRETAÑA", 64, 77);

        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 8));
        gc.setFill(Color.rgb(255, 210, 50));
        gc.fillText("52 vueltas · 5.891 km", 64, 90);
    }

    // ════════════════════════════════════════════════════════════════════
    //  TRAZADO DEL CIRCUITO
    // ════════════════════════════════════════════════════════════════════
    private void trazarCircuito(GraphicsContext gc) {
        gc.beginPath();
        gc.moveTo(592, 172);
        gc.lineTo(760, 172);
        gc.bezierCurveTo(800, 172, 820, 200, 820, 230);
        gc.bezierCurveTo(820, 260, 790, 270, 770, 280);
        gc.bezierCurveTo(750, 290, 740, 310, 760, 330);
        gc.bezierCurveTo(775, 345, 800, 348, 810, 360);
        gc.lineTo(810, 460);
        gc.bezierCurveTo(810, 510, 780, 540, 740, 540);
        gc.bezierCurveTo(710, 540, 695, 525, 690, 510);
        gc.bezierCurveTo(685, 492, 695, 478, 710, 472);
        gc.bezierCurveTo(730, 465, 740, 450, 730, 435);
        gc.bezierCurveTo(720, 420, 680, 410, 640, 415);
        gc.bezierCurveTo(560, 420, 480, 450, 420, 470);
        gc.bezierCurveTo(370, 480, 330, 470, 315, 448);
        gc.bezierCurveTo(300, 425, 295, 400, 305, 380);
        gc.bezierCurveTo(318, 358, 340, 352, 350, 335);
        gc.bezierCurveTo(360, 318, 355, 300, 340, 285);
        gc.lineTo(270, 250);
        gc.bezierCurveTo(220, 250, 190, 270, 190, 310);
        gc.bezierCurveTo(190, 350, 220, 370, 260, 370);
        gc.bezierCurveTo(300, 370, 330, 350, 340, 320);
        gc.bezierCurveTo(355, 285, 390, 240, 440, 210);
        gc.bezierCurveTo(490, 180, 540, 168, 592, 172);
        gc.stroke();
    }

    // ════════════════════════════════════════════════════════════════════
    //  PIT LANE — API pública para la lógica de carrera
    // ════════════════════════════════════════════════════════════════════

    public Punto getPuntoEntradaPit() { return new Punto(542, 172); }  // entra cerca de meta
    public Punto getPuntoSalidaPit()  { return new Punto(750, 172); }  // sale antes de Copse

    /** t ∈ [0,1]: recorre el pit lane de entrada (izq) a salida (der) */
    public Punto calcularPosicionPitLane(double t) {
        if (t <= 0.3) {
            // Curva de entrada: baja de y=172 a y=195 desde x=542
            return calcularBezier(t / 0.3,
                    new Punto(542, 172), new Punto(530, 172),
                    new Punto(530, 195), new Punto(542, 195));
        } else if (t <= 0.7) {
            // Recta del pit lane de izquierda a derecha
            return interp((t - 0.3) / 0.4,
                    new Punto(542, 195), new Punto(750, 195));
        } else {
            // Curva de salida: sube de y=195 a y=172 en x=750
            return calcularBezier((t - 0.7) / 0.3,
                    new Punto(750, 195), new Punto(762, 195),
                    new Punto(762, 172), new Punto(750, 172));
        }
    }

    /** Ventana para entrar al pit: justo después de cruzar meta */
    public boolean estaEnVentanaPit(double t) {
        return t >= 0.01 && t <= 0.06;
    }


    // ════════════════════════════════════════════════════════════════════
    //  POSICIÓN EN PISTA
    // ════════════════════════════════════════════════════════════════════
    @Override
    public Punto calcularPosicion(double t) {
        if      (t < 0.08) return interp(t / 0.08,
                p(592,172), p(760,172));
        else if (t < 0.13) return bezier((t-0.08)/0.05,
                p(760,172), p(800,172), p(820,200), p(820,230));
        else if (t < 0.18) return bezier((t-0.13)/0.05,
                p(820,230), p(820,260), p(790,270), p(770,280));
        else if (t < 0.23) return bezier((t-0.18)/0.05,
                p(770,280), p(750,290), p(740,310), p(760,330));
        else if (t < 0.27) return bezier((t-0.23)/0.04,
                p(760,330), p(775,345), p(800,348), p(810,360));
        else if (t < 0.38) return interp((t-0.27)/0.11,
                p(810,360), p(810,460));
        else if (t < 0.44) return bezier((t-0.38)/0.06,
                p(810,460), p(810,510), p(780,540), p(740,540));
        else if (t < 0.48) return bezier((t-0.44)/0.04,
                p(740,540), p(710,540), p(695,525), p(690,510));
        else if (t < 0.52) return bezier((t-0.48)/0.04,
                p(690,510), p(685,492), p(695,478), p(710,472));
        else if (t < 0.55) return bezier((t-0.52)/0.03,
                p(710,472), p(730,465), p(740,450), p(730,435));
        else if (t < 0.60) return bezier((t-0.55)/0.05,
                p(730,435), p(720,420), p(680,410), p(640,415));
        else if (t < 0.66) return bezier((t-0.60)/0.06,
                p(640,415), p(560,420), p(480,450), p(420,470));
        else if (t < 0.70) return bezier((t-0.66)/0.04,
                p(420,470), p(370,480), p(330,470), p(315,448));
        else if (t < 0.74) return bezier((t-0.70)/0.04,
                p(315,448), p(300,425), p(295,400), p(305,380));
        else if (t < 0.78) return bezier((t-0.74)/0.04,
                p(305,380), p(318,358), p(340,352), p(350,335));
        else if (t < 0.82) return bezier((t-0.78)/0.04,
                p(350,335), p(360,318), p(355,300), p(340,285));
        else if (t < 0.86) return interp((t-0.82)/0.04,
                p(340,285), p(270,250));
        else if (t < 0.90) return bezier((t-0.86)/0.04,
                p(270,250), p(220,250), p(190,270), p(190,310));
        else if (t < 0.94) return bezier((t-0.90)/0.04,
                p(190,310), p(190,350), p(220,370), p(260,370));
        else if (t < 0.97) return bezier((t-0.94)/0.03,
                p(260,370), p(300,370), p(330,350), p(340,320));
        else               return bezier((t-0.97)/0.03,
                    p(340,320), p(390,240), p(490,180), p(592,172));
    }

    // ── Helpers ──────────────────────────────────────────────────────────
    private Punto p(double x, double y) { return new Punto(x, y); }

    private Punto interp(double t, Punto p0, Punto p1) {
        return new Punto(p0.x + t*(p1.x-p0.x), p0.y + t*(p1.y-p0.y));
    }

    private Punto bezier(double t, Punto p0, Punto p1, Punto p2, Punto p3) {
        double u = 1-t, tt = t*t, uu = u*u;
        return new Punto(
                uu*u*p0.x + 3*uu*t*p1.x + 3*u*tt*p2.x + tt*t*p3.x,
                uu*u*p0.y + 3*uu*t*p1.y + 3*u*tt*p2.y + tt*t*p3.y
        );
    }

    // Alias para compatibilidad con calcularPosicionPitLane
    private Punto calcularBezier(double t, Punto p0, Punto p1, Punto p2, Punto p3) {
        return bezier(t, p0, p1, p2, p3);
    }
}