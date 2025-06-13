package pftrain;

import javafx.application.Application;
import javafx.scene.image.Image; 
import javafx.stage.Stage;
import pftrain.controllers.SceneController;


public class ProFootballTrain extends Application {

    @Override
    public void start(Stage primaryStage) {

        Image appIcon = new Image(getClass().getResourceAsStream("/images/logo.png"));

        primaryStage.getIcons().add(appIcon);


        SceneController sceneController = new SceneController(primaryStage);


        sceneController.showWelcomeScene();

        primaryStage.setTitle("ProFootballTrain");
        primaryStage.setWidth(400);
        primaryStage.setHeight(650);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}