package com.f1legends.modelo;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CircuitoMonza extends Circuito {

    public CircuitoMonza(int id, String nombre, String pais, int vueltas) {
        super(id, nombre, pais, vueltas);
    }
    // Constructor vacío para inicialización rápida
    public CircuitoMonza() {
        super(1, "Monza", "Italia", 53);
    }

    @Override
    public void dibujar(GraphicsContext gc) {
        // Calle de Boxes
        gc.setLineWidth(10);
        gc.setStroke(Color.rgb(85, 85, 85));
        gc.beginPath();
        gc.moveTo(520, 513);
        gc.lineTo(670, 513);
        gc.stroke();

        // Asfalto principal
        gc.setLineWidth(24);
        gc.setStroke(Color.rgb(55, 55, 55));
        trazarCurvasMonza(gc);

        // Pianos exteriores
        gc.setLineWidth(26);
        gc.setStroke(Color.WHITE);
        gc.setLineDashes(14, 14);
        trazarCurvasMonza(gc);
        gc.setLineDashes(0);

        // Re-pintar asfalto central
        gc.setLineWidth(22);
        gc.setStroke(Color.rgb(55, 55, 55));
        trazarCurvasMonza(gc);

        // Línea central discontinua
        gc.setLineWidth(1);
        gc.setStroke(Color.WHITE);
        gc.setLineDashes(8, 12);
        trazarCurvasMonza(gc);
        gc.setLineDashes(0);

        // Línea de Meta
        gc.setLineWidth(4);
        gc.setStroke(Color.WHITE);
        gc.setLineDashes(4, 4);
        gc.beginPath();
        gc.moveTo(630, 489);
        gc.lineTo(630, 511);
        gc.stroke();
        gc.setLineDashes(0);
    }

    private void trazarCurvasMonza(GraphicsContext gc) {
        gc.beginPath();
        gc.moveTo(630, 500);
        gc.lineTo(410, 500);
        gc.bezierCurveTo(390, 500, 385, 485, 370, 485);
        gc.bezierCurveTo(355, 485, 350, 500, 330, 500);
        gc.lineTo(260, 500);
        gc.bezierCurveTo(140, 500, 80, 440, 110, 320);
        gc.bezierCurveTo(120, 270, 100, 260, 115, 240);
        gc.bezierCurveTo(130, 220, 120, 200, 130, 150);
        gc.bezierCurveTo(140, 90, 200, 60, 240, 110);
        gc.bezierCurveTo(260, 130, 270, 130, 290, 160);
        gc.lineTo(470, 380);
        gc.bezierCurveTo(490, 405, 510, 405, 530, 390);
        gc.bezierCurveTo(555, 370, 580, 390, 600, 400);
        gc.lineTo(760, 400);
        gc.bezierCurveTo(920, 400, 920, 500, 760, 500);
        gc.lineTo(630, 500);
        gc.stroke();
    }

    @Override
    public Punto calcularPosicion(double t) {
        if (t < 0.10) {
            return interpolarLinea(t / 0.10, new Punto(630, 500), new Punto(410, 500));
        } else if (t < 0.14) {
            return calcularBezierCubic((t - 0.10) / 0.04, new Punto(410, 500), new Punto(390, 500), new Punto(385, 485), new Punto(370, 485));
        } else if (t < 0.18) {
            return calcularBezierCubic((t - 0.14) / 0.04, new Punto(370, 485), new Punto(355, 485), new Punto(350, 500), new Punto(330, 500));
        } else if (t < 0.22) {
            return interpolarLinea((t - 0.18) / 0.04, new Punto(330, 500), new Punto(260, 500));
        } else if (t < 0.38) {
            return calcularBezierCubic((t - 0.22) / 0.16, new Punto(260, 500), new Punto(140, 500), new Punto(80, 440), new Punto(110, 320));
        } else if (t < 0.44) {
            return calcularBezierCubic((t - 0.38) / 0.06, new Punto(110, 320), new Punto(120, 270), new Punto(100, 260), new Punto(115, 240));
        } else if (t < 0.50) {
            return calcularBezierCubic((t - 0.44) / 0.06, new Punto(115, 240), new Punto(130, 220), new Punto(120, 200), new Punto(130, 150));
        } else if (t < 0.58) {
            return calcularBezierCubic((t - 0.50) / 0.08, new Punto(130, 150), new Punto(140, 90), new Punto(200, 60), new Punto(240, 110));
        } else if (t < 0.64) {
            return calcularBezierCubic((t - 0.58) / 0.06, new Punto(240, 110), new Punto(260, 130), new Punto(270, 130), new Punto(290, 160));
        } else if (t < 0.76) {
            return interpolarLinea((t - 0.64) / 0.12, new Punto(290, 160), new Punto(470, 380));
        } else if (t < 0.81) {
            return calcularBezierCubic((t - 0.76) / 0.05, new Punto(470, 380), new Punto(490, 405), new Punto(510, 405), new Punto(530, 390));
        } else if (t < 0.86) {
            return calcularBezierCubic((t - 0.81) / 0.05, new Punto(530, 390), new Punto(555, 370), new Punto(580, 390), new Punto(600, 400));
        } else if (t < 0.91) {
            return interpolarLinea((t - 0.86) / 0.05, new Punto(600, 400), new Punto(760, 400));
        } else {
            return calcularBezierCubic((t - 0.91) / 0.09, new Punto(760, 400), new Punto(920, 400), new Punto(920, 500), new Punto(760, 500));
        }
    }

    private Punto interpolarLinea(double t, Punto p0, Punto p1) {
        return new Punto(p0.x + t * (p1.x - p0.x), p0.y + t * (p1.y - p0.y));
    }

    private Punto calcularBezierCubic(double t, Punto p0, Punto p1, Punto p2, Punto p3) {
        double u = 1 - t;
        double tt = t * t;
        double uu = u * u;
        double uuu = uu * u;
        double ttt = tt * t;

        double x = uuu * p0.x + 3 * uu * t * p1.x + 3 * u * tt * p2.x + ttt * p3.x;
        double y = uuu * p0.y + 3 * uu * t * p1.y + 3 * u * tt * p2.y + ttt * p3.y;

        return new Punto(x, y);
    }
}

