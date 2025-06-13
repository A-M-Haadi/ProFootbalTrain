package pftrain.views;

import pftrain.controllers.SceneController;
import pftrain.models.DataManager;
import pftrain.models.JadwalLatihan;
import pftrain.models.User;
import pftrain.models.Player;
import pftrain.models.UserSession;
import pftrain.utils.UIComponents;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JadwalView {
    private Scene scene;
    private final SceneController sceneController;
    private final UserSession userSession;
    private final DataManager dataManager;
    private TextField searchField;
    private VBox jadwalListContainer;
    

    public JadwalView(SceneController sceneController) {
        this.sceneController = sceneController;
        this.userSession = UserSession.getInstance();
        this.dataManager = DataManager.getInstance();
        createScene();
    }

    private void createScene() {
        BorderPane jadwalLayout = new BorderPane();
        jadwalLayout.setStyle("-fx-background-color: #f8f8f8;");

        HBox header = UIComponents.createSceneHeader("Jadwal Latihan", sceneController::showDashboardScene);
        header.setStyle("-fx-text-fill: #7FB069;");
        jadwalLayout.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f8f8f8; -fx-background-color: #f8f8f8;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        VBox content = new VBox();
        content.setSpacing(20);
        content.setPadding(new Insets(25));

        VBox searchSection = createSearchSection();
        content.getChildren().add(searchSection);

        User currentUser = userSession.getCurrentUser();
        if (currentUser != null && "admin".equals(currentUser.getRole())) {
            VBox buttonSection = new VBox();
            buttonSection.setSpacing(5);
            buttonSection.setPadding(new Insets(0, 0, 10, 0));
            
            Button addJadwalBtn = new Button("+ Tambah Jadwal Latihan");
            addJadwalBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            addJadwalBtn.setPrefWidth(200);
            addJadwalBtn.setPrefHeight(40);
            addJadwalBtn.setStyle("-fx-background-color: #7FB069; -fx-text-fill: white; " +
                                 "-fx-background-radius: 8; -fx-cursor: hand; " +
                                 "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 4, 0, 0, 2);");
            
            addJadwalBtn.setOnMouseEntered(e -> {
                addJadwalBtn.setStyle("-fx-background-color: #6B9A57; -fx-text-fill: white; " +
                                     "-fx-background-radius: 8; -fx-cursor: hand; " +
                                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 6, 0, 0, 3);");
            });
            
            addJadwalBtn.setOnMouseExited(e -> {
                addJadwalBtn.setStyle("-fx-background-color: #7FB069; -fx-text-fill: white; " +
                                     "-fx-background-radius: 8; -fx-cursor: hand; " +
                                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 4, 0, 0, 2);");
            });
            
            addJadwalBtn.setOnAction(e -> showAddJadwalDialog());
            
            HBox buttonContainer = new HBox();
            buttonContainer.setAlignment(Pos.CENTER_LEFT);
            buttonContainer.getChildren().add(addJadwalBtn);
            buttonSection.getChildren().add(buttonContainer);
            
            content.getChildren().add(buttonSection);
        }

        jadwalListContainer = new VBox();
        jadwalListContainer.setSpacing(15);
        refreshJadwalList("");

        content.getChildren().add(jadwalListContainer);
        
        scrollPane.setContent(content);
        jadwalLayout.setCenter(scrollPane);

        VBox bottomNav = createModernBottomNavigation();
        jadwalLayout.setBottom(bottomNav);

        scene = new Scene(jadwalLayout, 450, 700);
    }
    
    private VBox createSearchSection() {
        VBox searchSection = new VBox();
        searchSection.setSpacing(8);
        searchSection.setPadding(new Insets(0, 0, 10, 0));
        
        Label searchLabel = new Label("Cari Jadwal Latihan");
        searchLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        searchLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        HBox searchContainer = new HBox();
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.setSpacing(0);
        searchContainer.setStyle("-fx-background-color: white; -fx-background-radius: 25; " +
                                "-fx-border-color: #ddd; -fx-border-radius: 25; -fx-border-width: 1;");
        
        Label searchIcon = new Label("🔍");
        searchIcon.setFont(Font.font(16));
        searchIcon.setStyle("-fx-padding: 12 8 12 15;");
        
        searchField = new TextField();
        searchField.setPromptText("Search");
        searchField.setFont(Font.font("Arial", 14));
        searchField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; " +
                           "-fx-padding: 12 15 12 0;");
        searchField.setPrefHeight(50);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        searchField.textProperty().addListener((obs, oldV, newV) -> refreshJadwalList(newV));
        
        searchContainer.getChildren().addAll(searchIcon, searchField);
        searchSection.getChildren().addAll(searchLabel, searchContainer);
        
        return searchSection;
    }

    private void refreshJadwalList(String searchTerm) {
        jadwalListContainer.getChildren().clear();
        String lowerCaseTerm = searchTerm.toLowerCase().trim();
        User currentUser = userSession.getCurrentUser();

        List<JadwalLatihan> filteredList = dataManager.getJadwalList().stream()
            .filter(jadwal -> {
                if (currentUser != null && "player".equals(currentUser.getRole())) {
                    if (currentUser instanceof Player) {
                        Player currentPlayer = (Player) currentUser;
                        String userTeam = currentPlayer.getTim();
                        String jadwalTeam = jadwal.getTim();
                        
                        
                        String normalizedUserTeam = normalizeTeamFormat(userTeam);
                        String normalizedJadwalTeam = normalizeTeamFormat(jadwalTeam);
                        
                        
                        boolean isMatch = normalizedJadwalTeam.equals(normalizedUserTeam) || 
                                         normalizedJadwalTeam.equals("kedua_tim") ||
                                         normalizedJadwalTeam.equals("kedua tim") ||
                                         normalizedJadwalTeam.equals("semua tim");
                        
                        if (!isMatch) {
                            return false;
                        }
                    } else {
                        System.err.println("Warning: User with role 'player' is not an instance of Player class.");
                        return false; 
                    }
                }
                return lowerCaseTerm.isEmpty() || jadwal.getNamaKegiatan().toLowerCase().contains(lowerCaseTerm);
            })
            .sorted(Comparator.comparing(JadwalLatihan::getTanggal).thenComparing(JadwalLatihan::getWaktu).reversed())
            .collect(Collectors.toList());

        if (filteredList.isEmpty()) {
            Label emptyLabel = new Label(searchTerm.isEmpty() ? 
                "Belum ada jadwal latihan untuk tim Anda" : 
                "Tidak ada jadwal yang sesuai dengan pencarian \"" + searchTerm + "\"");
            emptyLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
            emptyLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-padding: 30;");
            emptyLabel.setWrapText(true);
            emptyLabel.setMaxWidth(Double.MAX_VALUE);
            jadwalListContainer.getChildren().add(emptyLabel);
            jadwalListContainer.setAlignment(Pos.CENTER);
        } else {
            jadwalListContainer.setAlignment(Pos.TOP_LEFT);
            for (JadwalLatihan jadwal : filteredList) {
                jadwalListContainer.getChildren().add(createJadwalCard(jadwal));
            }
        }
    }

    
    private String normalizeTeamFormat(String team) {
        if (team == null) return "kedua_tim";
        
        String normalized = team.toLowerCase().trim();
        
        
        switch (normalized) {
            case "tim a":
            case "tim_a":
            case "team a":
                return "tim_a";
            case "tim b":
            case "tim_b":  
            case "team b":
                return "tim_b";
            case "kedua tim":
            case "kedua_tim":
            case "semua tim":
            case "all teams":
            case "both teams":
                return "kedua_tim";
            default:
                return normalized;
        }
    }

    private VBox createJadwalCard(JadwalLatihan jadwal) {
        VBox card = new VBox();
        card.setSpacing(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        card.setMaxWidth(Double.MAX_VALUE);

        
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.setSpacing(10);
        
        
        String status = jadwal.getStatus() != null ? jadwal.getStatus() : "Terjadwal";
        Label statusLabel = new Label(status);
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        statusLabel.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 15;",
            status.equals("Selesai") ? "#50C878" : 
            status.equals("Berlangsung") ? "#FFD700" : 
            status.equals("Dibatalkan") ? "#e74c3c" : "#7FB069")); 
        
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
        
        Button detailBtn = createCompactButton("Detail", "#ffffff", "#7FB069");
        detailBtn.setOnAction(e -> showJadwalDetail(jadwal));
        buttonRow.getChildren().add(detailBtn);

        User currentUser = userSession.getCurrentUser();
        if (currentUser != null && "admin".equals(currentUser.getRole())) {
            Button editBtn = createCompactButton("Edit", "#4A90E2", "#ffffff");
            Button deleteBtn = createCompactButton("Hapus", "#ff6b6b", "#ffffff");
            editBtn.setOnAction(e -> showEditJadwalDialog(jadwal));
            deleteBtn.setOnAction(e -> confirmDeleteJadwal(jadwal));
            buttonRow.getChildren().addAll(editBtn, deleteBtn);
        }
        
        card.getChildren().addAll(headerRow, judulLabel, infoSection, buttonRow);
        return card;
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
        
        Button homeBtn = createModernNavIcon("🏠", false);
        homeBtn.setOnAction(e -> sceneController.showDashboardScene());
        
        Button calendarBtn = createModernNavIcon("📅", true);
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

    
    private void showAddJadwalDialog() {
        Dialog<JadwalLatihan> dialog = new Dialog<>();
        dialog.setTitle("Jadwal Latihan");
        dialog.setHeaderText(null);
        
        dialog.setResizable(false);
        dialog.getDialogPane().setPrefSize(380, 520); 
        dialog.getDialogPane().setMaxSize(380, 520);
        dialog.getDialogPane().setStyle("-fx-font-family: Arial; -fx-background-color: #f5f5f5;");
        
        ButtonType saveButtonType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);
        
        ScrollPane dialogScrollPane = new ScrollPane();
        dialogScrollPane.setFitToWidth(true);
        dialogScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        dialogScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        dialogScrollPane.setStyle("-fx-background: #f5f5f5; -fx-background-color: #f5f5f5;");
        
        VBox mainContainer = new VBox();
        mainContainer.setSpacing(0);
        mainContainer.setPrefWidth(340);
        
        
        VBox headerSection = new VBox();
        headerSection.setAlignment(Pos.CENTER);
        headerSection.setPadding(new Insets(15, 10, 15, 10));
        headerSection.setStyle("-fx-background-color: white;");
        
        Label titleLabel = new Label("Tambah Jadwal Latihan");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");
        headerSection.getChildren().add(titleLabel);
        
        
        VBox formSection = new VBox();
        formSection.setSpacing(12);
        formSection.setPadding(new Insets(15, 20, 15, 20));
        formSection.setStyle("-fx-background-color: #f5f5f5;");
        
        
        VBox namaSection = createFormFieldWithIconAndError("📝", "Nama Latihan");
        TextField namaField = createStyledFormTextField("Contoh: Latihan Teknik Dasar");
        namaField.setPrefWidth(240);
        namaSection.getChildren().add(namaField);
        
        VBox tanggalSection = createFormFieldWithIconAndError("📅", "Tanggal");
        HBox tanggalContainer = new HBox();
        tanggalContainer.setSpacing(6);
        
        ComboBox<Integer> dayCombo = createStyledIntegerComboBox("Tanggal");
        dayCombo.setPrefWidth(100);
        for (int i = 1; i <= 31; i++) {
            dayCombo.getItems().add(i);
        }
        
        ComboBox<String> monthCombo = createStyledStringComboBox("Bulan");
        monthCombo.setPrefWidth(100);
        String[] months = {"Januari", "Februari", "Maret", "April", "Mei", "Juni",
                          "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
        for (String month : months) {
            monthCombo.getItems().add(month);
        }
        
        ComboBox<Integer> yearCombo = createStyledIntegerComboBox("Tahun");
        yearCombo.setPrefWidth(100);
        for (int i = 2025; i >= 1950; i--) {
            yearCombo.getItems().add(i);
        }
        
        tanggalContainer.getChildren().addAll(dayCombo, monthCombo, yearCombo);
        tanggalSection.getChildren().add(tanggalContainer);
        
        VBox waktuSection = createFormFieldWithIconAndError("🕐", "Waktu");
        HBox waktuContainer = new HBox();
        waktuContainer.setSpacing(6);
        waktuContainer.setAlignment(Pos.CENTER_LEFT);
        
        ComboBox<Integer> jamCombo = createStyledIntegerComboBox("Jam");
        jamCombo.setPrefWidth(85);
        for (int i = 0; i <= 23; i++) {
            jamCombo.getItems().add(i);
        }
        
        ComboBox<Integer> menitCombo = createStyledIntegerComboBox("Menit");
        menitCombo.setPrefWidth(85);
        for (int i = 0; i <= 59; i++) {
            menitCombo.getItems().add(i);
        }
        
        Label witaLabel = new Label("WITA");
        witaLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        witaLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        waktuContainer.getChildren().addAll(jamCombo, menitCombo, witaLabel);
        waktuSection.getChildren().add(waktuContainer);
        
        VBox timSection = createFormFieldWithIconAndError("🏆", "Tim");
        ComboBox<String> timCombo = createStyledStringComboBox("Tim");
        timCombo.setPrefWidth(240);
        timCombo.getItems().addAll("Tim A", "Tim B", "Kedua Tim");
        timSection.getChildren().add(timCombo);
        
        VBox lokasiSection = createFormFieldWithIconAndError("📍", "Lokasi");
        TextField lokasiField = createStyledFormTextField("Lokasi");
        lokasiField.setPrefWidth(240);
        lokasiSection.getChildren().add(lokasiField);
        
        
        VBox statusSection = createFormFieldWithIconAndError("🔄", "Status Latihan");
        ComboBox<String> statusCombo = createStyledStringComboBox("Status");
        statusCombo.setPrefWidth(240);
        statusCombo.getItems().addAll("Terjadwal", "Berlangsung", "Selesai", "Dibatalkan");
        statusCombo.setValue("Terjadwal"); 
        statusSection.getChildren().add(statusCombo);
        
        VBox deskripsiSection = createFormFieldWithIconAndError("📄", "Deskripsi Latihan");
        TextArea deskripsiArea = new TextArea();
        deskripsiArea.setPromptText("Jelaskan detail latihan, target, atau catatan khusus...");
        deskripsiArea.setPrefRowCount(3);
        deskripsiArea.setMaxHeight(80);
        deskripsiArea.setWrapText(true);
        deskripsiArea.setStyle("-fx-font-size: 12; -fx-padding: 8; -fx-border-color: #ddd; " +
                             "-fx-border-radius: 4; -fx-background-radius: 4; -fx-background-color: white;");
        deskripsiSection.getChildren().add(deskripsiArea);
        
        
        formSection.getChildren().addAll(namaSection, tanggalSection, waktuSection, timSection, lokasiSection, statusSection, deskripsiSection);
        
        mainContainer.getChildren().addAll(headerSection, formSection);
        dialogScrollPane.setContent(mainContainer);
        dialog.getDialogPane().setContent(dialogScrollPane);
        
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setText("💾 Save");
        saveButton.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; " +
                           "-fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 11;");
        
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.setText("❌ Cancel");
        cancelButton.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; " +
                             "-fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 11;");
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                
                clearAllFormErrors(namaSection, tanggalSection, waktuSection, timSection, lokasiSection, statusSection, deskripsiSection);
                
                boolean hasErrors = false;
                
                
                if (namaField.getText().trim().isEmpty()) {
                    setFormFieldError(namaSection, "Nama latihan harus diisi");
                    hasErrors = true;
                }
                
                if (dayCombo.getValue() == null || monthCombo.getValue() == null || yearCombo.getValue() == null) {
                    setFormFieldError(tanggalSection, "Tanggal harus dipilih lengkap");
                    hasErrors = true;
                }
                
                if (jamCombo.getValue() == null || menitCombo.getValue() == null) {
                    setFormFieldError(waktuSection, "Waktu harus dipilih lengkap");
                    hasErrors = true;
                }
                
                if (timCombo.getValue() == null) {
                    setFormFieldError(timSection, "Tim harus dipilih");
                    hasErrors = true;
                }
                
                if (lokasiField.getText().trim().isEmpty()) {
                    setFormFieldError(lokasiSection, "Lokasi harus diisi");
                    hasErrors = true;
                }
                
                if (statusCombo.getValue() == null) {
                    setFormFieldError(statusSection, "Status harus dipilih");
                    hasErrors = true;
                }
                
                if (hasErrors) {
                    return null; 
                }
                
                try {
                    LocalDate tanggal = LocalDate.of(yearCombo.getValue(), 
                                                   monthCombo.getSelectionModel().getSelectedIndex() + 1, 
                                                   dayCombo.getValue());
                    LocalTime waktu = LocalTime.of(jamCombo.getValue(), menitCombo.getValue());
                    
                    String timValue = timCombo.getValue();
                    String finalTimValue = "Kedua Tim";
                    if ("Tim A".equals(timValue)) finalTimValue = "Tim A";
                    else if ("Tim B".equals(timValue)) finalTimValue = "Tim B";
                    
                return new JadwalLatihan(
                    0, 
                    namaField.getText().trim(), 
                    tanggal,
                    waktu,
                    lokasiField.getText().trim().isEmpty() ? "TBD" : lokasiField.getText().trim(),
                    statusCombo.getValue(),
                    deskripsiArea.getText().trim().isEmpty() ? "Tidak ada deskripsi." : deskripsiArea.getText().trim(),
                    finalTimValue
                );

                } catch (Exception e) {
                    setFormFieldError(tanggalSection, "Tanggal tidak valid");
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(jadwal -> {
            dataManager.addJadwal(jadwal);
            showCompactSuccessAlert("Berhasil!", "Jadwal latihan berhasil ditambahkan");
            refreshJadwalList(searchField.getText().toLowerCase().trim());
        });
    }

    
    private void showEditJadwalDialog(JadwalLatihan existingJadwal) {
        Dialog<JadwalLatihan> dialog = new Dialog<>();
        dialog.setTitle("Edit Jadwal Latihan");
        dialog.setHeaderText(null);
        
        dialog.setResizable(false);
        dialog.getDialogPane().setPrefSize(380, 520);
        dialog.getDialogPane().setMaxSize(380, 520);
        dialog.getDialogPane().setStyle("-fx-font-family: Arial; -fx-background-color: #f5f5f5;");
        
        ButtonType saveButtonType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);
        
        ScrollPane dialogScrollPane = new ScrollPane();
        dialogScrollPane.setFitToWidth(true);
        dialogScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        dialogScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        dialogScrollPane.setStyle("-fx-background: #f5f5f5; -fx-background-color: #f5f5f5;");
        
        VBox mainContainer = new VBox();
        mainContainer.setSpacing(0);
        mainContainer.setPrefWidth(340);
        
        
        VBox headerSection = new VBox();
        headerSection.setAlignment(Pos.CENTER);
        headerSection.setPadding(new Insets(15, 10, 15, 10));
        headerSection.setStyle("-fx-background-color: white;");
        
        Label titleLabel = new Label("Edit Jadwal Latihan");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");
        headerSection.getChildren().add(titleLabel);
        
        
        VBox formSection = new VBox();
        formSection.setSpacing(12);
        formSection.setPadding(new Insets(15, 20, 15, 20));
        formSection.setStyle("-fx-background-color: #f5f5f5;");
        
        
        VBox namaSection = createFormFieldWithIconAndError("📝", "Nama Latihan");
        TextField namaField = createStyledFormTextField("Contoh: Latihan Teknik Dasar");
        namaField.setPrefWidth(240);
        namaField.setText(existingJadwal.getNamaKegiatan());
        namaSection.getChildren().add(namaField);
        
        VBox tanggalSection = createFormFieldWithIconAndError("📅", "Tanggal");
        HBox tanggalContainer = new HBox();
        tanggalContainer.setSpacing(6);
        
        ComboBox<Integer> dayCombo = createStyledIntegerComboBox("Tanggal");
        dayCombo.setPrefWidth(100);
        for (int i = 1; i <= 31; i++) {
            dayCombo.getItems().add(i);
        }
        dayCombo.setValue(existingJadwal.getTanggal().getDayOfMonth());
        
        ComboBox<String> monthCombo = createStyledStringComboBox("Bulan");
        monthCombo.setPrefWidth(100);
        String[] months = {"Januari", "Februari", "Maret", "April", "Mei", "Juni",
                          "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
        for (String month : months) {
            monthCombo.getItems().add(month);
        }
        monthCombo.setValue(months[existingJadwal.getTanggal().getMonthValue() - 1]);
        
        ComboBox<Integer> yearCombo = createStyledIntegerComboBox("Tahun");
        yearCombo.setPrefWidth(100);
        for (int i = 2025; i >= 1950; i--) {
            yearCombo.getItems().add(i);
        }
        yearCombo.setValue(existingJadwal.getTanggal().getYear());
        
        tanggalContainer.getChildren().addAll(dayCombo, monthCombo, yearCombo);
        tanggalSection.getChildren().add(tanggalContainer);
        
        VBox waktuSection = createFormFieldWithIconAndError("🕐", "Waktu");
        HBox waktuContainer = new HBox();
        waktuContainer.setSpacing(6);
        waktuContainer.setAlignment(Pos.CENTER_LEFT);
        
        ComboBox<Integer> jamCombo = createStyledIntegerComboBox("Jam");
        jamCombo.setPrefWidth(85);
        for (int i = 0; i <= 23; i++) {
            jamCombo.getItems().add(i);
        }
        jamCombo.setValue(existingJadwal.getWaktu().getHour());
        
        ComboBox<Integer> menitCombo = createStyledIntegerComboBox("Menit");
        menitCombo.setPrefWidth(85);
        for (int i = 0; i <= 59; i++) {
            menitCombo.getItems().add(i);
        }
        menitCombo.setValue(existingJadwal.getWaktu().getMinute());
        
        Label witaLabel = new Label("WITA");
        witaLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        witaLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        waktuContainer.getChildren().addAll(jamCombo, menitCombo, witaLabel);
        waktuSection.getChildren().add(waktuContainer);
        
        VBox timSection = createFormFieldWithIconAndError("🏆", "Tim");
        ComboBox<String> timCombo = createStyledStringComboBox("Tim");
        timCombo.setPrefWidth(240);
        timCombo.getItems().addAll("Tim A", "Tim B", "Kedua Tim");
        timCombo.setValue(existingJadwal.getTim());
        timSection.getChildren().add(timCombo);
        
        VBox lokasiSection = createFormFieldWithIconAndError("📍", "Lokasi");
        TextField lokasiField = createStyledFormTextField("Lokasi");
        lokasiField.setPrefWidth(240);
        lokasiField.setText(existingJadwal.getLokasi());
        lokasiSection.getChildren().add(lokasiField);
        
        VBox statusSection = createFormFieldWithIconAndError("🔄", "Status Latihan");
        ComboBox<String> statusCombo = createStyledStringComboBox("Status");
        statusCombo.setPrefWidth(240);
        statusCombo.getItems().addAll("Terjadwal", "Berlangsung", "Selesai", "Dibatalkan");
        statusCombo.setValue(existingJadwal.getStatus());
        statusSection.getChildren().add(statusCombo);
        
        VBox deskripsiSection = createFormFieldWithIconAndError("📄", "Deskripsi Latihan");
        TextArea deskripsiArea = new TextArea();
        deskripsiArea.setPromptText("Jelaskan detail latihan, target, atau catatan khusus...");
        deskripsiArea.setPrefRowCount(3);
        deskripsiArea.setMaxHeight(80);
        deskripsiArea.setWrapText(true);
        deskripsiArea.setText(existingJadwal.getDeskripsi());
        deskripsiArea.setStyle("-fx-font-size: 12; -fx-padding: 8; -fx-border-color: #ddd; " +
                             "-fx-border-radius: 4; -fx-background-radius: 4; -fx-background-color: white;");
        deskripsiSection.getChildren().add(deskripsiArea);
        
        formSection.getChildren().addAll(namaSection, tanggalSection, waktuSection, timSection, lokasiSection, statusSection, deskripsiSection);
        
        mainContainer.getChildren().addAll(headerSection, formSection);
        dialogScrollPane.setContent(mainContainer);
        dialog.getDialogPane().setContent(dialogScrollPane);
        
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setText("💾 Update");
        saveButton.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; " +
                           "-fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 11;");
        
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.setText("❌ Cancel");
        cancelButton.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; " +
                             "-fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 11;");
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                clearAllFormErrors(namaSection, tanggalSection, waktuSection, timSection, lokasiSection, statusSection, deskripsiSection);
                
                boolean hasErrors = false;
                
                if (namaField.getText().trim().isEmpty()) {
                    setFormFieldError(namaSection, "Nama latihan harus diisi");
                    hasErrors = true;
                }
                
                if (dayCombo.getValue() == null || monthCombo.getValue() == null || yearCombo.getValue() == null) {
                    setFormFieldError(tanggalSection, "Tanggal harus dipilih lengkap");
                    hasErrors = true;
                }
                
                if (jamCombo.getValue() == null || menitCombo.getValue() == null) {
                    setFormFieldError(waktuSection, "Waktu harus dipilih lengkap");
                    hasErrors = true;
                }
                
                if (timCombo.getValue() == null) {
                    setFormFieldError(timSection, "Tim harus dipilih");
                    hasErrors = true;
                }
                
                if (lokasiField.getText().trim().isEmpty()) {
                    setFormFieldError(lokasiSection, "Lokasi harus diisi");
                    hasErrors = true;
                }
                
                if (statusCombo.getValue() == null) {
                    setFormFieldError(statusSection, "Status harus dipilih");
                    hasErrors = true;
                }
                
                if (hasErrors) {
                    return null;
                }
                
                try {
                    LocalDate tanggal = LocalDate.of(yearCombo.getValue(), 
                                                   monthCombo.getSelectionModel().getSelectedIndex() + 1, 
                                                   dayCombo.getValue());
                    LocalTime waktu = LocalTime.of(jamCombo.getValue(), menitCombo.getValue());

                    existingJadwal.setNamaKegiatan(namaField.getText().trim());
                    existingJadwal.setTanggal(tanggal);
                    existingJadwal.setWaktu(waktu);
                    existingJadwal.setLokasi(lokasiField.getText().trim());
                    existingJadwal.setStatus(statusCombo.getValue()); 
                    existingJadwal.setDeskripsi(deskripsiArea.getText().trim().isEmpty() ? "Tidak ada deskripsi." : deskripsiArea.getText().trim());
                    existingJadwal.setTim(timCombo.getValue()); 

                    return existingJadwal;
                } catch (Exception e) {
                    setFormFieldError(tanggalSection, "Tanggal tidak valid");
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(jadwal -> {
            dataManager.updateJadwal(jadwal);
            showCompactSuccessAlert("Berhasil!", "Jadwal berhasil diperbarui");
            refreshJadwalList(searchField.getText());
        });
    }

    private VBox createFormFieldWithIconAndError(String icon, String labelText) {
        VBox section = new VBox();
        section.setSpacing(6);
        
        HBox labelContainer = new HBox();
        labelContainer.setSpacing(6);
        labelContainer.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(14));
        
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        label.setStyle("-fx-text-fill: #2c3e50;");
        
        labelContainer.getChildren().addAll(iconLabel, label);
        
        Label errorLabel = new Label();
        errorLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
        errorLabel.setStyle("-fx-text-fill: #ff4444;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        
        section.getChildren().addAll(labelContainer, errorLabel);
        return section;
    }

    
    private void clearAllFormErrors(VBox... sections) {
        for (VBox section : sections) {
            if (section.getChildren().size() > 1) {
                Label errorLabel = (Label) section.getChildren().get(1);
                errorLabel.setVisible(false);
                errorLabel.setManaged(false);
            }
        }
    }
    
    private void setFormFieldError(VBox section, String errorMessage) {
        if (section.getChildren().size() > 1) {
            Label errorLabel = (Label) section.getChildren().get(1);
            errorLabel.setText(errorMessage);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }

    private ComboBox<String> createStyledStringComboBox(String promptText) {
        ComboBox<String> combo = new ComboBox<>();
        combo.setPromptText(promptText);
        combo.setStyle("-fx-font-size: 11; -fx-padding: 5; -fx-border-color: #ddd; " +
                      "-fx-border-radius: 4; -fx-background-radius: 4; -fx-background-color: white;");
        combo.setPrefWidth(80);
        return combo;
    }
    
    private ComboBox<Integer> createStyledIntegerComboBox(String promptText) {
        ComboBox<Integer> combo = new ComboBox<>();
        combo.setPromptText(promptText);
        combo.setStyle("-fx-font-size: 11; -fx-padding: 5; -fx-border-color: #ddd; " +
                      "-fx-border-radius: 4; -fx-background-radius: 4; -fx-background-color: white;");
        combo.setPrefWidth(80);
        return combo;
    }
    
    private TextField createStyledFormTextField(String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setStyle("-fx-font-size: 12; -fx-padding: 8; -fx-border-color: #ddd; " +
                      "-fx-border-radius: 4; -fx-background-radius: 4; -fx-background-color: white;");
        return field;
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

    
    private void confirmDeleteJadwal(JadwalLatihan jadwal) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Hapus");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText(null);
        
        confirmAlert.getDialogPane().setPrefSize(320, 200);
        confirmAlert.getDialogPane().setMaxSize(320, 200);
        
        DialogPane dialogPane = confirmAlert.getDialogPane();
        dialogPane.setGraphic(null);
        
        dialogPane.setStyle(
            "-fx-background-color: #fff8f8; " +
            "-fx-border-color: #ff6b6b; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 3);"
        );
        
        VBox content = new VBox();
        content.setAlignment(Pos.CENTER);
        content.setSpacing(12);
        content.setPadding(new Insets(20, 25, 15, 25));
        
        HBox titleContainer = new HBox();
        titleContainer.setAlignment(Pos.CENTER);
        titleContainer.setSpacing(8);
        
        Label iconLabel = new Label("⚠");
        iconLabel.setFont(Font.font("Arial", 20));
        
        Label titleLabel = new Label("Konfirmasi Hapus");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: #d32f2f;");
        
        titleContainer.getChildren().addAll(iconLabel, titleLabel);
        
        Label messageLabel = new Label("Apakah Anda yakin ingin menghapus jadwal:");
        messageLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        messageLabel.setStyle("-fx-text-fill: #2c3e50;");
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        messageLabel.setWrapText(true);
        
        Label jadwalLabel = new Label("\"" + jadwal.getNamaKegiatan() + "\"");
        jadwalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        jadwalLabel.setStyle("-fx-text-fill: #d32f2f;");
        jadwalLabel.setTextAlignment(TextAlignment.CENTER);
        jadwalLabel.setWrapText(true);
        
        Label warningLabel = new Label("Tindakan ini tidak dapat dibatalkan!");
        warningLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
        warningLabel.setStyle("-fx-text-fill: #666;");
        warningLabel.setTextAlignment(TextAlignment.CENTER);
        
        content.getChildren().addAll(titleContainer, messageLabel, jadwalLabel, warningLabel);
        dialogPane.setContent(content);
        
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setText("🗑 Hapus");
        okButton.setStyle(
            "-fx-background-color: #d32f2f; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 12px; " +
            "-fx-background-radius: 6; " +
            "-fx-border-radius: 6; " +
            "-fx-padding: 8 20; " +
            "-fx-cursor: hand;"
        );
        
        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
        cancelButton.setText("❌ Batal");
        cancelButton.setStyle(
            "-fx-background-color: #757575; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 12px; " +
            "-fx-background-radius: 6; " +
            "-fx-border-radius: 6; " +
            "-fx-padding: 8 20; " +
            "-fx-cursor: hand;"
        );
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            dataManager.deleteJadwal(jadwal.getId());
            showCompactSuccessAlert("Berhasil!", "Jadwal telah dihapus.");
            refreshJadwalList(searchField.getText());
        }
    }

    private void showCompactSuccessAlert(String title, String message) {
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
        
        VBox content = new VBox();
        content.setAlignment(Pos.CENTER);
        content.setSpacing(12);
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