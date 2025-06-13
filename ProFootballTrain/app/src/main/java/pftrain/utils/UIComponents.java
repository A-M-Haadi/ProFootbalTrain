package pftrain.utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;


public class UIComponents {

    public static Button createWelcomeButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(300);
        button.setPrefHeight(50);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        String defaultStyle = "-fx-background-color: #7FB069; -fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: #6B9A57; -fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand;";
        
        button.setStyle(defaultStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(defaultStyle));
        
        return button;
    }

    public static Button createActionButton(String text) {
        Button button = new Button(text);
        button.setPrefHeight(45);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        String defaultStyle = "-fx-background-color: #4A5D23; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: #3e4f1c; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;";
        
        button.setStyle(defaultStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(defaultStyle));
        
        return button;
    }

    public static Button createSecondaryButton(String text) {
        Button button = new Button(text);
        button.setPrefHeight(40);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        String defaultStyle = "-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: #e05a5a; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;";
        
        button.setStyle(defaultStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(defaultStyle));
        
        return button;
    }

    public static Button createCardButton(String text, String bgColor, String textColor) {
        Button button = new Button(text);
        button.setPrefWidth(80);
        button.setPrefHeight(30);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        button.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: %s; -fx-background-radius: 6; -fx-cursor: hand;", bgColor, textColor));
        
        String defaultStyle = String.format("-fx-background-color: %s; -fx-text-fill: %s; -fx-background-radius: 6; -fx-cursor: hand;", bgColor, textColor);
        String hoverStyle = String.format("-fx-background-color: %s; -fx-text-fill: %s; -fx-background-radius: 6; -fx-cursor: hand; -fx-opacity: 0.8;", bgColor, textColor);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(defaultStyle));

        return button;
    }

    public static TextField createStyledTextField(String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);
        field.setPrefWidth(280);
        field.setPrefHeight(45);
        field.setFont(Font.font("Arial", 14));
        field.setStyle("-fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-radius: 8; -fx-padding: 12; -fx-background-color: white;");
        return field;
    }

    public static PasswordField createStyledPasswordField(String placeholder) {
        PasswordField field = new PasswordField();
        field.setPromptText(placeholder);
        field.setPrefWidth(280);
        field.setPrefHeight(45);
        field.setFont(Font.font("Arial", 14));
        field.setStyle("-fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-radius: 8; -fx-padding: 12; -fx-background-color: white;");
        return field;
    }

    public static HBox createSceneHeader(String title, Runnable backAction) {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: white; -fx-border-color: #eee; -fx-border-width: 0 0 1 0;");

        Button backBtn = new Button("←");
        backBtn.setFont(Font.font(18));
        backBtn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: #666; -fx-cursor: hand;");
        backBtn.setOnAction(e -> backAction.run());

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setStyle("-fx-text-fill: #4A5D23;");
        
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        
        Region placeholder = new Region();
        placeholder.setPrefWidth(backBtn.getPrefWidth());
        
        header.getChildren().addAll(backBtn, spacer1, titleLabel, spacer2, placeholder);
        
        return header;
    }

    public static HBox createBottomNavigation(Runnable homeAction, Runnable jadwalAction, Runnable absensiAction, Runnable profileAction) {
        HBox bottomNav = new HBox();
        bottomNav.setAlignment(Pos.CENTER);
        bottomNav.setPadding(new Insets(10, 0, 10, 0));
        bottomNav.setStyle("-fx-background-color: white; -fx-border-color: #eee; -fx-border-width: 1 0 0 0;");
        
        Button homeBtn = createNavButton("🏠", "Home");
        Button jadwalBtn = createNavButton("📅", "Jadwal");
        Button absensiBtn = createNavButton("✅", "Absensi");
        Button profileBtn = createNavButton("👤", "Profil");
        
        HBox.setHgrow(homeBtn, Priority.ALWAYS);
        HBox.setHgrow(jadwalBtn, Priority.ALWAYS);
        HBox.setHgrow(absensiBtn, Priority.ALWAYS);
        HBox.setHgrow(profileBtn, Priority.ALWAYS);
        
        homeBtn.setOnAction(e -> homeAction.run());
        jadwalBtn.setOnAction(e -> jadwalAction.run());
        absensiBtn.setOnAction(e -> absensiAction.run());
        profileBtn.setOnAction(e -> profileAction.run());
        
        bottomNav.getChildren().addAll(homeBtn, jadwalBtn, absensiBtn, profileBtn);
        return bottomNav;
    }
    
    private static Button createNavButton(String icon, String text) {
        VBox navItem = new VBox(5);
        navItem.setAlignment(Pos.CENTER);

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(20));
        
        Label textLabel = new Label(text);
        textLabel.setFont(Font.font("Arial", 11));
        
        navItem.getChildren().addAll(iconLabel, textLabel);
        
        Button navBtn = new Button();
        navBtn.setGraphic(navItem);
        navBtn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-cursor: hand;");
        navBtn.setPrefWidth(Double.MAX_VALUE);
        
        setNavButtonActive(navBtn, false);

        return navBtn;
    }

    public static void showCustomInfoDialog(Stage owner, String title, Node content) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle(title);

        // 2. Buat DialogPane kustom
        DialogPane dialogPane = new DialogPane();
        dialogPane.setPrefWidth(350);
        dialogPane.setStyle(
            "-fx-background-color: #f0f8ff; " + 
            "-fx-border-color: #a0c4e4; -fx-border-width: 1.5; " +
            "-fx-border-radius: 12; -fx-background-radius: 12;"
        );

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-border-color: #d0e0f0; -fx-border-width: 0 0 1 0;");

        Label iconLabel = new Label("ℹ");
        iconLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setStyle("-fx-text-fill: #004080;");

        header.getChildren().addAll(iconLabel, titleLabel);
        dialogPane.setHeader(header);

        VBox contentWrapper = new VBox(content);
        contentWrapper.setPadding(new Insets(20));
        dialogPane.setContent(contentWrapper);

        ButtonType closeButtonType = new ButtonType("Tutup", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().add(closeButtonType);

        Button closeButton = (Button) dialogPane.lookupButton(closeButtonType);
        closeButton.setStyle(
            "-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold; " +
            "-fx-font-size: 13px; -fx-background-radius: 8; -fx-padding: 8 25; -fx-cursor: hand;"
        );
        closeButton.setOnMouseEntered(e -> closeButton.setStyle(
            "-fx-background-color: #0056b3; -fx-text-fill: white; -fx-font-weight: bold; " +
            "-fx-font-size: 13px; -fx-background-radius: 8; -fx-padding: 8 25; -fx-cursor: hand;"
        ));
        closeButton.setOnMouseExited(e -> closeButton.setStyle(
            "-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold; " +
            "-fx-font-size: 13px; -fx-background-radius: 8; -fx-padding: 8 25; -fx-cursor: hand;"
        ));

        dialog.setDialogPane(dialogPane);
        dialog.showAndWait();
    }

    public static void setNavButtonActive(Button button, boolean isActive) {
        if (button.getGraphic() instanceof VBox) {
            VBox navItem = (VBox) button.getGraphic();
            if (navItem.getChildren().size() > 1) {
                Label iconLabel = (Label) navItem.getChildren().get(0);
                Label textLabel = (Label) navItem.getChildren().get(1);

                String activeColor = "#4A5D23";
                String inactiveColor = "#999999";

                iconLabel.setStyle("-fx-text-fill: " + (isActive ? activeColor : inactiveColor) + ";");
                textLabel.setStyle("-fx-text-fill: " + (isActive ? activeColor : inactiveColor) + ";");
                textLabel.setFont(Font.font("Arial", isActive ? FontWeight.BOLD : FontWeight.NORMAL, 11));
            }
        }
    }

    public static VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setPrefSize(100, 90);
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        valueLabel.setStyle("-fx-text-fill: white;");
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 12));
        titleLabel.setStyle("-fx-text-fill: white;");
        
        card.getChildren().addAll(valueLabel, titleLabel);
        return card;
    }

    public static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(title);
        
        DialogPane dialogPane = alert.getDialogPane();
        String iconText;
        String borderColor;
        String titleColor;

        if (type == Alert.AlertType.INFORMATION || type == Alert.AlertType.CONFIRMATION) {
            iconText = "✅";
            borderColor = "#7FB069";
            titleColor = "#2d5016";
        } else if (type == Alert.AlertType.ERROR) {
            iconText = "❌";
            borderColor = "#ff6b6b";
            titleColor = "#D32F2F";
        } else {
            iconText = "⚠️";
            borderColor = "#F5A623";
            titleColor = "#b0700c";
        }

        dialogPane.setStyle(
            "-fx-background-color: #f8fff8; " +
            "-fx-border-color: " + borderColor + "; -fx-border-width: 2; -fx-border-radius: 12; " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        VBox content = new VBox(12);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(25, 30, 20, 30));
        
        Label iconLabel = new Label(iconText);
        iconLabel.setFont(Font.font("Arial", 30));
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: " + titleColor + ";");
        
        Label messageLabel = new Label(message);
        messageLabel.setFont(Font.font("Arial", 13));
        messageLabel.setWrapText(true);
        messageLabel.setTextAlignment(TextAlignment.CENTER);

        content.getChildren().addAll(iconLabel, titleLabel, messageLabel);
        dialogPane.setContent(content);
        
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().add(okButtonType);
        
        Button okButton = (Button) dialogPane.lookupButton(okButtonType);
        okButton.setStyle(
            "-fx-background-color: " + borderColor + "; -fx-text-fill: white; -fx-font-weight: bold; " +
            "-fx-font-size: 13px; -fx-background-radius: 6; -fx-padding: 8 25; -fx-cursor: hand;"
        );
        
        alert.showAndWait();
    }
}