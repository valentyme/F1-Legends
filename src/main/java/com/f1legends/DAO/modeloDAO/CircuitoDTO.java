package com.f1legends.DAO.modeloDAO;


public class CircuitoDTO {
    private int id;
    private String nombre;
    private String pais;
    private int vueltas;

    public CircuitoDTO(int id, String nombre, String pais, int vueltas) {
        this.id = id;
        this.nombre = nombre;
        this.pais = pais;
        this.vueltas = vueltas;
    }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getPais() { return pais; }
    public int getVueltas() { return vueltas; }
}
