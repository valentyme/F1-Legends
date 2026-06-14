package com.f1legends.controller;

import com.f1legends.DAO.modeloDAO.AutoDAO;
import com.f1legends.DAO.modeloDAO.EscuderiaDAO;
import com.f1legends.DAO.modeloDAO.PilotoDAO;
import com.f1legends.modelo.Escuderias.Escuderia;
import com.f1legends.modelo.Piloto;
import com.f1legends.modelo.auto.Auto;
import com.f1legends.patrones.facade.SistemaCarreraFacade;

import java.util.List;
import java.util.Scanner;

import static com.f1legends.vista.ConsoleUI.*;

/**
 * Controlador de CU21 — Gestión de pilotos (alta, baja, modificación, consulta).
 */
public class PilotoController {

    private final SistemaCarreraFacade facade;
    private final AutoDAO autoDAO;
    private final Scanner sc;

    public PilotoController(SistemaCarreraFacade facade, AutoDAO autoDAO, Scanner sc) {
        this.facade = facade;
        this.autoDAO = autoDAO;
        this.sc = sc;
    }

    // ════════════════════════════════════════════════
    // CU21 — Gestión de pilotos
    // ════════════════════════════════════════════════
    public void cuGestionarPilotos() {
        linea();
        titulo("  GESTIONAR PILOTOS (CU21)");
        linea();
        System.out.println("  [1] Alta de piloto");
        System.out.println("  [2] Baja de piloto");
        System.out.println("  [3] Modificar piloto");
        System.out.println("  [4] Consultar piloto");
        System.out.print("  Opción: ");
        String opcion = sc.nextLine().trim();

        Piloto piloto;
        switch (opcion) {
            case "1" -> {
                System.out.print("Nombre del piloto: ");
                String nombre = sc.nextLine();
                System.out.print("Habilidad: ");
                int habilidad = Integer.parseInt(sc.nextLine());
                mostrarEscuderias();
                System.out.print("ID de escudería: ");
                int escuderiaId = Integer.parseInt(sc.nextLine());

                piloto = altaPiloto(nombre, habilidad, escuderiaId);
                mostrarPiloto(piloto);
            }
            case "2" -> {
                mostrarPilotos(); // mostrar antes de eliminar
                System.out.print("ID del piloto a eliminar: ");
                int id = Integer.parseInt(sc.nextLine());
                bajaPiloto(id);
            }
            case "3" -> {
                mostrarPilotos(); // mostrar antes de modificar
                System.out.print("ID del piloto a modificar: ");
                int id = Integer.parseInt(sc.nextLine());
                System.out.print("Nuevo nombre: ");
                String nombre = sc.nextLine();
                System.out.print("Nueva habilidad: ");
                int habilidad = Integer.parseInt(sc.nextLine());
                System.out.print("Nuevo ID de escudería: ");
                int escuderiaId = Integer.parseInt(sc.nextLine());

                modificarPiloto(id, nombre, habilidad, escuderiaId);
            }
            case "4" -> {
                System.out.print("ID del piloto a consultar: ");
                int id = Integer.parseInt(sc.nextLine());
                consultarPiloto(id);
            }
            default -> msgError("Opción inválida.");
        }
    }

    // ════════════════════════════════════════════════
    // Lógica pura (reutilizable desde JavaFX)
    // ════════════════════════════════════════════════

    /** Crea un piloto y lo asocia al auto de la escudería indicada. */
    public Piloto altaPiloto(String nombre, int habilidad, int escuderiaId) {
        Auto auto = autoDAO.obtenerPorEscuderiaId(escuderiaId);
        int autoId = auto.getId();
        Piloto piloto = new Piloto(0, nombre, habilidad, escuderiaId, autoId);
        facade.gestionarPilotos("alta", piloto);
        return piloto;
    }

    public void bajaPiloto(int id) {
        Piloto piloto = new Piloto(id, "", 0, 0, 0);
        facade.gestionarPilotos("baja", piloto);
    }

    public void modificarPiloto(int id, String nombre, int habilidad, int escuderiaId) {
        Auto auto = autoDAO.obtenerPorEscuderiaId(escuderiaId);
        int autoId = auto.getId();
        Piloto piloto = new Piloto(id, nombre, habilidad, escuderiaId, autoId);
        facade.gestionarPilotos("modificacion", piloto);
    }

    public void consultarPiloto(int id) {
        Piloto piloto = new Piloto(id, "", 0, 0, 0);
        facade.gestionarPilotos("consulta", piloto);
    }

    public List<Piloto> obtenerTodos() {
        return PilotoDAO.obtenerTodos();
    }

    public Piloto obtenerPorId(int id) {
        return PilotoDAO.obtenerPorId(id);
    }

    public List<Escuderia> obtenerEscuderias() {
        return EscuderiaDAO.obtenerTodos();
    }

    // ════════════════════════════════════════════════
    // Salida por consola
    // ════════════════════════════════════════════════

    public void mostrarPilotos() {
        List<Piloto> pilotos = obtenerTodos();
        linea();
        titulo("  LISTA DE PILOTOS");
        linea();
        for (Piloto p : pilotos) {
            System.out.println("ID: " + p.getId() +
                    " | Nombre: " + p.getNombre() +
                    " | Habilidad: " + p.getHabilidad() +
                    " | Escudería: " + p.getEscuderiaId() +
                    " | Auto: " + p.getAutoId());
        }
        linea();
    }

    public void mostrarPiloto(Piloto piloto) {
        linea();
        titulo("  PILOTO REGISTRADO");
        linea();
        System.out.println("ID: " + piloto.getId() +
                " | Nombre: " + piloto.getNombre() +
                " | Habilidad: " + piloto.getHabilidad() +
                " | Escudería: " + piloto.getEscuderiaId() +
                " | Auto: " + piloto.getAutoId());
        linea();
    }

    public void mostrarEscuderias() {
        List<Escuderia> escuderias = obtenerEscuderias();
        linea();
        titulo("  LISTA DE ESCUDERÍAS");
        linea();
        for (Escuderia e : escuderias) {
            System.out.println("ID: " + e.getId() +
                    " | Nombre: " + e.getNombre() +
                    " | Color: " + e.getColor());
        }
        linea();
    }
}