package com.f1legends.modelo;

public class Auto {
    private int id;
    private String modelo;
    private double velocidadBase;
    private Escuderia escuderia;
    private double progreso;

    // ── Factor aleatorio único por auto ───────────────────────────────────
    // Se genera una sola vez al construir el objeto.
    // Rango: 0.80 (muy lento) … 1.20 (muy rápido)
    // Así cada auto tiene su propio "talento" en esta carrera.
    private final double factorAleatorio;

    // Micro-variación acumulada (simula frenadas, tráfico, curvas)
    private double variacionMomento;
    private int ticksHastaProximaVariacion;

    public Auto(int id, String modelo, double velocidadBase, Escuderia escuderia) {
        this.id           = id;
        this.modelo       = modelo;
        this.velocidadBase = velocidadBase;
        this.escuderia    = escuderia;
        this.progreso     = 0.0;

        // Factor fijo por carrera: define quién es rápido y quién lento
        this.factorAleatorio = 0.80 + Math.random() * 0.40;

        // Empieza con variación neutra
        this.variacionMomento = 1.0;
        this.ticksHastaProximaVariacion = 0;
    }

    // ── Avance con velocidad aleatoria ───────────────────────────────────
    /**
     * Avanza el auto en el circuito.
     *
     * La velocidad real en cada frame es:
     *   velocidadBase × factorAleatorio × variacionMomento
     *
     * - factorAleatorio: fijo por carrera, define la "capacidad" del auto
     * - variacionMomento: cambia cada ~30 ticks, simula batallas en pista
     */
    public void avanzar(double deltaTiempo) {
        actualizarVariacionMomento();

        double velocidadReal = velocidadBase * factorAleatorio * variacionMomento;
        progreso += velocidadReal * deltaTiempo;
        if (progreso > 1.0) progreso -= 1.0;
    }

    private void actualizarVariacionMomento() {
        ticksHastaProximaVariacion--;
        if (ticksHastaProximaVariacion <= 0) {
            // Nueva variación: ±15% respecto a 1.0
            variacionMomento = 0.85 + Math.random() * 0.30;
            // Próximo cambio en 20-50 ticks (~0.3-0.8 seg a 60fps)
            ticksHastaProximaVariacion = 20 + (int)(Math.random() * 30);
        }
    }

    // ── Getters ───────────────────────────────────────────────────────────
    public int getId()               { return id; }
    public String getModelo()        { return modelo; }
    public double getVelocidadBase() { return velocidadBase; }
    public Escuderia getEscuderia()  { return escuderia; }
    public double getProgreso()      { return progreso; }
    public double getFactorAleatorio() { return factorAleatorio; }

    /** Velocidad efectiva actual (útil para mostrar en UI) */
    public double getVelocidadEfectiva() {
        return velocidadBase * factorAleatorio * variacionMomento;
    }

    // ── Setters ───────────────────────────────────────────────────────────
    public void setModelo(String modelo)             { this.modelo = modelo; }
    public void setVelocidadBase(double velocidadBase) { this.velocidadBase = velocidadBase; }
    public void setEscuderia(Escuderia escuderia)    { this.escuderia = escuderia; }
    public void setProgreso(double progreso)         { this.progreso = progreso; }
}