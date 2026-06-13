UPDATE modules
SET name = 'Role',
    description = 'Mengatur hak akses module yang tampil di navbar berdasarkan role.',
    sort_order = 3
WHERE code = 'ROLE_MODULE';

UPDATE modules
SET sort_order = 4
WHERE code = 'LAPORAN';

UPDATE modules
SET sort_order = 5
WHERE code = 'PENGATURAN';
