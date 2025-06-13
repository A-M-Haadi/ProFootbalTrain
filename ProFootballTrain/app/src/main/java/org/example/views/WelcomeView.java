package pftrain.views;

import pftrain.controllers.SceneController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import pftrain.utils.UIComponents;

public class WelcomeView {
    private Scene scene;
    private SceneController sceneController;

    private static final String BACKGROUND_COLOR = "#f8f8f8";
    private static final String PRIMARY_TEXT_COLOR = "#333333";
    private static final String SECONDARY_TEXT_COLOR = "#666666";
    private static final String FONT_FAMILY = "Montserrat";

    public WelcomeView(SceneController sceneController) {
        this.sceneController = sceneController;
        createScene();
    }

    private void createScene() {
        BorderPane welcomeLayout = new BorderPane();
        welcomeLayout.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        VBox centerContent = new VBox();
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setSpacing(35);
        centerContent.setPadding(new Insets(20, 40, 60, 40));

        VBox logoSection = new VBox();
        logoSection.setAlignment(Pos.CENTER);
        Image logoImage = new Image(getClass().getResourceAsStream("/images/logo.png"));
        ImageView logoImageView = new ImageView(logoImage);
        logoImageView.setFitWidth(140);
        logoImageView.setPreserveRatio(true);
        logoSection.getChildren().add(logoImageView);

        VBox textSection = new VBox();
        textSection.setAlignment(Pos.CENTER);
        textSection.setSpacing(12);

        Label welcomeTitle = new Label("Welcome To\nProFootballTrain!");
        welcomeTitle.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 26));
        welcomeTitle.setStyle("-fx-text-fill: " + PRIMARY_TEXT_COLOR + ";");
        welcomeTitle.setTextAlignment(TextAlignment.CENTER);

        Label welcomeSubtitle = new Label("Please Login or Sign Up to\ncontinue using our app");
        welcomeSubtitle.setFont(Font.font(FONT_FAMILY, FontWeight.NORMAL, 16));
        welcomeSubtitle.setStyle("-fx-text-fill: " + SECONDARY_TEXT_COLOR + ";");
        welcomeSubtitle.setTextAlignment(TextAlignment.CENTER);

        textSection.getChildren().addAll(welcomeTitle, welcomeSubtitle);

        VBox buttonSection = new VBox();
        buttonSection.setAlignment(Pos.CENTER);
        buttonSection.setSpacing(15);
        buttonSection.setPadding(new Insets(25, 0, 0, 0));

        Button loginBtn = UIComponents.createWelcomeButton("Login");
        loginBtn.setOnAction(e -> sceneController.showLoginScene());

        Button signUpBtn = UIComponents.createWelcomeButton("Sign Up");
        signUpBtn.setOnAction(e -> sceneController.showSignUpScene());

        buttonSection.getChildren().addAll(loginBtn, signUpBtn);

        centerContent.getChildren().addAll(logoSection, textSection, buttonSection);
        welcomeLayout.setCenter(centerContent);

        scene = new Scene(welcomeLayout, 400, 700);
    }

    public Scene getScene() {
        return scene;
    }
}