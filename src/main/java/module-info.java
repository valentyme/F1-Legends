module com.f1legends {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires java.sql;

    exports com.f1legends.vista;
    exports com.f1legends.modelo;
    exports com.f1legends.servicios;
    exports com.f1legends.DAO;
    exports com.f1legends.DAO.modeloDAO;
    exports com.f1legends.patrones.estado;
    exports com.f1legends.patrones.observer;
    exports com.f1legends.patrones.factory;
    exports com.f1legends.patrones.facade;
    exports com.f1legends.modelo.circuitos;
    exports com.f1legends.modelo.Escuderias;
    exports com.f1legends.modelo.Usuarios;
    exports com.f1legends.modelo.carreras;
    exports com.f1legends.modelo.auto;
    exports com.f1legends.utiles;
    exports com.f1legends.controller;

    opens com.f1legends.vista to javafx.fxml;
    opens com.f1legends.controller to javafx.fxml;
}
