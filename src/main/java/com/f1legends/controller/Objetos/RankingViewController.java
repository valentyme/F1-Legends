package com.f1legends.controller.Objetos;

import com.f1legends.DAO.modeloDAO.RankingGlobalDAO;
import com.f1legends.DAO.modeloDAO.UsuarioDAO;
import com.f1legends.controller.Usuario.UsuarioController;
import com.f1legends.vista.FxRouter;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.util.List;

public class RankingViewController {
    private final UsuarioController usuarioController = new UsuarioController(
            new UsuarioDAO(),
            new RankingGlobalDAO()
    );

    @FXML private TableView<RankingRow> rankingTable;
    @FXML private TableColumn<RankingRow, Number> posicionColumn;
    @FXML private TableColumn<RankingRow, String> usuarioColumn;
    @FXML private TableColumn<RankingRow, Number> puntosColumn;
    @FXML private Label estadoLabel;

    @FXML
    private void initialize() {
        posicionColumn.setCellValueFactory(cell -> new ReadOnlyIntegerWrapper(cell.getValue().posicion()));
        usuarioColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().username()));
        puntosColumn.setCellValueFactory(cell -> new ReadOnlyIntegerWrapper(cell.getValue().puntaje()));
        cargarRanking();
    }

    @FXML
    private void cargarRanking() {
        rankingTable.getItems().clear();
        List<RankingGlobalDAO.EntradaRanking> ranking = usuarioController.obtenerRankingGlobal();
        for (int i = 0; i < ranking.size(); i++) {
            RankingGlobalDAO.EntradaRanking entrada = ranking.get(i);
            rankingTable.getItems().add(new RankingRow(i + 1, entrada.username, entrada.puntaje));
        }
        estadoLabel.setText(ranking.isEmpty()
                ? "Todavía no hay resultados cargados."
                : "Ranking actualizado: " + ranking.size() + " participantes.");
    }

    @FXML
    private void volver() throws IOException {
        FxRouter.volverAlHome();
    }

    private record RankingRow(int posicion, String username, int puntaje) {
    }
}