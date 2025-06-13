package pftrain.views;

import pftrain.controllers.SceneController;
import pftrain.models.DataManager;
import pftrain.models.User;
import pftrain.models.UserSession;
import pftrain.models.IDisplayable;  
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import pftrain.utils.UIComponents;
import java.util.Optional;

public class AdminProfileView {
    private Scene scene;
    private SceneController sceneController;
    private UserSession userSession;
    private DataManager dataManager;

    private Label nameLabel;
    private Button adminAvatar;
    private TextField usernameField;
    private TextField fullNameField;
    private VBox usernameSection;
    private VBox fullNameSection;

    public AdminProfileView(SceneController sceneController) {
        this.sceneController = sceneController;
        this.userSession = UserSession.getInstance();
        this.dataManager = DataManager.getInstance();
        createScene();
    }

    private void createScene() {
        BorderPane profileLayout = new BorderPane();
        profileLayout.setStyle("-fx-background-color: #f8f8f8;");

        HBox header = UIComponents.createSceneHeader("Profil Admin", () -> sceneController.showDashboardScene());
        profileLayout.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f8f8f8; -fx-background-color: #f8f8f8;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        VBox centerContent = new VBox();
        centerContent.setAlignment(Pos.TOP_CENTER);
        centerContent.setSpacing(25);
        centerContent.setPadding(new Insets(40));

        VBox profileHeader = createAdminProfileHeader();
        VBox formContainer = createAdminForm();

        centerContent.getChildren().addAll(profileHeader, formContainer);
        scrollPane.setContent(centerContent);
        profileLayout.setCenter(scrollPane);

        scene = new Scene(profileLayout);
        refresh();
    }

    private VBox createAdminProfileHeader() {
        VBox profileHeader = new VBox();
        profileHeader.setAlignment(Pos.CENTER);
        profileHeader.setSpacing(10);

        adminAvatar = new Button();
        adminAvatar.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        adminAvatar.setOnAction(e -> showAvatarColorDialog());

        nameLabel = new Label();
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        nameLabel.setStyle("-fx-text-fill: #333;");

        
        Label roleLabel;
        User currentUser = userSession.getCurrentUser();
        if (currentUser instanceof IDisplayable) {
            IDisplayable displayableUser = (IDisplayable) currentUser;
            roleLabel = new Label(displayableUser.getDisplayInfo());
        } else {
            roleLabel = new Label("Administrator/Pelatih");
        }
        roleLabel.setFont(Font.font("Arial", 14));
        roleLabel.setStyle("-fx-text-fill: #666;");

        Label avatarInstruction = new Label("Klik avatar untuk mengubah warna");
        avatarInstruction.setFont(Font.font("Arial", 10));
        avatarInstruction.setStyle("-fx-text-fill: #999;");

        VBox nameContainer = new VBox(5, nameLabel, roleLabel);
        nameContainer.setAlignment(Pos.CENTER);

        profileHeader.getChildren().addAll(adminAvatar, nameContainer, avatarInstruction);
        return profileHeader;
    }

    private void showAvatarColorDialog() {
        User currentUser = userSession.getCurrentUser();
        if (currentUser == null) return;

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Pilih Warna Avatar");
        dialog.setHeaderText(null);
        dialog.setResizable(false);
        dialog.getDialogPane().setPrefSize(320, 220);

        dialog.getDialogPane().setStyle("-fx-font-family: Arial; -fx-background-color: #f8fff8; " +
                "-fx-border-color: #7FB069; -fx-border-width: 2; " +
                "-fx-border-radius: 10; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 3);");

        ButtonType saveButtonType = new ButtonType("Pilih", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        VBox content = new VBox(15);
        content.setPadding(new Insets(15, 20, 10, 20));
        content.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("🎨 Pilih Warna Avatar");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        titleLabel.setStyle("-fx-text-fill: #2d5016;");

        GridPane colorGrid = new GridPane();
        colorGrid.setAlignment(Pos.CENTER);
        colorGrid.setHgap(8);
        colorGrid.setVgap(8);

        String[] colors = {
                "#7FB069", "#4A90E2", "#F5A623", "#BD10E0",
                "#B8E986", "#50E3C2", "#D0021B", "#F8E71C",
                "#9013FE", "#FF6B6B", "#4ECDC4", "#45B7D1"
        };
        String[] colorNames = {
                "Hijau", "Biru", "Oranye", "Ungu",
                "Hijau Muda", "Tosca", "Merah", "Kuning",
                "Violet", "Pink", "Cyan", "Biru Muda"
        };

        final String[] selectedColor = {currentUser.getAvatarColor()};

        for (int i = 0; i < colors.length; i++) {
            Button colorButton = new Button();
            String color = colors[i];
            String colorName = colorNames[i];
            colorButton.setPrefSize(35, 35);
            colorButton.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 18; " +
                    "-fx-border-radius: 18; -fx-cursor: hand; " +
                    (color.equals(selectedColor[0]) ? "-fx-border-color: #333; -fx-border-width: 2;" : "-fx-border-color: #ccc; -fx-border-width: 1;"));

            colorButton.setOnAction(e -> {
                selectedColor[0] = color;
                for (var node : colorGrid.getChildren()) {
                    if (node instanceof Button) {
                        Button btn = (Button) node;
                        String btnColorStyle = btn.getStyle().split(";")[0];
                        String btnColor = btnColorStyle.substring(btnColorStyle.indexOf(":") + 1).trim();
                        btn.setStyle("-fx-background-color: " + btnColor + "; -fx-background-radius: 18; " +
                                "-fx-border-radius: 18; -fx-cursor: hand; " +
                                (btnColor.equalsIgnoreCase(color) ? "-fx-border-color: #333; -fx-border-width: 2;" : "-fx-border-color: #ccc; -fx-border-width: 1;"));
                    }
                }
            });
            Tooltip tooltip = new Tooltip(colorName);
            tooltip.setStyle("-fx-font-size: 10px;");
            Tooltip.install(colorButton, tooltip);
            int row = i / 4;
            int col = i % 4;
            colorGrid.add(colorButton, col, row);
        }
        content.getChildren().addAll(titleLabel, colorGrid);
        dialog.getDialogPane().setContent(content);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setStyle("-fx-background-color: #7FB069; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 6 15; -fx-font-size: 11;");
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.setStyle("-fx-background-color: #ccc; -fx-text-fill: #333; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 6 15; -fx-font-size: 11;");
        dialog.setResultConverter(dialogButton -> (dialogButton == saveButtonType) ? selectedColor[0] : null);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newColor -> {
            currentUser.setAvatarColor(newColor);
            dataManager.updateUser(currentUser);
            userSession.setCurrentUser(currentUser);
            showSuccessAlert("Berhasil!", "Warna avatar berhasil diubah!");
            refresh();
        });
    }

    private VBox createAdminForm() {
        VBox formContainer = new VBox(15);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setMaxWidth(320);

        usernameField = createStyledTextField("Username");
        usernameField.setEditable(false);
        usernameField.setStyle(usernameField.getStyle() + "-fx-background-color: #f0f0f0;");
        usernameSection = createFieldSection("Username");
        usernameSection.getChildren().add(usernameField);

        fullNameField = createStyledTextField("Masukkan nama lengkap Anda");
        fullNameSection = createFieldSectionWithError("Nama Lengkap");
        fullNameSection.getChildren().add(1, fullNameField);

        PasswordField passwordDisplayField = new PasswordField();
        passwordDisplayField.setText("••••••••");
        passwordDisplayField.setEditable(false);
        passwordDisplayField.setPrefHeight(45);
        passwordDisplayField.setStyle("-fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-radius: 8; " +
                "-fx-padding: 12; -fx-background-color: #f0f0f0; -fx-font-size: 14px;");
        VBox passwordSection = createFieldSection("Password");
        passwordSection.getChildren().add(passwordDisplayField);

        Hyperlink changePasswordLink = new Hyperlink("Ubah kata sandi");
        changePasswordLink.setFont(Font.font("Arial", 11));
        changePasswordLink.setStyle("-fx-text-fill: #4A5D23; -fx-underline: true;");
        changePasswordLink.setOnAction(e -> showChangePasswordDialog());
        HBox.setMargin(changePasswordLink, new Insets(0, 0, 0, 5));
        
        VBox passwordContainer = new VBox(2, passwordSection, changePasswordLink);
        passwordContainer.setAlignment(Pos.CENTER_LEFT);

        Button saveBtn = UIComponents.createActionButton("Simpan Profile");
        saveBtn.setOnAction(e -> handleSaveChanges());
        
        Button logoutBtn = UIComponents.createSecondaryButton("Logout");
        
        logoutBtn.setOnAction(e -> handleLogout());
        
        HBox buttonContainer = new HBox(15, saveBtn, logoutBtn);
        buttonContainer.setAlignment(Pos.CENTER);
        VBox.setMargin(buttonContainer, new Insets(15, 0, 0, 0));

        formContainer.getChildren().addAll(usernameSection, fullNameSection, passwordContainer, buttonContainer);
        return formContainer;
    }

    
    private void handleLogout() {
        User currentUser = userSession.getCurrentUser();
        if (currentUser == null) return;

        
        currentUser.logout();
        
        
        showLogoutConfirmDialog();
    }

    
    private void showLogoutConfirmDialog() {
        User currentUser = userSession.getCurrentUser();
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Logout");
        alert.setHeaderText(null);
        alert.setContentText(null);
        alert.getDialogPane().setPrefSize(320, 180);
        alert.getDialogPane().setMaxSize(320, 180);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setGraphic(null);
        dialogPane.setStyle(
                "-fx-background-color: #fff8f8; " +
                "-fx-border-color: #FF6B6B; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 3);"
        );

        VBox content = new VBox(12);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20, 25, 15, 25));

        Label iconLabel = new Label("👋");
        iconLabel.setFont(Font.font("Arial", 28));

        Label titleLabel = new Label("Logout Konfirmasi");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: #d63031;");
        titleLabel.setTextAlignment(TextAlignment.CENTER);

        
        String userName = "Admin";
        if (currentUser instanceof IDisplayable) {
            IDisplayable displayableUser = (IDisplayable) currentUser;
            userName = displayableUser.getDisplayName();
        }

        Label messageLabel = new Label("Sampai jumpa, " + userName + "!\nApakah Anda yakin ingin logout?");
        messageLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        messageLabel.setStyle("-fx-text-fill: #2d3436;");
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        messageLabel.setWrapText(true);

        content.getChildren().addAll(iconLabel, titleLabel, messageLabel);
        dialogPane.setContent(content);

        
        ButtonType yesButton = new ButtonType("Ya, Logout", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("Batal", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);

        Button yesBtn = (Button) dialogPane.lookupButton(yesButton);
        yesBtn.setStyle(
                "-fx-background-color: #FF6B6B; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 11px; " +
                "-fx-background-radius: 6; " +
                "-fx-border-radius: 6; " +
                "-fx-padding: 6 15; " +
                "-fx-cursor: hand;"
        );

        Button noBtn = (Button) dialogPane.lookupButton(noButton);
        noBtn.setStyle(
                "-fx-background-color: #ddd; " +
                "-fx-text-fill: #333; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 11px; " +
                "-fx-background-radius: 6; " +
                "-fx-border-radius: 6; " +
                "-fx-padding: 6 15; " +
                "-fx-cursor: hand;"
        );

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            
            userSession.cleanSession();
            sceneController.showWelcomeScene();
        }
        
    }

    private void handleSaveChanges() {
        clearAllFieldErrors(fullNameSection);
        User currentUser = userSession.getCurrentUser();
        if (currentUser == null) return;
        String newFullName = fullNameField.getText().trim();

        if (newFullName.isEmpty()) {
            setFieldError(fullNameField, fullNameSection, "Nama lengkap tidak boleh kosong.");
            return;
        }

        currentUser.setFullName(newFullName);
        dataManager.updateUser(currentUser);
        userSession.setCurrentUser(currentUser);
        showSuccessAlert("Berhasil!", "Profil berhasil diperbarui!");
        refresh();
    }

    private void showChangePasswordDialog() {
        User currentUser = userSession.getCurrentUser();
        if (currentUser == null) return;

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Ubah Kata Sandi");
        dialog.setHeaderText(null);
        dialog.setResizable(false);
        dialog.getDialogPane().setPrefSize(380, 420);
        dialog.getDialogPane().setMaxSize(380, 420);

        dialog.getDialogPane().setStyle("-fx-font-family: Arial; -fx-background-color: #f8fff8; " +
                "-fx-border-color: #7FB069; -fx-border-width: 2; " +
                "-fx-border-radius: 10; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 3);");

        ButtonType saveButtonType = new ButtonType("Ubah Password", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20, 25, 15, 25));
        content.setAlignment(Pos.CENTER_LEFT);

        HBox titleContainer = new HBox(8);
        titleContainer.setAlignment(Pos.CENTER);
        Label iconLabel = new Label("🔒");
        iconLabel.setFont(Font.font("Arial", 20));
        Label titleLabelDialog = new Label("Ubah Kata Sandi");
        titleLabelDialog.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabelDialog.setStyle("-fx-text-fill: #2d5016;");
        titleContainer.getChildren().addAll(iconLabel, titleLabelDialog);

        VBox fieldsContainer = new VBox(5);
        String passwordFieldStyle = "-fx-font-size: 12; -fx-padding: 8 35 8 8; -fx-border-color: #ddd; -fx-border-radius: 4; -fx-background-radius: 4; -fx-background-color: white;";
        String eyeButtonStyle = "-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: #666; -fx-font-size: 12; -fx-cursor: hand;";
        String labelStyle = "-fx-text-fill: #2d5016; -fx-font-weight: bold; -fx-font-size: 12;";
        String errorLabelStyle = "-fx-text-fill: #D8000C; -fx-font-size: 10px;";

        Label oldPasswordLabel = new Label("Kata Sandi Lama:");
        oldPasswordLabel.setStyle(labelStyle);
        StackPane oldPasswordContainer = new StackPane();
        PasswordField oldPasswordField = new PasswordField();
        TextField oldPasswordVisible = new TextField();
        oldPasswordField.setPromptText("Masukkan kata sandi lama");
        oldPasswordVisible.setPromptText("Masukkan kata sandi lama");
        oldPasswordField.setStyle(passwordFieldStyle);
        oldPasswordVisible.setStyle(passwordFieldStyle);
        oldPasswordVisible.setVisible(false);
        Button oldEyeButton = new Button("👁");
        oldEyeButton.setStyle(eyeButtonStyle);
        StackPane.setAlignment(oldEyeButton, Pos.CENTER_RIGHT);
        StackPane.setMargin(oldEyeButton, new Insets(0, 5, 0, 0));
        oldPasswordField.textProperty().bindBidirectional(oldPasswordVisible.textProperty());
        oldEyeButton.setOnAction(e -> togglePasswordVisibility(oldPasswordField, oldPasswordVisible, oldEyeButton));
        oldPasswordContainer.getChildren().addAll(oldPasswordField, oldPasswordVisible, oldEyeButton);
        Label oldPasswordErrorLabel = new Label();
        oldPasswordErrorLabel.setStyle(errorLabelStyle);
        oldPasswordErrorLabel.setVisible(false);

        Label newPasswordLabel = new Label("Kata Sandi Baru:");
        newPasswordLabel.setStyle(labelStyle);
        StackPane newPasswordContainer = new StackPane();
        PasswordField newPasswordField = new PasswordField();
        TextField newPasswordVisible = new TextField();
        newPasswordField.setPromptText("Masukkan kata sandi baru");
        newPasswordVisible.setPromptText("Masukkan kata sandi baru");
        newPasswordField.setStyle(passwordFieldStyle);
        newPasswordVisible.setStyle(passwordFieldStyle);
        newPasswordVisible.setVisible(false);
        Button newEyeButton = new Button("👁");
        newEyeButton.setStyle(eyeButtonStyle);
        StackPane.setAlignment(newEyeButton, Pos.CENTER_RIGHT);
        StackPane.setMargin(newEyeButton, new Insets(0, 5, 0, 0));
        newPasswordField.textProperty().bindBidirectional(newPasswordVisible.textProperty());
        newEyeButton.setOnAction(e -> togglePasswordVisibility(newPasswordField, newPasswordVisible, newEyeButton));
        newPasswordContainer.getChildren().addAll(newPasswordField, newPasswordVisible, newEyeButton);
        Label newPasswordErrorLabel = new Label();
        newPasswordErrorLabel.setStyle(errorLabelStyle);
        newPasswordErrorLabel.setVisible(false);

        Label confirmPasswordLabel = new Label("Konfirmasi Kata Sandi:");
        confirmPasswordLabel.setStyle(labelStyle);
        StackPane confirmPasswordContainer = new StackPane();
        PasswordField confirmPasswordField = new PasswordField();
        TextField confirmPasswordVisible = new TextField();
        confirmPasswordField.setPromptText("Konfirmasi kata sandi baru");
        confirmPasswordVisible.setPromptText("Konfirmasi kata sandi baru");
        confirmPasswordField.setStyle(passwordFieldStyle);
        confirmPasswordVisible.setStyle(passwordFieldStyle);
        confirmPasswordVisible.setVisible(false);
        Button confirmEyeButton = new Button("👁");
        confirmEyeButton.setStyle(eyeButtonStyle);
        StackPane.setAlignment(confirmEyeButton, Pos.CENTER_RIGHT);
        StackPane.setMargin(confirmEyeButton, new Insets(0, 5, 0, 0));
        confirmPasswordField.textProperty().bindBidirectional(confirmPasswordVisible.textProperty());
        confirmEyeButton.setOnAction(e -> togglePasswordVisibility(confirmPasswordField, confirmPasswordVisible, confirmEyeButton));
        confirmPasswordContainer.getChildren().addAll(confirmPasswordField, confirmPasswordVisible, confirmEyeButton);
        Label confirmPasswordErrorLabel = new Label();
        confirmPasswordErrorLabel.setStyle(errorLabelStyle);
        confirmPasswordErrorLabel.setVisible(false);

        fieldsContainer.getChildren().addAll(
                oldPasswordLabel, oldPasswordContainer, oldPasswordErrorLabel,
                newPasswordLabel, newPasswordContainer, newPasswordErrorLabel,
                confirmPasswordLabel, confirmPasswordContainer, confirmPasswordErrorLabel
        );
        content.getChildren().addAll(titleContainer, fieldsContainer);
        dialog.getDialogPane().setContent(content);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setStyle("-fx-background-color: #7FB069; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 20; -fx-font-size: 12;");
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.setStyle("-fx-background-color: #ccc; -fx-text-fill: #333; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 20; -fx-font-size: 12;");

        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            oldPasswordErrorLabel.setVisible(false);
            newPasswordErrorLabel.setVisible(false);
            confirmPasswordErrorLabel.setVisible(false);
            String oldPassword = oldPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            boolean hasError = false;

            if (oldPassword.trim().isEmpty()) {
                oldPasswordErrorLabel.setText("Kata sandi lama tidak boleh kosong!");
                oldPasswordErrorLabel.setVisible(true);
                hasError = true;
            }
            if (newPassword.trim().isEmpty()) {
                newPasswordErrorLabel.setText("Kata sandi baru tidak boleh kosong!");
                newPasswordErrorLabel.setVisible(true);
                hasError = true;
            }
            if (confirmPassword.trim().isEmpty()) {
                confirmPasswordErrorLabel.setText("Konfirmasi kata sandi tidak boleh kosong!");
                confirmPasswordErrorLabel.setVisible(true);
                hasError = true;
            }
            if (hasError) {
                event.consume();
                return;
            }
            if (!oldPassword.equals(currentUser.getPassword())) {
                oldPasswordErrorLabel.setText("Kata sandi lama salah!");
                oldPasswordErrorLabel.setVisible(true);
                event.consume();
                return;
            }
            String passwordError = validatePasswordComplexity(newPassword);
            if (passwordError != null) {
                newPasswordErrorLabel.setText(passwordError);
                newPasswordErrorLabel.setVisible(true);
                event.consume();
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                confirmPasswordErrorLabel.setText("Konfirmasi kata sandi tidak cocok!");
                confirmPasswordErrorLabel.setVisible(true);
                event.consume();
                return;
            }
            currentUser.setPassword(newPassword);
            dataManager.updateUser(currentUser);
            userSession.setCurrentUser(currentUser);
            showSuccessAlert("Berhasil!", "Kata sandi berhasil diubah!");
        });
        dialog.showAndWait();
    }

    private void togglePasswordVisibility(PasswordField passwordField, TextField textField, Button eyeButton) {
        if (passwordField.isVisible()) {
            passwordField.setVisible(false);
            textField.setVisible(true);
            textField.requestFocus();
            eyeButton.setText("🙈");
        } else {
            textField.setVisible(false);
            passwordField.setVisible(true);
            passwordField.requestFocus();
            eyeButton.setText("👁");
        }
    }

    private String validatePasswordComplexity(String password) {
        if (password == null || password.trim().isEmpty()) {
            return "Password tidak boleh kosong!";
        }
        password = password.trim();
        if (password.length() < 8) {
            return "Password harus minimal 8 karakter!";
        }
        if (!password.chars().anyMatch(Character::isUpperCase)) {
            return "Password harus mengandung minimal 1 huruf kapital (A-Z)!";
        }
        if (!password.chars().anyMatch(Character::isLowerCase)) {
            return "Password harus mengandung minimal 1 huruf kecil (a-z)!";
        }
        if (!password.chars().anyMatch(Character::isDigit)) {
            return "Password harus mengandung minimal 1 angka (0-9)!";
        }
        return null;
    }

    public void refresh() {
        User currentUser = userSession.getCurrentUser();
        if (currentUser != null) {
            
            if (currentUser instanceof IDisplayable) {
                IDisplayable displayableUser = (IDisplayable) currentUser;
                nameLabel.setText(displayableUser.getDisplayName()); 
            } else {
                nameLabel.setText(currentUser.getFullName());
            }
            
            String initials = getUserInitials(currentUser.getFullName());
            adminAvatar.setText(initials);
            String avatarColor = currentUser.getAvatarColor() != null ? currentUser.getAvatarColor() : "#4A90E2";
            adminAvatar.setStyle("-fx-background-color: " + avatarColor + "; -fx-text-fill: white; " +
                    "-fx-background-radius: 40; -fx-min-width: 80; -fx-min-height: 80; " +
                    "-fx-max-width: 80; -fx-max-height: 80; -fx-cursor: hand;");
            usernameField.setText(currentUser.getUsername());
            fullNameField.setText(currentUser.getFullName());
        }
    }

    private String getUserInitials(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "A";
        }
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].charAt(0) + "" + parts[1].charAt(0)).toUpperCase();
        } else if (!parts[0].isEmpty()){
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        } else {
            return "A";
        }
    }

    private VBox createFieldSection(String labelText) {
        VBox section = new VBox(8);
        section.setAlignment(Pos.CENTER_LEFT);
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        label.setStyle("-fx-text-fill: #333;");
        section.getChildren().add(label);
        return section;
    }

    private VBox createFieldSectionWithError(String labelText) {
        VBox section = new VBox(5);
        section.setAlignment(Pos.CENTER_LEFT);
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        label.setStyle("-fx-text-fill: #333;");
        Label errorLabel = new Label();
        errorLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
        errorLabel.setStyle("-fx-text-fill: #D8000C;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        section.getChildren().addAll(label, errorLabel);
        return section;
    }

    private TextField createStyledTextField(String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);
        field.setPrefWidth(320);
        field.setPrefHeight(45);
        field.setFont(Font.font("Arial", 14));
        field.setStyle("-fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-radius: 8; " +
                "-fx-padding: 12; -fx-background-color: white;");
        return field;
    }

    private void clearAllFieldErrors(VBox... fieldSections) {
        for (VBox section : fieldSections) {
            TextField field = (TextField) section.getChildren().get(0);
            field.setStyle("-fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-radius: 8; " +
                    "-fx-padding: 12; -fx-background-color: white;");
            Label errorLabel = (Label) section.getChildren().get(2);
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        }
    }

    private void setFieldError(TextField field, VBox fieldSection, String errorMessage) {
        field.setStyle("-fx-background-radius: 8; -fx-border-color: #ff4444; -fx-border-width: 1.5; -fx-border-radius: 8; " +
                "-fx-padding: 12; -fx-background-color: white;");
        if (fieldSection.getChildren().size() > 2) {
            Label errorLabel = (Label) fieldSection.getChildren().get(2);
            errorLabel.setText(errorMessage);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(null);
        alert.getDialogPane().setPrefSize(300, 180);
        alert.getDialogPane().setMaxSize(300, 180);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setGraphic(null);
        dialogPane.setStyle(
                "-fx-background-color: #f8fff8; " +
                        "-fx-border-color: #7FB069; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 3);"
        );
        VBox content = new VBox(12);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20, 25, 15, 25));
        Label iconLabel = new Label("✅");
        iconLabel.setFont(Font.font("Arial", 24));
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: #2d5016;");
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        Label messageLabel = new Label(message);
        messageLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        messageLabel.setStyle("-fx-text-fill: #2d5016;");
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        messageLabel.setWrapText(true);
        content.getChildren().addAll(iconLabel, titleLabel, messageLabel);
        dialogPane.setContent(content);
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setText("OK");
        okButton.setStyle(
                "-fx-background-color: #7FB069; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 12px; " +
                        "-fx-background-radius: 6; " +
                        "-fx-border-radius: 6; " +
                        "-fx-padding: 6 20; " +
                        "-fx-cursor: hand;"
        );
        alert.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }
}