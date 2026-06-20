package com.f1legends.vista;

import com.f1legends.DAO.modeloDAO.CircuitoDTO;
import com.f1legends.DAO.modeloDAO.PilotoDAO;
import com.f1legends.DAO.modeloDAO.RankingGlobalDAO;
import com.f1legends.controller.PreparadorGrilla;
import com.f1legends.controller.RankingController;
import com.f1legends.modelo.Piloto;
import com.f1legends.modelo.Usuarios.Jugador;
import com.f1legends.modelo.Usuarios.Participante;
import com.f1legends.modelo.auto.Auto;
import com.f1legends.modelo.carreras.Carrera;
import com.f1legends.patrones.estrategias.EstrategiaEquilibrada;
import com.f1legends.patrones.facade.SistemaCarreraFacade;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main extends Application {

    private final PreparadorGrilla preparadorGrilla = new PreparadorGrilla();

    @Override
    public void start(Stage stage) {

        SistemaCarreraFacade facade = new SistemaCarreraFacade();


        Jugador jugador = new Jugador(1, "valentyme", "1234", "19/06/2026");
        facade.getConfiguracionCarrera().setJugadorPrincipal(jugador);
        facade.seleccionarModoJuego("Singleplayer");


        List<Piloto> pilotos = PilotoDAO.obtenerTodos();
        if (pilotos.isEmpty()) {
            throw new IllegalStateException("No hay pilotos cargados en la base de datos.");
        }
        Piloto pilotoJugador = pilotos.get(0);

        facade.seleccionarPiloto(pilotoJugador.getId());
        facade.seleccionarEstrategiaConduccion(new EstrategiaEquilibrada());

        var autoDAO = new com.f1legends.DAO.modeloDAO.AutoDAO();
        Auto autoJugador = autoDAO.obtenerTodosPorEscuderiaId(pilotoJugador.getEscuderiaId())
                .stream()
                .findFirst()
                .orElse(null);
        if (autoJugador == null) {
            throw new IllegalStateException(
                    "El piloto " + pilotoJugador.getNombre() + " no tiene autos cargados en su escudería.");
        }

        List<Participante> participantes = new ArrayList<>();
        participantes.add(new Participante(jugador, pilotoJugador, copiarAutoParaCarrera(
                autoJugador, jugador.getId(), pilotoJugador.getNombre())));
        facade.getConfiguracionCarrera().setParticipantes(participantes);


        if (!completarGrillaConAutosDisponibles(facade, autoJugador, 5)) {
            throw new IllegalStateException(
                    "No hay suficientes pilotos con auto cargado en la base para completar la grilla.");
        }


        List<CircuitoDTO> circuitos = new com.f1legends.DAO.modeloDAO.CircuitoDAO().obtenerTodos();
        if (circuitos.isEmpty()) {
            throw new IllegalStateException("No hay circuitos cargados en la base de datos.");
        }
        CircuitoDTO circuitoDto = circuitos.get(3);
        facade.seleccionarCircuito(circuitoDto.getId());

        int vueltas = Math.min(3, Math.max(1, circuitoDto.getVueltas()));
        facade.configurarCarrera(vueltas, "Soleado");

        Carrera carrera = facade.iniciarCarrera();
        if (!preparadorGrilla.prepararAutosParticipantes(carrera, facade)) {
            throw new IllegalStateException("No se pudo preparar la grilla de la carrera.");
        }
        carrera.iniciar();


        RankingController rankingController = new RankingController(new RankingGlobalDAO());
        CarreraFXWindow ventana = new CarreraFXWindow(
                carrera,
                jugador,
                facade.getConfiguracionCarrera().getParticipantes(),
                rankingController
        );
        ventana.mostrar(stage);
    }


    private Auto copiarAutoParaCarrera(Auto auto, int idCarrera, String pilotoNombre) {
        return new Auto(
                idCarrera,
                pilotoNombre + " / " + auto.getModelo(),
                velocidadParaSimulacion(auto.getVelocidadBase()),
                auto.getEscuderia(),
                auto.getTipoAuto()
        );
    }

    private double velocidadParaSimulacion(double velocidadBase) {
        return velocidadBase > 1.0 ? velocidadBase / 5000.0 : velocidadBase;
    }

    private boolean completarGrillaConAutosDisponibles(SistemaCarreraFacade facade, Auto autoJugador,
                                                       int minimoParticipantes) {
        List<Participante> participantes = facade.getConfiguracionCarrera().getParticipantes();
        if (participantes == null || minimoParticipantes < 2) {
            return false;
        }

        Set<Integer> pilotosUsados = new HashSet<>();
        for (Participante participante : participantes) {
            pilotosUsados.add(participante.getPiloto().getId());
        }

        var autoDAO = new com.f1legends.DAO.modeloDAO.AutoDAO();

        List<Piloto> pilotosCpu = PilotoDAO.obtenerTodos().stream()
                .filter(piloto -> !pilotosUsados.contains(piloto.getId()))
                .filter(piloto -> !autoDAO.obtenerTodosPorEscuderiaId(piloto.getEscuderiaId()).isEmpty())
                .sorted(Comparator.comparingInt(Piloto::getHabilidad).reversed())
                .toList();

        if (pilotosCpu.size() + participantes.size() < minimoParticipantes) {
            return false;
        }

        int cpuId = -1;
        for (Piloto pilotoCpu : pilotosCpu) {
            if (participantes.size() >= minimoParticipantes) {
                break;
            }
            Auto autoCpu = autoDAO.obtenerTodosPorEscuderiaId(pilotoCpu.getEscuderiaId())
                    .stream()
                    .filter(a -> a.getId() != autoJugador.getId())
                    .findFirst()
                    .orElse(null);
            if (autoCpu == null) {
                continue;
            }
            Jugador jugadorCpu = new Jugador(cpuId, "CPU - " + pilotoCpu.getNombre(), "", "CPU");
            participantes.add(new Participante(
                    jugadorCpu,
                    pilotoCpu,
                    copiarAutoParaCarrera(autoCpu, cpuId, pilotoCpu.getNombre())
            ));
            cpuId--;
        }

        return participantes.size() >= minimoParticipantes;
    }

    public static void main(String[] args) {
        launch(args);
    }
}