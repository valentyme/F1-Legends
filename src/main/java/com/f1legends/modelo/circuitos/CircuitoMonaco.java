package com.f1legends.modelo.circuitos;

import com.f1legends.modelo.Punto;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class CircuitoMonaco extends Circuito {

    private static final Color ASFALTO     = Color.rgb(42,  42,  46);
    private static final Color ASFALTO_MID = Color.rgb(56,  56,  62);
    private static final Color KERB_ROJO   = Color.rgb(195, 28,  28);
    private static final Color KERB_BLANCO = Color.rgb(235, 235, 235);
    private static final Color HIERBA_OSC  = Color.rgb(30,  88,  30);
    private static final Color HIERBA_CLAR = Color.rgb(44, 108,  44);
    private static final Color META_BLANCA = Color.WHITE;
    private static final Color META_NEGRA  = Color.rgb(15,  15,  15);

    public CircuitoMonaco(int id, String nombre, String pais, int vueltas) {
        super(id, nombre, pais, vueltas);
    }

    public CircuitoMonaco() {
        super(3, "Monaco", "Mónaco", 78);
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

    // Meta en la recta de salida de la curva Sainte-Dévote (arriba izquierda)
    private void dibujarLineaMeta(GraphicsContext gc) {
        double metaX = 310, trackY1 = 148, trackY2 = 173;
        double cellW = 3.5, cellH = 3.5;
        int cols = 5;
        for (int col = 0; col < cols; col++) {
            int filas = (int) Math.ceil((trackY2 - trackY1) / cellH);
            for (int row = 0; row < filas; row++) {
                gc.setFill((col + row) % 2 == 0 ? META_BLANCA : META_NEGRA);
                double cy = trackY1 + row * cellH;
                gc.fillRect(metaX + col * cellW, cy,
                        cellW, Math.min(cellH, trackY2 - cy));
            }
        }
        gc.setStroke(Color.rgb(230, 230, 230));
        gc.setLineWidth(1.5);
        double mastX = metaX + cols * cellW / 2.0;
        gc.strokeLine(mastX, trackY1 - 22, mastX, trackY1);
        double fw = 3.0, fh = 2.5;
        for (int fc = 0; fc < 5; fc++)
            for (int fr = 0; fr < 3; fr++) {
                gc.setFill((fc + fr) % 2 == 0 ? META_BLANCA : META_NEGRA);
                gc.fillRect(mastX + 1 + fc * fw, trackY1 - 22 + fr * fh, fw, fh);
            }
    }

    private void dibujarInfoCircuito(GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 0, 0, 0.60));
        gc.fillRoundRect(60, 38, 165, 62, 8, 8);
        // Bandera de Mónaco (rojo/blanco)
        double fx = 64, fy = 42, fw2 = 21, fh2 = 10;
        gc.setFill(Color.rgb(206, 17, 38));  gc.fillRect(fx,       fy, fw2, fh2);
        gc.setFill(Color.WHITE);             gc.fillRect(fx, fy + fh2, fw2, fh2);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("CIRCUIT DE MONACO", 64, 65);
        gc.fillText("MÓNACO", 64, 77);
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 8));
        gc.setFill(Color.rgb(255, 210, 50));
        gc.fillText("78 vueltas · 3.337 km", 64, 90);
    }

    // ════════════════════════════════════════════════════════════════════
    //  TRAZADO — Monaco (imagen 1: START arriba izquierda, Loews abajo
    //  izquierda, gran curva derecha, túnel centro)
    //
    //  Curvas clave:
    //  Sainte-Dévote (1):  curva derecha desde recta meta
    //  Massenet (2-3):     subida larga a la izquierda
    //  Casino (4):         curva derecha en lo alto
    //  Mirabeau (5-6):     bajada chicane
    //  Loews/Fairmont (7): horquilla cerradísima izquierda (abajo izq)
    //  Portier (8):        curva derecha saliendo de Loews
    //  Tunnel (9):         recta larga por el túnel
    //  Chicane/Piscine(10-11): chicane rápida junto al mar
    //  Rascasse (12):      curva lenta izquierda
    //  Anthony Noghes(13): curva derecha final a meta
    // ════════════════════════════════════════════════════════════════════
    private void trazarCircuito(GraphicsContext gc) {
        gc.beginPath();

        // Meta — recta hacia Sainte-Dévote
        gc.moveTo(310, 160);
        gc.lineTo(480, 160);

        // Sainte-Dévote (T1): curva derecha cerrada
        gc.bezierCurveTo(530, 160, 548, 180, 548, 210);

        // Subida a Massenet (larga recta cuesta arriba izquierda)
        gc.lineTo(548, 310);

        // Massenet (T4): curva izquierda suave
        gc.bezierCurveTo(548, 340, 530, 355, 510, 355);

        // Hacia Casino (curva derecha en la cima)
        gc.lineTo(430, 355);
        gc.bezierCurveTo(400, 355, 378, 340, 370, 315);

        // Mirabeau alto (T5): chicane, baja
        gc.bezierCurveTo(362, 290, 348, 278, 330, 275);
        gc.bezierCurveTo(310, 272, 295, 285, 285, 305);

        // Mirabeau bajo (T6): sigue bajando
        gc.bezierCurveTo(275, 325, 265, 345, 255, 360);

        // Loews / Fairmont (T7): horquilla cerradísima izquierda
        gc.bezierCurveTo(245, 380, 230, 400, 215, 410);
        gc.bezierCurveTo(188, 428, 165, 420, 158, 400);
        gc.bezierCurveTo(150, 378, 165, 355, 190, 348);
        gc.lineTo(240, 348);

        // Portier (T8): curva derecha saliendo al túnel
        gc.bezierCurveTo(270, 348, 290, 335, 295, 312);
        gc.lineTo(310, 270);

        // Túnel (recta larga hacia la derecha)
        gc.bezierCurveTo(318, 245, 340, 230, 370, 228);
        gc.lineTo(620, 228);

        // Salida del túnel → Nouvelle Chicane / Piscine
        gc.bezierCurveTo(650, 228, 668, 240, 670, 260);

        // Piscine chicane (T10-T11): rápida izquierda-derecha
        gc.bezierCurveTo(672, 278, 660, 292, 645, 292);
        gc.bezierCurveTo(628, 292, 615, 278, 615, 260);
        gc.bezierCurveTo(615, 242, 628, 228, 645, 228);

        // Continuación hacia Rascasse
        gc.bezierCurveTo(665, 228, 682, 242, 685, 265);
        gc.lineTo(690, 310);

        // Rascasse (T12): curva izquierda lenta
        gc.bezierCurveTo(692, 335, 680, 355, 658, 358);
        gc.lineTo(580, 358);

        // Anthony Noghes (T13): curva derecha final
        gc.bezierCurveTo(545, 358, 520, 340, 515, 310);
        gc.lineTo(505, 240);
        gc.bezierCurveTo(502, 215, 488, 185, 460, 170);

        // De vuelta a meta
        gc.lineTo(310, 160);

        gc.stroke();
    }

    @Override
    public Punto calcularPosicion(double t) {

        if      (t < 0.07) return interp((t)/0.07,         p(310,160), p(480,160));
        else if (t < 0.12) return bezier((t-0.07)/0.05,    p(480,160),  p(530,160),  p(548,180),  p(548,210));
        else if (t < 0.22) return interp((t-0.12)/0.10,    p(548,210),  p(548,310));
        else if (t < 0.26) return bezier((t-0.22)/0.04,    p(548,310),  p(548,340),  p(530,355),  p(510,355));
        else if (t < 0.30) return interp((t-0.26)/0.04,    p(510,355),  p(430,355));
        else if (t < 0.34) return bezier((t-0.30)/0.04,    p(430,355),  p(400,355),  p(378,340),  p(370,315));
        else if (t < 0.38) return bezier((t-0.34)/0.04,    p(370,315),  p(362,290),  p(348,278),  p(330,275));
        else if (t < 0.41) return bezier((t-0.38)/0.03,    p(330,275),  p(310,272),  p(295,285),  p(285,305));
        else if (t < 0.44) return bezier((t-0.41)/0.03,    p(285,305),  p(275,325),  p(265,345),  p(255,360));
        else if (t < 0.47) return bezier((t-0.44)/0.03,    p(255,360),  p(245,380),  p(230,400),  p(215,410));
        else if (t < 0.50) return bezier((t-0.47)/0.03,    p(215,410),  p(188,428),  p(165,420),  p(158,400));
        else if (t < 0.53) return bezier((t-0.50)/0.03,    p(158,400),  p(150,378),  p(165,355),  p(190,348));
        else if (t < 0.56) return interp((t-0.53)/0.03,    p(190,348),  p(240,348));
        else if (t < 0.59) return bezier((t-0.56)/0.03,    p(240,348),  p(270,348),  p(290,335),  p(295,312));
        else if (t < 0.62) return interp((t-0.59)/0.03,    p(295,312),  p(310,270));
        else if (t < 0.65) return bezier((t-0.62)/0.03,    p(310,270),  p(318,245),  p(340,230),  p(370,228));
        else if (t < 0.72) return interp((t-0.65)/0.07,    p(370,228),  p(620,228));
        else if (t < 0.75) return bezier((t-0.72)/0.03,    p(620,228),  p(650,228),  p(668,240),  p(670,260));
        else if (t < 0.78) return bezier((t-0.75)/0.03,    p(670,260),  p(672,278),  p(660,292),  p(645,292));
        else if (t < 0.81) return bezier((t-0.78)/0.03,    p(645,292),  p(628,292),  p(615,278),  p(615,260));
        else if (t < 0.84) return bezier((t-0.81)/0.03,    p(615,260),  p(615,242),  p(628,228),  p(645,228));
        else if (t < 0.87) return bezier((t-0.84)/0.03,    p(645,228),  p(665,228),  p(682,242),  p(685,265));
        else if (t < 0.90) return interp((t-0.87)/0.03,    p(685,265),  p(690,310));
        else if (t < 0.93) return bezier((t-0.90)/0.03,    p(690,310),  p(692,335),  p(680,355),  p(658,358));
        else if (t < 0.95) return interp((t-0.93)/0.02,    p(658,358),  p(580,358));
        else if (t < 0.97) return bezier((t-0.95)/0.02,    p(580,358),  p(545,358),  p(520,340),  p(515,310));
        else if (t < 0.985) return interp((t-0.97)/0.015,  p(515,310),  p(505,240));
        else               return bezier((t-0.985)/0.015,   p(505,240),  p(488,185),  p(460,170),  p(310,160));
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