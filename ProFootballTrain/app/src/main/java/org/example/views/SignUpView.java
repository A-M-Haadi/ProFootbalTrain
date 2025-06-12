
package org.example.views;

import org.example.controllers.SceneController;
import org.example.models.DataManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import org.example.utils.UIComponents;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.util.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SignUpView {
    private Scene signUpScene, successScene;
    private SceneController sceneController;
    private DataManager dataManager;
    
    
    private String generatedUsername = "";
    private String generatedPassword = "";
    
    public SignUpView(SceneController sceneController) {
        this.sceneController = sceneController;
        this.dataManager = DataManager.getInstance();
        createSignUpScene();
    }
    
    private void createSignUpScene() {
        BorderPane signUpLayout = new BorderPane();
        signUpLayout.setStyle("-fx-background-color: #f8f8f8;");
        
        
        HBox header = createHeader();
        signUpLayout.setTop(header);
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: #f8f8f8; -fx-background-color: #f8f8f8;");
        
        VBox centerContent = new VBox();
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setSpacing(25);
        centerContent.setPadding(new Insets(40));
        
        
        Label titleLabel = new Label("Sign Up");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setStyle("-fx-text-fill: #4A5D23;");
        
        VBox formContainer = new VBox();
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setSpacing(20);
        formContainer.setMaxWidth(350);
        
        
        VBox namaSection = createFieldSectionWithError("Nama Lengkap");
        TextField namaField = createStyledTextField("Nama Lengkap");
        namaSection.getChildren().add(namaField);
        
        VBox tanggalSection = createFieldSectionWithError("Tanggal Lahir");
        DateInputContainer tanggalContainer = createDateInputContainer();
        tanggalSection.getChildren().add(tanggalContainer.getContainer());
        
        VBox nomorSection = createFieldSectionWithError("Nomor Punggung");
        TextField nomorField = createStyledTextField("Nomor Punggung");
        nomorSection.getChildren().add(nomorField);
        
        VBox posisiSection = createFieldSectionWithError("Posisi");
        TextField posisiField = createStyledTextField("Posisi");
        posisiSection.getChildren().add(posisiField);
        
        Button signUpBtn = UIComponents.createActionButton("Sign Up");
        signUpBtn.setOnAction(e -> {
            
            clearAllFieldErrors(namaSection, nomorSection, posisiSection, tanggalSection);
            tanggalContainer.clearError();
            
            LocalDate selectedDate = tanggalContainer.getSelectedDate();
            if (validateSignUpWithFieldErrors(namaField, selectedDate, nomorField, posisiField, 
                                            namaSection, tanggalSection, nomorSection, posisiSection, tanggalContainer)) {
                generateCredentials(namaField.getText(), selectedDate);
                createSuccessScene();
                sceneController.getPrimaryStage().setScene(successScene);
            }
        });
        
        
        HBox loginLinkBox = new HBox();
        loginLinkBox.setAlignment(Pos.CENTER);
        loginLinkBox.setSpacing(5);

        Label alreadyLabel = new Label("Already have an account?");
        alreadyLabel.setFont(Font.font("Arial", 14));
        alreadyLabel.setStyle("-fx-text-fill: #666;");

        Hyperlink loginLink = new Hyperlink("Log In");
        loginLink.setFont(Font.font("Arial", 14));
        loginLink.setStyle("-fx-text-fill: #4A5D23;");
        loginLink.setOnAction(e -> sceneController.showLoginScene());

        loginLinkBox.getChildren().addAll(alreadyLabel, loginLink);
        
        
        formContainer.getChildren().addAll(namaSection, tanggalSection, nomorSection, posisiSection, 
                                          signUpBtn, loginLinkBox);
        
        centerContent.getChildren().addAll(titleLabel, formContainer);
        scrollPane.setContent(centerContent);
        signUpLayout.setCenter(scrollPane);
        
        signUpScene = new Scene(signUpLayout);
    }
    
    private void createSuccessScene() {
        BorderPane successLayout = new BorderPane();
        successLayout.setStyle("-fx-background-color: #f8f8f8;");
        
        
        HBox header = createSuccessHeader();
        successLayout.setTop(header);
        
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: #f8f8f8; -fx-background-color: #f8f8f8;");
        
        VBox centerContent = new VBox();
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setSpacing(30);
        centerContent.setPadding(new Insets(30, 50, 50, 50));
        
        
        Label successTitle = new Label("Sign Up Successfully!");
        successTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        successTitle.setStyle("-fx-text-fill: #4A5D23;");
        
        
        StackPane iconContainer = createAnimatedSuccessIcon();
        iconContainer.setPrefSize(120, 120);
        
        
        Label credentialsTitle = new Label("Here your Username and Password!");
        credentialsTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        credentialsTitle.setStyle("-fx-text-fill: #333;");
        
        VBox credentialsContainer = new VBox();
        credentialsContainer.setAlignment(Pos.CENTER);
        credentialsContainer.setSpacing(15);
        credentialsContainer.setMaxWidth(350);
        
        
        VBox usernameSection = createFieldSection("Username");
        TextField usernameField = createStyledTextField("");
        usernameField.setText(generatedUsername);
        usernameField.setEditable(false);
        usernameField.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ddd; " +
                              "-fx-border-radius: 8; -fx-padding: 12;");
        usernameSection.getChildren().add(usernameField);
        
        
        VBox passwordSection = createFieldSection("Password");
        HBox passwordContainer = new HBox();
        passwordContainer.setAlignment(Pos.CENTER_LEFT);
        
        TextField passwordField = createStyledTextField("");
        passwordField.setText(generatedPassword);
        passwordField.setEditable(false);
        passwordField.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ddd; " +
                              "-fx-border-radius: 8; -fx-padding: 12;");
        passwordField.setPrefWidth(300);
        
        Button copyButton = new Button("📋");
        copyButton.setStyle("-fx-background-color: #4A5D23; -fx-text-fill: white; " +
                           "-fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
        copyButton.setPrefSize(40, 50);
        copyButton.setOnAction(e -> {
            showCustomCopyAlert("Password berhasil disalin!", generatedPassword);
        });
        
        passwordContainer.getChildren().addAll(passwordField, copyButton);
        passwordContainer.setSpacing(10);
        passwordSection.getChildren().add(passwordContainer);
        
        credentialsContainer.getChildren().addAll(usernameSection, passwordSection);
        
        
        HBox loginLinkBox = new HBox();
        loginLinkBox.setAlignment(Pos.CENTER);
        loginLinkBox.setSpacing(5);

        Label alreadyLabel = new Label("Ready to start?");
        alreadyLabel.setFont(Font.font("Arial", 14));
        alreadyLabel.setStyle("-fx-text-fill: #666;");

        Hyperlink loginLink = new Hyperlink("Log In Now");
        loginLink.setFont(Font.font("Arial", 14));
        loginLink.setStyle("-fx-text-fill: #4A5D23;");
        loginLink.setOnAction(e -> sceneController.showLoginScene());

        loginLinkBox.getChildren().addAll(alreadyLabel, loginLink);
        
        centerContent.getChildren().addAll(successTitle, iconContainer, credentialsTitle, 
                                          credentialsContainer, loginLinkBox);
        
        scrollPane.setContent(centerContent);
        successLayout.setCenter(scrollPane);
        
        successScene = new Scene(successLayout);
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
    
    private HBox createSuccessHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: white; -fx-border-color: #eee; -fx-border-width: 0 0 1 0;");
        
        Button backBtn = new Button("←");
        backBtn.setFont(Font.font(18));
        backBtn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; " +
                        "-fx-text-fill: #666; -fx-cursor: hand;");
        backBtn.setOnAction(e -> sceneController.showWelcomeScene());
        
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        
        
        VBox logoSection = new VBox();
        logoSection.setAlignment(Pos.CENTER);
        logoSection.setSpacing(0);
        
        Label logoLine1 = new Label("LO");
        logoLine1.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        logoLine1.setStyle("-fx-text-fill: #4A5D23;");
        
        Label logoLine2 = new Label("GO");
        logoLine2.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        logoLine2.setStyle("-fx-text-fill: #4A5D23; -fx-underline: true;");
        
        logoSection.getChildren().addAll(logoLine1, logoLine2);
        
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        
        Label brandLabel = new Label("MyTraining");
        brandLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        brandLabel.setStyle("-fx-text-fill: #4A5D23;");
        
        header.getChildren().addAll(backBtn, spacer1, logoSection, spacer2, brandLabel);
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
    
    
    private static class DateInputContainer {
        private HBox container;
        private ComboBox<Integer> dayBox;
        private ComboBox<String> monthBox;
        private ComboBox<Integer> yearBox;
        private String errorMessage;
        
        public DateInputContainer() {
            createContainer();
        }
        
        private void createContainer() {
            container = new HBox();
            container.setSpacing(10);
            container.setAlignment(Pos.CENTER_LEFT);
            
            
            dayBox = new ComboBox<>();
            for (int i = 1; i <= 31; i++) {
                dayBox.getItems().add(i);
            }
            dayBox.setPromptText("Hari");
            dayBox.setPrefWidth(80);
            dayBox.setPrefHeight(50);
            dayBox.setStyle("-fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-radius: 8; " +
                           "-fx-background-color: white;");
            
            
            monthBox = new ComboBox<>();
            String[] months = {"Januari", "Februari", "Maret", "April", "Mei", "Juni",
                              "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
            for (String month : months) {
                monthBox.getItems().add(month);
            }
            monthBox.setPromptText("Bulan");
            monthBox.setPrefWidth(120);
            monthBox.setPrefHeight(50);
            monthBox.setStyle("-fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-radius: 8; " +
                             "-fx-background-color: white;");
            
            
            yearBox = new ComboBox<>();
            for (int i = 2025; i >= 1950; i--) { 
                yearBox.getItems().add(i);
            }
            yearBox.setPromptText("Tahun");
            yearBox.setPrefWidth(100);
            yearBox.setPrefHeight(50);
            yearBox.setStyle("-fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-radius: 8; " +
                            "-fx-background-color: white;");
            
            
            monthBox.setOnAction(e -> updateDayOptions());
            yearBox.setOnAction(e -> updateDayOptions());
            
            container.getChildren().addAll(dayBox, monthBox, yearBox);
        }
        
        private void updateDayOptions() {
            if (monthBox.getValue() != null && yearBox.getValue() != null) {
                int month = monthBox.getSelectionModel().getSelectedIndex() + 1;
                int year = yearBox.getValue();
                
                
                LocalDate firstDay = LocalDate.of(year, month, 1);
                int maxDays = firstDay.lengthOfMonth();
                
                
                Integer selectedDay = dayBox.getValue();
                dayBox.getItems().clear();
                for (int i = 1; i <= maxDays; i++) {
                    dayBox.getItems().add(i);
                }
                
                
                if (selectedDay != null && selectedDay <= maxDays) {
                    dayBox.setValue(selectedDay);
                }
            }
        }
        
        public HBox getContainer() {
            return container;
        }
        
        public void setError(String errorMessage) {
            dayBox.setStyle("-fx-background-radius: 8; -fx-border-color: #ff4444; -fx-border-width: 2; -fx-border-radius: 8; " +
                           "-fx-background-color: white;");
            monthBox.setStyle("-fx-background-radius: 8; -fx-border-color: #ff4444; -fx-border-width: 2; -fx-border-radius: 8; " +
                             "-fx-background-color: white;");
            yearBox.setStyle("-fx-background-radius: 8; -fx-border-color: #ff4444; -fx-border-width: 2; -fx-border-radius: 8; " +
                            "-fx-background-color: white;");
            
            
            this.errorMessage = errorMessage;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public void clearError() {
            String normalStyle = "-fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-radius: 8; " +
                                "-fx-background-color: white;";
            dayBox.setStyle(normalStyle);
            monthBox.setStyle(normalStyle);
            yearBox.setStyle(normalStyle);
            this.errorMessage = null;
        }
        
        public LocalDate getSelectedDate() {
            try {
                if (dayBox.getValue() != null && monthBox.getValue() != null && yearBox.getValue() != null) {
                    int day = dayBox.getValue();
                    int month = monthBox.getSelectionModel().getSelectedIndex() + 1;
                    int year = yearBox.getValue();
                    
                    return LocalDate.of(year, month, day);
                }
            } catch (Exception e) {
                
            }
            return null;
        }
    }
    
    private DateInputContainer createDateInputContainer() {
        return new DateInputContainer();
    }
    
    
    private void generateCredentials(String namaLengkap, LocalDate tanggalLahir) {
        
        String nama = namaLengkap.replaceAll("\\s+", "").toLowerCase();
        String tanggal = tanggalLahir.format(DateTimeFormatter.ofPattern("ddMM"));
        
        generatedUsername = nama + tanggal + "@pemain";
        
        
        String namaCapital = namaLengkap.split("\\s+")[0]; 
        generatedPassword = namaCapital + tanggal;
    }
    
    private StackPane createAnimatedSuccessIcon() {
        StackPane container = new StackPane();
        container.setAlignment(Pos.CENTER);
        
        
        Circle successCircle = new Circle(60);
        successCircle.setFill(Color.web("#7FB069"));
        successCircle.setOpacity(0); 
        
        
        Label checkmark = new Label("✓");
        checkmark.setFont(Font.font("Arial", FontWeight.BOLD, 60));
        checkmark.setStyle("-fx-text-fill: white;");
        checkmark.setOpacity(0); 
        checkmark.setScaleX(0); 
        checkmark.setScaleY(0);
        
        container.getChildren().addAll(successCircle, checkmark);
        
        
        createSuccessAnimation(successCircle, checkmark);
        
        return container;
    }
    
    private void createSuccessAnimation(Circle circle, Label checkmark) {
        
        Timeline circleAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(circle.opacityProperty(), 0),
                new KeyValue(circle.scaleXProperty(), 0.3),
                new KeyValue(circle.scaleYProperty(), 0.3)
            ),
            new KeyFrame(Duration.millis(400),
                new KeyValue(circle.opacityProperty(), 1),
                new KeyValue(circle.scaleXProperty(), 1.1),
                new KeyValue(circle.scaleYProperty(), 1.1)
            ),
            new KeyFrame(Duration.millis(600),
                new KeyValue(circle.scaleXProperty(), 1.0),
                new KeyValue(circle.scaleYProperty(), 1.0)
            )
        );
        
        
        Timeline checkmarkAnimation = new Timeline(
            new KeyFrame(Duration.millis(600), 
                new KeyValue(checkmark.opacityProperty(), 0),
                new KeyValue(checkmark.scaleXProperty(), 0),
                new KeyValue(checkmark.scaleYProperty(), 0)
            ),
            new KeyFrame(Duration.millis(900),
                new KeyValue(checkmark.opacityProperty(), 1),
                new KeyValue(checkmark.scaleXProperty(), 1.2),
                new KeyValue(checkmark.scaleYProperty(), 1.2)
            ),
            new KeyFrame(Duration.millis(1100),
                new KeyValue(checkmark.scaleXProperty(), 1.0),
                new KeyValue(checkmark.scaleYProperty(), 1.0)
            )
        );
        
        
        ScaleTransition pulse = new ScaleTransition(Duration.millis(2000), circle);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.05);
        pulse.setToY(1.05);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(ScaleTransition.INDEFINITE);
        
        
        circleAnimation.play();
        checkmarkAnimation.play();
        
        
        checkmarkAnimation.setOnFinished(e -> pulse.play());
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
    
    private void setDateError(VBox dateSection, DateInputContainer dateContainer, String errorMessage) {
        dateContainer.setError(errorMessage);
        
        
        if (dateSection.getChildren().size() > 2) { 
            Label errorLabel = (Label) dateSection.getChildren().get(1); 
            errorLabel.setText(errorMessage);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }
    
    private boolean validateSignUpWithFieldErrors(TextField namaField, LocalDate tanggal, 
                                                 TextField nomorField, TextField posisiField,
                                                 VBox namaSection, VBox tanggalSection, 
                                                 VBox nomorSection, VBox posisiSection,
                                                 DateInputContainer dateContainer) {
        boolean isValid = true;
        
        
        if (namaField.getText().trim().isEmpty()) {
            setFieldError(namaField, namaSection, "Nama lengkap harus diisi");
            isValid = false;
        }
        
        
        if (tanggal == null) {
            setDateError(tanggalSection, dateContainer, "Tanggal lahir harus dipilih");
            isValid = false;
        } else if (tanggal.isAfter(LocalDate.now())) {
            setDateError(tanggalSection, dateContainer, "Tanggal lahir tidak boleh di masa depan");
            isValid = false;
        }
        
        
        if (nomorField.getText().trim().isEmpty()) {
            setFieldError(nomorField, nomorSection, "Nomor punggung harus diisi");
            isValid = false;
        }
        
        
        if (posisiField.getText().trim().isEmpty()) {
            setFieldError(posisiField, posisiSection, "Posisi harus diisi");
            isValid = false;
        }
        
        return isValid;
    }
    
    public Scene getSignUpScene() {
        return signUpScene;
    }
    
    public Scene getSuccessScene() {
        return successScene;
    }
    
    private void showCustomCopyAlert(String message, String copiedText) {
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sukses");
        alert.setHeaderText(null);
        alert.setContentText(null); 
        
        
        DialogPane dialogPane = alert.getDialogPane();
        
        
        dialogPane.setGraphic(null);
        
        
        dialogPane.setStyle(
            "-fx-background-color: #f8fff8; " +
            "-fx-border-color: #7FB069; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 12; " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);"
        );
        
        
        VBox content = new VBox();
        content.setAlignment(Pos.CENTER);
        content.setSpacing(12);
        content.setPadding(new Insets(25, 30, 20, 30)); 
        
        
        Label iconLabel = new Label("✅");
        iconLabel.setFont(Font.font("Arial", 24));
        
        
        Label messageLabel = new Label(message);
        messageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        messageLabel.setStyle("-fx-text-fill: #2d5016;");
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        messageLabel.setWrapText(true);
        
        
        VBox copiedContainer = new VBox();
        copiedContainer.setAlignment(Pos.CENTER);
        copiedContainer.setSpacing(6);
        copiedContainer.setMaxWidth(220); 
        copiedContainer.setStyle(
            "-fx-background-color: #e8f5e8; " +
            "-fx-border-color: #7FB069; " +
            "-fx-border-radius: 6; " +
            "-fx-background-radius: 6; " +
            "-fx-padding: 10;"
        );
        
        Label copiedLabel = new Label("Password yang disalin:");
        copiedLabel.setFont(Font.font("Arial", 11));
        copiedLabel.setStyle("-fx-text-fill: #666;");
        
        Label passwordLabel = new Label(copiedText);
        passwordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        passwordLabel.setStyle("-fx-text-fill: #2d5016;");
        
        copiedContainer.getChildren().addAll(copiedLabel, passwordLabel);
        
        content.getChildren().addAll(iconLabel, messageLabel, copiedContainer);
        
        
        dialogPane.setContent(content);
        
        
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setText("OK");
        okButton.setStyle(
            "-fx-background-color: #7FB069; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 13px; " +
            "-fx-background-radius: 6; " +
            "-fx-border-radius: 6; " +
            "-fx-padding: 8 25; " +
            "-fx-cursor: hand;"
        );
        
        
        alert.getDialogPane().setPrefWidth(280);
        alert.getDialogPane().setMaxWidth(300);
        alert.getDialogPane().setPrefHeight(Region.USE_COMPUTED_SIZE);
        alert.getDialogPane().setMaxHeight(250);
        
        
        alert.initOwner(sceneController.getPrimaryStage());
        
        
        alert.show();
        
        
        dialogPane.setOpacity(0);
        Timeline fadeIn = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(dialogPane.opacityProperty(), 0)),
            new KeyFrame(Duration.millis(200), new KeyValue(dialogPane.opacityProperty(), 1))
        );
        fadeIn.play();
    }
}