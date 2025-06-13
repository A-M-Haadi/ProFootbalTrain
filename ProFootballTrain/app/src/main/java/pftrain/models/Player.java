package pftrain.models;

import java.time.LocalDate;

public class Player extends User implements IDisplayable{
    private LocalDate tanggalLahir;
    private String posisi;
    private int nomorPunggung;
    private String tim;

    public Player(String username, String password, String fullName, 
                  LocalDate tanggalLahir, String posisi, int nomorPunggung, String tim) {
        super(username, password, fullName, "player");
        this.tanggalLahir = tanggalLahir;
        this.posisi = posisi;
        this.nomorPunggung = nomorPunggung;
        this.tim = tim;
    }

    public LocalDate getTanggalLahir() { return tanggalLahir; }
    public String getPosisi() { return posisi; }
    public int getNomorPunggung() { return nomorPunggung; }
    public String getTim() { return tim; }

    public void setTanggalLahir(LocalDate tanggalLahir) { this.tanggalLahir = tanggalLahir; }
    public void setPosisi(String posisi) { this.posisi = posisi; }
    public void setNomorPunggung(int nomorPunggung) { this.nomorPunggung = nomorPunggung; }
    public void setTim(String tim) { this.tim = tim; }

    @Override
    public void login() {
        System.out.println(getFullName() + " logged in");
    }

    @Override
    public void logout() {
        System.out.println(getFullName() + " logged out");
    }
    @Override
    public String getDisplayName() {
        return getFullName() + " (#" + nomorPunggung + ")";
    }

    @Override
    public String getDisplayInfo() {
        return posisi + " - " + tim + " - Age: " + calculateAge();
    }

    public int calculateAge() {
    if (tanggalLahir == null) return 0;
    return java.time.Period.between(tanggalLahir, java.time.LocalDate.now()).getYears();
    }

}
