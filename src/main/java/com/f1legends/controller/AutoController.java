package com.f1legends.controller;

import com.f1legends.DAO.modeloDAO.EscuderiaDAO;
import com.f1legends.modelo.Escuderias.Escuderia;
import com.f1legends.modelo.auto.Auto;
import com.f1legends.patrones.factory.TipoAuto;
import com.f1legends.servicios.AutoService;

import java.util.List;
import java.util.Scanner;

import static com.f1legends.vista.ConsoleUI.linea;
import static com.f1legends.vista.ConsoleUI.msgError;
import static com.f1legends.vista.ConsoleUI.msgOk;
import static com.f1legends.vista.ConsoleUI.titulo;

public class AutoController {
    private final AutoService autoService;
    private final EscuderiaDAO escuderiaDAO;
    private final Scanner sc;

    public AutoController(AutoService autoService, EscuderiaDAO escuderiaDAO, Scanner sc) {
        this.autoService = autoService;
        this.escuderiaDAO = escuderiaDAO;
        this.sc = sc;
    }

    public void cuGestionarAutos() {
        linea();
        titulo("  GESTIONAR AUTOS");
        linea();
        System.out.println("  [1] Alta de auto");
        System.out.println("  [2] Baja de auto");
        System.out.println("  [3] Modificar auto");
        System.out.println("  [4] Consultar autos");
        System.out.print("  Opcion: ");

        try {
            switch (sc.nextLine().trim()) {
                case "1" -> altaAuto();
                case "2" -> bajaAuto();
                case "3" -> modificarAuto();
                case "4" -> mostrarAutos();
                default -> msgError("Opcion invalida.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            msgError(e.getMessage());
        }
    }

    public Auto altaAuto(String modelo, double velocidadBase, int escuderiaId, TipoAuto tipoAuto) {
        return autoService.altaAuto(modelo, velocidadBase, escuderiaId, tipoAuto);
    }

    public void bajaAuto(int id) {
        autoService.bajaAuto(id);
    }

    public void modificarAuto(int id, String modelo, double velocidadBase, int escuderiaId, TipoAuto tipoAuto) {
        autoService.modificarAuto(id, modelo, velocidadBase, escuderiaId, tipoAuto);
    }

    public List<Auto> consultarAutos() {
        return autoService.consultarAutos();
    }

    private void altaAuto() {
        mostrarEscuderias();
        System.out.print("Modelo: ");
        String modelo = sc.nextLine().trim();
        double velocidadBase = leerDouble("Velocidad base: ");
        int escuderiaId = leerEntero("ID de escuderia: ");
        TipoAuto tipoAuto = leerTipoAuto();

        Auto auto = altaAuto(modelo, velocidadBase, escuderiaId, tipoAuto);
        msgOk("Auto registrado: " + auto.getModelo());
    }

    private void bajaAuto() {
        mostrarAutos();
        int id = leerEntero("ID de auto a eliminar: ");
        bajaAuto(id);
        msgOk("Auto eliminado.");
    }

    private void modificarAuto() {
        mostrarAutos();
        int id = leerEntero("ID de auto a modificar: ");
        Auto actual = autoService.obtenerAuto(id);
        if (actual == null) {
            throw new IllegalArgumentException("No existe un auto con ID " + id + ".");
        }

        System.out.print("Nuevo modelo [" + actual.getModelo() + "]: ");
        String modelo = sc.nextLine().trim();
        if (modelo.isBlank()) {
            modelo = actual.getModelo();
        }

        System.out.print("Nueva velocidad base [" + actual.getVelocidadBase() + "]: ");
        String velocidadTexto = sc.nextLine().trim();
        double velocidadBase = velocidadTexto.isBlank()
                ? actual.getVelocidadBase()
                : Double.parseDouble(velocidadTexto);

        mostrarEscuderias();
        int escuderiaActualId = actual.getEscuderia() == null ? 0 : actual.getEscuderia().getId();
        System.out.print("Nuevo ID de escuderia [" + escuderiaActualId + "]: ");
        String escuderiaTexto = sc.nextLine().trim();
        int escuderiaId = escuderiaTexto.isBlank()
                ? escuderiaActualId
                : Integer.parseInt(escuderiaTexto);

        TipoAuto tipoAuto = leerTipoAuto(actual.getTipoAuto());

        modificarAuto(id, modelo, velocidadBase, escuderiaId, tipoAuto);
        msgOk("Auto actualizado.");
    }

    private void mostrarAutos() {
        List<Auto> autos = consultarAutos();
        if (autos.isEmpty()) {
            System.out.println("  No hay autos registrados.");
            return;
        }

        System.out.printf("  %-5s %-20s %-15s %-20s %s%n", "ID", "MODELO", "VELOCIDAD", "REGLAMENTO", "ESCUDERIA");
        for (Auto auto : autos) {
            String escuderia = auto.getEscuderia() == null ? "Sin escuderia" : auto.getEscuderia().getNombre();
            System.out.printf("  %-5d %-20s %-15.3f %-20s %s%n",
                    auto.getId(),
                    auto.getModelo(),
                    auto.getVelocidadBase(),
                    auto.getTipoAuto(),
                    escuderia);
        }
    }

    private void mostrarEscuderias() {
        List<Escuderia> escuderias = EscuderiaDAO.obtenerTodos();
        if (escuderias.isEmpty()) {
            System.out.println("  No hay escuderias registradas.");
            return;
        }

        System.out.println("  ESCUDERIAS DISPONIBLES:");
        for (Escuderia escuderia : escuderias) {
            System.out.println("  " + escuderia);
        }
    }

    private int leerEntero(String prompt) {
        System.out.print(prompt);
        return Integer.parseInt(sc.nextLine().trim());
    }

    private double leerDouble(String prompt) {
        System.out.print(prompt);
        return Double.parseDouble(sc.nextLine().trim());
    }

    private TipoAuto leerTipoAuto() {
        return leerTipoAuto(null);
    }

    private TipoAuto leerTipoAuto(TipoAuto actual) {
        System.out.println("Reglamentacion:");
        TipoAuto[] tipos = TipoAuto.values();
        for (int i = 0; i < tipos.length; i++) {
            System.out.printf("  [%d] %s%n", i + 1, tipos[i]);
        }

        String sufijo = actual == null ? "" : " [" + actual + "]";
        System.out.print("Opcion" + sufijo + ": ");
        String texto = sc.nextLine().trim();
        if (texto.isBlank() && actual != null) {
            return actual;
        }

        int opcion = Integer.parseInt(texto);
        if (opcion < 1 || opcion > tipos.length) {
            throw new IllegalArgumentException("Reglamentacion invalida.");
        }
        return tipos[opcion - 1];
    }
}
