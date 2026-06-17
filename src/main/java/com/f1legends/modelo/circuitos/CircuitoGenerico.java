package com.f1legends.modelo.circuitos;

import com.f1legends.modelo.Punto;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class CircuitoGenerico extends Circuito {
    public CircuitoGenerico(int id, String nombre, String pais, int vueltas) {
        super(id, nombre, pais, vueltas);
    }

    @Override
    public void dibujar(GraphicsContext gc) {
        dibujarFondo(gc);
        gc.setLineWidth(32);
        gc.setStroke(Color.rgb(210, 30, 30));
        trazarCircuito(gc);
        gc.setLineWidth(24);
        gc.setStroke(Color.rgb(42, 42, 46));
        trazarCircuito(gc);
        gc.setLineWidth(14);
        gc.setStroke(Color.rgb(56, 56, 62));
        trazarCircuito(gc);
        gc.setLineWidth(1.2);
        gc.setStroke(Color.rgb(255, 255, 255, 0.45));
        gc.setLineDashes(10, 12);
        trazarCircuito(gc);
        gc.setLineDashes(0);
        dibujarMeta(gc);
        dibujarInfo(gc);
    }

    @Override
    public Punto calcularPosicion(double t) {
        t = ((t % 1.0) + 1.0) % 1.0;
        if (t < 0.20) return interp(t / 0.20, p(690, 170), p(280, 170));
        if (t < 0.35) return bezier((t - 0.20) / 0.15, p(280, 170), p(150, 170), p(120, 300), p(220, 370));
        if (t < 0.55) return interp((t - 0.35) / 0.20, p(220, 370), p(600, 370));
        if (t < 0.72) return bezier((t - 0.55) / 0.17, p(600, 370), p(800, 370), p(825, 235), p(690, 170));
        if (t < 0.86) return bezier((t - 0.72) / 0.14, p(690, 170), p(610, 110), p(500, 115), p(455, 210));
        return bezier((t - 0.86) / 0.14, p(455, 210), p(410, 305), p(560, 315), p(690, 170));
    }

    private void dibujarFondo(GraphicsContext gc) {
        int tile = 40;
        for (int col = 0; col * tile < 1080; col++) {
            for (int row = 0; row * tile < 700; row++) {
                gc.setFill((col + row) % 2 == 0 ? Color.rgb(30, 88, 30) : Color.rgb(44, 108, 44));
                gc.fillRect(col * tile, row * tile, tile, tile);
            }
        }
    }

    private void trazarCircuito(GraphicsContext gc) {
        gc.beginPath();
        gc.moveTo(690, 170);
        gc.lineTo(280, 170);
        gc.bezierCurveTo(150, 170, 120, 300, 220, 370);
        gc.lineTo(600, 370);
        gc.bezierCurveTo(800, 370, 825, 235, 690, 170);
        gc.bezierCurveTo(610, 110, 500, 115, 455, 210);
        gc.bezierCurveTo(410, 305, 560, 315, 690, 170);
        gc.stroke();
    }

    private void dibujarMeta(GraphicsContext gc) {
        double x = 680;
        double y1 = 156;
        double cell = 4;
        for (int col = 0; col < 5; col++) {
            for (int row = 0; row < 7; row++) {
                gc.setFill((col + row) % 2 == 0 ? Color.WHITE : Color.rgb(15, 15, 15));
                gc.fillRect(x + col * cell, y1 + row * cell, cell, cell);
            }
        }
    }

    private void dibujarInfo(GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 0, 0, 0.60));
        gc.fillRoundRect(60, 38, 230, 62, 8, 8);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText(getNombre().toUpperCase(), 64, 65);
        gc.fillText(getPais().toUpperCase(), 64, 77);
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 8));
        gc.setFill(Color.rgb(255, 210, 50));
        gc.fillText(getVueltas() + " vueltas", 64, 90);
    }

    private Punto p(double x, double y) {
        return new Punto(x, y);
    }

    private Punto interp(double t, Punto a, Punto b) {
        return new Punto(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t);
    }

    private Punto bezier(double t, Punto p0, Punto p1, Punto p2, Punto p3) {
        double u = 1 - t;
        double x = u*u*u*p0.x + 3*u*u*t*p1.x + 3*u*t*t*p2.x + t*t*t*p3.x;
        double y = u*u*u*p0.y + 3*u*u*t*p1.y + 3*u*t*t*p2.y + t*t*t*p3.y;
        return new Punto(x, y);
    }
}
