package pftrain.views;

import pftrain.controllers.SceneController;
import pftrain.models.Absensi;
import pftrain.models.DataManager;
import pftrain.models.JadwalLatihan;
import pftrain.models.User;
import pftrain.models.UserSession;
import pftrain.utils.UIComponents;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import java.util.Comparator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DashboardView {
    private Scene scene;
    private SceneController sceneController;
    private UserSession userSession;
    private DataManager dataManager;
    private VBox mainContent;

    public DashboardView(SceneController sceneController) {
        this.sceneController = sceneController;
        this.userSession = UserSession.getInstance();
        this.dataManager = DataManager.getInstance();
        createScene();
    }

    private void createScene() {
        BorderPane dashboardLayout = new BorderPane();
        dashboardLayout.setStyle("-fx-background-color: #f8f8f8;");

        
        HBox topBar = createDashboardTopBar();
        dashboardLayout.setTop(topBar);

        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f8f8f8; -fx-background-color: #f8f8f8;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        mainContent = new VBox();
        mainContent.setSpacing(25);
        mainContent.setPadding(new Insets(30, 25, 30, 25));

        refreshContent();

        scrollPane.setContent(mainContent);
        dashboardLayout.setCenter(scrollPane);

        
        VBox bottomNav = createModernBottomNavigation();
        dashboardLayout.setBottom(bottomNav);

        scene = new Scene(dashboardLayout);
    }

    private HBox createDashboardTopBar() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(20));
        topBar.setStyle("-fx-background-color: white; -fx-border-color: #eee; -fx-border-width: 0 0 1 0;");

        Label logoLabel = new Label("ProFootballTrain");
        logoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        logoLabel.setStyle("-fx-text-fill: #7FB069;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button profileBtn = createCustomProfileAvatar();
        profileBtn.setOnAction(e -> {
            User currentUser = userSession.getCurrentUser();
            if (currentUser != null) {
                
                System.out.println("Avatar clicked by user: " + currentUser.getUsername() + " with role: " + currentUser.getRole());
                
                
                if ("admin".equalsIgnoreCase(currentUser.getRole())) {
                    sceneController.showAdminProfileScene();
                } else {
                    sceneController.showPlayerProfileScene();
                }
            } else {
                System.out.println("Current user is null, redirecting to login");
                sceneController.showLoginScene();
            }
        });

        topBar.getChildren().addAll(logoLabel, spacer, profileBtn);
        return topBar;
    }

    private Button createCustomProfileAvatar() {
        Button profileBtn = new Button();

        String customPhotoPath = getUserCustomPhoto();

        if (customPhotoPath != null && !customPhotoPath.isEmpty()) {
            profileBtn.setText(getUserInitials());
            profileBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            profileBtn.setStyle("-fx-background-color: " + getUserAvatarColor() + "; -fx-text-fill: white; " +
                              "-fx-background-radius: 25; -fx-min-width: 50; -fx-min-height: 50; " +
                              "-fx-max-width: 50; -fx-max-height: 50; -fx-cursor: hand;");
        } else {
            profileBtn.setText(getUserInitials());
            profileBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            profileBtn.setStyle("-fx-background-color: " + getUserAvatarColor() + "; -fx-text-fill: white; " +
                              "-fx-background-radius: 25; -fx-min-width: 50; -fx-min-height: 50; " +
                              "-fx-max-width: 50; -fx-max-height: 50; -fx-cursor: hand;");
        }

        profileBtn.setOnMouseEntered(e -> {
            profileBtn.setStyle("-fx-background-color: " + getDarkerColor(getUserAvatarColor()) + "; -fx-text-fill: white; " +
                              "-fx-background-radius: 25; -fx-min-width: 50; -fx-min-height: 50; " +
                              "-fx-max-width: 50; -fx-max-height: 50; -fx-cursor: hand;");
        });

        profileBtn.setOnMouseExited(e -> {
            profileBtn.setStyle("-fx-background-color: " + getUserAvatarColor() + "; -fx-text-fill: white; " +
                              "-fx-background-radius: 25; -fx-min-width: 50; -fx-min-height: 50; " +
                              "-fx-max-width: 50; -fx-max-height: 50; -fx-cursor: hand;");
        });

        return profileBtn;
    }

    private String getUserInitials() {
        User currentUser = userSession.getCurrentUser();
        if (currentUser == null || currentUser.getFullName() == null || currentUser.getFullName().trim().isEmpty()) {
            return "U";
        }

        String fullName = currentUser.getFullName().trim();
        String[] parts = fullName.split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].charAt(0) + "" + parts[1].charAt(0)).toUpperCase();
        } else {
            return fullName.substring(0, Math.min(2, fullName.length())).toUpperCase();
        }
    }

    private String getUserAvatarColor() {
        User currentUser = userSession.getCurrentUser();
        if (currentUser == null || currentUser.getUsername() == null || currentUser.getUsername().isEmpty()) {
            return "#7FB069";
        }

        
        String savedColor = currentUser.getAvatarColor();
        if (savedColor != null && !savedColor.isEmpty()) {
            return savedColor;
        }

        
        String[] colors = {
            "#7FB069", "#4A90E2", "#F5A623", "#BD10E0", 
            "#B8E986", "#50E3C2", "#D0021B", "#F8E71C",
            "#9013FE", "#FF6B6B", "#4ECDC4", "#45B7D1"
        };

        int hash = currentUser.getUsername().hashCode();
        int index = Math.abs(hash) % colors.length;
        return colors[index];
    }

    private String getDarkerColor(String color) {
        switch (color) {
            case "#7FB069": return "#6B9A57";
            case "#4A90E2": return "#3A7BC8";
            case "#F5A623": return "#E8940F";
            case "#BD10E0": return "#9A0CB8";
            case "#B8E986": return "#A4D572";
            case "#50E3C2": return "#3CCF9F";
            case "#D0021B": return "#B40218";
            case "#F8E71C": return "#E5D419";
            case "#9013FE": return "#7C0FE1";
            case "#FF6B6B": return "#FF5252";
            case "#4ECDC4": return "#42B8B1";
            case "#45B7D1": return "#3A9BC1";
            default: return "#666666";
        }
    }

    private String getUserCustomPhoto() {
        return null; 
    }

    private void refreshContent() {
        mainContent.getChildren().clear();

        VBox welcomeSection = createModernWelcomeSection();
        HBox statsBox = createStatsBox();

        Label jadwalTitle = new Label("Jadwal Latihan Terdekat");
        jadwalTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        jadwalTitle.setStyle("-fx-text-fill: #2c3e50;");

        VBox jadwalCards = createJadwalCards();

        mainContent.getChildren().addAll(welcomeSection, statsBox, jadwalTitle, jadwalCards);
    }

    private VBox createModernWelcomeSection() {
        VBox welcomeSection = new VBox();
        welcomeSection.setSpacing(8);
        welcomeSection.setPadding(new Insets(10, 0, 20, 0));

        User currentUser = userSession.getCurrentUser();
        if (currentUser == null) {
            sceneController.showLoginScene();
            return welcomeSection;
        }

        String firstName = getFirstName(currentUser.getFullName());

        HBox greetingBox = new HBox();
        greetingBox.setAlignment(Pos.CENTER_LEFT);
        greetingBox.setSpacing(8);

        Label heyLabel = new Label("Hey, " + firstName);
        heyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        heyLabel.setStyle("-fx-text-fill: #2c3e50;");

        Label waveEmoji = new Label("👋");
        waveEmoji.setFont(Font.font(20));

        greetingBox.getChildren().addAll(heyLabel, waveEmoji);

        Label welcomeBackLabel = new Label("Welcome back!");
        welcomeBackLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        welcomeBackLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        
        currentUser.login(); 
        
        String role = currentUser.getRole();
        Label roleLabel = new Label("Anda login sebagai: " + role.substring(0, 1).toUpperCase() + role.substring(1));
        roleLabel.setFont(Font.font("Arial", 16));
        roleLabel.setStyle("-fx-text-fill: #7f8c8d;");

        welcomeSection.getChildren().addAll(greetingBox, welcomeBackLabel, roleLabel);
        return welcomeSection;
    }

    private String getFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "User";
        String[] parts = fullName.trim().split("\\s+");
        return parts[0];
    }

    private HBox createStatsBox() {
        HBox statsBox = new HBox();
        statsBox.setSpacing(12);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPadding(new Insets(10, 0, 15, 0));
        statsBox.setMaxWidth(Double.MAX_VALUE);
        
        User currentUser = userSession.getCurrentUser();
        String userRole = currentUser.getRole();

        VBox box1, box2, box3;

        if ("admin".equals(userRole)) {
            long totalPemain = dataManager.getPlayers().size();
            long jadwalHariIni = dataManager.getJadwalList().stream()
                .filter(j -> j.getTanggal().isEqual(LocalDate.now()))
                .count();
            
            box1 = createFlexibleStatCard("Total Jadwal", String.valueOf(dataManager.getJadwalList().size()), "#7FB069");
            box2 = createFlexibleStatCard("Total Pemain", String.valueOf(totalPemain), "#4A90E2");
            box3 = createFlexibleStatCard("Jadwal Hari Ini", String.valueOf(jadwalHariIni), "#F5A623");
        } else {
            long totalKehadiran = dataManager.getTotalKehadiran(currentUser.getUsername());
            long totalJadwal = dataManager.getJadwalList().size();
            double kehadiranPct = (totalJadwal == 0) ? 0 : ((double) totalKehadiran / totalJadwal) * 100;

            box1 = createFlexibleStatCard("Total Jadwal", String.valueOf(totalJadwal), "#7FB069");
            box2 = createFlexibleStatCard("Kehadiran Saya", String.valueOf(totalKehadiran), "#4A90E2");
            box3 = createFlexibleStatCard("Persentase Hadir", String.format("%.0f%%", kehadiranPct), "#F5A623");
        }

        HBox.setHgrow(box1, Priority.ALWAYS);
        HBox.setHgrow(box2, Priority.ALWAYS);
        HBox.setHgrow(box3, Priority.ALWAYS);

        statsBox.getChildren().addAll(box1, box2, box3);
        return statsBox;
    }

    private VBox createFlexibleStatCard(String title, String value, String color) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setSpacing(6);
        card.setPadding(new Insets(16, 12, 16, 12));
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 12; " +
                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        card.setMinWidth(90);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPrefWidth(Region.USE_COMPUTED_SIZE);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        valueLabel.setStyle("-fx-text-fill: white;");
        valueLabel.setWrapText(false);
        valueLabel.setAlignment(Pos.CENTER);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.MEDIUM, 12));
        titleLabel.setStyle("-fx-text-fill: white;");
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        card.getChildren().addAll(valueLabel, titleLabel);
        return card;
    }

    private VBox createJadwalCards() {
        VBox cardsContainer = new VBox();
        cardsContainer.setSpacing(15);
        
        List<JadwalLatihan> jadwalToShow = dataManager.getJadwalList().stream()
                .filter(j -> !j.getTanggal().isBefore(LocalDate.now()))
                .sorted(Comparator.comparing(JadwalLatihan::getTanggal).thenComparing(JadwalLatihan::getWaktu))
                .limit(3)
                .collect(Collectors.toList());

        if (jadwalToShow.isEmpty()) {
            Label emptyLabel = new Label("Belum ada jadwal latihan terdekat.");
            emptyLabel.setFont(Font.font("Arial", 16));
            emptyLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-padding: 30;");
            emptyLabel.setWrapText(true);
            emptyLabel.setMaxWidth(Double.MAX_VALUE);
            emptyLabel.setTextAlignment(TextAlignment.CENTER);
            cardsContainer.getChildren().add(emptyLabel);
        } else {
            for (JadwalLatihan jadwal : jadwalToShow) {
                cardsContainer.getChildren().add(createSingleJadwalCard(jadwal));
            }
        }
        return cardsContainer;
    }

    private VBox createSingleJadwalCard(JadwalLatihan jadwal) {
        VBox card = new VBox();
        card.setSpacing(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        card.setMaxWidth(Double.MAX_VALUE);

        
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT); 
        headerRow.setSpacing(10);

        
        Label statusLabel = new Label(jadwal.getStatus());
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        statusLabel.setStyle(getStatusStyle(jadwal.getStatus()));

        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        
        String tim = jadwal.getTim() != null ? jadwal.getTim() : "Kedua Tim";
        Label teamLabel = new Label(tim.equals("Tim A") ? "Tim A" : tim.equals("Tim B") ? "Tim B" : "Kedua Tim");
        teamLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        teamLabel.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 10;",
            tim.equals("Tim A") ? "#4A90E2" : tim.equals("Tim B") ? "#F5A623" : "#87CEEB"));
        
        headerRow.getChildren().addAll(statusLabel, spacer, teamLabel);
        

        Label judulLabel = new Label(jadwal.getNamaKegiatan());
        judulLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        judulLabel.setStyle("-fx-text-fill: #2c3e50;");
        judulLabel.setWrapText(true);
        judulLabel.setMaxWidth(Double.MAX_VALUE);

        VBox infoSection = new VBox();
        infoSection.setSpacing(6);
        infoSection.setMaxWidth(Double.MAX_VALUE);
        
        HBox dateTimeRow = new HBox();
        dateTimeRow.setSpacing(20);
        dateTimeRow.setAlignment(Pos.CENTER_LEFT);
        
        Label tanggalLabel = new Label("📅 " + jadwal.getTanggal().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        tanggalLabel.setFont(Font.font("Arial", 12));
        tanggalLabel.setStyle("-fx-text-fill: #5a6c7d;");
        
        Label waktuLabel = new Label("🕐 " + jadwal.getWaktu().format(DateTimeFormatter.ofPattern("HH:mm")));
        waktuLabel.setFont(Font.font("Arial", 12));
        waktuLabel.setStyle("-fx-text-fill: #5a6c7d;");
        
        dateTimeRow.getChildren().addAll(tanggalLabel, waktuLabel);
        
        Label lokasiLabel = new Label("📍 " + jadwal.getLokasi());
        lokasiLabel.setFont(Font.font("Arial", 12));
        lokasiLabel.setStyle("-fx-text-fill: #5a6c7d;");
        lokasiLabel.setWrapText(true);
        lokasiLabel.setMaxWidth(Double.MAX_VALUE);
        
        infoSection.getChildren().addAll(dateTimeRow, lokasiLabel);

        HBox buttonRow = new HBox();
        buttonRow.setAlignment(Pos.CENTER_RIGHT);
        buttonRow.setSpacing(10);
        buttonRow.setMaxWidth(Double.MAX_VALUE);
        
        Button detailBtn = createCompactButton("Detail", "#ffffff", "#7FB069");
        detailBtn.setOnAction(e -> showJadwalDetail(jadwal));
        buttonRow.getChildren().add(detailBtn);
        
        if (!"admin".equals(userSession.getCurrentUser().getRole())) {
            Button cekAbsensiBtn = createCompactButton("Cek Absensi", "#50C878", "#ffffff");
            cekAbsensiBtn.setOnAction(e -> showAbsensiStatus(jadwal));
            buttonRow.getChildren().add(cekAbsensiBtn);
        }

        card.getChildren().addAll(headerRow, judulLabel, infoSection, buttonRow);
        return card;
    }
    
    private String getStatusStyle(String status) {
        String color;
        switch (status) {
            case "Selesai": 
                color = "#50C878"; 
                break;
            case "Berlangsung": 
                color = "#FFD700"; 
                break;
            case "Dibatalkan":
                color = "#e74c3c";
                break;
            case "Terjadwal":
            default: 
                color = "#7FB069";
                break;
        }
        return "-fx-background-color: " + color + "; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 15;";
    }
    
    private void showJadwalDetail(JadwalLatihan jadwal) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detail Jadwal");
        alert.setHeaderText(null);
        alert.setContentText(null);

        alert.getDialogPane().setPrefSize(350, 400);
        alert.getDialogPane().setMaxSize(350, 400);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setGraphic(null);

        dialogPane.setStyle(
                "-fx-background-color: #f0f7ff; " +
                "-fx-border-color: #4A90E2; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 3);"
        );

        VBox content = new VBox();
        content.setAlignment(Pos.CENTER_LEFT);
        content.setSpacing(12);
        content.setPadding(new Insets(20, 25, 15, 25));

        HBox titleContainer = new HBox();
        titleContainer.setAlignment(Pos.CENTER);
        titleContainer.setSpacing(8);

        Label iconLabel = new Label("📋");
        iconLabel.setFont(Font.font("Arial", 20));

        Label titleLabel = new Label("Detail Jadwal Latihan");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: #1a5490;");

        titleContainer.getChildren().addAll(iconLabel, titleLabel);

        VBox detailsContainer = new VBox();
        detailsContainer.setSpacing(8);
        detailsContainer.setAlignment(Pos.CENTER_LEFT);

        VBox judulRow = createDetailRow("📝 Judul:", jadwal.getNamaKegiatan());
        VBox timRow = createDetailRow("🏆 Tim:",
                jadwal.getTim() != null ? jadwal.getTim() : "Informasi tim tidak tersedia");
        VBox tanggalRow = createDetailRow("📅 Tanggal:",
                jadwal.getTanggal().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        VBox waktuRow = createDetailRow("🕐 Waktu:",
                jadwal.getWaktu().format(DateTimeFormatter.ofPattern("HH:mm")) + " WITA");
        VBox lokasiRow = createDetailRow("📍 Lokasi:", jadwal.getLokasi());
        VBox statusRow = createDetailRow("🔄 Status:", jadwal.getStatus());
        VBox deskripsiRow = createDetailRow("📄 Deskripsi:", jadwal.getDeskripsi());

        detailsContainer.getChildren().addAll(
                judulRow, timRow, tanggalRow, waktuRow, lokasiRow, statusRow, deskripsiRow
        );

        content.getChildren().addAll(titleContainer, detailsContainer);
        dialogPane.setContent(content);

        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setText("Tutup");
        okButton.setStyle(
                "-fx-background-color: #4A90E2; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 12px; " +
                "-fx-background-radius: 6; " +
                "-fx-border-radius: 6; " +
                "-fx-padding: 8 20; " +
                "-fx-cursor: hand;"
        );

        alert.showAndWait();
    }

    private void showAbsensiStatus(JadwalLatihan jadwal) {
        String username = userSession.getCurrentUser().getUsername();

        Optional<Absensi> absensiOpt = dataManager.getAbsensiByJadwal(jadwal.getId())
            .stream()
            .filter(a -> a.getUsernamePemain().equals(username))
            .findFirst();

        VBox contentLayout = new VBox(12);
        contentLayout.setAlignment(Pos.CENTER_LEFT);

        Label statusLabel;
        if (absensiOpt.isPresent()) {
            statusLabel = new Label("✅ Anda tercatat: " + absensiOpt.get().getStatusKehadiran().toUpperCase());
            statusLabel.setStyle("-fx-text-fill: #2d5016; -fx-font-weight: bold;");
        } else {
            statusLabel = new Label("ℹ Data absensi Anda untuk jadwal ini belum direkam.");
            statusLabel.setStyle("-fx-text-fill: #555555;");
        }
        statusLabel.setFont(Font.font("Arial", 14));
        statusLabel.setWrapText(true);

        Region separator = new Region();
        separator.setPrefHeight(1);
        separator.setStyle("-fx-background-color: #d0e0f0;");
        separator.setMaxWidth(Double.MAX_VALUE);

        VBox detailLatihanBox = new VBox(2);
        Label latihanTitle = new Label("Untuk Latihan:");
        latihanTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        latihanTitle.setStyle("-fx-text-fill: #004080;");
        Label latihanValue = new Label(jadwal.getNamaKegiatan());
        latihanValue.setFont(Font.font("Arial", 14));
        detailLatihanBox.getChildren().addAll(latihanTitle, latihanValue);

        VBox detailTanggalBox = new VBox(2);
        Label tanggalTitle = new Label("Tanggal:");
        tanggalTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        tanggalTitle.setStyle("-fx-text-fill: #004080;");
        Label tanggalValue = new Label(jadwal.getTanggal().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        tanggalValue.setFont(Font.font("Arial", 14));
        detailTanggalBox.getChildren().addAll(tanggalTitle, tanggalValue);

        contentLayout.getChildren().addAll(statusLabel, separator, detailLatihanBox, detailTanggalBox);

        UIComponents.showCustomInfoDialog(
            sceneController.getPrimaryStage(),
            "Status Absensi",
            contentLayout
        );
    }

    

    private Button createCompactButton(String text, String bgColor, String textColor) {
        Button button = new Button(text);
        button.setPrefWidth(75);
        button.setPrefHeight(32);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        button.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: %s; " +
                                     "-fx-background-radius: 8; -fx-cursor: hand; " +
                                     "-fx-border-color: %s; -fx-border-width: 1; -fx-border-radius: 8;", 
                                     bgColor, textColor, textColor));
        return button;
    }
    
    private VBox createModernBottomNavigation() {
        VBox mainContainer = new VBox();
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setSpacing(12);
        mainContainer.setStyle("-fx-background-color: white; -fx-border-color: #eee; -fx-border-width: 1 0 0 0;");
        
        HBox bottomNav = new HBox();
        bottomNav.setAlignment(Pos.CENTER);
        bottomNav.setSpacing(80);
        bottomNav.setPadding(new Insets(18, 0, 8, 0));
        
        Button homeBtn = createModernNavIcon("🏠", true); 
        homeBtn.setOnAction(e -> sceneController.showDashboardScene());
        
        Button calendarBtn = createModernNavIcon("📅", false);
        calendarBtn.setOnAction(e -> sceneController.showJadwalScene());
        
        Button profileBtn = createModernNavIcon("👤", false);
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

        private VBox createDetailRow(String label, String value) {
        VBox row = new VBox();
        row.setSpacing(2);

        Label labelText = new Label(label);
        labelText.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        labelText.setStyle("-fx-text-fill: #1a5490;");

        Label valueText = new Label(value);
        valueText.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        valueText.setStyle("-fx-text-fill: #2c3e50;");
        valueText.setWrapText(true);
        valueText.setMaxWidth(280);

        row.getChildren().addAll(labelText, valueText);
        return row;
    }
    
    public void refresh() {
        if(mainContent != null) refreshContent();
    }

    public Scene getScene() {
        return scene;
    }
}