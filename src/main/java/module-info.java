module com.f1legends {
    requires javafx.controls;
    requires javafx.graphics;
    requires java.sql;

    exports com.f1legends.ui;
    exports com.f1legends.modelo;
    exports com.f1legends.servicios;
    exports com.f1legends.DAO;
    exports com.f1legends.DAO.modeloDAO;
}
