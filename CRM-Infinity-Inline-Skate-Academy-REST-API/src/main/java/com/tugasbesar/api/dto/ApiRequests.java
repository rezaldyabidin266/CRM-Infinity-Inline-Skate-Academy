package com.tugasbesar.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

public final class ApiRequests {
    private ApiRequests() {
    }

    public static class LoginRequest {
        @Schema(description = "Username atau email", example = "adam")
        private String usernameAtauEmail;
        @Schema(description = "Password akun", example = "rahasia123")
        private String password;

        public String getUsernameAtauEmail() {
            return usernameAtauEmail;
        }

        public void setUsernameAtauEmail(String usernameAtauEmail) {
            this.usernameAtauEmail = usernameAtauEmail;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class RegisterRequest {
        @Schema(description = "Nama Lengkap", example = "Adam Yasin")
        private String namaLengkap;
        @Schema(description = "Username", example = "adam")
        private String username;
        @Schema(description = "Email", example = "adam@example.com")
        private String email;
        @Schema(description = "Password", example = "rahasia123")
        private String password;
        @Schema(description = "Konfirmasi Password", example = "rahasia123")
        private String konfirmasiPassword;
        @Schema(description = "Role", example = "Murid")
        private String role;

        public String getNamaLengkap() {
            return namaLengkap;
        }

        public void setNamaLengkap(String namaLengkap) {
            this.namaLengkap = namaLengkap;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getKonfirmasiPassword() {
            return konfirmasiPassword;
        }

        public void setKonfirmasiPassword(String konfirmasiPassword) {
            this.konfirmasiPassword = konfirmasiPassword;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    public static class UserRequest {
        @Schema(description = "Nama Lengkap", example = "Adam Yasin")
        private String namaLengkap;
        @Schema(description = "Username", example = "adam")
        private String username;
        @Schema(description = "Email", example = "adam@example.com")
        private String email;
        @Schema(description = "Password", example = "rahasia123")
        private String password;
        @Schema(description = "Role UUID", example = "ROLE_MURID")
        private String roleUuid;
        @Schema(description = "Level UUID", example = "level-basic-uuid")
        private String levelUuid;
        @Schema(description = "Grade UUID", example = "grade-1-uuid")
        private String gradeUuid;
        @Schema(description = "Status Aktif", example = "true")
        private boolean statusAktif = true;

        public String getNamaLengkap() {
            return namaLengkap;
        }

        public void setNamaLengkap(String namaLengkap) {
            this.namaLengkap = namaLengkap;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRoleUuid() {
            return roleUuid;
        }

        public void setRoleUuid(String roleUuid) {
            this.roleUuid = roleUuid;
        }

        public String getLevelUuid() {
            return levelUuid;
        }

        public void setLevelUuid(String levelUuid) {
            this.levelUuid = levelUuid;
        }

        public String getGradeUuid() {
            return gradeUuid;
        }

        public void setGradeUuid(String gradeUuid) {
            this.gradeUuid = gradeUuid;
        }

        public boolean isStatusAktif() {
            return statusAktif;
        }

        public void setStatusAktif(boolean statusAktif) {
            this.statusAktif = statusAktif;
        }
    }

    public static class RolePermissionRequest {
        @Schema(description = "Kode Module", example = "master_user")
        private String moduleCode;
        @Schema(example = "true")
        private boolean canView;
        @Schema(example = "true")
        private boolean canCreate;
        @Schema(example = "true")
        private boolean canUpdate;
        @Schema(example = "false")
        private boolean canDelete;
        @Schema(example = "false")
        private boolean canExport;
        @Schema(example = "false")
        private boolean canImport;

        public String getModuleCode() {
            return moduleCode;
        }

        public void setModuleCode(String moduleCode) {
            this.moduleCode = moduleCode;
        }

        public boolean isCanView() {
            return canView;
        }

        public void setCanView(boolean canView) {
            this.canView = canView;
        }

        public boolean isCanCreate() {
            return canCreate;
        }

        public void setCanCreate(boolean canCreate) {
            this.canCreate = canCreate;
        }

        public boolean isCanUpdate() {
            return canUpdate;
        }

        public void setCanUpdate(boolean canUpdate) {
            this.canUpdate = canUpdate;
        }

        public boolean isCanDelete() {
            return canDelete;
        }

        public void setCanDelete(boolean canDelete) {
            this.canDelete = canDelete;
        }

        public boolean isCanExport() {
            return canExport;
        }

        public void setCanExport(boolean canExport) {
            this.canExport = canExport;
        }

        public boolean isCanImport() {
            return canImport;
        }

        public void setCanImport(boolean canImport) {
            this.canImport = canImport;
        }
    }

    public static class RoleRequest {
        @Schema(description = "Code Role", example = "ROLE_MANAGER")
        private String kodeRole;
        @Schema(description = "Nama Role", example = "Manager")
        private String namaRole;
        @Schema(description = "Deskripsi Role", example = "Role untuk manager operasional")
        private String deskripsiRole;
        @Schema(description = "Hak akses module")
        private List<RolePermissionRequest> hakAksesModules = new ArrayList<RolePermissionRequest>();

        public String getKodeRole() {
            return kodeRole;
        }

        public void setKodeRole(String kodeRole) {
            this.kodeRole = kodeRole;
        }

        public String getNamaRole() {
            return namaRole;
        }

        public void setNamaRole(String namaRole) {
            this.namaRole = namaRole;
        }

        public String getDeskripsiRole() {
            return deskripsiRole;
        }

        public void setDeskripsiRole(String deskripsiRole) {
            this.deskripsiRole = deskripsiRole;
        }

        public List<RolePermissionRequest> getHakAksesModules() {
            return hakAksesModules;
        }

        public void setHakAksesModules(List<RolePermissionRequest> hakAksesModules) {
            this.hakAksesModules = hakAksesModules;
        }
    }

    public static class LevelRequest {
        @Schema(description = "Nama Level", example = "Basic")
        private String namaLevel;
        @Schema(description = "Deskripsi Level", example = "Level dasar untuk murid baru")
        private String deskripsiLevel;
        @Schema(description = "Grade UUID", example = "grade-1-uuid")
        private String gradeUuid;

        public String getNamaLevel() {
            return namaLevel;
        }

        public void setNamaLevel(String namaLevel) {
            this.namaLevel = namaLevel;
        }

        public String getDeskripsiLevel() {
            return deskripsiLevel;
        }

        public void setDeskripsiLevel(String deskripsiLevel) {
            this.deskripsiLevel = deskripsiLevel;
        }

        public String getGradeUuid() {
            return gradeUuid;
        }

        public void setGradeUuid(String gradeUuid) {
            this.gradeUuid = gradeUuid;
        }
    }

    public static class GradeRequest {
        @Schema(description = "Nama Grade", example = "Grade 1")
        private String namaGrade;
        @Schema(description = "Deskripsi Grade", example = "Grade dasar")
        private String deskripsiGrade;
        @Schema(description = "Nilai Grade", example = "1")
        private String nilaiGrade;

        public String getNamaGrade() {
            return namaGrade;
        }

        public void setNamaGrade(String namaGrade) {
            this.namaGrade = namaGrade;
        }

        public String getDeskripsiGrade() {
            return deskripsiGrade;
        }

        public void setDeskripsiGrade(String deskripsiGrade) {
            this.deskripsiGrade = deskripsiGrade;
        }

        public String getNilaiGrade() {
            return nilaiGrade;
        }

        public void setNilaiGrade(String nilaiGrade) {
            this.nilaiGrade = nilaiGrade;
        }
    }

    public static class EquipmentRequest {
        @Schema(description = "Nama Peralatan", example = "Helm")
        private String namaPeralatan;
        @Schema(description = "Jenis Peralatan", example = "Safety Gear")
        private String jenisPeralatan;
        @Schema(description = "Ukuran", example = "M")
        private String ukuran;
        @Schema(description = "Jumlah", example = "10")
        private String jumlah;
        @Schema(description = "Kondisi", example = "Baik")
        private String kondisi;
        @Schema(description = "Status", example = "Tersedia")
        private String status;
        @Schema(description = "Catatan", example = "Disimpan di gudang utama")
        private String catatan;

        public String getNamaPeralatan() {
            return namaPeralatan;
        }

        public void setNamaPeralatan(String namaPeralatan) {
            this.namaPeralatan = namaPeralatan;
        }

        public String getJenisPeralatan() {
            return jenisPeralatan;
        }

        public void setJenisPeralatan(String jenisPeralatan) {
            this.jenisPeralatan = jenisPeralatan;
        }

        public String getUkuran() {
            return ukuran;
        }

        public void setUkuran(String ukuran) {
            this.ukuran = ukuran;
        }

        public String getJumlah() {
            return jumlah;
        }

        public void setJumlah(String jumlah) {
            this.jumlah = jumlah;
        }

        public String getKondisi() {
            return kondisi;
        }

        public void setKondisi(String kondisi) {
            this.kondisi = kondisi;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCatatan() {
            return catatan;
        }

        public void setCatatan(String catatan) {
            this.catatan = catatan;
        }
    }

    public static class AttendanceFormRequest {
        @Schema(description = "Coach UUID", example = "coach-uuid")
        private String coachUuid;
        @Schema(description = "Class (Level) UUID", example = "level-uuid")
        private String classLevelUuid;
        @Schema(description = "Tanggal", example = "2026-05-24")
        private String tanggal;
        @Schema(description = "Pertemuan", example = "1")
        private String pertemuan;
        @Schema(description = "Status Form", example = "true")
        private boolean statusFormAktif = true;
        @Schema(description = "Catatan", example = "Form latihan minggu pertama")
        private String catatan;

        public String getCoachUuid() {
            return coachUuid;
        }

        public void setCoachUuid(String coachUuid) {
            this.coachUuid = coachUuid;
        }

        public String getClassLevelUuid() {
            return classLevelUuid;
        }

        public void setClassLevelUuid(String classLevelUuid) {
            this.classLevelUuid = classLevelUuid;
        }

        public String getTanggal() {
            return tanggal;
        }

        public void setTanggal(String tanggal) {
            this.tanggal = tanggal;
        }

        public String getPertemuan() {
            return pertemuan;
        }

        public void setPertemuan(String pertemuan) {
            this.pertemuan = pertemuan;
        }

        public boolean isStatusFormAktif() {
            return statusFormAktif;
        }

        public void setStatusFormAktif(boolean statusFormAktif) {
            this.statusFormAktif = statusFormAktif;
        }

        public String getCatatan() {
            return catatan;
        }

        public void setCatatan(String catatan) {
            this.catatan = catatan;
        }
    }

    public static class AttendanceRequest {
        @Schema(description = "Form Absensi UUID", example = "form-uuid")
        private String formAbsensiUuid;
        @Schema(description = "Coach UUID", example = "coach-uuid")
        private String coachUuid;
        @Schema(description = "Murid UUID", example = "murid-uuid")
        private String muridUuid;
        @Schema(description = "Class (Level) UUID", example = "level-uuid")
        private String classLevelUuid;
        @Schema(description = "Tanggal Absensi", example = "2026-05-24")
        private String tanggalAbsensi;
        @Schema(description = "Pertemuan", example = "1")
        private int pertemuan;
        @Schema(description = "Status Absensi", example = "Hadir")
        private String statusAbsensi;
        @Schema(description = "Catatan", example = "Datang tepat waktu")
        private String catatan;

        public String getFormAbsensiUuid() {
            return formAbsensiUuid;
        }

        public void setFormAbsensiUuid(String formAbsensiUuid) {
            this.formAbsensiUuid = formAbsensiUuid;
        }

        public String getCoachUuid() {
            return coachUuid;
        }

        public void setCoachUuid(String coachUuid) {
            this.coachUuid = coachUuid;
        }

        public String getMuridUuid() {
            return muridUuid;
        }

        public void setMuridUuid(String muridUuid) {
            this.muridUuid = muridUuid;
        }

        public String getClassLevelUuid() {
            return classLevelUuid;
        }

        public void setClassLevelUuid(String classLevelUuid) {
            this.classLevelUuid = classLevelUuid;
        }

        public String getTanggalAbsensi() {
            return tanggalAbsensi;
        }

        public void setTanggalAbsensi(String tanggalAbsensi) {
            this.tanggalAbsensi = tanggalAbsensi;
        }

        public int getPertemuan() {
            return pertemuan;
        }

        public void setPertemuan(int pertemuan) {
            this.pertemuan = pertemuan;
        }

        public String getStatusAbsensi() {
            return statusAbsensi;
        }

        public void setStatusAbsensi(String statusAbsensi) {
            this.statusAbsensi = statusAbsensi;
        }

        public String getCatatan() {
            return catatan;
        }

        public void setCatatan(String catatan) {
            this.catatan = catatan;
        }
    }

    public static class LevelPaymentConfigRequest {
        @Schema(description = "Level UUID", example = "level-basic-uuid")
        private String levelUuid;
        @Schema(description = "Nominal SPP", example = "150000")
        private String nominalSpp;

        public String getLevelUuid() {
            return levelUuid;
        }

        public void setLevelUuid(String levelUuid) {
            this.levelUuid = levelUuid;
        }

        public String getNominalSpp() {
            return nominalSpp;
        }

        public void setNominalSpp(String nominalSpp) {
            this.nominalSpp = nominalSpp;
        }
    }

    public static class GradeCoachRateRequest {
        @Schema(description = "Grade UUID", example = "grade-1-uuid")
        private String gradeUuid;
        @Schema(description = "Nominal Pembayaran Coach", example = "500000")
        private String nominalPembayaranCoach;

        public String getGradeUuid() {
            return gradeUuid;
        }

        public void setGradeUuid(String gradeUuid) {
            this.gradeUuid = gradeUuid;
        }

        public String getNominalPembayaranCoach() {
            return nominalPembayaranCoach;
        }

        public void setNominalPembayaranCoach(String nominalPembayaranCoach) {
            this.nominalPembayaranCoach = nominalPembayaranCoach;
        }
    }

    public static class PaymentStatusRequest {
        @Schema(description = "Status Pembayaran", example = "true")
        private boolean statusPembayaran;
        @Schema(description = "Catatan", example = "Sudah dibayar tunai")
        private String catatan;

        public boolean isStatusPembayaran() {
            return statusPembayaran;
        }

        public void setStatusPembayaran(boolean statusPembayaran) {
            this.statusPembayaran = statusPembayaran;
        }

        public String getCatatan() {
            return catatan;
        }

        public void setCatatan(String catatan) {
            this.catatan = catatan;
        }
    }

    public static class ProgressTemplateRequest {
        @Schema(description = "Level UUID", example = "level-basic-uuid")
        private String levelUuid;
        @Schema(description = "Nama Template", example = "Form Basic")
        private String namaTemplate;
        @Schema(description = "Catatan", example = "Checklist level basic")
        private String catatan;
        @Schema(description = "Status Aktif", example = "true")
        private boolean statusAktif = true;

        public String getLevelUuid() {
            return levelUuid;
        }

        public void setLevelUuid(String levelUuid) {
            this.levelUuid = levelUuid;
        }

        public String getNamaTemplate() {
            return namaTemplate;
        }

        public void setNamaTemplate(String namaTemplate) {
            this.namaTemplate = namaTemplate;
        }

        public String getCatatan() {
            return catatan;
        }

        public void setCatatan(String catatan) {
            this.catatan = catatan;
        }

        public boolean isStatusAktif() {
            return statusAktif;
        }

        public void setStatusAktif(boolean statusAktif) {
            this.statusAktif = statusAktif;
        }
    }

    public static class ProgressItemRequest {
        @Schema(description = "Template UUID", example = "template-uuid")
        private String templateUuid;
        @Schema(description = "Kode Unit", example = "F-01")
        private String kodeUnit;
        @Schema(description = "Kompetensi", example = "Mampu menjaga keseimbangan")
        private String kompetensi;
        @Schema(description = "Kategori", example = "FISIK")
        private String kategori;
        @Schema(description = "Urutan", example = "1")
        private String urutan;
        @Schema(description = "Status Aktif", example = "true")
        private boolean statusAktif = true;

        public String getTemplateUuid() {
            return templateUuid;
        }

        public void setTemplateUuid(String templateUuid) {
            this.templateUuid = templateUuid;
        }

        public String getKodeUnit() {
            return kodeUnit;
        }

        public void setKodeUnit(String kodeUnit) {
            this.kodeUnit = kodeUnit;
        }

        public String getKompetensi() {
            return kompetensi;
        }

        public void setKompetensi(String kompetensi) {
            this.kompetensi = kompetensi;
        }

        public String getKategori() {
            return kategori;
        }

        public void setKategori(String kategori) {
            this.kategori = kategori;
        }

        public String getUrutan() {
            return urutan;
        }

        public void setUrutan(String urutan) {
            this.urutan = urutan;
        }

        public boolean isStatusAktif() {
            return statusAktif;
        }

        public void setStatusAktif(boolean statusAktif) {
            this.statusAktif = statusAktif;
        }
    }

    public static class ProgressAssessmentRequest {
        @Schema(description = "Murid UUID", example = "murid-uuid")
        private String muridUuid;
        @Schema(description = "Template UUID", example = "template-uuid")
        private String templateUuid;
        @Schema(description = "Nama Riwayat Progress", example = "Basic - Mei 2026")
        private String namaRiwayatProgress;
        @Schema(description = "Tanggal Progress", example = "2026-05-24")
        private String tanggalProgress;
        @Schema(description = "Catatan", example = "Evaluasi pekan pertama")
        private String catatan;

        public String getMuridUuid() {
            return muridUuid;
        }

        public void setMuridUuid(String muridUuid) {
            this.muridUuid = muridUuid;
        }

        public String getTemplateUuid() {
            return templateUuid;
        }

        public void setTemplateUuid(String templateUuid) {
            this.templateUuid = templateUuid;
        }

        public String getNamaRiwayatProgress() {
            return namaRiwayatProgress;
        }

        public void setNamaRiwayatProgress(String namaRiwayatProgress) {
            this.namaRiwayatProgress = namaRiwayatProgress;
        }

        public String getTanggalProgress() {
            return tanggalProgress;
        }

        public void setTanggalProgress(String tanggalProgress) {
            this.tanggalProgress = tanggalProgress;
        }

        public String getCatatan() {
            return catatan;
        }

        public void setCatatan(String catatan) {
            this.catatan = catatan;
        }
    }

    public static class ProgressChecklistItemRequest {
        @Schema(description = "Item UUID", example = "item-uuid")
        private String itemUuid;
        @Schema(description = "Kode Unit", example = "F-01")
        private String kodeUnit;
        @Schema(description = "Kompetensi", example = "Mampu menjaga keseimbangan")
        private String kompetensi;
        @Schema(description = "Kategori", example = "FISIK")
        private String kategori;
        @Schema(description = "Lolos", example = "true")
        private boolean lolos;
        @Schema(description = "Catatan", example = "Sudah stabil")
        private String catatan;

        public String getItemUuid() {
            return itemUuid;
        }

        public void setItemUuid(String itemUuid) {
            this.itemUuid = itemUuid;
        }

        public String getKodeUnit() {
            return kodeUnit;
        }

        public void setKodeUnit(String kodeUnit) {
            this.kodeUnit = kodeUnit;
        }

        public String getKompetensi() {
            return kompetensi;
        }

        public void setKompetensi(String kompetensi) {
            this.kompetensi = kompetensi;
        }

        public String getKategori() {
            return kategori;
        }

        public void setKategori(String kategori) {
            this.kategori = kategori;
        }

        public boolean isLolos() {
            return lolos;
        }

        public void setLolos(boolean lolos) {
            this.lolos = lolos;
        }

        public String getCatatan() {
            return catatan;
        }

        public void setCatatan(String catatan) {
            this.catatan = catatan;
        }
    }

    public static class ProgressChecklistSaveRequest {
        @Schema(description = "Murid UUID", example = "murid-uuid")
        private String muridUuid;
        @Schema(description = "Template UUID", example = "template-uuid")
        private String templateUuid;
        @Schema(description = "Assessment UUID", example = "assessment-uuid")
        private String assessmentUuid;
        @Schema(description = "Items checklist progress")
        private List<ProgressChecklistItemRequest> items = new ArrayList<ProgressChecklistItemRequest>();

        public String getMuridUuid() {
            return muridUuid;
        }

        public void setMuridUuid(String muridUuid) {
            this.muridUuid = muridUuid;
        }

        public String getTemplateUuid() {
            return templateUuid;
        }

        public void setTemplateUuid(String templateUuid) {
            this.templateUuid = templateUuid;
        }

        public String getAssessmentUuid() {
            return assessmentUuid;
        }

        public void setAssessmentUuid(String assessmentUuid) {
            this.assessmentUuid = assessmentUuid;
        }

        public List<ProgressChecklistItemRequest> getItems() {
            return items;
        }

        public void setItems(List<ProgressChecklistItemRequest> items) {
            this.items = items;
        }
    }
}
