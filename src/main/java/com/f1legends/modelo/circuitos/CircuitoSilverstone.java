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
        dibujarKerbs(gc);
        dibujarAsfalto(gc);
        dibujarAsfaltoDetalle(gc);
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

    // ── 2. Kerbs ─────────────────────────────────────────────────────────
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

    // ── 3. Asfalto ───────────────────────────────────────────────────────
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

    // ── 4. Detalle asfalto ───────────────────────────────────────────────
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

    // ── 5. Línea de meta ─────────────────────────────────────────────────
    // La meta está en la recta de Hangar Straight, lado derecho superior
    private void dibujarLineaMeta(GraphicsContext gc) {
        // Meta en la recta Hangar (arriba derecha, horizontal)
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

    // ── 6. Panel info ─────────────────────────────────────────────────────
    private void dibujarInfoCircuito(GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 0, 0, 0.60));
        gc.fillRoundRect(60, 38, 170, 62, 8, 8);

        // Bandera UK (simplificada como azul)
        double fx = 64, fy = 42, fw2 = 14, fh2 = 10;
        gc.setFill(Color.rgb(0, 36, 125));
        gc.fillRect(fx, fy, fw2 * 3, fh2);
        gc.setFill(Color.WHITE);
        gc.fillRect(fx + fw2, fy, fw2, fh2);
        gc.setFill(Color.rgb(207, 20, 43));
        gc.fillRect(fx + fw2, fy, fw2 * 0.4, fh2);

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("SILVERSTONE CIRCUIT", 64, 65);
        gc.fillText("GRAN BRETAÑA", 64, 77);

        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 8));
        gc.setFill(Color.rgb(255, 210, 50));
        gc.fillText("52 vueltas · 5.891 km", 64, 90);
    }

    // ════════════════════════════════════════════════════════════════════
    //  TRAZADO — forma de Silverstone según imagen de referencia
    //
    //  Puntos clave del layout (en canvas 950x650):
    //
    //  Recta de meta / pit straight: horizontal superior derecha ~(580,170)→(750,170)
    //  Curva Copse:         esquina superior derecha, radio amplio
    //  Maggotts/Becketts:   chicane izquierda-derecha centro-derecha
    //  Chapel:              curva rápida bajando
    //  Hangar Straight:     recta bajando al costado derecho
    //  Stowe:               curva amplia inferior derecha
    //  Vale/Club:           chicane inferior
    //  Abbey:               curva amplia inferior izquierda
    //  Farm/Village:        curva izquierda subiendo
    //  Loop:                curva cerrada pequeña izquierda
    //  Aintree/Wellington:  sección superior izquierda
    //  Luffield:            curva cerrada vuelta al inicio
    // ════════════════════════════════════════════════════════════════════
    private void trazarCircuito(GraphicsContext gc) {
        gc.beginPath();

        // Inicio — línea de meta (recta pit straight, va hacia derecha)
        gc.moveTo(592, 172);

        // → Copse (curva amplia a la derecha, esquina superior derecha)
        gc.lineTo(760, 172);
        gc.bezierCurveTo(800, 172, 820, 200, 820, 230);

        // ↓ Maggotts (primer giro del chicane, curva izquierda)
        gc.bezierCurveTo(820, 260, 790, 270, 770, 280);

        // Becketts (curva derecha del chicane)
        gc.bezierCurveTo(750, 290, 740, 310, 760, 330);

        // Chapel (curva amplia saliendo del chicane hacia abajo)
        gc.bezierCurveTo(775, 345, 800, 348, 810, 360);

        // ↓ Hangar Straight (recta larga bajando lado derecho)
        gc.lineTo(810, 460);

        // Stowe (curva amplia inferior derecha)
        gc.bezierCurveTo(810, 510, 780, 540, 740, 540);

        // Vale (pequeña curva izquierda inferior)
        gc.bezierCurveTo(710, 540, 695, 525, 690, 510);

        // Club (curva derecha saliendo)
        gc.bezierCurveTo(685, 492, 695, 478, 710, 472);
        gc.bezierCurveTo(730, 465, 740, 450, 730, 435);

        // Abbey (gran curva inferior izquierda, el gran arco de abajo)
        gc.bezierCurveTo(720, 420, 680, 410, 640, 415);
        gc.bezierCurveTo(560, 420, 480, 450, 420, 470);

        // Farm / Village (curva izquierda subiendo)
        gc.bezierCurveTo(370, 480, 330, 470, 315, 448);

        // Loop (curva cerrada pequeña hacia arriba-izquierda)
        gc.bezierCurveTo(300, 425, 295, 400, 305, 380);
        gc.bezierCurveTo(318, 358, 340, 352, 350, 335);

        // Aintree (recta superior izquierda, subiendo)
        gc.bezierCurveTo(360, 318, 355, 300, 340, 285);

        // Wellington Straight (recta izquierda bajando un poco)
        gc.lineTo(270, 250);

        // Luffield (curva cerrada que gira a la derecha para volver)
        gc.bezierCurveTo(220, 250, 190, 270, 190, 310);
        gc.bezierCurveTo(190, 350, 220, 370, 260, 370);
        gc.bezierCurveTo(300, 370, 330, 350, 340, 320);

        // Woodcote / recta de regreso a meta
        gc.bezierCurveTo(355, 285, 390, 240, 440, 210);
        gc.bezierCurveTo(490, 180, 540, 168, 592, 172);

        gc.stroke();
    }

    // ════════════════════════════════════════════════════════════════════
    //  POSICIÓN EN PISTA — divide el trazado en segmentos con t ∈ [0,1]
    // ════════════════════════════════════════════════════════════════════
    @Override
    public Punto calcularPosicion(double t) {

        // Pit straight → meta
        if      (t < 0.08) return interp(t / 0.08,
                p(592,172), p(760,172));

            // Copse
        else if (t < 0.13) return bezier((t-0.08)/0.05,
                p(760,172), p(800,172), p(820,200), p(820,230));

            // Maggotts
        else if (t < 0.18) return bezier((t-0.13)/0.05,
                p(820,230), p(820,260), p(790,270), p(770,280));

            // Becketts
        else if (t < 0.23) return bezier((t-0.18)/0.05,
                p(770,280), p(750,290), p(740,310), p(760,330));

            // Chapel
        else if (t < 0.27) return bezier((t-0.23)/0.04,
                p(760,330), p(775,345), p(800,348), p(810,360));

            // Hangar Straight
        else if (t < 0.38) return interp((t-0.27)/0.11,
                p(810,360), p(810,460));

            // Stowe
        else if (t < 0.44) return bezier((t-0.38)/0.06,
                p(810,460), p(810,510), p(780,540), p(740,540));

            // Vale
        else if (t < 0.48) return bezier((t-0.44)/0.04,
                p(740,540), p(710,540), p(695,525), p(690,510));

            // Club entrada
        else if (t < 0.52) return bezier((t-0.48)/0.04,
                p(690,510), p(685,492), p(695,478), p(710,472));

            // Club salida
        else if (t < 0.55) return bezier((t-0.52)/0.03,
                p(710,472), p(730,465), p(740,450), p(730,435));

            // Abbey inicio
        else if (t < 0.60) return bezier((t-0.55)/0.05,
                p(730,435), p(720,420), p(680,410), p(640,415));

            // Abbey / Farm
        else if (t < 0.66) return bezier((t-0.60)/0.06,
                p(640,415), p(560,420), p(480,450), p(420,470));

            // Farm / Village
        else if (t < 0.70) return bezier((t-0.66)/0.04,
                p(420,470), p(370,480), p(330,470), p(315,448));

            // Loop entrada
        else if (t < 0.74) return bezier((t-0.70)/0.04,
                p(315,448), p(300,425), p(295,400), p(305,380));

            // Loop salida
        else if (t < 0.78) return bezier((t-0.74)/0.04,
                p(305,380), p(318,358), p(340,352), p(350,335));

            // Aintree
        else if (t < 0.82) return bezier((t-0.78)/0.04,
                p(350,335), p(360,318), p(355,300), p(340,285));

            // Wellington Straight
        else if (t < 0.86) return interp((t-0.82)/0.04,
                p(340,285), p(270,250));

            // Luffield entrada
        else if (t < 0.90) return bezier((t-0.86)/0.04,
                p(270,250), p(220,250), p(190,270), p(190,310));

            // Luffield salida
        else if (t < 0.94) return bezier((t-0.90)/0.04,
                p(190,310), p(190,350), p(220,370), p(260,370));

            // Woodcote
        else if (t < 0.97) return bezier((t-0.94)/0.03,
                p(260,370), p(300,370), p(330,350), p(340,320));

            // Recta de regreso a meta
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
}