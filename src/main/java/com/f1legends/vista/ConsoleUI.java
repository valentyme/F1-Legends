package com.f1legends.vista;

/**
 * Utilidades de formato/impresión para la interfaz de consola.
 * Al migrar a JavaFX, esta clase deja de usarse (o se reemplaza
 * por componentes visuales equivalentes), pero mantenerla aparte
 * evita que la lógica de negocio dependa de System.out.
 */
public class ConsoleUI {

    private ConsoleUI() {}

    public static void linea() {
        System.out.println("  " + "─".repeat(78));
    }

    public static void titulo(String texto) {
        System.out.println(texto);
    }

    public static void subtitulo(String texto) {
        System.out.println();
        System.out.println("  ▶ " + texto);
        System.out.println("  " + "·".repeat(50));
    }

    public static void msgOk(String msg) {
        System.out.println("  ✔ " + msg);
    }

    public static void msgError(String msg) {
        System.out.println("  ✘ ERROR: " + msg);
    }

    public static void cabecera() {
        System.out.println();
        linea();
        System.out.println("  ███████╗ ██╗      ██╗      ███████╗  ██████╗  ███████╗  ███╗  ██╗  ██████╗  ███████╗");
        System.out.println("  ██╔════╝ ██║      ██║      ██╔════╝ ██╔════╝  ██╔════╝  ████╗ ██║  ██╔══██╗ ██╔════╝");
        System.out.println("  █████╗   ██║      ██║      █████╗   ██║  ███╗ █████╗    ██╔██╗██║  ██║  ██║ ███████╗");
        System.out.println("  ██╔══╝   ██║      ██║      ██╔══╝   ██║   ██║ ██╔══╝    ██║╚████║  ██║  ██║ ╚════██║");
        System.out.println("  ██║      ███████╗ ███████╗ ███████╗ ╚██████╔╝ ███████╗  ██║ ╚███║  ██████╔╝ ███████║");
        System.out.println("  ╚═╝      ╚══════╝ ╚══════╝ ╚══════╝  ╚═════╝  ╚══════╝  ╚═╝  ╚══╝  ╚═════╝  ╚══════╝");
        System.out.println("                              F1 LEGENDS  v1.0  — Desarrollo de Software");
        linea();
    }
}