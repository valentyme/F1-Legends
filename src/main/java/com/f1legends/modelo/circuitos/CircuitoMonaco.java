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

    private void dibujarPitLaneCarril(GraphicsContext gc) {
        gc.setLineWidth(12);
        gc.setStroke(Color.rgb(70, 70, 78));
        gc.beginPath();
        gc.moveTo(280, 520); // Entrada en la Rascasse
        gc.lineTo(160, 400);
        gc.lineTo(260, 180); // Salida después de Sainte-Dévote
        gc.stroke();
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
        gc.setLineWidth(3.0);
        gc.setStroke(Color.rgb(22, 22, 25, 0.45));
        gc.setLineDashes(24, 60); trazarCircuito(gc); gc.setLineDashes(0);
        gc.setLineWidth(1.0);
        gc.setStroke(Color.rgb(255, 255, 255, 0.40));
        gc.setLineDashes(8, 12);  trazarCircuito(gc); gc.setLineDashes(0);
    }

    private void dibujarPitLaneDetalle(GraphicsContext gc) {
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 8));
        gc.setFill(Color.rgb(255, 200, 0, 0.9));
        gc.fillText("▼ PIT IN", 290, 530);
        gc.fillText("PIT OUT ▲", 270, 170);
    }

    // Línea de meta en la recta principal (Subiendo por el lateral izquierdo)
    private void dibujarLineaMeta(GraphicsContext gc) {
        double metaX = 145, trackY1 = 360, trackY2 = 385;
        double cellW = 3.5, cellH = 3.5;
        int cols = 5;
        for (int col = 0; col < cols; col++) {
            int filas = (int) Math.ceil((trackY2 - trackY1) / cellH);
            for (int row = 0; row < filas; row++) {
                gc.setFill((col + row) % 2 == 0 ? META_BLANCA : META_NEGRA);
                double cy = trackY1 + row * cellH;
                gc.fillRect(metaX + col * cellW, cy, cellW, Math.min(cellH, trackY2 - cy));
            }
        }
    }

    private void dibujarInfoCircuito(GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 0, 0, 0.65));
        gc.fillRoundRect(50, 40, 165, 62, 8, 8);

        gc.setFill(Color.rgb(206, 17, 38));  gc.fillRect(54, 44, 21, 10);
        gc.setFill(Color.WHITE);             gc.fillRect(54, 54, 21, 10);

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        gc.setFill(Color.WHITE);
        gc.fillText("CIRCUIT DE MONACO", 54, 77);
        gc.fillText("MÓNACO", 54, 89);
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 8));
        gc.setFill(Color.rgb(255, 210, 50));
        gc.fillText("78 vueltas · 3.337 km", 54, 102);
    }

    // ════════════════════════════════════════════════════════════════════
    //  TRAZADO OFICIAL DE MÓNACO (Fiel a la silueta de la FIA)
    // ════════════════════════════════════════════════════════════════════
    private void trazarCircuito(GraphicsContext gc) {
        gc.beginPath();

        // 1. Recta de Meta (Subiendo en diagonal por la izquierda, curvas 19 a 1)
        gc.moveTo(115, 480);
        gc.lineTo(260, 150);

        // 2. Curva 1 (Sainte-Dévote): giro cerrado a la derecha
        gc.bezierCurveTo(290, 120, 310, 130, 330, 160);

        // 3. Subida del Beau Rivage hacia Massenet (Curva 2 y 3)
        gc.lineTo(460, 260);
        gc.bezierCurveTo(500, 290, 560, 280, 600, 240);

        // 4. Plaza del Casino (Curva 4) y bajada a Mirabeau (Curva 5)
        gc.bezierCurveTo(630, 210, 660, 230, 690, 260);
        gc.lineTo(760, 310);

        // 5. Horquilla de Loews / Fairmont (Curva 6) - Cerradísima a la izquierda
        gc.bezierCurveTo(790, 330, 810, 360, 780, 380);
        gc.bezierCurveTo(740, 400, 730, 350, 750, 330);

        // 6. Curvas del Portier (7 y 8) apuntando hacia la entrada del Túnel
        gc.lineTo(820, 390);
        gc.bezierCurveTo(850, 420, 880, 400, 860, 440);

        // 7. El Túnel (Gran arco largo y veloz hacia abajo y a la izquierda)
        gc.bezierCurveTo(800, 550, 630, 640, 490, 580);

        // 8. Nouvelle Chicane (Curvas 10 y 11) y recta de Bureau de Tabac (Curva 12)
        gc.bezierCurveTo(450, 560, 430, 540, 410, 550);
        gc.lineTo(320, 590);

        // 9. Sección de la Piscina (Chicanes rápidas de Louis Chiron: 13, 14, 15 y 16)
        gc.bezierCurveTo(280, 610, 240, 590, 220, 550);
        gc.bezierCurveTo(210, 520, 230, 500, 210, 470);

        // 10. La Rascasse y Anthony Noghes (Curvas 17, 18 y 19) para volver a Meta
        gc.bezierCurveTo(190, 440, 140, 430, 115, 480);

        gc.stroke();
    }

    // ════════════════════════════════════════════════════════════════════
    //  CÁLCULO DE POSICIÓN DE LOS MONOPLAZAS (t ∈ [0,1])
    // ════════════════════════════════════════════════════════════════════
    @Override
    public Punto calcularPosicion(double t) {
        // Tramo 1: Recta Principal / Meta
        if      (t < 0.15) return interp(t / 0.15, p(115, 480), p(260, 150));

            // Tramo 2: Sainte-Dévote (Curva 1)
        else if (t < 0.22) return bezier((t - 0.15) / 0.07, p(260, 150), p(290, 120), p(310, 130), p(330, 160));

            // Tramo 3: Beau Rivage y Massenet (Curvas 2 y 3)
        else if (t < 0.32) return interp((t - 0.22) / 0.10, p(330, 160), p(460, 260));
        else if (t < 0.40) return bezier((t - 0.32) / 0.08, p(460, 260), p(500, 290), p(560, 280), p(600, 240));

            // Tramo 4: Casino y Mirabeau (Curvas 4 y 5)
        else if (t < 0.46) return bezier((t - 0.40) / 0.06, p(600, 240), p(630, 210), p(660, 230), p(690, 260));
        else if (t < 0.52) return interp((t - 0.46) / 0.06, p(690, 260), p(760, 310));

            // Tramo 5: Horquilla de Loews (Curva 6)
        else if (t < 0.56) return bezier((t - 0.52) / 0.04, p(760, 310), p(790, 330), p(810, 360), p(780, 380));
        else if (t < 0.60) return bezier((t - 0.56) / 0.04, p(780, 380), p(740, 400), p(730, 350), p(750, 330));

            // Tramo 6: Portier (Curvas 7 y 8)
        else if (t < 0.65) return interp((t - 0.60) / 0.05, p(750, 330), p(820, 390));
        else if (t < 0.70) return bezier((t - 0.65) / 0.05, p(820, 390), p(850, 420), p(880, 400), p(860, 440));

            // Tramo 7: El Túnel largo peraltado
        else if (t < 0.82) return bezier((t - 0.70) / 0.12, p(860, 440), p(800, 550), p(630, 640), p(490, 580));

            // Tramo 8: Nouvelle Chicane y Tabac (Curvas 10, 11 y 12)
        else if (t < 0.86) return bezier((t - 0.82) / 0.04, p(490, 580), p(450, 560), p(430, 540), p(410, 550));
        else if (t < 0.90) return interp((t - 0.86) / 0.04, p(410, 550), p(320, 590));

            // Tramo 9: Complejo de las Piscinas (Curvas 13 a 16)
        else if (t < 0.94) return bezier((t - 0.90) / 0.04, p(320, 590), p(280, 610), p(240, 590), p(220, 550));
        else if (t < 0.97) return bezier((t - 0.94) / 0.03, p(220, 550), p(210, 520), p(230, 500), p(210, 470));

            // Tramo 10: Cierre por Rascasse y Anthony Noghes (Curvas 17 a 19)
        else               return bezier((t - 0.97) / 0.03, p(210, 470), p(190, 440), p(140, 430), p(115, 480));
    }

    private Punto p(double x, double y) {
        return new Punto(x, y);
    }

    private Punto interp(double t, Punto a, Punto b) {
        return new Punto(a.x + t * (b.x - a.x), a.y + t * (b.y - a.y));
    }

    private Punto bezier(double t, Punto p0, Punto p1, Punto p2, Punto p3) {
        double u = 1 - t, tt = t * t, uu = u * u;
        return new Punto(
                uu * u * p0.x + 3 * uu * t * p1.x + 3 * u * tt * p2.x + tt * t * p3.x,
                uu * u * p0.y + 3 * uu * t * p1.y + 3 * u * tt * p2.y + tt * t * p3.y
        );
    }
}