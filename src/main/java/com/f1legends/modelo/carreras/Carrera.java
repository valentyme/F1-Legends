package com.f1legends.modelo.carreras;

import com.f1legends.DAO.modeloDAO.TipoRuedaDAO;
import com.f1legends.modelo.TipoRueda;
import com.f1legends.modelo.auto.Auto;
import com.f1legends.modelo.circuitos.Circuito;
import com.f1legends.patrones.estado.EstadoCarrera;
import com.f1legends.patrones.estado.EstadoFinalizada;
import com.f1legends.patrones.estado.EstadoInicio;
import com.f1legends.patrones.observer.ObservableCarrera;
import com.f1legends.patrones.observer.ObservadorCarrera;
import com.f1legends.servicios.ConfiguracionCarrera;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Carrera implements ObservableCarrera {
    private int id;
    private Circuito circuito;
    private String fecha;
    private int vueltas;
    private String climaInicial;
    private List<Auto> autos;
    private EstadoCarrera estadoActual;
    private String modoJuego;
    private final List<ObservadorCarrera> observadores;
    private final Random random;
    private String climaActual;
    private int ticksDesdeEvento;
    private List<Integer> ordenAnterior;
    private List<TipoRueda> tiposRueda;

    public Carrera(int id, Circuito circuito, String fecha, int vueltas, String climaInicial) {
        this.id = id;
        this.circuito = circuito;
        this.fecha = fecha;
        this.vueltas = vueltas;
        this.climaInicial = climaInicial;
        this.autos = new ArrayList<>();
        this.estadoActual = new EstadoInicio();
        this.observadores = new ArrayList<>();
        this.random = new Random();
        this.climaActual = climaInicial;
        this.ordenAnterior = new ArrayList<>();
        this.tiposRueda = new TipoRuedaDAO().obtenerTodos();
    }

    public Carrera(ConfiguracionCarrera configuracionCarrera) {
        this.circuito = configuracionCarrera.getCircuito();
        this.vueltas = configuracionCarrera.getVueltas();
        this.climaInicial = configuracionCarrera.getClimaInicial();
        this.modoJuego = configuracionCarrera.getModoJuego();
        this.fecha = java.time.LocalDate.now().toString();
        this.autos = new ArrayList<>();
        this.estadoActual = new EstadoInicio();
        this.observadores = new ArrayList<>();
        this.random = new Random();
        this.climaActual = climaInicial;
        this.ordenAnterior = new ArrayList<>();
        this.tiposRueda = new TipoRuedaDAO().obtenerTodos();
    }

    public void iniciar() {
        estadoActual.iniciar(this);
    }

    public void actualizar(double deltaTiempo) {
        estadoActual.actualizar(this, deltaTiempo);
    }

    public void pausar() {
        estadoActual.pausar(this);
    }

    public void reanudar() {
        estadoActual.reanudar(this);
    }

    public void abandonar() {
        notificarEvento("ABANDONO", "La carrera fue abandonada.");
        estadoActual.abandonar(this);
    }

    public void cambiarEstado(EstadoCarrera nuevoEstado) {
        this.estadoActual = nuevoEstado;
        notificarEvento("CAMBIO_ESTADO", "Estado de carrera: " + nuevoEstado.getNombre());
        if (nuevoEstado instanceof EstadoFinalizada) {
            notificarEvento("FINALIZACION", "La carrera finalizo.");
        }
    }

    public void agregarAuto(Auto auto) {
        if (auto.getTipoRuedaActual() == null) {
            auto.setTipoRuedaActual(ruedaInicial());
        }
        autos.add(auto);
        ordenAnterior = idsEnOrden();
        notificarEvento("AUTO_AGREGADO", "Auto agregado a la carrera: " + auto.getModelo());
    }

    public void avanzarAutos(double deltaTiempo) {
        List<Integer> ordenAntes = idsEnOrden();

        for (Auto auto : autos) {
            int vueltasAntes = auto.getVueltasCompletadas();
            aplicarDesgaste(auto, deltaTiempo);
            auto.avanzar(deltaTiempo);
            if (auto.getVueltasCompletadas() > vueltasAntes) {
                notificarEvento(
                        "VUELTA_COMPLETADA",
                        auto.getModelo() + " completo la vuelta " + auto.getVueltasCompletadas(),
                        auto.getVueltasCompletadas()
                );
            }
        }

        detectarCambiosPosicion(ordenAntes);
        procesarEventosDinamicos();
    }

    public boolean estaCompletada() {
        boolean llegoAlguien = autos.stream()
                .filter(auto -> !auto.isFueraCarrera())
                .anyMatch(auto -> auto.getVueltasCompletadas() >= vueltas);
        boolean quedanAutos = autos.stream().anyMatch(auto -> !auto.isFueraCarrera());
        return llegoAlguien || !quedanAutos;
    }

    public List<Auto> getPosiciones() {
        return autos.stream()
                .sorted(Comparator
                        .comparing(Auto::isFueraCarrera)
                        .thenComparing(Comparator.comparingInt(Auto::getVueltasCompletadas).reversed())
                        .thenComparing(Comparator.comparingDouble(Auto::getProgreso).reversed()))
                .toList();
    }

    private void aplicarDesgaste(Auto auto, double deltaTiempo) {
        if (auto.isFueraCarrera()) {
            return;
        }

        double factorClima = switch (climaActual) {
            case "Lluvioso" -> 0.7;
            case "Nublado" -> 0.9;
            default -> 1.25;
        };
        auto.aumentarDesgaste(deltaTiempo * 7.5 * factorClima);
    }

    private void procesarEventosDinamicos() {
        ticksDesdeEvento++;
        if (ticksDesdeEvento < 70) {
            return;
        }
        ticksDesdeEvento = 0;

        List<Auto> activos = autos.stream()
                .filter(auto -> !auto.isFueraCarrera())
                .toList();
        if (activos.isEmpty()) {
            return;
        }

        if (random.nextDouble() < probabilidadCambioClima()) {
            cambiarClimaAleatorio();
        }

        for (Auto auto : activos) {
            if (!auto.estaDetenido()
                    && auto.getParadasBoxes() < 2
                    && auto.getDesgasteNeumaticos() > 78
                    && random.nextDouble() < probabilidadBoxes(auto)) {
                entrarABoXes(auto);
            }
        }

        if (activos.size() >= 2 && random.nextDouble() < probabilidadAccidente()) {
            generarAccidente(activos);
        }
    }

    private double probabilidadBoxes(Auto auto) {
        double base = switch (climaActual) {
            case "Lluvioso" -> 0.018;
            case "Nublado" -> 0.014;
            default -> 0.010;
        };
        return base + (auto.getDesgasteNeumaticos() / 6000.0);
    }

    private double probabilidadAccidente() {
        return switch (climaActual) {
            case "Lluvioso" -> 0.025;
            case "Nublado" -> 0.012;
            default -> 0.006;
        };
    }

    private double probabilidadCambioClima() {
        return 0.035;
    }

    private void entrarABoXes(Auto auto) {
        TipoRueda ruedaNueva = elegirRuedaParaClima();
        double demora = switch (climaActual) {
            case "Lluvioso" -> 2.0 + random.nextDouble() * 2.0;
            default -> 1.4 + random.nextDouble() * 1.6;
        };
        auto.setTipoRuedaActual(ruedaNueva);
        auto.entrarABoxes(demora);
        auto.reducirDesgaste(85);
        notificarEvento("BOXES", auto.getModelo() + " entra a boxes por "
                + nombreRueda(ruedaNueva) + " (" + String.format("%.1f", demora) + "s)");
    }

    private TipoRueda ruedaInicial() {
        return tiposRueda.stream()
                .filter(rueda -> "Media".equalsIgnoreCase(rueda.getNombre()))
                .findFirst()
                .orElse(tiposRueda.isEmpty() ? null : tiposRueda.get(0));
    }

    private TipoRueda elegirRuedaParaClima() {
        if ("Lluvioso".equals(climaActual)) {
            return tiposRueda.stream()
                    .filter(rueda -> rueda.getNombre().equalsIgnoreCase("Intermedia")
                            || rueda.getNombre().equalsIgnoreCase("Lluvia Extrema"))
                    .findAny()
                    .orElse(ruedaInicial());
        }

        return tiposRueda.stream()
                .filter(rueda -> rueda.getNombre().equalsIgnoreCase("Blanda")
                        || rueda.getNombre().equalsIgnoreCase("Media")
                        || rueda.getNombre().equalsIgnoreCase("Dura"))
                .skip(random.nextInt(Math.max(1, (int) tiposRueda.stream()
                        .filter(rueda -> rueda.getNombre().equalsIgnoreCase("Blanda")
                                || rueda.getNombre().equalsIgnoreCase("Media")
                                || rueda.getNombre().equalsIgnoreCase("Dura"))
                        .count())))
                .findFirst()
                .orElse(ruedaInicial());
    }

    private String nombreRueda(TipoRueda rueda) {
        return rueda == null ? "rueda nueva" : rueda.getNombre();
    }

    private void generarAccidente(List<Auto> activos) {
        List<Auto> posiciones = getPosiciones().stream()
                .filter(auto -> !auto.isFueraCarrera())
                .toList();
        if (posiciones.size() < 2) {
            return;
        }

        int indice = random.nextInt(posiciones.size() - 1);
        Auto primero = posiciones.get(indice);
        Auto segundo = posiciones.get(indice + 1);

        primero.retirar();
        segundo.retirar();
        notificarEvento("ACCIDENTE", "Colision entre " + primero.getModelo()
                + " y " + segundo.getModelo() + ": ambos abandonan la carrera");
    }

    private void cambiarClimaAleatorio() {
        String nuevoClima;
        do {
            nuevoClima = switch (random.nextInt(3)) {
                case 0 -> "Soleado";
                case 1 -> "Nublado";
                default -> "Lluvioso";
            };
        } while (nuevoClima.equals(climaActual));

        climaActual = nuevoClima;
        notificarEvento("CAMBIO_CLIMA", "El clima cambia a " + climaActual);
    }

    private void detectarCambiosPosicion(List<Integer> ordenAntes) {
        List<Integer> ordenDespues = idsEnOrden();
        if (!ordenAntes.isEmpty() && !ordenDespues.equals(ordenAntes)) {
            notificarEvento("ADELANTAMIENTO", "Cambio de posiciones en pista");
        }
        ordenAnterior = ordenDespues;
    }

    private List<Integer> idsEnOrden() {
        return getPosiciones().stream()
                .map(Auto::getId)
                .toList();
    }

    @Override
    public void agregarObservador(ObservadorCarrera observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    @Override
    public void eliminarObservador(ObservadorCarrera observador) {
        observadores.remove(observador);
    }

    @Override
    public void notificarObservadores(EventoCarrera evento) {
        for (ObservadorCarrera observador : List.copyOf(observadores)) {
            observador.actualizar(evento, this);
        }
    }

    public void notificarEvento(String tipo, String descripcion) {
        notificarEvento(tipo, descripcion, getVueltaActual());
    }

    public void notificarEvento(String tipo, String descripcion, int vuelta) {
        notificarObservadores(new EventoCarrera(tipo, descripcion, vuelta));
    }

    private int getVueltaActual() {
        return autos.stream()
                .mapToInt(Auto::getVueltasCompletadas)
                .max()
                .orElse(0);
    }

    public int getId() { return id; }
    public Circuito getCircuito() { return circuito; }
    public String getFecha() { return fecha; }
    public int getVueltas() { return vueltas; }
    public String getClimaInicial() { return climaInicial; }
    public String getClimaActual() { return climaActual; }
    public List<Auto> getAutos() { return autos; }
    public EstadoCarrera getEstadoActual() { return estadoActual; }
    public String getNombreEstado() { return estadoActual.getNombre(); }
    public String getModoJuego() { return modoJuego; }
}
