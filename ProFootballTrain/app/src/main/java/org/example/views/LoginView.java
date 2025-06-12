
package org.example.views;

import org.example.controllers.SceneController;
import org.example.models.UserSession;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.layout.Priority;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import org.example.utils.UIComponents;

public class LoginView {
    private Scene scene;
    private SceneController sceneController;
    private UserSession userSession;
    
    
    private static class PasswordFieldContainer {
        private StackPane container;
        private PasswordField passwordField;
        private TextField textField;
        private Button toggleButton;
        private String errorMessage;
        
        public PasswordFieldContainer() {
            createContainer();
        }
        
        private void createContainer() {
            container = new StackPane();
            container.setPrefWidth(350);
            container.setPrefHeight(50);
            
            
            passwordField = new PasswordField();
            passwordField.setPromptText("Password");
            passwordField.setPrefWidth(350);
            passwordField.setPrefHeight(50);
            passwordField.setFont(Font.font("Arial", 14));
            passwordField.setStyle("-fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-radius: 8; " +
                                  "-fx-padding: 12 40 12 12; -fx-background-color: white;");
            
            
            textField = new TextField();
            textField.setPromptText("Password");
            textField.setPrefWidth(350);
            textField.setPrefHeight(50);
            textField.setFont(Font.font("Arial", 14));
            textField.setStyle("-fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-radius: 8; " +
                              "-fx-padding: 12 40 12 12; -fx-background-color: white;");
            textField.setVisible(false);
            
            
            toggleButton = new Button("👁");
            toggleButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; " +
                                 "-fx-text-fill: #666; -fx-font-size: 14; -fx-cursor: hand;");
            toggleButton.setPrefSize(30, 30);
            
            
            StackPane.setAlignment(toggleButton, Pos.CENTER_RIGHT);
            StackPane.setMargin(toggleButton, new Insets(0, 10, 0, 0));
            
            
            toggleButton.setOnAction(e -> {
                if (passwordField.isVisible()) {
                    
                    textField.setText(passwordField.getText());
                    passwordField.setVisible(false);
                    textField.setVisible(true);
                    toggleButton.setText("🙈");
                    textField.requestFocus();
                    textField.end();
                } else {
                    
                    passwordField.setText(textField.getText());
                    textField.setVisible(false);
                    passwordField.setVisible(true);
                    toggleButton.setText("👁");
                    passwordField.requestFocus();
                    passwordField.end();
                }
            });
            
            
            passwordField.textProperty().addListener((obs, oldText, newText) -> {
                if (passwordField.isVisible()) {
                    textField.setText(newText);
                }
            });
            
            textField.textProperty().addListener((obs, oldText, newText) -> {
                if (textField.isVisible()) {
                    passwordField.setText(newText);
                }
            });
            
            container.getChildren().addAll(passwordField, textField, toggleButton);
        }
        
        public StackPane getContainer() {
            return container;
        }
        
        public String getPassword() {
            if (passwordField.isVisible()) {
                return passwordField.getText();
            } else {
                return textField.getText();
            }
        }
        
        public void setError(String errorMessage) {
            String errorStyle = "-fx-background-radius: 8; -fx-border-color: #ff4444; -fx-border-width: 2; -fx-border-radius: 8; " +
                               "-fx-padding: 12 40 12 12; -fx-background-color: white;";
            passwordField.setStyle(errorStyle);
            textField.setStyle(errorStyle);
            this.errorMessage = errorMessage;
        }
        
        public void clearError() {
            String normalStyle = "-fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-radius: 8; " +
                                "-fx-padding: 12 40 12 12; -fx-background-color: white;";
            passwordField.setStyle(normalStyle);
            textField.setStyle(normalStyle);
            this.errorMessage = null;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }
    
    public LoginView(SceneController sceneController) {
        this.sceneController = sceneController;
        this.userSession = UserSession.getInstance();
        createScene();
    }
    
    private void createScene() {
        BorderPane loginLayout = new BorderPane();
        loginLayout.setStyle("-fx-background-color: #f8f8f8;");
        
        
        HBox header = createHeader();
        loginLayout.setTop(header);
        
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: #f8f8f8; -fx-background-color: #f8f8f8;");
        
        
        VBox centerContent = new VBox();
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setSpacing(25);
        centerContent.setPadding(new Insets(40));
        
        
        Label titleLabel = new Label("Log In");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setStyle("-fx-text-fill: #4A5D23;");
        
        VBox formContainer = new VBox();
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setSpacing(20);
        formContainer.setMaxWidth(350);
        
        
        VBox usernameSection = createFieldSectionWithError("Username");
        TextField usernameField = createStyledTextField("Username");
        usernameSection.getChildren().add(usernameField);
        
        
        VBox passwordSection = createFieldSectionWithError("Password");
        PasswordFieldContainer passwordContainer = new PasswordFieldContainer();
        passwordSection.getChildren().add(passwordContainer.getContainer());
        
        
        VBox roleSection = createFieldSection("Login as:");
        HBox roleSelection = new HBox();
        roleSelection.setAlignment(Pos.CENTER);
        roleSelection.setSpacing(20);
        
        ToggleGroup roleGroup = new ToggleGroup();
        RadioButton pemainRadio = new RadioButton("Pemain");
        RadioButton pelatihRadio = new RadioButton("Pelatih/Admin");
        pemainRadio.setToggleGroup(roleGroup);
        pelatihRadio.setToggleGroup(roleGroup);
        pemainRadio.setSelected(true);
        
        
        pemainRadio.setStyle("-fx-text-fill: #333;");
        pelatihRadio.setStyle("-fx-text-fill: #333;");
        
        roleSelection.getChildren().addAll(pemainRadio, pelatihRadio);
        roleSection.getChildren().add(roleSelection);
        
        Button loginBtn = UIComponents.createActionButton("Log In");
        loginBtn.setOnAction(e -> {
            
            clearAllFieldErrors(usernameSection, passwordSection);
            passwordContainer.clearError();
            
            String username = usernameField.getText().trim();
            String password = passwordContainer.getPassword();
            boolean isPemain = pemainRadio.isSelected();
            
            if (validateLoginWithFieldErrors(username, password, isPemain, usernameField, passwordContainer, usernameSection, passwordSection)) {
                handleSuccessfulLogin(username, isPemain);
            }
        });
        
        
        HBox signUpLinkBox = new HBox();
        signUpLinkBox.setAlignment(Pos.CENTER);
        signUpLinkBox.setSpacing(5);
        
        Label dontHaveLabel = new Label("Don't have an account?");
        dontHaveLabel.setFont(Font.font("Arial", 14));
        dontHaveLabel.setStyle("-fx-text-fill: #666;");
        
        Hyperlink signUpLink = new Hyperlink("Sign Up");
        signUpLink.setFont(Font.font("Arial", 14));
        signUpLink.setStyle("-fx-text-fill: #4A5D23;");
        signUpLink.setOnAction(e -> sceneController.showSignUpScene());
        
        signUpLinkBox.getChildren().addAll(dontHaveLabel, signUpLink);
        
        formContainer.getChildren().addAll(usernameSection, passwordSection, roleSection, loginBtn, signUpLinkBox);
        
        centerContent.getChildren().addAll(titleLabel, formContainer);
        
        scrollPane.setContent(centerContent);
        loginLayout.setCenter(scrollPane);
        
        scene = new Scene(loginLayout);
    }
    
    private void handleSuccessfulLogin(String username, boolean isPemain) {
        if (isPemain) {
            
            String displayName = username.replace("@pemain", "");
            userSession.setCurrentUser(displayName);
            userSession.setCurrentUserRole("pemain");
            userSession.setCurrentUserTeam("tim_a"); 
        } else {
            
            userSession.setCurrentUser(username);
            userSession.setCurrentUserRole("pelatih");
            userSession.setCurrentUserTeam(""); 
        }
        
        sceneController.showDashboardScene();
    }
    
    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: white; -fx-border-color: #eee; -fx-border-width: 0 0 1 0;");
        
        
        VBox logoSection = new VBox();
        logoSection.setAlignment(Pos.CENTER);
        logoSection.setSpacing(0);
        
        Label logoLine1 = new Label("LO");
        logoLine1.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        logoLine1.setStyle("-fx-text-fill: #4A5D23;");
        
        Label logoLine2 = new Label("GO");
        logoLine2.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        logoLine2.setStyle("-fx-text-fill: #4A5D23; -fx-underline: true;");
        
        logoSection.getChildren().addAll(logoLine1, logoLine2);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label brandLabel = new Label("MyTraining");
        brandLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        brandLabel.setStyle("-fx-text-fill: #4A5D23;");
        
        header.getChildren().addAll(logoSection, spacer, brandLabel);
        return header;
    }
    
    private VBox createFieldSection(String labelText) {
        VBox section = new VBox();
        section.setSpacing(8);
        section.setAlignment(Pos.CENTER_LEFT);
        
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        label.setStyle("-fx-text-fill: #333;");
        
        section.getChildren().add(label);
        return section;
    }
    
    private VBox createFieldSectionWithError(String labelText) {
        VBox section = new VBox();
        section.setSpacing(8);
        section.setAlignment(Pos.CENTER_LEFT);
        
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        label.setStyle("-fx-text-fill: #333;");
        
        
        Label errorLabel = new Label();
        errorLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        errorLabel.setStyle("-fx-text-fill: #ff4444;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        
        section.getChildren().addAll(label, errorLabel);
        return section;
    }
    
    private TextField createStyledTextField(String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);
        field.setPrefWidth(350);
        field.setPrefHeight(50);
        field.setFont(Font.font("Arial", 14));
        field.setStyle("-fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-radius: 8; " +
                      "-fx-padding: 12; -fx-background-color: white;");
        return field;
    }
    
    private void clearAllFieldErrors(VBox... fieldSections) {
        String normalStyle = "-fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-radius: 8; " +
                            "-fx-padding: 12; -fx-background-color: white;";
        
        for (VBox section : fieldSections) {
            
            for (var node : section.getChildren()) {
                if (node instanceof TextField) {
                    ((TextField) node).setStyle(normalStyle);
                }
            }
            
            
            if (section.getChildren().size() > 2) { 
                Label errorLabel = (Label) section.getChildren().get(1); 
                errorLabel.setVisible(false);
                errorLabel.setManaged(false);
            }
        }
    }
    
    private void setFieldError(TextField field, VBox fieldSection, String errorMessage) {
        
        field.setStyle("-fx-background-radius: 8; -fx-border-color: #ff4444; -fx-border-width: 2; -fx-border-radius: 8; " +
                      "-fx-padding: 12; -fx-background-color: white;");
        
        
        if (fieldSection.getChildren().size() > 2) { 
            Label errorLabel = (Label) fieldSection.getChildren().get(1); 
            errorLabel.setText(errorMessage);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }
    
    private void setPasswordError(VBox passwordSection, PasswordFieldContainer passwordContainer, String errorMessage) {
        passwordContainer.setError(errorMessage);
        
        
        if (passwordSection.getChildren().size() > 2) { 
            Label errorLabel = (Label) passwordSection.getChildren().get(1); 
            errorLabel.setText(errorMessage);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }
    
    
    private boolean isValidAdminCredentials(String username, String password) {
        
        return (username.equals("admin") && password.equals("admin123")) ||
               (username.equals("pelatih") && password.equals("pelatih123")) ||
               (username.equals("coach") && password.equals("coach123"));
    }
    
    public Scene getScene() {
        return scene;
    }
}