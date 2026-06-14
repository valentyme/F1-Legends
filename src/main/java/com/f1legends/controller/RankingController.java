package com.f1legends.controller;

import com.f1legends.DAO.modeloDAO.RankingGlobalDAO;

/**
 * Pequeño wrapper sobre RankingGlobalDAO para desacoplar a
 * CarreraController del acceso a datos directo.
 */
public class RankingController {

    private final RankingGlobalDAO rankingDAO;

    public RankingController(RankingGlobalDAO rankingDAO) {
        this.rankingDAO = rankingDAO;
    }

    public void sumarPuntos(int idJugador, int puntos) {
        rankingDAO.sumarPuntos(idJugador, puntos);
    }

    public RankingGlobalDAO getDao() {
        return rankingDAO;
    }
}