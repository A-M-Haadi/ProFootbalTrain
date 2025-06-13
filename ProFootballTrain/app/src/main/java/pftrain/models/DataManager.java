package pftrain.models;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataManager {
    private static DataManager instance;
    
    private static final String DATA_FOLDER = "data";
    private static final String USERS_FILE = DATA_FOLDER + File.separator + "users.txt";
    private static final String JADWAL_FILE = DATA_FOLDER + File.separator + "jadwal.txt";
    private static final String ABSENSI_FILE = DATA_FOLDER + File.separator + "absensi.txt";

    private List<User> users;
    private List<JadwalLatihan> jadwalList;
    private List<Absensi> absensiList;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private DataManager() {
        File dataDir = new File(DATA_FOLDER);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        this.users = new ArrayList<>();
        this.jadwalList = new ArrayList<>();
        this.absensiList = new ArrayList<>();
        loadAllData();
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    private void loadAllData() {
        loadUsers();
        loadJadwal();
        loadAbsensi();
        if (users.isEmpty()) {
            Admin admin = new Admin("admin1@admin", "admin111", "Administrator");
            users.add(admin);
            saveUsers();
        }
    }

    private void loadUsers() {
        users.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);

                if (parts.length < 4) {
                    System.err.println("Warning: Invalid user data line (too few parts): " + line);
                    continue; 
                }

                String username = parts[0];
                String password = parts[1];
                String fullName = parts[2];
                String role = parts[3].toLowerCase(); 
                
                String avatarColor = (parts.length > 9 && !parts[8].isEmpty()) ? parts[8] : "#7FB069";

                User user;

                
                if ("admin".equalsIgnoreCase(role)) {
                    user = new Admin(username, password, fullName);
                } else if ("player".equalsIgnoreCase(role)) {
                    if (parts.length < 8) {
                        System.err.println("Warning: Invalid player data line (too few parts for player): " + line);
                        continue;
                    }
                    LocalDate tanggalLahir = LocalDate.parse(parts[4], DATE_FORMATTER);
                    String posisi = parts[5];
                    int nomorPunggung = Integer.parseInt(parts[6]);
                    String tim = (parts[7] != null && !parts[7].isEmpty()) ? parts[7] : null; 
                    
                    user = new Player(username, password, fullName, tanggalLahir, posisi, nomorPunggung, tim);
                } else {
                    System.err.println("Warning: Unknown user role encountered: " + role + " in line: " + line);
                    continue;
                }

                user.setAvatarColor(avatarColor);
                
                users.add(user);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Users file not found. Creating a new one on save.");
        } catch (IOException e) {
            System.err.println("Error reading users file: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException | java.time.format.DateTimeParseException e) {
            System.err.println("Error parsing user data (number or date format): " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadJadwal() {
        jadwalList.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(JADWAL_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 8) {
                    jadwalList.add(new JadwalLatihan(
                        Integer.parseInt(parts[0]), parts[1], LocalDate.parse(parts[2], DATE_FORMATTER), 
                        LocalTime.parse(parts[3]), parts[4], parts[5], parts[6], parts[7]
                    ));
                } else {
                    System.err.println("Warning: Invalid jadwal data line (incorrect parts count): " + line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Jadwal file not found. Creating a new one on save.");
        } catch (IOException e) { 
            System.err.println("Error reading jadwal file: " + e.getMessage());
            e.printStackTrace(); 
        } catch (NumberFormatException | java.time.format.DateTimeParseException e) {
            System.err.println("Error parsing jadwal data (number or time/date format): " + e.getMessage());
            e.printStackTrace(); 
        }
    }

    private void loadAbsensi() {
        absensiList.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(ABSENSI_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    absensiList.add(new Absensi(Integer.parseInt(parts[0]), parts[1], parts[2]));
                } else {
                    System.err.println("Warning: Invalid absensi data line (incorrect parts count): " + line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Absensi file not found. Creating a new one on save.");
        } catch (IOException e) { 
            System.err.println("Error reading absensi file: " + e.getMessage());
            e.printStackTrace(); 
        } catch (NumberFormatException e) {
            System.err.println("Error parsing absensi data (number format): " + e.getMessage());
            e.printStackTrace(); 
        }
    }

    private void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User user : users) {
                String avatarColor = (user.getAvatarColor() != null) ? user.getAvatarColor() : "";

                if (user instanceof Admin) {
                    Admin admin = (Admin) user;
                    writer.write(String.join(",",
                            admin.getUsername(), admin.getPassword(), admin.getFullName(), admin.getRole(),
                            avatarColor
                    ));
                } else if (user instanceof Player) {
                    Player player = (Player) user;
                    String tim = (player.getTim() != null) ? player.getTim() : ""; 
                    writer.write(String.join(",",
                            player.getUsername(), player.getPassword(), player.getFullName(), player.getRole(),
                            player.getTanggalLahir().format(DATE_FORMATTER), player.getPosisi(), 
                            String.valueOf(player.getNomorPunggung()), tim,
                            avatarColor
                    ));
                } else {
                    System.err.println("Warning: Attempting to save unknown user type: " + user.getClass().getName());
                    writer.write(String.join(",", user.getUsername(), user.getPassword(), user.getFullName(), user.getRole(),
                                             "", "", "", "", avatarColor));
                }
                writer.newLine();
            }
        } catch (IOException e) { 
            System.err.println("Error writing users file: " + e.getMessage());
            e.printStackTrace(); 
        }
    }

    private void saveJadwal() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(JADWAL_FILE))) {
            for (JadwalLatihan j : jadwalList) {
                writer.write(String.join(",",
                    String.valueOf(j.getId()), j.getNamaKegiatan(), j.getTanggal().format(DATE_FORMATTER),
                    j.getWaktu().toString(), j.getLokasi(), j.getStatus(),
                    j.getDeskripsi(), j.getTim()
                ));
                writer.newLine();
            }
        } catch (IOException e) { 
            System.err.println("Error writing jadwal file: " + e.getMessage());
            e.printStackTrace(); 
        }
    }
    
    private void saveAbsensi() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ABSENSI_FILE))) {
            for (Absensi a : absensiList) {
                writer.write(String.join(",", String.valueOf(a.getJadwalId()), a.getUsernamePemain(), a.getStatusKehadiran()));
                writer.newLine();
            }
        } catch (IOException e) { 
            System.err.println("Error writing absensi file: " + e.getMessage());
            e.printStackTrace(); 
        }
    }

    public User login(String username, String password) {
        return users.stream()
            .filter(user -> user.getUsername().equals(username) && user.verifyPassword(password))
            .findFirst().orElse(null);
    }

    public void addUser(User user) { 
        boolean usernameExists = users.stream().anyMatch(u -> u.getUsername().equals(user.getUsername()));
        if (usernameExists) {
            System.err.println("Error: Username '" + user.getUsername() + "' already exists.");
            return;
        }
        users.add(user); 
        saveUsers(); 
    }
    
    public void updateUser(User userToUpdate) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(userToUpdate.getUsername())) {
                users.set(i, userToUpdate);
                saveUsers();
                return;
            }
        }
        System.err.println("Error: User with username '" + userToUpdate.getUsername() + "' not found for update.");
    }
    
    public List<User> getUsers() { return new ArrayList<>(users); }

    public List<Player> getPlayers() { 
        return users.stream()
                     .filter(u -> "player".equalsIgnoreCase(u.getRole()) && u instanceof Player) 
                     .map(u -> (Player) u)
                     .collect(Collectors.toList()); 
    }

    public List<JadwalLatihan> getJadwalList() { return new ArrayList<>(jadwalList); }
    
    public void addJadwal(JadwalLatihan jadwal) {
        int nextId = jadwalList.stream().mapToInt(JadwalLatihan::getId).max().orElse(0) + 1;
        jadwal.setId(nextId);
        jadwalList.add(jadwal);
        saveJadwal();
    }
    
    public void updateJadwal(JadwalLatihan jadwalToUpdate) {
        for (int i = 0; i < jadwalList.size(); i++) {
            if (jadwalList.get(i).getId() == jadwalToUpdate.getId()) {
                jadwalList.set(i, jadwalToUpdate);
                break;
            }
        }
        saveJadwal();
    }
    
    public void deleteJadwal(int jadwalId) {
        jadwalList.removeIf(j -> j.getId() == jadwalId);
        absensiList.removeIf(a -> a.getJadwalId() == jadwalId);
        saveJadwal();
        saveAbsensi();
    }

    public void rekamAbsensi(Absensi absensi) {
        boolean found = false;
        for (int i = 0; i < absensiList.size(); i++) {
            Absensi existing = absensiList.get(i);
            if (existing.getJadwalId() == absensi.getJadwalId() && existing.getUsernamePemain().equals(absensi.getUsernamePemain())) {
                absensiList.set(i, absensi);
                found = true;
                break;
            }
        }
        if (!found) {
            absensiList.add(absensi);
        }
        saveAbsensi();
    }

    public List<Absensi> getAbsensiByJadwal(int jadwalId) { 
        return absensiList.stream()
            .filter(a -> a.getJadwalId() == jadwalId)
            .collect(Collectors.toList()); 
    }
    
    public long getTotalKehadiran(String usernamePemain) { 
        return absensiList.stream()
            .filter(a -> a.getUsernamePemain().equals(usernamePemain) && "Hadir".equalsIgnoreCase(a.getStatusKehadiran()))
            .count(); 
    }

    public List<Absensi> getAllAbsensi() {
        return new ArrayList<>(absensiList);
    }

    
    public long getJumlahPemainTim(String tim) {
        if (tim == null) {
            return users.stream()
                .filter(u -> u instanceof Player)
                .map(u -> (Player) u)
                .filter(player -> player.getTim() == null)
                .count();
        }
        
        return users.stream()
            .filter(u -> u instanceof Player)
            .map(u -> (Player) u)
            .filter(player -> tim.equals(player.getTim()))
            .count();
    }

    
    public Map<String, List<Player>> getTimRoster() {
        Map<String, List<Player>> roster = new HashMap<>();
        roster.put("tim_a", new ArrayList<>());
        roster.put("tim_b", new ArrayList<>());
        
        for (User user : users) {
            if (user instanceof Player) {
                Player player = (Player) user;
                String tim = player.getTim();
                if ("tim_a".equals(tim)) {
                    roster.get("tim_a").add(player);
                } else if ("tim_b".equals(tim)) {
                    roster.get("tim_b").add(player);
                }
                
            }
        }
        
        return roster;
    }

    public User getUserByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    
    public List<Player> getPlayersWithoutTeam() {
        return users.stream()
                .filter(u -> u instanceof Player)
                .map(u -> (Player) u)
                .filter(player -> player.getTim() == null || player.getTim().isEmpty())
                .collect(Collectors.toList());
    }

    
    public boolean assignPlayerToTeam(String username, String tim) {
        User user = getUserByUsername(username);
        if (user instanceof Player) {
            Player player = (Player) user;
            player.setTim(tim);
            updateUser(player);
            return true;
        }
        return false;
    }
}