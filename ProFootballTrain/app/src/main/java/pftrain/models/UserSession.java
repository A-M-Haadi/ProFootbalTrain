package pftrain.models;

public final class UserSession {
    private static UserSession instance;
    private User currentUser;

    private UserSession() {}

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setCurrentUser(User user) { this.currentUser = user; }
    public User getCurrentUser() { return this.currentUser; }
    public String getCurrentUserRole() { return (currentUser != null) ? currentUser.getRole() : null; }
    public void cleanSession() { this.currentUser = null; }
}