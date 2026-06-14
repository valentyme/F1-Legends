package com.f1legends.controller;

import com.f1legends.DAO.modeloDAO.EscuderiaDAO;
import com.f1legends.modelo.Escuderias.Escuderia;
import com.f1legends.patrones.facade.SistemaCarreraFacade;
import com.f1legends.utiles.PaletaColoresDemo;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Scanner;

import static com.f1legends.vista.ConsoleUI.*;

/**
 * Controlador de CU22 — Gestión de escuderías (alta, baja, modificación, consulta).
 */
public class EscuderiaController {

    private final SistemaCarreraFacade facade;
    private final EscuderiaDAO escuderiaDAO;
    private final Scanner sc;

    public EscuderiaController(SistemaCarreraFacade facade, EscuderiaDAO escuderiaDAO, Scanner sc) {
        this.facade = facade;
        this.escuderiaDAO = escuderiaDAO;
        this.sc = sc;
    }

    // ════════════════════════════════════════════════
    // CU22 — Gestión de escuderías
    // ════════════════════════════════════════════════
    public void cuGestionarEscuderias() {
        linea();
        titulo("  GESTIONAR ESCUDERÍAS (CU22)");
        linea();
        System.out.println("  [1] Alta de escudería");
        System.out.println("  [2] Baja de escudería");
        System.out.println("  [3] Modificar escudería");
        System.out.println("  [4] Consultar escuderías");
        System.out.print("  Opción: ");
        String opcion = sc.nextLine().trim();

        switch (opcion) {
            case "1" -> {
                System.out.print("Nombre: ");
                String nombre = sc.nextLine();
                Color color = PaletaColoresDemo.mostrarPicker();
                altaEscuderia(nombre, color);
            }
            case "2" -> {
                mostrarEscuderias();
                System.out.print("ID de escudería a eliminar: ");
                int id = Integer.parseInt(sc.nextLine());
                bajaEscuderia(id);
            }
            case "3" -> {
                mostrarEscuderias();
                System.out.print("ID de escudería a modificar: ");
                int id = Integer.parseInt(sc.nextLine());
                System.out.print("Nuevo nombre: ");
                String nombre = sc.nextLine();
                System.out.print("Nuevo color (hex #RRGGBB): ");
                String colorHex = sc.nextLine().trim();
                modificarEscuderia(id, nombre, colorHex);
            }
            case "4" -> mostrarEscuderias();
            default -> msgError("⚠ Opción inválida.");
        }
    }

    // ════════════════════════════════════════════════
    // Lógica pura (reutilizable desde JavaFX)
    // ════════════════════════════════════════════════

    public void altaEscuderia(String nombre, Color color) {
        Escuderia escuderia = new Escuderia(0, nombre, color);
        escuderiaDAO.insertar(escuderia);
    }

    public void bajaEscuderia(int id) {
        facade.bajaEscuderia(id);
    }

    public void modificarEscuderia(int id, String nombre, String colorHex) {
        facade.modificarEscuderia(id, nombre, colorHex);
    }

    public List<Escuderia> consultarEscuderias() {
        return facade.consultarEscuderias();
    }

    // ════════════════════════════════════════════════
    // Salida por consola
    // ════════════════════════════════════════════════

    public void mostrarEscuderias() {
        consultarEscuderias().forEach(System.out::println);
    }
}