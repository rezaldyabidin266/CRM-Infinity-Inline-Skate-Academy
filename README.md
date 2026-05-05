# Java Swing Auth App

Project desktop Java versi 8 dengan Java Swing dan MySQL tanpa harus menjalankan Maven saat debug di VS Code.

## Kenapa Masih Error Walau `java -version` Jalan?

Kalau `java -version` berhasil tetapi `javac -version` gagal, berarti environment Anda baru menemukan runtime Java, belum menemukan compiler JDK.

Project ini butuh:

- `java` untuk menjalankan aplikasi
- `javac` untuk compile source code
- idealnya `JAVA_HOME` mengarah ke folder JDK 8

Cek cepat:

```bat
java -version
javac -version
where java
where javac
echo %JAVA_HOME%
```

Kalau `javac` tidak ketemu, biasanya penyebabnya salah satu ini:

- yang masuk `PATH` hanya runtime Java
- `JAVA_HOME` belum diset
- VS Code masih membaca runtime lama dan perlu di-reload

## Fitur

- Login dengan username atau email
- Register user baru
- Dashboard kosong sebagai placeholder
- Auto create table `users` saat aplikasi dijalankan
- Struktur tabel user dibuat umum agar mudah dikembangkan

## Struktur Tabel User

- `id`
- `full_name`
- `username`
- `email`
- `password_hash`
- `role`
- `is_active`
- `last_login_at`
- `created_at`
- `updated_at`

## Konfigurasi Database

Default koneksi:

- Host: `localhost`
- Port: `3306`
- Database: `javafx_auth_app`
- User: `root`
- Password: kosong

Jika ingin mengganti, jalankan aplikasi dengan system property atau environment variable:

- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`

## Menjalankan Tanpa Maven

Gunakan JDK 8 untuk menjalankan aplikasi Swing ini.

### Langkah

1. Pastikan `javac -version` berhasil, bukan hanya `java -version`
2. Siapkan file JAR dependency lalu simpan ke folder `lib/`
3. Buka folder project ini di VS Code
4. Install extension `Extension Pack for Java` jika belum ada
5. Jalankan perintah `Java: Clean Java Language Server Workspace` jika library baru saja ditambahkan atau Java baru diinstall
6. Klik `Run and Debug`
7. Pilih konfigurasi `Run MainApp`

```bash
Start Debugging -> Run MainApp
```

Catatan:
- Project ini tidak lagi membutuhkan JavaFX.
- Password sekarang memakai hash `SHA-256` bawaan Java, jadi tidak perlu library BCrypt.
- Yang masih wajib hanya driver MySQL Connector/J karena JDBC MySQL tidak ada bawaan JDK.
- Workspace ini juga sudah punya task `Build Java App` dan `Run Java App`.
- File `build.bat` dan `run.bat` sekarang mencoba mencari JDK otomatis di lokasi instalasi umum Temurin/Adoptium jika `PATH` belum rapi.

Jika database belum ada, aplikasi akan mencoba membuat database dan tabel otomatis. File SQL manual juga tersedia di `database/schema.sql`.
