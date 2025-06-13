package pftrain.views;

import pftrain.controllers.SceneController;
import pftrain.models.Absensi;
import pftrain.models.DataManager;
import pftrain.models.JadwalLatihan;
import pftrain.models.User;
import pftrain.models.Player;
import pftrain.models.UserSession;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.util.stream.Collectors;

public class AbsensiView {

    private Scene scene;
    private SceneController sceneController;
    private UserSession userSession;
    private DataManager dataManager;

    private Map<String, String> attendanceStatus = new HashMap<>();

    private VBox attendanceListContainer;
    private Button saveBtn;
    private ComboBox<JadwalLatihan> jadwalCombo;
    private JadwalLatihan selectedJadwal;

    private Button homeNavBtnField;
    private Button jadwalNavBtnField;
    private Button absensiNavBtnField;
    private Button profileNavBtnField;

    public AbsensiView(SceneController sceneController) {
        this.sceneController = sceneController;
        this.userSession = UserSession.getInstance();
        this.dataManager = DataManager.getInstance();
        createScene();
    }

    private void createScene() {
        BorderPane absensiLayout = new BorderPane();
        absensiLayout.setStyle("-fx-background-color: #f5f5f5;");

        
        HBox header = createHeader();
        absensiLayout.setTop(header);

        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f5f5f5; -fx-background-color: #f5f5f5;");

        VBox content = new VBox();
        content.setSpacing(20);
        content.setPadding(new Insets(20));

        User currentUser = userSession.getCurrentUser();
        if (currentUser != null && "admin".equals(currentUser.getRole())) {
            
            VBox filterSection = createFilterSection();
            attendanceListContainer = new VBox(20);
            content.getChildren().addAll(filterSection, attendanceListContainer);
        } else {
            Label restrictedLabel = new Label("Hanya pelatih yang dapat mengakses halaman absensi");
            restrictedLabel.setFont(Font.font("Arial", 16));
            restrictedLabel.setStyle("-fx-text-fill: #666; -fx-padding: 50;");
            content.getChildren().add(restrictedLabel);
        }

        scrollPane.setContent(content);
        absensiLayout.setCenter(scrollPane);

        
        VBox bottomNav = createBottomNavigation();
        absensiLayout.setBottom(bottomNav);

        scene = new Scene(absensiLayout);
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20));
        header.setSpacing(15);
        header.setStyle("-fx-background-color: white; -fx-border-color: #eee; -fx-border-width: 0 0 1 0;");

        Button backBtn = new Button("←");
        backBtn.setFont(Font.font(18));
        backBtn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; " +
                        "-fx-text-fill: #666; -fx-cursor: hand;");
        backBtn.setOnAction(e -> sceneController.showDashboardScene());

        Label titleLabel = new Label("Absensi");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setStyle("-fx-text-fill: #7FB069;");

        header.getChildren().addAll(backBtn, titleLabel);
        return header;
    }

    private VBox createFilterSection() {
        VBox filterSection = new VBox();
        filterSection.setSpacing(15);

        
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search");
        searchField.setPrefHeight(45);
        searchField.setStyle("-fx-background-radius: 25; -fx-border-radius: 25; " +
                            "-fx-border-color: #ccc; -fx-border-width: 1; " +
                            "-fx-padding: 0 20; -fx-background-color: white;");

        
        Label pilihJadwalLabel = new Label("Pilih Jadwal Latihan:");
        pilihJadwalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        pilihJadwalLabel.setStyle("-fx-text-fill: #333;");

        jadwalCombo = new ComboBox<>();
        jadwalCombo.setPromptText("Pilih Jadwal...");
        jadwalCombo.setPrefWidth(Double.MAX_VALUE);
        jadwalCombo.setPrefHeight(40);
        jadwalCombo.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; " +
                           "-fx-border-color: #ccc; -fx-background-color: white;");
        jadwalCombo.getItems().addAll(dataManager.getJadwalList());

        jadwalCombo.setConverter(new StringConverter<JadwalLatihan>() {
            @Override
            public String toString(JadwalLatihan jadwal) {
                return jadwal == null ? "" : jadwal.getNamaKegiatan() + " (" + jadwal.getTanggal() + ")";
            }

            @Override
            public JadwalLatihan fromString(String string) {
                return null;
            }
        });

        jadwalCombo.setOnAction(e -> {
            selectedJadwal = jadwalCombo.getValue();
            if (selectedJadwal != null) {
                displayAttendanceForJadwal(selectedJadwal);
            } else {
                attendanceListContainer.getChildren().clear();
                attendanceStatus.clear();
                Label chooseJadwalLabel = new Label("Silakan pilih jadwal untuk melihat daftar pemain.");
                chooseJadwalLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 14; -fx-padding: 20;");
                attendanceListContainer.getChildren().add(chooseJadwalLabel);
                attendanceListContainer.setAlignment(Pos.CENTER);
            }
        });

        filterSection.getChildren().addAll(searchField, pilihJadwalLabel, jadwalCombo);
        return filterSection;
    }

    private ComboBox<String> createFilterCombo(String prompt) {
        ComboBox<String> combo = new ComboBox<>();
        combo.setPromptText(prompt);
        combo.setPrefWidth(80);
        combo.setPrefHeight(35);
        combo.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; " +
                      "-fx-border-color: #ccc; -fx-background-color: white;");
        return combo;
    }

    private void displayAttendanceForJadwal(JadwalLatihan jadwal) {
        attendanceListContainer.getChildren().clear();
        attendanceStatus.clear();

        List<Player> allPlayers = dataManager.getPlayers();

        List<Absensi> existingAbsensi = dataManager.getAbsensiByJadwal(jadwal.getId());
        Map<String, String> existingStatusMap = new HashMap<>();
        for (Absensi absensi : existingAbsensi) {
            existingStatusMap.put(absensi.getUsernamePemain(), absensi.getStatusKehadiran());
        }

        List<Player> filteredPlayersByTeam = allPlayers.stream()
            .filter(player -> "kedua_tim".equals(jadwal.getTim()) || jadwal.getTim().equals(player.getTim()))
            .collect(Collectors.toList());

        if (filteredPlayersByTeam.isEmpty()) {
            Label noPlayersLabel = new Label("Tidak ada pemain untuk jadwal\nini atau tim yang cocok.");
            noPlayersLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 14; -fx-padding: 20;");
            attendanceListContainer.getChildren().add(noPlayersLabel);
            attendanceListContainer.setAlignment(Pos.CENTER);
            if (saveBtn != null) saveBtn.setDisable(true);
            return;
        } else {
            if (saveBtn != null) saveBtn.setDisable(false);
        }

        
        Map<Character, List<Player>> groupedPlayers = filteredPlayersByTeam.stream()
            .sorted(Comparator.comparing(User::getFullName, String.CASE_INSENSITIVE_ORDER))
            .collect(Collectors.groupingBy(user -> Character.toUpperCase(user.getFullName().charAt(0))));

        for (char letter = 'A'; letter <= 'Z'; letter++) {
            List<Player> playersInGroup = groupedPlayers.get(letter);
            if (playersInGroup != null && !playersInGroup.isEmpty()) {
                VBox letterSection = createLetterSection(letter, playersInGroup, existingStatusMap);
                attendanceListContainer.getChildren().add(letterSection);
            }
        }

        
        HBox saveButtonContainer = new HBox();
        saveButtonContainer.setAlignment(Pos.CENTER_RIGHT);
        saveButtonContainer.setPadding(new Insets(20, 0, 0, 0));

        saveBtn = new Button("💾 Save Changes");
        saveBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        saveBtn.setStyle("-fx-background-color: #4A5D23; -fx-text-fill: white; " +
                        "-fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");
        saveBtn.setOnAction(e -> saveAttendanceChanges());

        saveButtonContainer.getChildren().add(saveBtn);
        attendanceListContainer.getChildren().add(saveButtonContainer);
    }

    private VBox createLetterSection(char letter, List<Player> players, Map<String, String> existingStatusMap) {
        VBox section = new VBox();
        section.setSpacing(0);

        
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(10, 15, 10, 15));
        headerBox.setStyle("-fx-background-color: #B8D4B8; -fx-background-radius: 8 8 0 0;");

        Label letterLabel = new Label(String.valueOf(letter));
        letterLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        letterLabel.setStyle("-fx-text-fill: #333;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label keteranganLabel = new Label("Keterangan");
        keteranganLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        keteranganLabel.setStyle("-fx-text-fill: #333;");

        headerBox.getChildren().addAll(letterLabel, spacer, keteranganLabel);

        
        VBox playersList = new VBox();
        playersList.setStyle("-fx-background-color: #E8F4E8; -fx-background-radius: 0 0 8 8;");

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            String currentStatus = existingStatusMap.getOrDefault(player.getUsername(), "Alpa");
            attendanceStatus.put(player.getUsername(), currentStatus);
            HBox playerRow = createPlayerRow(player.getFullName(), player.getUsername(), i == players.size() - 1);
            playersList.getChildren().add(playerRow);
        }

        section.getChildren().addAll(headerBox, playersList);
        return section;
    }

    private HBox createPlayerRow(String playerFullName, String playerUsername, boolean isLast) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(15);
        row.setPadding(new Insets(12, 15, 12, 15));

        
        if (!isLast) {
            row.setStyle("-fx-border-color: transparent transparent #D0E8D0 transparent; -fx-border-width: 0 0 1 0;");
        }

        User player = dataManager.getUserByUsername(playerUsername);
        
        
        Circle profileIcon = new Circle(18);
        profileIcon.setFill(Color.web(player != null && player.getAvatarColor() != null ? player.getAvatarColor() : "#4A5D23"));
        profileIcon.setStroke(Color.WHITE);
        profileIcon.setStrokeWidth(2);

        Label nameLabel = new Label(playerFullName);
        nameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        nameLabel.setStyle("-fx-text-fill: #333;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox statusButtons = new HBox();
        statusButtons.setSpacing(8);
        String currentStatus = attendanceStatus.getOrDefault(playerUsername, "Alpa");

        
        Button presentBtn = createStatusButton("P", currentStatus.equals("Hadir"));
        Button sickBtn = createStatusButton("S", currentStatus.equals("Sakit"));
        Button excusedBtn = createStatusButton("E", currentStatus.equals("Izin"));
        Button absentBtn = createStatusButton("A", currentStatus.equals("Alpa"));

        presentBtn.setOnAction(e -> updatePlayerStatus(playerUsername, "Hadir", statusButtons));
        sickBtn.setOnAction(e -> updatePlayerStatus(playerUsername, "Sakit", statusButtons));
        excusedBtn.setOnAction(e -> updatePlayerStatus(playerUsername, "Izin", statusButtons));
        absentBtn.setOnAction(e -> updatePlayerStatus(playerUsername, "Alpa", statusButtons));

        statusButtons.getChildren().addAll(presentBtn, sickBtn, excusedBtn, absentBtn);
        row.getChildren().addAll(profileIcon, nameLabel, spacer, statusButtons);
        return row;
    }

    private Button createStatusButton(String status, boolean isSelected) {
        Button btn = new Button(status);
        btn.setPrefWidth(35);
        btn.setPrefHeight(35);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        String color = getStatusColor(status);
        String style = "-fx-background-radius: 50; -fx-border-radius: 50; -fx-cursor: hand; ";

        if (isSelected) {
            style += "-fx-background-color: " + color + "; -fx-text-fill: white; -fx-border-color: " + color + "; -fx-border-width: 2;";
        } else {
            style += "-fx-background-color: white; -fx-text-fill: " + color + "; -fx-border-color: " + color + "; -fx-border-width: 2;";
        }

        btn.setStyle(style);
        return btn;
    }

    private String getStatusColor(String status) {
        switch (status) {
            case "P": return "#4CAF50"; 
            case "S": return "#FF9800"; 
            case "E": return "#2196F3"; 
            case "A": return "#F44336"; 
            default: return "#666666";
        }
    }

    private void updatePlayerStatus(String playerUsername, String newStatus, HBox statusButtons) {
        attendanceStatus.put(playerUsername, newStatus);

        
        for (int i = 0; i < statusButtons.getChildren().size(); i++) {
            Button btn = (Button) statusButtons.getChildren().get(i);
            String btnStatus = btn.getText();
            
            
            boolean isSelected = false;
            switch (btnStatus) {
                case "P": isSelected = newStatus.equals("Hadir"); break;
                case "S": isSelected = newStatus.equals("Sakit"); break;
                case "E": isSelected = newStatus.equals("Izin"); break;
                case "A": isSelected = newStatus.equals("Alpa"); break;
            }

            String color = getStatusColor(btnStatus);
            String style = "-fx-background-radius: 50; -fx-border-radius: 50; -fx-cursor: hand; ";

            if (isSelected) {
                style += "-fx-background-color: " + color + "; -fx-text-fill: white; -fx-border-color: " + color + "; -fx-border-width: 2;";
            } else {
                style += "-fx-background-color: white; -fx-text-fill: " + color + "; -fx-border-color: " + color + "; -fx-border-width: 2;";
            }

            btn.setStyle(style);
        }
    }

    private void saveAttendanceChanges() {
        if (selectedJadwal == null) {
            showAlert("Error", "Pilih jadwal latihan terlebih dahulu sebelum menyimpan.");
            return;
        }

        for (Map.Entry<String, String> entry : attendanceStatus.entrySet()) {
            String playerUsername = entry.getKey();
            String status = entry.getValue();

            Absensi absensi = new Absensi(selectedJadwal.getId(), playerUsername, status);
            dataManager.rekamAbsensi(absensi);
        }

        showAlert("Berhasil", "Perubahan absensi berhasil disimpan!");
    }

    private VBox createBottomNavigation() {
        VBox mainContainer = new VBox();
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setSpacing(12);
        mainContainer.setStyle("-fx-background-color: white; -fx-border-color: #eee; -fx-border-width: 1 0 0 0;");
        
        HBox bottomNav = new HBox();
        bottomNav.setAlignment(Pos.CENTER);
        bottomNav.setSpacing(80);
        bottomNav.setPadding(new Insets(18, 0, 8, 0));
        
        Button homeBtn = createModernNavIcon("🏠", false);
        homeBtn.setOnAction(e -> sceneController.showDashboardScene());
        
        Button calendarBtn = createModernNavIcon("📅", false);
        calendarBtn.setOnAction(e -> sceneController.showJadwalScene());
        
        Button profileBtn = createModernNavIcon("👤", true); 
        profileBtn.setOnAction(e -> sceneController.showAbsensiScene());
        
        bottomNav.getChildren().addAll(homeBtn, calendarBtn, profileBtn);
        
        Label creditLabel = new Label("Project By Kelompok 2 LAB OOP");
        creditLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
        creditLabel.setStyle("-fx-text-fill: #95a5a6;");
        
        mainContainer.getChildren().addAll(bottomNav, creditLabel);
        return mainContainer;
    }

    private Button createModernNavIcon(String icon, boolean isActive) {
        Button btn = new Button(icon);
        
        if (isActive) {
            btn.setFont(Font.font(28));
            btn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; " +
                        "-fx-cursor: hand; -fx-padding: 8; -fx-text-fill: #4A5D23;");
        } else {
            btn.setFont(Font.font(24));
            btn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; " +
                        "-fx-cursor: hand; -fx-padding: 8; -fx-text-fill: #95a5a6;");
        }
        
        if (!isActive) {
            btn.setOnMouseEntered(e -> {
                btn.setFont(Font.font(26));
                btn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; " +
                           "-fx-cursor: hand; -fx-padding: 8; -fx-text-fill: #7f8c8d;");
            });
            
            btn.setOnMouseExited(e -> {
                btn.setFont(Font.font(24));
                btn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; " +
                           "-fx-cursor: hand; -fx-padding: 8; -fx-text-fill: #95a5a6;");
            });
        }
        
        return btn;
    }

    

private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(null);
    alert.getDialogPane().setPrefSize(300, 180);
    alert.getDialogPane().setMaxSize(300, 180);

    DialogPane dialogPane = alert.getDialogPane();
    dialogPane.setGraphic(null);
    
    
    if ("Berhasil".equals(title)) {
        dialogPane.setStyle(
            "-fx-background-color: #f8fff8; " +
            "-fx-border-color: #7FB069; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 3);"
        );
    } else {
        
        dialogPane.setStyle(
            "-fx-background-color: #fff8f8; " +
            "-fx-border-color: #FF6B6B; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 3);"
        );
    }

    VBox content = new VBox(12);
    content.setAlignment(Pos.CENTER);
    content.setPadding(new Insets(20, 25, 15, 25));

    
    Label iconLabel = new Label("Berhasil".equals(title) ? "✅" : "⚠️");
    iconLabel.setFont(Font.font("Arial", 28));

    Label titleLabel = new Label(title);
    titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
    if ("Berhasil".equals(title)) {
        titleLabel.setStyle("-fx-text-fill: #2d5016;");
    } else {
        titleLabel.setStyle("-fx-text-fill: #d63031;");
    }
    titleLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

    Label messageLabel = new Label(message);
    messageLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
    if ("Berhasil".equals(title)) {
        messageLabel.setStyle("-fx-text-fill: #2d5016;");
    } else {
        messageLabel.setStyle("-fx-text-fill: #2d3436;");
    }
    messageLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
    messageLabel.setWrapText(true);

    content.getChildren().addAll(iconLabel, titleLabel, messageLabel);
    dialogPane.setContent(content);

    
    Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
    okButton.setText("OK");
    if ("Berhasil".equals(title)) {
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
    } else {
        okButton.setStyle(
            "-fx-background-color: #FF6B6B; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 12px; " +
            "-fx-background-radius: 6; " +
            "-fx-border-radius: 6; " +
            "-fx-padding: 6 20; " +
            "-fx-cursor: hand;"
        );
    }

    alert.showAndWait();
}

private void showSuccessAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(null);
    alert.getDialogPane().setPrefSize(320, 200);
    alert.getDialogPane().setMaxSize(320, 200);

    DialogPane dialogPane = alert.getDialogPane();
    dialogPane.setGraphic(null);
    dialogPane.setStyle(
        "-fx-background-color: #f8fff8; " +
        "-fx-border-color: #7FB069; " +
        "-fx-border-width: 2; " +
        "-fx-border-radius: 12; " +
        "-fx-background-radius: 12; " +
        "-fx-effect: dropshadow(gaussian, rgba(127,176,105,0.3), 12, 0, 0, 4);"
    );

    VBox content = new VBox(15);
    content.setAlignment(Pos.CENTER);
    content.setPadding(new Insets(25, 30, 20, 30));


    Label iconLabel = new Label("💾");
    iconLabel.setFont(Font.font("Arial", 32));

    Label titleLabel = new Label(title);
    titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
    titleLabel.setStyle("-fx-text-fill: #2d5016;");
    titleLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

    Label messageLabel = new Label(message);
    messageLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
    messageLabel.setStyle("-fx-text-fill: #4a5d23;");
    messageLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
    messageLabel.setWrapText(true);

    content.getChildren().addAll(iconLabel, titleLabel, messageLabel);
    dialogPane.setContent(content);

    Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
    okButton.setText("Mantap! 👍");
    okButton.setStyle(
        "-fx-background-color: linear-gradient(to bottom, #7FB069, #6a9c57); " +
        "-fx-text-fill: white; " +
        "-fx-font-weight: bold; " +
        "-fx-font-size: 13px; " +
        "-fx-background-radius: 8; " +
        "-fx-border-radius: 8; " +
        "-fx-padding: 8 25; " +
        "-fx-cursor: hand; " +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 0, 2);"
    );

    okButton.setOnMouseEntered(e -> {
        okButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #8bc474, #7fb069); " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 13px; " +
            "-fx-background-radius: 8; " +
            "-fx-border-radius: 8; " +
            "-fx-padding: 8 25; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 6, 0, 0, 3);"
        );
    });

    okButton.setOnMouseExited(e -> {
        okButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #7FB069, #6a9c57); " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 13px; " +
            "-fx-background-radius: 8; " +
            "-fx-border-radius: 8; " +
            "-fx-padding: 8 25; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 0, 2);"
        );
    });

    alert.showAndWait();
}



    public Scene getScene() {
        return scene;
    }
}