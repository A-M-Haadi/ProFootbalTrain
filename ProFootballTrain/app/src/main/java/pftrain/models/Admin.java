package pftrain.models;

public class Admin extends User implements IDisplayable {
    public Admin(String username, String password, String fullName) {
        super(username, password, fullName, "admin");
    }

    @Override
    public void login() {
        System.out.println("Anda login sebagai: " + getFullName());
    }

    @Override
    public void logout() {
        System.out.println(getFullName() + " logged out");
    }

    @Override
    public String getDisplayName() {
        return getFullName() + " (Admin)";
    }

    @Override 
    public String getDisplayInfo() {
        return "Administrator - Full System Access";
    }


}

