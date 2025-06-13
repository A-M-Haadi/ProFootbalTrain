package pftrain.views;

import pftrain.controllers.SceneController;
import pftrain.models.Absensi;
import pftrain.models.DataManager;
import pftrain.models.JadwalLatihan;
import pftrain.models.User;
import pftrain.models.UserSession;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerAbsensiView {
    private Scene summaryScene, detailScene;
    private final SceneController sceneController;
    private final UserSession userSession;
    private final DataManager dataManager;

    private long presentDays, sickDays, excuseDays, absentDays, totalRecords;

    public PlayerAbsensiView(SceneController sceneController) {
        this.sceneController = sceneController;
        this.userSession = UserSession.getInstance();
        this.dataManager = DataManager.getInstance();
        
        calculateAttendanceData();
        
        createSummaryScene();
        createDetailScene();
    }

    private void calculateAttendanceData() {
        User currentUser = userSession.getCurrentUser();
        if (currentUser == null) return;

        List<Absensi> myAbsensi = dataManager.getAllAbsensi().stream()
                .filter(a -> a.getUsernamePemain().equals(currentUser.getUsername()))
                .collect(Collectors.toList());
        
        presentDays = myAbsensi.stream().filter(a -> "Hadir".equalsIgnoreCase(a.getStatusKehadiran())).count();
        sickDays = myAbsensi.stream().filter(a -> "Sakit".equalsIgnoreCase(a.getStatusKehadiran())).count();
        excuseDays = myAbsensi.stream().filter(a -> "Izin".equalsIgnoreCase(a.getStatusKehadiran())).count();
        absentDays = myAbsensi.stream().filter(a -> "Alpa".equalsIgnoreCase(a.getStatusKehadiran())).count();
        totalRecords = myAbsensi.size();
    }

    private void createSummaryScene() {
        BorderPane absensiLayout = new BorderPane();
        absensiLayout.setStyle("-fx-background-color: #f5f5f5;");

        
        HBox header = createHeader("Absensi");
        absensiLayout.setTop(header);
        
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f5f5f5; -fx-background-color: #f5f5f5;");
        
        VBox content = new VBox();
        content.setSpacing(25);
        content.setPadding(new Insets(30, 20, 20, 20));
        content.setAlignment(Pos.CENTER);
        
        
        VBox profileSection = createProfileSection();
        
        
        HBox statsCards = createStatsCards();
        
        
        Button viewDetailBtn = createViewDetailButton();
        
        
        VBox summarySection = createSummarySection();
        
        content.getChildren().addAll(profileSection, statsCards, viewDetailBtn, summarySection);
        scrollPane.setContent(content);
        absensiLayout.setCenter(scrollPane);
        
        
        VBox bottomNav = createBottomNavigation();
        absensiLayout.setBottom(bottomNav);
        
        summaryScene = new Scene(absensiLayout, 450, 700);
    }
    
    private void createDetailScene() {
        BorderPane detailLayout = new BorderPane();
        detailLayout.setStyle("-fx-background-color: #f5f5f5;");
    
        
        HBox header = createHeader("Absensi");
        detailLayout.setTop(header);
    
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f5f5f5; -fx-background-color: #f5f5f5;");
        
        VBox content = new VBox();
        content.setSpacing(20);
        content.setPadding(new Insets(20));
        
        
        VBox profileHeader = createDetailProfileHeader();
        
        
        VBox detailContainer = createDetailContainer();
        
        content.getChildren().addAll(profileHeader, detailContainer);
        scrollPane.setContent(content);
        detailLayout.setCenter(scrollPane);
        
        detailScene = new Scene(detailLayout, 450, 700);
    }

    private HBox createHeader(String title) {
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

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setStyle("-fx-text-fill: #333333;");

        header.getChildren().addAll(backBtn, titleLabel);
        return header;
    }

    private VBox createProfileSection() {
        VBox profileSection = new VBox();
        profileSection.setAlignment(Pos.CENTER);
        profileSection.setSpacing(15);
        
        
        Circle avatar = new Circle(50);
        avatar.setFill(Color.web("#4A5D23"));
        avatar.setStroke(Color.WHITE);
        avatar.setStrokeWidth(3);
        
        
        Label nameLabel = new Label(userSession.getCurrentUser().getFullName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        nameLabel.setStyle("-fx-text-fill: #333333;");
        
        profileSection.getChildren().addAll(avatar, nameLabel);
        return profileSection;
    }

    private HBox createStatsCards() {
        HBox statsContainer = new HBox();
        statsContainer.setSpacing(8);
        statsContainer.setAlignment(Pos.CENTER);
        statsContainer.setMaxWidth(Double.MAX_VALUE);
        
        
        VBox presentCard = createStatCard(String.valueOf(presentDays), "Days", "Attendance", "#A8D5A8");
        VBox sickCard = createStatCard(String.valueOf(sickDays), "Days", "Sick", "#A8D5A8");
        VBox excuseCard = createStatCard(String.valueOf(excuseDays), "Days", "Excuse", "#A8D5A8");
        VBox absentCard = createStatCard(String.valueOf(absentDays), "Days", "Absent", "#CCCCCC");
        
        
        HBox.setHgrow(presentCard, Priority.ALWAYS);
        HBox.setHgrow(sickCard, Priority.ALWAYS);
        HBox.setHgrow(excuseCard, Priority.ALWAYS);
        HBox.setHgrow(absentCard, Priority.ALWAYS);
        
        statsContainer.getChildren().addAll(presentCard, sickCard, excuseCard, absentCard);
        return statsContainer;
    }
    
    private VBox createStatCard(String value, String unit, String label, String color) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setSpacing(8);
        card.setPadding(new Insets(15, 8, 15, 8));
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 12;");
        card.setMinWidth(70);
        card.setMaxWidth(Double.MAX_VALUE);
        
        
        String icon = getIconForType(label);
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(20));
        iconLabel.setStyle("-fx-text-fill: white;");
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        valueLabel.setStyle("-fx-text-fill: white;");
        
        Label unitLabel = new Label(unit);
        unitLabel.setFont(Font.font("Arial", 10));
        unitLabel.setStyle("-fx-text-fill: white;");
        
        Label labelLabel = new Label(label);
        labelLabel.setFont(Font.font("Arial", FontWeight.BOLD, 9));
        labelLabel.setStyle("-fx-text-fill: white;");
        labelLabel.setTextAlignment(TextAlignment.CENTER);
        labelLabel.setWrapText(true);
        
        card.getChildren().addAll(iconLabel, valueLabel, unitLabel, labelLabel);
        return card;
    }
    
    private String getIconForType(String type) {
        switch (type) {
            case "Attendance": return "✓";
            case "Sick": return "🤒";
            case "Excuse": return "📝";
            case "Absent": return "✗";
            default: return "•";
        }
    }

    private Button createViewDetailButton() {
        Button viewDetailBtn = new Button("View Detail");
        viewDetailBtn.setPrefWidth(200);
        viewDetailBtn.setPrefHeight(45);
        viewDetailBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        viewDetailBtn.setStyle("-fx-background-color: #B8D4B8; -fx-text-fill: #333; " +
                              "-fx-background-radius: 12; -fx-cursor: hand;");
        viewDetailBtn.setOnAction(e -> sceneController.showPlayerAbsensiDetail());
        return viewDetailBtn;
    }

    private VBox createSummarySection() {
        VBox summarySection = new VBox();
        summarySection.setAlignment(Pos.CENTER);
        summarySection.setSpacing(20);
        
        Label summaryTitle = new Label("Summary Attendance");
        summaryTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        summaryTitle.setStyle("-fx-text-fill: #333333;");
        
        
        StackPane chartContainer = createAnimatedCircularChart();
        
        Label totalDaysLabel = new Label("Total Training Days");
        totalDaysLabel.setFont(Font.font("Arial", 14));
        totalDaysLabel.setStyle("-fx-text-fill: #666666;");
        
        Label daysValueLabel = new Label(totalRecords + " Days");
        daysValueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        daysValueLabel.setStyle("-fx-text-fill: #333333;");
        
        summarySection.getChildren().addAll(summaryTitle, chartContainer, totalDaysLabel, daysValueLabel);
        return summarySection;
    }

    private StackPane createAnimatedCircularChart() {
        StackPane chartContainer = new StackPane();
        chartContainer.setPrefSize(150, 150);
        
        
        Circle backgroundCircle = new Circle(60);
        backgroundCircle.setFill(Color.TRANSPARENT);
        backgroundCircle.setStroke(Color.web("#E0E0E0"));
        backgroundCircle.setStrokeWidth(12);
        
        
        double percentage = (totalRecords > 0) ? (double) presentDays / totalRecords : 0;
        Arc progressArc = new Arc();
        progressArc.setCenterX(0);
        progressArc.setCenterY(0);
        progressArc.setRadiusX(60);
        progressArc.setRadiusY(60);
        progressArc.setStartAngle(90);
        progressArc.setLength(0); 
        progressArc.setType(ArcType.OPEN);
        progressArc.setFill(Color.TRANSPARENT);
        progressArc.setStroke(Color.web("#4A5D23"));
        progressArc.setStrokeWidth(12);
        
        
        Label percentageLabel = new Label("0%"); 
        percentageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        percentageLabel.setStyle("-fx-text-fill: #333333;");
        
        
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
            
            new KeyFrame(Duration.ZERO, 
                new KeyValue(progressArc.lengthProperty(), 0)),
            new KeyFrame(Duration.seconds(2), 
                new KeyValue(progressArc.lengthProperty(), -360 * percentage))
            
            
        );
        
        
        timeline.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            double progress = newTime.toSeconds() / 2.0; 
            if (progress <= 1.0) {
                int currentPercentage = (int) (percentage * 100 * progress);
                percentageLabel.setText(currentPercentage + "%");
            }
        });
        
        timeline.setOnFinished(e -> {
            
            percentageLabel.setText(String.format("%.0f%%", percentage * 100));
        });
        
        chartContainer.getChildren().addAll(backgroundCircle, progressArc, percentageLabel);
        
        
        timeline.play();
        
        return chartContainer;
    }
    
    private VBox createDetailProfileHeader() {
        VBox profileHeader = new VBox();
        profileHeader.setAlignment(Pos.CENTER);
        profileHeader.setPadding(new Insets(0, 0, 20, 0));
        
        
        Circle avatar = new Circle(40);
        avatar.setFill(Color.web("#4A5D23"));
        avatar.setStroke(Color.WHITE);
        avatar.setStrokeWidth(3);
        
        profileHeader.getChildren().add(avatar);
        return profileHeader;
    }

    private VBox createDetailContainer() {
        VBox container = new VBox();
        container.setSpacing(0);
        container.setStyle("-fx-background-color: #B8D4B8; -fx-background-radius: 15; " +
                          "-fx-border-color: #007AFF; -fx-border-width: 3; -fx-border-radius: 15;");
        container.setPadding(new Insets(20));
        
        
        List<Absensi> myAbsensi = dataManager.getAllAbsensi().stream()
            .filter(a -> a.getUsernamePemain().equals(userSession.getCurrentUser().getUsername()))
            .collect(Collectors.toList());
            
        Map<Integer, JadwalLatihan> jadwalMap = dataManager.getJadwalList().stream()
            .collect(Collectors.toMap(JadwalLatihan::getId, j -> j));

        if (myAbsensi.isEmpty()) {
            Label noHistoryLabel = new Label("Belum ada riwayat absensi.");
            noHistoryLabel.setStyle("-fx-text-fill: #333333;");
            container.getChildren().add(noHistoryLabel);
            container.setAlignment(Pos.CENTER);
        } else {
            for (int i = 0; i < myAbsensi.size(); i++) {
                Absensi absensi = myAbsensi.get(i);
                JadwalLatihan jadwal = jadwalMap.get(absensi.getJadwalId());
                if (jadwal != null) {
                    VBox recordCard = createDetailCard(absensi, jadwal);
                    
                    if (i < myAbsensi.size() - 1) {
                        Separator separator = new Separator();
                        separator.setStyle("-fx-background-color: #A0C4A0;");
                        container.getChildren().addAll(recordCard, separator);
                    } else {
                        container.getChildren().add(recordCard);
                    }
                }
            }
        }
        
        return container;
    }
    
    private VBox createDetailCard(Absensi absensi, JadwalLatihan jadwal) {
        VBox card = new VBox();
        card.setSpacing(8);
        card.setPadding(new Insets(15, 10, 15, 10));
        
        
        String statusDisplay = mapStatusToDisplay(absensi.getStatusKehadiran());
        Label statusLabel = new Label(statusDisplay);
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        statusLabel.setStyle("-fx-background-color: " + getStatusColor(statusDisplay) + "; " +
                           "-fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 6;");
        
        
        String description = String.format("%s %s the %s on %s.", 
            userSession.getCurrentUser().getFullName(),
            getStatusAction(absensi.getStatusKehadiran()),
            jadwal.getNamaKegiatan(),
            jadwal.getTanggal().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"))
        );
        
        Label descriptionLabel = new Label(description);
        descriptionLabel.setFont(Font.font("Arial", 12));
        descriptionLabel.setStyle("-fx-text-fill: #333333;");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(Double.MAX_VALUE);
        
        card.getChildren().addAll(statusLabel, descriptionLabel);
        return card;
    }
    
    private String mapStatusToDisplay(String originalStatus) {
        switch (originalStatus.toLowerCase()) {
            case "hadir": return "Present";
            case "sakit": return "Sick";
            case "izin": return "Excuse";
            case "alpa": return "Absent";
            default: return originalStatus;
        }
    }
    
    private String getStatusAction(String status) {
        switch (status.toLowerCase()) {
            case "hadir": return "attended";
            case "sakit": return "was absent due to illness from";
            case "izin": return "was excused from";
            case "alpa": return "was absent from";
            default: return "participated in";
        }
    }
    
    private String getStatusColor(String status) {
        switch (status) {
            case "Present": return "#4CAF50";
            case "Sick": return "#FF9800";
            case "Excuse": return "#2196F3";
            case "Absent": return "#F44336";
            default: return "#9E9E9E";
        }
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
        profileBtn.setOnAction(e -> sceneController.showPlayerAbsensiSummary());
        
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

    public Scene getSummaryScene() { return summaryScene; }
    public Scene getDetailScene() { return detailScene; }
}