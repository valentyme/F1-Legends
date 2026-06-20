package com.f1legends.controller.Objetos;

import com.f1legends.DAO.modeloDAO.RankingGlobalDAO;


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