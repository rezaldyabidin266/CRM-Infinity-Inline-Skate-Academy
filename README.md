# Java Swing Auth App

Project desktop Java versi 8 dengan Java Swing dan MySQL tanpa harus menjalankan Maven saat debug di VS Code.

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

1. Siapkan file JAR MySQL Connector/J lalu simpan ke folder `lib/`
2. Buka folder project ini di VS Code
3. Jalankan perintah `Java: Clean Java Language Server Workspace` jika library baru saja ditambahkan
4. Klik `Run and Debug`
5. Pilih konfigurasi `MainApp`

```bash
Start Debugging -> MainApp
```

Catatan:
- Project ini tidak lagi membutuhkan JavaFX.
- Password sekarang memakai hash `SHA-256` bawaan Java, jadi tidak perlu library BCrypt.
- Yang masih wajib hanya driver MySQL Connector/J karena JDBC MySQL tidak ada bawaan JDK.

Jika database belum ada, aplikasi akan mencoba membuat database dan tabel otomatis. File SQL manual juga tersedia di `database/schema.sql`.
