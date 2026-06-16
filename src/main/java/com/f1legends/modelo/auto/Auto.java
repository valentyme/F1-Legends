package com.f1legends.modelo.auto;

import com.f1legends.modelo.Escuderias.Escuderia;
import com.f1legends.patrones.factory.TipoAuto;

public class Auto {
    private int id;
    private String modelo;
    private double velocidadBase;
    private Escuderia escuderia;
    private TipoAuto tipoAuto;
    private double progreso;
    private int vueltasCompletadas;
    private boolean fueraCarrera;
    private double tiempoDetenido;
    private double desgasteNeumaticos;

    private final double factorAleatorio;
    private double variacionMomento;
    private int ticksHastaProximaVariacion;

    /** Multiplicador aplicado por la estrategia de conducción del piloto (1.0 = sin efecto). */
    private double factorEstrategia = 1.0;

    public Auto(int id, String modelo, double velocidadBase, Escuderia escuderia) {
        this(id, modelo, velocidadBase, escuderia, TipoAuto.REGLAMENTO_2024);
    }

    public Auto(int id, String modelo, double velocidadBase, Escuderia escuderia, TipoAuto tipoAuto) {
        this.id = id;
        this.modelo = modelo;
        this.velocidadBase = velocidadBase;
        this.escuderia = escuderia;
        this.tipoAuto = tipoAuto;
        this.progreso = 0.0;
        this.vueltasCompletadas = 0;
        this.factorAleatorio = 0.80 + Math.random() * 0.40;
        this.variacionMomento = 1.0;
        this.ticksHastaProximaVariacion = 0;
    }

    public void avanzar(double deltaTiempo) {
        if (fueraCarrera) {
            return;
        }
        if (tiempoDetenido > 0) {
            tiempoDetenido = Math.max(0, tiempoDetenido - deltaTiempo);
            return;
        }

        actualizarVariacionMomento();

        double velocidadReal = velocidadBase * factorAleatorio * variacionMomento * factorEstrategia;
        progreso += velocidadReal * deltaTiempo;

        while (progreso >= 1.0) {
            progreso -= 1.0;
            vueltasCompletadas++;
        }
    }

    private void actualizarVariacionMomento() {
        ticksHastaProximaVariacion--;
        if (ticksHastaProximaVariacion <= 0) {
            variacionMomento = 0.85 + Math.random() * 0.30;
            ticksHastaProximaVariacion = 20 + (int) (Math.random() * 30);
        }
    }

    public int getId() { return id; }
    public String getModelo() { return modelo; }
    public double getVelocidadBase() { return velocidadBase; }
    public Escuderia getEscuderia() { return escuderia; }
    public TipoAuto getTipoAuto() { return tipoAuto; }
    public double getProgreso() { return progreso; }
    public int getVueltasCompletadas() { return vueltasCompletadas; }
    public double getFactorAleatorio() { return factorAleatorio; }
    public boolean isFueraCarrera() { return fueraCarrera; }
    public double getTiempoDetenido() { return tiempoDetenido; }
    public double getDesgasteNeumaticos() { return desgasteNeumaticos; }

    public double getVelocidadEfectiva() {
        return velocidadBase * factorAleatorio * variacionMomento * factorEstrategia;
    }

    public double getFactorEstrategia() { return factorEstrategia; }
    public void setFactorEstrategia(double factorEstrategia) { this.factorEstrategia = factorEstrategia; }

    public void setModelo(String modelo) { this.modelo = modelo; }
    public void setVelocidadBase(double velocidadBase) { this.velocidadBase = velocidadBase; }
    public void setEscuderia(Escuderia escuderia) { this.escuderia = escuderia; }
    public void setTipoAuto(TipoAuto tipoAuto) { this.tipoAuto = tipoAuto; }
    public void setProgreso(double progreso) { this.progreso = progreso; }

    public void detener(double segundos) {
        if (!fueraCarrera) {
            tiempoDetenido = Math.max(tiempoDetenido, segundos);
        }
    }

    public void retirar() {
        fueraCarrera = true;
        tiempoDetenido = 0;
    }

    // Si el auto está detenido por alguna razón (accidente, etc.)
    public boolean estaDetenido() {
        return tiempoDetenido > 0;  // el nombre exacto del campo depende de tu implementación
    }

    public void aumentarDesgaste(double cantidad) {
        desgasteNeumaticos = Math.min(100, desgasteNeumaticos + cantidad);
    }

    public void reducirDesgaste(double cantidad) {
        desgasteNeumaticos = Math.max(0, desgasteNeumaticos - cantidad);
    }
}
