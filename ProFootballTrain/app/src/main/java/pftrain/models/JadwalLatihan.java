package pftrain.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class JadwalLatihan {
    private int id;
    private String namaKegiatan;
    private LocalDate tanggal;
    private LocalTime waktu;
    private String lokasi;
    private String status;
    private String deskripsi;
    private String tim;

    public JadwalLatihan() {}

    public JadwalLatihan(int id, String namaKegiatan, LocalDate tanggal, LocalTime waktu, String lokasi, String status, String deskripsi, String tim) {
        this.id = id;
        this.namaKegiatan = namaKegiatan;
        this.tanggal = tanggal;
        this.waktu = waktu;
        this.lokasi = lokasi;
        this.status = status;
        this.deskripsi = deskripsi;
        this.tim = tim;
    }
    
    public int getId() { return id; }
    public String getNamaKegiatan() { return namaKegiatan; }
    public LocalDate getTanggal() { return tanggal; }
    public LocalTime getWaktu() { return waktu; }
    public String getLokasi() { return lokasi; }
    public String getStatus() { return status; }
    public String getDeskripsi() { return deskripsi; }
    public String getTim() { return this.tim;}

    public void setId(int id) { this.id = id; }
    public void setNamaKegiatan(String namaKegiatan) { this.namaKegiatan = namaKegiatan; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }
    public void setWaktu(LocalTime waktu) { this.waktu = waktu; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }
    public void setStatus(String status) { this.status = status; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public void setTim(String tim) { this.tim = tim;}
}