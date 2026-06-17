package com.f1legends.vista;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainFX extends Application {
    public static volatile Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        stage.setTitle("F1 Legends");
        stage.setMinWidth(980);
        stage.setMinHeight(640);
        FxRouter.init(stage);
        FxRouter.showAuth();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
