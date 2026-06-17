package com.f1legends.modelo.circuitos;

import com.f1legends.modelo.Punto;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class CircuitoSuzuka extends Circuito {

    private static final Color ASFALTO     = Color.rgb(42,  42,  46);
    private static final Color ASFALTO_MID = Color.rgb(56,  56,  62);
    private static final Color KERB_ROJO   = Color.rgb(195, 28,  28);
    private static final Color KERB_BLANCO = Color.rgb(235, 235, 235);
    private static final Color HIERBA_OSC  = Color.rgb(30,  88,  30);
    private static final Color HIERBA_CLAR = Color.rgb(44, 108,  44);
    private static final Color META_BLANCA = Color.WHITE;
    private static final Color META_NEGRA  = Color.rgb(15,  15,  15);

    public CircuitoSuzuka(int id, String nombre, String pais, int vueltas) {
        super(id, nombre, pais, vueltas);
    }

    public CircuitoSuzuka() {
        super(4, "Suzuka", "Japón", 53);
    }

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

    private void dibujarFondo(GraphicsContext gc) {
        int tile = 40;
        for (int col = 0; col * tile < 1080; col++)
            for (int row = 0; row * tile < 700; row++) {
                gc.setFill((col + row) % 2 == 0 ? HIERBA_OSC : HIERBA_CLAR);
                gc.fillRect(col * tile, row * tile, tile, tile);
            }
    }

    private void dibujarKerbs(GraphicsContext gc) {
        gc.setLineWidth(32);
        gc.setLineDashes(16, 16);
        gc.setStroke(KERB_BLANCO); gc.setLineDashOffset(0);  trazarCircuito(gc);
        gc.setStroke(KERB_ROJO);   gc.setLineDashOffset(16); trazarCircuito(gc);
        gc.setLineDashes(0); gc.setLineDashOffset(0);
    }

    private void dibujarAsfalto(GraphicsContext gc) {
        gc.setLineWidth(24); gc.setStroke(ASFALTO);     trazarCircuito(gc);
        gc.setLineWidth(16); gc.setStroke(ASFALTO_MID); trazarCircuito(gc);
        gc.setLineWidth(8);  gc.setStroke(ASFALTO);     trazarCircuito(gc);
    }

    private void dibujarAsfaltoDetalle(GraphicsContext gc) {
        gc.setLineWidth(3.5);
        gc.setStroke(Color.rgb(22, 22, 25, 0.50));
        gc.setLineDashes(26, 70); trazarCircuito(gc); gc.setLineDashes(0);
        gc.setLineWidth(1.0);
        gc.setStroke(Color.rgb(255, 255, 255, 0.48));
        gc.setLineDashes(9, 13);  trazarCircuito(gc); gc.setLineDashes(0);
    }

    // Meta en la recta principal (arriba derecha, horizontal)
    private void dibujarLineaMeta(GraphicsContext gc) {
        double metaY = 172, trackX1 = 630, trackX2 = 658;
        double cellW = 3.5, cellH = 3.5;
        int filas = 5;
        for (int fila = 0; fila < filas; fila++) {
            int cols = (int) Math.ceil((trackX2 - trackX1) / cellW);
            for (int col = 0; col < cols; col++) {
                gc.setFill((col + fila) % 2 == 0 ? META_BLANCA : META_NEGRA);
                double cx = trackX1 + col * cellW;
                gc.fillRect(cx, metaY + fila * cellH,
                        Math.min(cellW, trackX2 - cx), cellH);
            }
        }
        gc.setStroke(Color.rgb(230, 230, 230));
        gc.setLineWidth(1.5);
        double mastY = metaY + filas * cellH / 2.0;
        gc.strokeLine(trackX1, mastY - 22, trackX1, mastY);
        double fw = 2.5, fh = 3.0;
        for (int fc = 0; fc < 3; fc++)
            for (int fr = 0; fr < 5; fr++) {
                gc.setFill((fc + fr) % 2 == 0 ? META_BLANCA : META_NEGRA);
                gc.fillRect(trackX1 + fc * fw, mastY - 22 + fr * fh, fw, fh);
            }
    }

    private void dibujarInfoCircuito(GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 0, 0, 0.60));
        gc.fillRoundRect(60, 38, 155, 62, 8, 8);
        // Bandera de Japón
        double fx = 64, fy = 42, fw2 = 42, fh2 = 10;
        gc.setFill(Color.WHITE); gc.fillRect(fx, fy, fw2, fh2 * 2);
        gc.setFill(Color.rgb(188, 0, 45));
        gc.fillOval(fx + fw2/2.0 - 6, fy + fh2 - 6, 12, 12);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("SUZUKA CIRCUIT", 64, 65);
        gc.fillText("JAPÓN", 64, 77);
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 8));
        gc.setFill(Color.rgb(255, 210, 50));
        gc.fillText("53 vueltas · 5.807 km", 64, 90);
    }

    // ════════════════════════════════════════════════════════════════════
    //  TRAZADO — Suzuka (imagen 2: figura en 8, START arriba derecha)
    //
    //  Curvas clave:
    //  T1-T2 (1-2):        curva derecha amplia desde recta meta
    //  Esses (3-4-5):      chicane rápida izquierda-derecha-izquierda
    //  Dunlop (6):         curva derecha lenta
    //  Degner (7-8):       curvas rápidas bajando
    //  Hairpin (9):        horquilla muy cerrada abajo centro
    //  Spoon (10-11):      curva larga izquierda
    //  130R (12):          curva rapidísima izquierda arriba
    //  Chicane (13):       chicane final antes de meta
    //  CRUCE (figura 8):   el overpass que cruza el circuito (T16/T17)
    // ════════════════════════════════════════════════════════════════════
    private void trazarCircuito(GraphicsContext gc) {
        gc.beginPath();

        // Recta principal (meta) — va hacia izquierda
        gc.moveTo(660, 180);
        gc.lineTo(440, 180);

        // T1-T2: curva derecha amplia (baja)
        gc.bezierCurveTo(390, 180, 360, 205, 355, 240);
        gc.bezierCurveTo(350, 270, 365, 290, 390, 295);

        // Esses (T3-T4-T5): chicane rápida
        gc.bezierCurveTo(415, 300, 430, 318, 425, 340);
        gc.bezierCurveTo(420, 360, 400, 372, 380, 370);
        gc.bezierCurveTo(358, 368, 342, 352, 340, 330);
        gc.bezierCurveTo(338, 308, 352, 292, 370, 290);

        // Dunlop (T6): curva amplia derecha bajando
        gc.bezierCurveTo(395, 288, 415, 300, 420, 325);
        gc.lineTo(425, 390);

        // Degner 1 (T7): curva derecha
        gc.bezierCurveTo(426, 418, 440, 435, 462, 438);

        // Degner 2 (T8): curva izquierda saliendo
        gc.bezierCurveTo(488, 441, 505, 428, 508, 405);
        gc.lineTo(510, 370);

        // Hairpin (T9): horquilla cerrada abajo
        gc.bezierCurveTo(512, 340, 528, 320, 550, 318);
        gc.lineTo(620, 318);
        gc.bezierCurveTo(658, 318, 680, 345, 680, 378);
        gc.bezierCurveTo(680, 415, 655, 440, 618, 440);
        gc.lineTo(560, 440);

        // Hacia Spoon (T10-T11): larga curva izquierda
        gc.bezierCurveTo(520, 440, 488, 458, 475, 490);
        gc.bezierCurveTo(462, 522, 468, 555, 490, 572);
        gc.lineTo(560, 590);
        gc.bezierCurveTo(610, 598, 655, 580, 668, 548);

        // 130R (T12): curva izquierda rapidísima arriba
        gc.bezierCurveTo(682, 515, 688, 478, 678, 448);
        gc.bezierCurveTo(668, 418, 645, 400, 618, 398);
        gc.lineTo(560, 398);

        // Hacia chicane final
        gc.bezierCurveTo(530, 398, 508, 382, 505, 355);
        gc.lineTo(500, 300);

        // CRUCE / overpass (figura en 8): el circuito pasa POR DEBAJO
        // de la recta principal en este punto (~x=500, y=240)
        gc.bezierCurveTo(498, 270, 496, 255, 495, 240);
        gc.lineTo(495, 215);

        // Chicane (T13-T14): antes de meta, izquierda-derecha
        gc.bezierCurveTo(496, 198, 505, 188, 518, 186);
        gc.bezierCurveTo(532, 184, 545, 192, 548, 206);
        gc.bezierCurveTo(551, 220, 542, 232, 528, 234);
        gc.bezierCurveTo(512, 236, 500, 225, 500, 210);
        gc.bezierCurveTo(500, 194, 514, 182, 532, 180);

        // Recta final a meta
        gc.lineTo(660, 180);

        gc.stroke();
    }

    @Override
    public Punto calcularPosicion(double t) {

        if      (t < 0.07) return interp(t/0.07,             p(660,180), p(440,180));
        else if (t < 0.11) return bezier((t-0.07)/0.04,      p(440,180), p(390,180), p(360,205), p(355,240));
        else if (t < 0.15) return bezier((t-0.11)/0.04,      p(355,240), p(350,270), p(365,290), p(390,295));
        else if (t < 0.18) return bezier((t-0.15)/0.03,      p(390,295), p(415,300), p(430,318), p(425,340));
        else if (t < 0.21) return bezier((t-0.18)/0.03,      p(425,340), p(420,360), p(400,372), p(380,370));
        else if (t < 0.24) return bezier((t-0.21)/0.03,      p(380,370), p(358,368), p(342,352), p(340,330));
        else if (t < 0.27) return bezier((t-0.24)/0.03,      p(340,330), p(338,308), p(352,292), p(370,290));
        else if (t < 0.30) return bezier((t-0.27)/0.03,      p(370,290), p(395,288), p(415,300), p(420,325));
        else if (t < 0.35) return interp((t-0.30)/0.05,      p(420,325), p(425,390));
        else if (t < 0.38) return bezier((t-0.35)/0.03,      p(425,390), p(426,418), p(440,435), p(462,438));
        else if (t < 0.41) return bezier((t-0.38)/0.03,      p(462,438), p(488,441), p(505,428), p(508,405));
        else if (t < 0.44) return interp((t-0.41)/0.03,      p(508,405), p(510,370));
        else if (t < 0.47) return bezier((t-0.44)/0.03,      p(510,370), p(512,340), p(528,320), p(550,318));
        else if (t < 0.50) return interp((t-0.47)/0.03,      p(550,318), p(620,318));
        else if (t < 0.53) return bezier((t-0.50)/0.03,      p(620,318), p(658,318), p(680,345), p(680,378));
        else if (t < 0.56) return bezier((t-0.53)/0.03,      p(680,378), p(680,415), p(655,440), p(618,440));
        else if (t < 0.59) return interp((t-0.56)/0.03,      p(618,440), p(560,440));
        else if (t < 0.63) return bezier((t-0.59)/0.04,      p(560,440), p(520,440), p(488,458), p(475,490));
        else if (t < 0.67) return bezier((t-0.63)/0.04,      p(475,490), p(462,522), p(468,555), p(490,572));
        else if (t < 0.70) return interp((t-0.67)/0.03,      p(490,572), p(560,590));
        else if (t < 0.73) return bezier((t-0.70)/0.03,      p(560,590), p(610,598), p(655,580), p(668,548));
        else if (t < 0.77) return bezier((t-0.73)/0.04,      p(668,548), p(682,515), p(688,478), p(678,448));
        else if (t < 0.80) return bezier((t-0.77)/0.03,      p(678,448), p(668,418), p(645,400), p(618,398));
        else if (t < 0.83) return interp((t-0.80)/0.03,      p(618,398), p(560,398));
        else if (t < 0.86) return bezier((t-0.83)/0.03,      p(560,398), p(530,398), p(508,382), p(505,355));
        else if (t < 0.89) return interp((t-0.86)/0.03,      p(505,355), p(500,300));
        else if (t < 0.91) return bezier((t-0.89)/0.02,      p(500,300), p(498,270), p(496,255), p(495,240));
        else if (t < 0.93) return interp((t-0.91)/0.02,      p(495,240), p(495,215));
        else if (t < 0.95) return bezier((t-0.93)/0.02,      p(495,215), p(496,198), p(505,188), p(518,186));
        else if (t < 0.97) return bezier((t-0.95)/0.02,      p(518,186), p(532,184), p(545,192), p(548,206));
        else if (t < 0.985) return bezier((t-0.97)/0.015,    p(548,206), p(551,220), p(542,232), p(528,234));
        else               return bezier((t-0.985)/0.015,     p(528,234), p(512,236), p(500,210), p(532,180));
    }

    private Punto p(double x, double y) { return new Punto(x, y); }
    private Punto interp(double t, Punto a, Punto b) {
        return new Punto(a.x + t*(b.x-a.x), a.y + t*(b.y-a.y));
    }
    private Punto bezier(double t, Punto p0, Punto p1, Punto p2, Punto p3) {
        double u=1-t, tt=t*t, uu=u*u;
        return new Punto(uu*u*p0.x+3*uu*t*p1.x+3*u*tt*p2.x+tt*t*p3.x,
                uu*u*p0.y+3*uu*t*p1.y+3*u*tt*p2.y+tt*t*p3.y);
    }
}