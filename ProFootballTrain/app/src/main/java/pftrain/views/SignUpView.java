package pftrain.views;

import pftrain.controllers.SceneController;
import pftrain.models.DataManager;
import pftrain.models.Player;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
        TextField namaField = createStyledTextField("Masukkan Nama Lengkap");
        namaSection.getChildren().add(namaField);

        VBox tanggalSection = createFieldSectionWithError("Tanggal Lahir");
        DateInputContainer tanggalContainer = new DateInputContainer();
        tanggalSection.getChildren().add(tanggalContainer.getContainer());

        VBox timSection = createFieldSectionWithError("Pilih Tim");
        ComboBox<String> timComboBox = new ComboBox<>();
        timComboBox.getItems().addAll("Tim A", "Tim B");
        timComboBox.setPromptText("Pilih Tim");
        timComboBox.setPrefWidth(350);
        timComboBox.setPrefHeight(50);
        timComboBox.setStyle("-fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-radius: 8; " +
                            "-fx-padding: 12; -fx-background-color: white; -fx-font-family: Arial; -fx-font-size: 14px;");
        timSection.getChildren().add(timComboBox);

        VBox nomorSection = createFieldSectionWithError("Nomor Punggung");
        TextField nomorField = createStyledTextField("Masukkan Nomor Punggung");
        nomorField.textProperty().addListener((obs, oldV, newV) -> {
            if (!newV.matches("\\d*")) {
                nomorField.setText(newV.replaceAll("[^\\d]", ""));
            }
        });
        nomorSection.getChildren().add(nomorField);

        VBox posisiSection = createFieldSectionWithError("Posisi");
        TextField posisiField = createStyledTextField("Contoh: Midfielder, Goalkeeper");
        posisiSection.getChildren().add(posisiField);

        
        Button signUpBtn = createActionButton("Sign Up");
        signUpBtn.setOnAction(e -> {
            
            clearAllFieldErrors(namaSection, nomorSection, posisiSection, tanggalSection, timSection);
            tanggalContainer.clearError();

            
            if (validateSignUpWithFieldErrors(namaField, tanggalContainer.getSelectedDate(), nomorField, posisiField, timComboBox,
                                             namaSection, tanggalSection, nomorSection, posisiSection, timSection, tanggalContainer)) {

                String namaLengkap = namaField.getText();
                int nomorPunggung = Integer.parseInt(nomorField.getText());
                String posisi = posisiField.getText();
                String selectedTeam = timComboBox.getSelectionModel().getSelectedItem();
                
                generateCredentials(namaLengkap, tanggalContainer.getSelectedDate());

                Player newUser = new Player(generatedUsername, generatedPassword, namaLengkap, 
                                             tanggalContainer.getSelectedDate(), posisi, nomorPunggung, selectedTeam);
                dataManager.addUser(newUser);

                createSuccessScene();
                sceneController.getPrimaryStage().setScene(successScene);
            }
        });

        
        HBox loginLinkBox = new HBox();
        loginLinkBox.setAlignment(Pos.CENTER);
        loginLinkBox.setSpacing(5);

        Label alreadyLabel = new Label("Sudah punya akun?");
        alreadyLabel.setFont(Font.font("Arial", 14));
        alreadyLabel.setStyle("-fx-text-fill: #666;");

        Hyperlink loginLink = new Hyperlink("Log In");
        loginLink.setFont(Font.font("Arial", 14));
        loginLink.setStyle("-fx-text-fill: #4A5D23;");
        loginLink.setOnAction(e -> sceneController.showLoginScene());

        loginLinkBox.getChildren().addAll(alreadyLabel, loginLink);

        formContainer.getChildren().addAll(namaSection, tanggalSection, timSection, nomorSection, posisiSection, 
                                          signUpBtn, loginLinkBox);
        
        centerContent.getChildren().addAll(titleLabel, formContainer);
        scrollPane.setContent(centerContent);
        signUpLayout.setCenter(scrollPane);

        signUpScene = new Scene(signUpLayout, 450, 700);
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

        
        Label successTitle = new Label("Sign Up Berhasil!");
        successTitle.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        successTitle.setStyle("-fx-text-fill: #4A5D23;");

        
        StackPane iconContainer = createAnimatedSuccessIcon();
        iconContainer.setPrefSize(120, 120);

        
        Label credentialsTitle = new Label("Kredensial Anda untuk Login:");
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
        passwordContainer.setSpacing(10);

        TextField passwordField = createStyledTextField("");
        passwordField.setText(generatedPassword);
        passwordField.setEditable(false);
        passwordField.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ddd; " +
                              "-fx-border-radius: 8; -fx-padding: 12;");
        HBox.setHgrow(passwordField, Priority.ALWAYS);

        Button copyButton = new Button("📋");
        copyButton.setStyle("-fx-background-color: #4A5D23; -fx-text-fill: white; " +
                           "-fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 18px;");
        copyButton.setPrefHeight(50);
        copyButton.setMinWidth(50);
        copyButton.setOnAction(e -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(generatedPassword);
            clipboard.setContent(content);
            showCustomCopyAlert("Password berhasil disalin!", generatedPassword);
        });

        passwordContainer.getChildren().addAll(passwordField, copyButton);
        passwordSection.getChildren().add(passwordContainer);

        credentialsContainer.getChildren().addAll(usernameSection, passwordSection);

        
        Button loginButton = createActionButton("Lanjut ke Halaman Login");
        loginButton.setOnAction(e -> sceneController.showLoginScene());

        centerContent.getChildren().addAll(successTitle, iconContainer, credentialsTitle, 
                                          credentialsContainer, loginButton);

        scrollPane.setContent(centerContent);
        successLayout.setCenter(scrollPane);

        successScene = new Scene(successLayout, 450, 700);
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT); 
        header.setPadding(new Insets(10, 20, 10, 20));
        header.setStyle("-fx-background-color: white; -fx-border-color: #eee; -fx-border-width: 0 0 1 0;");

        Image logoImage = new Image(getClass().getResourceAsStream("/images/logo.png"));
        ImageView logoImageView = new ImageView(logoImage);
        logoImageView.setFitHeight(40);
        logoImageView.setPreserveRatio(true);
        
        Region spacer = new Region();

        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label brandLabel = new Label("ProFootballTrain");
        brandLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        brandLabel.setStyle("-fx-text-fill: #4A5D23;");

        header.getChildren().addAll(logoImageView, spacer, brandLabel);
        
        return header;
    }

    private HBox createSuccessHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 20, 10, 20));
        header.setStyle("-fx-background-color: white; -fx-border-color: #eee; -fx-border-width: 0 0 1 0;");

        Button backBtn = new Button("←");
        backBtn.setFont(Font.font(18));
        backBtn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; " +
                            "-fx-text-fill: #666; -fx-cursor: hand;");
        backBtn.setOnAction(e -> sceneController.showLoginScene());

        Image logoImage = new Image(getClass().getResourceAsStream("/images/logo.png"));
        ImageView logoImageView = new ImageView(logoImage);
        logoImageView.setFitHeight(40);
        logoImageView.setPreserveRatio(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label brandLabel = new Label("ProFootballTrain");
        brandLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        brandLabel.setStyle("-fx-text-fill: #4A5D23;");

        header.getChildren().addAll(backBtn, logoImageView, spacer, brandLabel);
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

    private Button createActionButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(350);
        button.setPrefHeight(50);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        button.setStyle("-fx-background-color: #7FB069; -fx-text-fill: white; " +
                        "-fx-background-radius: 8; -fx-cursor: hand;");
        return button;
    }

    private static class DateInputContainer {
        private HBox container;
        private ComboBox<Integer> dayBox;
        private ComboBox<String> monthBox;
        private ComboBox<Integer> yearBox;

        public DateInputContainer() {
            createContainer();
        }

        private void createContainer() {
            container = new HBox();
            container.setSpacing(10);
            container.setAlignment(Pos.CENTER_LEFT);

            String CbStyle = "-fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-radius: 8; -fx-background-color: white; -fx-font-family: Arial;";

            dayBox = new ComboBox<>();
            dayBox.setPromptText("Hari");
            dayBox.setPrefHeight(50);
            dayBox.setStyle(CbStyle);

            monthBox = new ComboBox<>();
            String[] months = {"Januari", "Februari", "Maret", "April", "Mei", "Juni",
                               "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
            monthBox.getItems().addAll(months);
            monthBox.setPromptText("Bulan");
            monthBox.setPrefHeight(50);
            monthBox.setStyle(CbStyle);

            yearBox = new ComboBox<>();
            for (int i = LocalDate.now().getYear(); i >= 1950; i--) {
                yearBox.getItems().add(i);
            }
            yearBox.setPromptText("Tahun");
            yearBox.setPrefHeight(50);
            yearBox.setStyle(CbStyle);

            dayBox.prefWidthProperty().bind(container.widthProperty().multiply(0.25));
            monthBox.prefWidthProperty().bind(container.widthProperty().multiply(0.40));
            yearBox.prefWidthProperty().bind(container.widthProperty().multiply(0.35));

            monthBox.setOnAction(e -> updateDayOptions());
            yearBox.setOnAction(e -> updateDayOptions());

            container.getChildren().addAll(dayBox, monthBox, yearBox);
        }

        private void updateDayOptions() {
            if (monthBox.getValue() == null || yearBox.getValue() == null) return;

            int month = monthBox.getSelectionModel().getSelectedIndex() + 1;
            int year = yearBox.getValue();
            int maxDays = LocalDate.of(year, month, 1).lengthOfMonth();

            Integer selectedDay = dayBox.getValue();
            dayBox.getItems().clear();
            for (int i = 1; i <= maxDays; i++) {
                dayBox.getItems().add(i);
            }
            if (selectedDay != null && selectedDay <= maxDays) {
                dayBox.setValue(selectedDay);
            }
        }

        public HBox getContainer() { return container; }

        public void setError(String errorMessage) {
            String errorStyle = "-fx-background-radius: 8; -fx-border-color: #ff4444; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-color: white;";
            dayBox.setStyle(errorStyle);
            monthBox.setStyle(errorStyle);
            yearBox.setStyle(errorStyle);
        }

        public void clearError() {
            String normalStyle = "-fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-radius: 8; -fx-background-color: white;";
            dayBox.setStyle(normalStyle);
            monthBox.setStyle(normalStyle);
            yearBox.setStyle(normalStyle);
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
                return null;
            }
            return null;
        }
    }

    
    private void generateCredentials(String namaLengkap, LocalDate tanggalLahir) {
        String nama = namaLengkap.split("\\s+")[0].toLowerCase().replaceAll("[^a-z]", "");
        String tanggal = tanggalLahir.format(DateTimeFormatter.ofPattern("ddMM"));
        
        generatedUsername = nama + tanggal + "@pemain";
        
        
        StringBuilder password = new StringBuilder();
        
        
        String[] namaParts = namaLengkap.trim().split("\\s+");
        for (String part : namaParts) {
            if (!part.isEmpty()) {
                
                int takeLength = Math.min(part.length(), namaParts.length > 2 ? 2 : 3);
                String partAbbrev = part.substring(0, 1).toUpperCase() + 
                                  (takeLength > 1 ? part.substring(1, takeLength).toLowerCase() : "");
                password.append(partAbbrev);
            }
        }
        
        
        int dateBasedNum = (tanggalLahir.getDayOfMonth() + tanggalLahir.getMonthValue()) * 
                          (tanggalLahir.getYear() % 100);
        int finalNum = (dateBasedNum % 89) + 10; 
        password.append(finalNum);
        
        
        while (password.length() < 8) {
            int randomDigit = (int) (Math.random() * 10);
            password.append(randomDigit);
        }
        
        
        if (password.length() > 10) {
            password = new StringBuilder(password.substring(0, 8));
            
            password.append(String.format("%02d", finalNum % 100));
        }
        
        generatedPassword = password.toString();
    }

    private StackPane createAnimatedSuccessIcon() {
        StackPane container = new StackPane();
        container.setAlignment(Pos.CENTER);
        
        Circle successCircle = new Circle(60, Color.web("#7FB069"));
        successCircle.setOpacity(0);
        
        Label checkmark = new Label("✓");
        checkmark.setFont(Font.font("Arial", FontWeight.BOLD, 60));
        checkmark.setStyle("-fx-text-fill: white;");
        checkmark.setOpacity(0);
        
        container.getChildren().addAll(successCircle, checkmark);
        
        createSuccessAnimation(successCircle, checkmark).play();
        
        return container;
    }
    
    private Timeline createSuccessAnimation(Circle circle, Label checkmark) {
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
            new KeyFrame(Duration.millis(500),
                new KeyValue(checkmark.opacityProperty(), 0),
                new KeyValue(checkmark.scaleXProperty(), 0),
                new KeyValue(checkmark.scaleYProperty(), 0)
            ),
            new KeyFrame(Duration.millis(800),
                new KeyValue(checkmark.opacityProperty(), 1),
                new KeyValue(checkmark.scaleXProperty(), 1.2),
                new KeyValue(checkmark.scaleYProperty(), 1.2)
            ),
            new KeyFrame(Duration.millis(1000),
                new KeyValue(checkmark.scaleXProperty(), 1.0),
                new KeyValue(checkmark.scaleYProperty(), 1.0)
            )
        );
        
        circleAnimation.play();
        checkmarkAnimation.play();
        
        return checkmarkAnimation;
    }

    private void clearAllFieldErrors(VBox... fieldSections) {
        for (VBox section : fieldSections) {
            if (section.getChildren().size() > 2 && section.getChildren().get(2) instanceof TextField) {
                TextField field = (TextField) section.getChildren().get(2);
                field.setStyle("-fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-radius: 8; " +
                             "-fx-padding: 12; -fx-background-color: white;");
            } 

            else if (section.getChildren().size() > 2 && section.getChildren().get(2) instanceof ComboBox) {
                ComboBox<?> comboBox = (ComboBox<?>) section.getChildren().get(2);
                comboBox.setStyle("-fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-radius: 8; " +
                                "-fx-padding: 12; -fx-background-color: white; -fx-font-family: Arial; -fx-font-size: 14px;");
            }

            else if (section.getChildren().size() > 2 && section.getChildren().get(2) instanceof HBox) {
                HBox dateHBox = (HBox) section.getChildren().get(2);
                dateHBox.getChildren().forEach(node -> {
                    if (node instanceof ComboBox) {
                        ((ComboBox<?>) node).setStyle("-fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-radius: 8; -fx-background-color: white;");
                    }
                });
            }

            Label errorLabel = (Label) section.getChildren().get(1);
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        }
    }

    private void setFieldError(TextField field, VBox fieldSection, String errorMessage) {
        field.setStyle("-fx-background-radius: 8; -fx-border-color: #ff4444; -fx-border-width: 2; -fx-border-radius: 8; " +
                       "-fx-padding: 12; -fx-background-color: white;");
        Label errorLabel = (Label) fieldSection.getChildren().get(1);
        errorLabel.setText("✖ " + errorMessage);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void setComboBoxError(ComboBox<?> comboBox, VBox fieldSection, String errorMessage) {
        comboBox.setStyle("-fx-background-radius: 8; -fx-border-color: #ff4444; -fx-border-width: 2; -fx-border-radius: 8; " +
                         "-fx-padding: 12; -fx-background-color: white; -fx-font-family: Arial; -fx-font-size: 14px;");
        Label errorLabel = (Label) fieldSection.getChildren().get(1);
        errorLabel.setText("✖ " + errorMessage);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void setDateError(VBox dateSection, DateInputContainer dateContainer, String errorMessage) {
        dateContainer.setError(errorMessage);
        Label errorLabel = (Label) dateSection.getChildren().get(1);
        errorLabel.setText("✖ " + errorMessage);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private boolean validateSignUpWithFieldErrors(TextField namaField, LocalDate tanggal, TextField nomorField, TextField posisiField, ComboBox<String> timComboBox,
                                                  VBox namaSection, VBox tanggalSection, VBox nomorSection, VBox posisiSection, VBox timSection,
                                                  DateInputContainer dateContainer) {
        boolean isValid = true;

        if (namaField.getText().trim().isEmpty()) {
            setFieldError(namaField, namaSection, "Nama lengkap harus diisi");
            isValid = false;
        }

        if (tanggal == null) {
            setDateError(tanggalSection, dateContainer, "Tanggal lahir harus lengkap");
            isValid = false;
        } else if (tanggal.isAfter(LocalDate.now())) {
            setDateError(tanggalSection, dateContainer, "Tanggal lahir tidak boleh di masa depan");
            isValid = false;
        }

        if (timComboBox.getSelectionModel().getSelectedItem() == null) {
            setComboBoxError(timComboBox, timSection, "Pilih tim terlebih dahulu");
            isValid = false;
        }
        
        if (nomorField.getText().trim().isEmpty()) {
            setFieldError(nomorField, nomorSection, "Nomor punggung harus diisi");
            isValid = false;
        } else {
            try {
                int nomor = Integer.parseInt(nomorField.getText().trim());
                if (nomor <= 0) {
                    setFieldError(nomorField, nomorSection, "Nomor punggung harus angka positif");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                setFieldError(nomorField, nomorSection, "Nomor punggung harus berupa angka");
                isValid = false;
            }
        }

        if (posisiField.getText().trim().isEmpty()) {
            setFieldError(posisiField, posisiSection, "Posisi harus diisi");
            isValid = false;
        }

        if (tanggal != null && !namaField.getText().trim().isEmpty()) {
            String tempUsername = namaField.getText().split("\\s+")[0].toLowerCase().replaceAll("[^a-z]", "") + 
                                  tanggal.format(DateTimeFormatter.ofPattern("ddMM")) + "@pemain";

            if (dataManager.getUserByUsername(tempUsername) != null) {
                setFieldError(namaField, namaSection, "Username sudah ada. Coba nama atau tanggal lahir lain.");
                isValid = false;
            }
        }

        return isValid;
    }

    
    private void showCustomCopyAlert(String message, String copiedText) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Sukses");
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
            "-fx-background-color: #f8fff8; " +
            "-fx-border-color: #7FB069; -fx-border-width: 2; -fx-border-radius: 12; " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        VBox content = new VBox(12);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(25, 30, 20, 30));
        
        Label iconLabel = new Label("✅");
        iconLabel.setFont(Font.font("Arial", 24));
        
        Label messageLabel = new Label(message);
        messageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        messageLabel.setStyle("-fx-text-fill: #2d5016;");
        
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().add(okButtonType);

        Button okButton = (Button) dialogPane.lookupButton(okButtonType);
        okButton.setStyle(
            "-fx-background-color: #7FB069; -fx-text-fill: white; -fx-font-weight: bold; " +
            "-fx-font-size: 13px; -fx-background-radius: 6; -fx-padding: 8 25; -fx-cursor: hand;"
        );
        
        content.getChildren().addAll(iconLabel, messageLabel);
        dialogPane.setContent(content);
        
        alert.initOwner(sceneController.getPrimaryStage());
        alert.show();
    }

    public Scene getSignUpScene() {
        return signUpScene;
    }
}