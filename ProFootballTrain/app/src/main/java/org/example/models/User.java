package pftrain.models;

public abstract class User {
    protected String username;
    protected String password;
    protected String fullName;
    protected String role;
    protected String avatarColor = "#7FB069";

    public User(String username, String password, String fullName, String role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }
    
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public String getAvatarColor() { return avatarColor; }

    public void setPassword(String password) { this.password = password; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setAvatarColor(String avatarColor) { this.avatarColor = avatarColor; }

    public boolean verifyPassword(String plainTextPassword) {
        if (this.password == null || plainTextPassword == null) {
            return false;
        }
        return this.password.equals(plainTextPassword);
    }

    public abstract void login();
    public abstract void logout();
}