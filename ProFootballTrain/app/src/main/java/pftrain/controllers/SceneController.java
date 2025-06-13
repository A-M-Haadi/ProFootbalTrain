package pftrain.controllers;

import javafx.stage.Stage;
import pftrain.models.User;
import pftrain.models.UserSession;
import pftrain.views.*;

public class SceneController {
    private final Stage primaryStage;
    private final UserSession userSession;

    private PlayerAbsensiView playerAbsensiView;

    public SceneController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.userSession = UserSession.getInstance();
    }

    public void showWelcomeScene() {
        WelcomeView welcomeView = new WelcomeView(this);
        primaryStage.setScene(welcomeView.getScene());
        primaryStage.setTitle("Welcome");
        primaryStage.show();
    }

    public void showLoginScene() {
        LoginView loginView = new LoginView(this);
        primaryStage.setScene(loginView.getScene());
        primaryStage.setTitle("Login");
        primaryStage.show();
    }

    public void showSignUpScene() {
        SignUpView signUpView = new SignUpView(this);
        primaryStage.setScene(signUpView.getSignUpScene());
        primaryStage.setTitle("Sign Up");
        primaryStage.show();
    }

    public void showDashboardScene() {
        DashboardView dashboardView = new DashboardView(this);
        primaryStage.setScene(dashboardView.getScene());
        primaryStage.setTitle("Dashboard");
        primaryStage.show();
    }

    
    public void showAdminProfileScene() {
        AdminProfileView adminProfileView = new AdminProfileView(this);
        primaryStage.setScene(adminProfileView.getScene());
        primaryStage.setTitle("Profil Admin");
        primaryStage.show();
    }

    public void showPlayerProfileScene() {
        PlayerProfileView playerProfileView = new PlayerProfileView(this);
        primaryStage.setScene(playerProfileView.getScene());
        primaryStage.setTitle("Profil Player");
        primaryStage.show();
    }

    
    public void showProfileScene() {
        User currentUser = userSession.getCurrentUser();

        if (currentUser != null && "admin".equalsIgnoreCase(currentUser.getRole())) {
            showAdminProfileScene();
        } else {
            showPlayerProfileScene();
        }
    }

    public void showJadwalScene() {
        JadwalView jadwalView = new JadwalView(this);
        primaryStage.setScene(jadwalView.getScene());
        primaryStage.setTitle("Jadwal Latihan");
        primaryStage.show();
    }

    public void showAbsensiScene() {
        User currentUser = userSession.getCurrentUser();
        if (currentUser != null && "admin".equalsIgnoreCase(currentUser.getRole())) {
            AbsensiView absensiView = new AbsensiView(this);
            primaryStage.setScene(absensiView.getScene());
            primaryStage.setTitle("Absensi Admin");
        } else {
            showPlayerAbsensiSummary();
        }
        primaryStage.show();
    }

    public void showPlayerAbsensiSummary() {
        this.playerAbsensiView = new PlayerAbsensiView(this);
        primaryStage.setScene(playerAbsensiView.getSummaryScene());
        primaryStage.setTitle("Ringkasan Absensi Pemain");
        primaryStage.show();
    }

    public void showPlayerAbsensiDetail() {
        if (this.playerAbsensiView != null) {
            primaryStage.setScene(playerAbsensiView.getDetailScene());
            primaryStage.setTitle("Detail Absensi Pemain");
        } else {
            showPlayerAbsensiSummary();
        }
        primaryStage.show();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}