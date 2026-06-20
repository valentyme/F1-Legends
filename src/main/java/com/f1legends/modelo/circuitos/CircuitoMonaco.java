package com.f1legends.modelo.circuitos;

import com.f1legends.modelo.Punto;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class CircuitoMonaco extends Circuito {

    private static final Color ASFALTO      = Color.rgb(42,  42,  46);
    private static final Color ASFALTO_MID  = Color.rgb(56,  56,  62);
    private static final Color KERB_ROJO    = Color.rgb(195, 28,  28);
    private static final Color KERB_BLANCO  = Color.rgb(235, 235, 235);
    private static final Color HIERBA_OSC   = Color.rgb(30,  88,  30);
    private static final Color HIERBA_CLAR  = Color.rgb(44, 108,  44);
    private static final Color META_BLANCA  = Color.WHITE;
    private static final Color META_NEGRA   = Color.rgb(15,  15,  15);

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
        int ts = 40;
        for (int col = 0; col * ts < 1080; col++)
            for (int row = 0; row * ts < 700; row++) {
                gc.setFill((col + row) % 2 == 0 ? HIERBA_OSC : HIERBA_CLAR);
                gc.fillRect(col * ts, row * ts, ts, ts);
            }
    }

    private void dibujarPitLaneCarril(GraphicsContext gc) {
        gc.setLineWidth(10);
        gc.setStroke(Color.rgb(68, 68, 76));
        gc.beginPath();
        gc.moveTo(358, 565);
        gc.lineTo(358, 322);
        gc.stroke();

        gc.setLineWidth(2);
        gc.setStroke(Color.rgb(210, 210, 210));
        gc.beginPath();
        gc.moveTo(366, 565);
        gc.lineTo(366, 322);
        gc.stroke();
    }

    private void dibujarKerbs(GraphicsContext gc) {
        gc.setLineWidth(28);
        gc.setLineDashes(13, 13);
        gc.setStroke(KERB_BLANCO); gc.setLineDashOffset(0);  trazarCircuito(gc);
        gc.setStroke(KERB_ROJO);   gc.setLineDashOffset(13); trazarCircuito(gc);
        gc.setLineDashes(0); gc.setLineDashOffset(0);
    }

    private void dibujarAsfalto(GraphicsContext gc) {
        gc.setLineWidth(20); gc.setStroke(ASFALTO);     trazarCircuito(gc);
        gc.setLineWidth(13); gc.setStroke(ASFALTO_MID); trazarCircuito(gc);
        gc.setLineWidth(6);  gc.setStroke(ASFALTO);     trazarCircuito(gc);
    }

    private void dibujarAsfaltoDetalle(GraphicsContext gc) {
        gc.setLineWidth(2.5);
        gc.setStroke(Color.rgb(22, 22, 25, 0.45));
        gc.setLineDashes(22, 55); trazarCircuito(gc); gc.setLineDashes(0);
        gc.setLineWidth(0.9);
        gc.setStroke(Color.rgb(255, 255, 255, 0.38));
        gc.setLineDashes(7, 11); trazarCircuito(gc); gc.setLineDashes(0);
    }

    private void dibujarPitLaneDetalle(GraphicsContext gc) {
        gc.setLineWidth(1.2);
        gc.setStroke(Color.rgb(240, 240, 240, 0.90));
        gc.setLineDashes(6, 5);
        gc.beginPath();
        gc.moveTo(358, 565);
        gc.lineTo(358, 322);
        gc.stroke();
        gc.setLineDashes(0);

        gc.setFill(Color.rgb(72, 72, 82));
        gc.setStroke(Color.rgb(110, 110, 118));
        gc.setLineWidth(0.7);
        for (int i = 0; i < 13; i++) {
            double by = 555 - i * 18.0;
            gc.fillRect(367, by - 5, 12, 7);
            gc.strokeRect(367, by - 5, 12, 7);
        }

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 7));
        gc.setFill(Color.rgb(255, 200, 0, 0.95));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("▼ PIT IN",  370, 576);
        gc.fillText("PIT OUT ▲", 370, 318);

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 6));
        gc.setFill(Color.rgb(255, 210, 60, 0.85));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("PIT LANE", 385, 445);
    }

    private void dibujarLineaMeta(GraphicsContext gc) {
        double mx = 340, y1 = 520, y2 = 545;
        double cs = 3.5;
        int cols = 5;
        int filas = (int) Math.ceil((y2 - y1) / cs);
        for (int col = 0; col < cols; col++)
            for (int row = 0; row < filas; row++) {
                gc.setFill((col + row) % 2 == 0 ? META_BLANCA : META_NEGRA);
                gc.fillRect(mx + col * cs, y1 + row * cs, cs, Math.min(cs, y2 - y1 - row * cs));
            }
        gc.setStroke(Color.rgb(220, 220, 220));
        gc.setLineWidth(1.5);
        gc.strokeLine(mx - 1, y1 - 18, mx - 1, y1);
        double fw = 3.0, fh = 2.5;
        for (int fc = 0; fc < 5; fc++)
            for (int fr = 0; fr < 3; fr++) {
                gc.setFill((fc + fr) % 2 == 0 ? META_BLANCA : META_NEGRA);
                gc.fillRect(mx - 1 + fc * fw, y1 - 18 + fr * fh, fw, fh);
            }
    }

    private void dibujarInfoCircuito(GraphicsContext gc) {
        // Reducimos el ancho del panel (de 165 a 145) y lo movemos a la izquierda (X = 20)
        gc.setFill(Color.rgb(0, 0, 0, 0.60));
        gc.fillRoundRect(20, 38, 145, 54, 8, 8);

        // Coordenadas de la bandera de Mónaco (achicada a 15x7 y alineada a X = 24)
        double fx = 24, fy = 44, fw = 15, fh = 3.5;
        gc.setFill(Color.rgb(206, 17, 38)); gc.fillRect(fx, fy,      fw, fh);
        gc.setFill(Color.WHITE);            gc.fillRect(fx, fy + fh, fw, fh);

        // Nombre del circuito ajustado a la izquierda
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("CIRCUIT DE MONACO", 24, 62);
        gc.fillText("MÓNACO", 24, 73);

        // Info de vueltas y kilometraje
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 8));
        gc.setFill(Color.rgb(255, 210, 50));
        gc.fillText("78 vueltas · 3.337 km", 24, 84);
    }

    private void trazarCircuito(GraphicsContext gc) {
        gc.beginPath();

        // (A) Recta meta
        gc.moveTo(340, 580);
        gc.lineTo(340, 310);

        // (B) Sainte-Dévote — horquilla a la derecha
        gc.bezierCurveTo(340, 268, 372, 248, 410, 258);

        // (C) Beau Rivage + Massenet — diagonal + curva izq en lo alto
        gc.lineTo(540, 188);
        gc.bezierCurveTo(582, 170, 628, 175, 655, 205);

        // (D) Casino + Mirabeau — arco der luego izq, baja
        gc.bezierCurveTo(690, 240, 695, 285, 658, 318);

        // (E) Loews — horquilla muy cerrada a la der
        gc.bezierCurveTo(638, 332, 622, 360, 636, 386);
        gc.bezierCurveTo(648, 410, 678, 414, 698, 400);

        // (F) Portier + Túnel — gran arco que va a la derecha
        gc.bezierCurveTo(748, 374, 820, 420, 890, 418);
        gc.bezierCurveTo(950, 416, 970, 388, 952, 362);

        // (G) Tabac — curva izquierda
        gc.bezierCurveTo(930, 330, 885, 322, 852, 342);

        // (H) Piscina — ese suave (una sola curva simplificada)
        gc.bezierCurveTo(820, 358, 798, 400, 750, 410);
        gc.bezierCurveTo(718, 417, 695, 405, 678, 418);

        // (I) Rascasse — horquilla a la derecha
        gc.bezierCurveTo(655, 432, 638, 462, 638, 492);
        gc.bezierCurveTo(638, 522, 662, 542, 694, 542);

        // (J) Anthony Noghes + cierre hacia meta
        gc.bezierCurveTo(730, 542, 748, 564, 724, 582);
        gc.bezierCurveTo(680, 608, 460, 610, 370, 592);
        gc.bezierCurveTo(356, 589, 344, 585, 340, 580);

        gc.stroke();
    }

    // ════════════════════════════════════════════════════════════════════
    //  PIT LANE API
    // ════════════════════════════════════════════════════════════════════
    public Punto getPuntoEntradaPit() { return new Punto(358, 565); }
    public Punto getPuntoSalidaPit()  { return new Punto(358, 322); }

    public Punto calcularPosicionPitLane(double t) {
        return interpolarLinea(t, new Punto(358, 565), new Punto(358, 322));
    }

    public boolean estaEnVentanaPit(double t) {
        return t >= 0.93 && t <= 0.99;
    }

    public double calcularIncrementoT(double velocidadBase, double factorVelocidad) {
        return velocidadBase * factorVelocidad * (1.0 + (Math.random() - 0.5) * 0.04);
    }

    public static double generarFactorVelocidad() {
        return 0.85 + Math.random() * 0.30;
    }

    // ════════════════════════════════════════════════════════════════════
    //  POSICIÓN EN PISTA  t ∈ [0,1]  — espeja exactamente trazarCircuito
    // ════════════════════════════════════════════════════════════════════
    @Override
    public Punto calcularPosicion(double t) {

        // (A) Recta meta
        if      (t < 0.10) return interpolarLinea(t / 0.10,
                p(340,580), p(340,310));

            // (B) Sainte-Dévote
        else if (t < 0.16) return calcularBezier((t-0.10)/0.06,
                p(340,310), p(340,268), p(372,248), p(410,258));

            // (C) Beau Rivage (recta) + Massenet (curva)
        else if (t < 0.23) return interpolarLinea((t-0.16)/0.07,
                p(410,258), p(540,188));
        else if (t < 0.28) return calcularBezier((t-0.23)/0.05,
                p(540,188), p(582,170), p(628,175), p(655,205));

            // (D) Casino + Mirabeau
        else if (t < 0.35) return calcularBezier((t-0.28)/0.07,
                p(655,205), p(690,240), p(695,285), p(658,318));

            // (E) Loews — primera mitad
        else if (t < 0.39) return calcularBezier((t-0.35)/0.04,
                p(658,318), p(638,332), p(622,360), p(636,386));
            // (E) Loews — segunda mitad
        else if (t < 0.43) return calcularBezier((t-0.39)/0.04,
                p(636,386), p(648,410), p(678,414), p(698,400));

            // (F) Portier + Túnel — primera mitad  <-- ¡CORREGIDO ACÁ!
        else if (t < 0.53) return calcularBezier((t-0.43)/0.10,
                p(698,400), p(748,374), p(820,420), p(890,418));
            // (F) Túnel — segunda mitad / salida
        else if (t < 0.59) return calcularBezier((t-0.53)/0.06,
                p(890,418), p(950,416), p(970,388), p(952,362));

            // (G) Tabac
        else if (t < 0.64) return calcularBezier((t-0.59)/0.05,
                p(952,362), p(930,330), p(885,322), p(852,342));

            // (H) Piscina S1
        else if (t < 0.69) return calcularBezier((t-0.64)/0.05,
                p(852,342), p(820,358), p(798,400), p(750,410));
            // (H) Piscina S2
        else if (t < 0.74) return calcularBezier((t-0.69)/0.05,
                p(750,410), p(718,417), p(695,405), p(678,418));

            // (I) Rascasse — primera mitad
        else if (t < 0.79) return calcularBezier((t-0.74)/0.05,
                p(678,418), p(655,432), p(638,462), p(638,492));
            // (I) Rascasse — segunda mitad
        else if (t < 0.83) return calcularBezier((t-0.79)/0.04,
                p(638,492), p(638,522), p(662,542), p(694,542));

            // (J) Anthony Noghes
        else if (t < 0.87) return calcularBezier((t-0.83)/0.04,
                p(694,542), p(730,542), p(748,564), p(724,582));

            // (J) Recta de cierre larga
        else if (t < 0.97) return calcularBezier((t-0.87)/0.10,
                p(724,582), p(680,608), p(460,610), p(370,592));

            // (J) Último tramo → meta
        else               return calcularBezier((t-0.97)/0.03,
                    p(370,592), p(356,589), p(344,585), p(340,580));
    }
    private Punto p(double x, double y) { return new Punto(x, y); }

    private Punto interpolarLinea(double t, Punto a, Punto b) {
        return new Punto(a.x + t*(b.x-a.x), a.y + t*(b.y-a.y));
    }

    private Punto calcularBezier(double t, Punto p0, Punto p1, Punto p2, Punto p3) {
        double u=1-t, tt=t*t, uu=u*u;
        return new Punto(
                uu*u*p0.x + 3*uu*t*p1.x + 3*u*tt*p2.x + tt*t*p3.x,
                uu*u*p0.y + 3*uu*t*p1.y + 3*u*tt*p2.y + tt*t*p3.y
        );
    }
}