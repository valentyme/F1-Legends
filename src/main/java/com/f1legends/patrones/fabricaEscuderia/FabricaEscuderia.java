package com.f1legends.patrones.fabricaEscuderia;

import com.f1legends.DAO.modeloDAO.EscuderiaDAO;
import com.f1legends.modelo.Escuderias.Escuderia;
import javafx.scene.paint.Color;

import java.util.List;

public class FabricaEscuderia {
        private EscuderiaDAO escuderiaDAO = new EscuderiaDAO();

        public Escuderia crearEscuderia(int id) {
            return escuderiaDAO.obtenerPorId(id);
        }

        public List<Escuderia> crearTodas() {
            return escuderiaDAO.obtenerTodos();
        }
    }

