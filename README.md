
# ProFootballTrain
ProFootballTrain merupakan aplikasi manajemen latihan berbasis Java  yang dirancang untuk membantu pelatih dalam merencanakan kegiatan latihan tim sepak bola secara efisien dan terstruktur. Aplikasi ini memungkinkan pelatih untuk membuat jadwal latihan dan mencatat kehadiran. Aplikasi ini juga menyediakan akses terbatas bagi pemain agar mereka dapat melihat jadwal latihan dan status kehadiran mereka sendiri.

## Registrasi & Login
Pengguna memilih peran sebagai `Pemain` atau `Admin/Pelatih`

### **1.Pemain**

Melakukan registrasi manual dengan mengisi:

1. Nama

2. Tanggal Lahir

3. Nomor Punggung

4. Posisi
   
5. Tim

Setelah data diisi sistem akan secara otomatis menghasilkan:

**1. Username** 

gabungan nama (tanpa spasi) dan tahun lahir dengan tambahan `@pemain`
Contoh: `andika0900@pemain`

**2. Password** 

Digenerate otomatis berdasarkan kombinasi karakter dari data yang mengandung:

* 1 huruf kapital

* 1 huruf kecil

* 1 angka
Contoh: `A7ndiaka`, `R2ianmp`

### **2. Pelatih**

1. Tidak perlu melakukan registrasi.

2. Login menggunakan Username dan Password tetap yang telah ditentukan sebelumnya oleh sistem.
   
   Gunakan **Username** dan **Password** berikut untuk login sebagai Admin/Pelatih:

* **Username 1**  `Admin1@admin` **Password** `Admin111`

* **Username 2**  `Admin2@admin` **Password** `Admin222`

Semua data pengguna, termasuk informasi personal dan login (username & password) akan disimpan dalam file `users.txt`

## Penyimpanan Data
Semua data disimpan dalam file:
1. `users.txt` menyimpan data username dan password pengguna (Pemain dan Admin/Pelatih)
2. `jadwal.txt` menyimpan data jadwal latihan sepak bola 
3. `absensi.txt` menyimpan data kehadiran dari pemain

   
## Struktur Folder
```
ProFootballTrain/
├── 

```
    
## Fitur untuk Pemain
**1. Lihat Jadwal Latihan**
Melihat jadwal yang telah diatur oleh admin berdasarkan tim masing-masing

**2. Cek Absensi**
Melihat data kehadiran tiap sesi latihan dan akumulasi kehadiran dari seluruh sesi latihan yang telah dilaksanakan


## Fitur untuk Pelatih
**1. Tambah Jadwal**
Menambahkan jadwal dengan informasi `Nama Latihan`, `Tanggal`, `Waktu`, `Tim`, `Lokasi`, `Status`, dan `Deskripsi`

**2. Lihat Jadwal**
Menampilkan seluruh jadwal yang telah ditambahkan di laman Jadwal Latihan dan Dashboard

**3. Edit Jadwal**
Memilih jadwal untuk mengubah `Nama Latihan`, `Tanggal`, `Waktu`, `Tim`, `Lokasi`, `Status`, dan `Deskripsi`

**4. Hapus Jadwal**
Menghapus jadwal dari data yang tersimpan

**5. Absensi**
Menginput data kehadiran dan mengedit kehadiran pemain tiap sesi latihan





```
    
## Fitur untuk Pemain
**1. Lihat Jadwal Latihan**
>>>>>>> 74cf614513f5087be5a1b143e2d2199873c2db69
