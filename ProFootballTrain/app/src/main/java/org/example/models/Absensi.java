package pftrain.models;

public class Absensi {
    private int jadwalId;
    private String usernamePemain;
    private String statusKehadiran;

    public Absensi(int jadwalId, String usernamePemain, String statusKehadiran) {
        this.jadwalId = jadwalId;
        this.usernamePemain = usernamePemain;
        this.statusKehadiran = statusKehadiran;
    }

    public int getJadwalId() { return jadwalId; }
    public String getUsernamePemain() { return usernamePemain; }
    public String getStatusKehadiran() { return statusKehadiran; }
    public void setStatusKehadiran(String status) { this.statusKehadiran = status; }
}