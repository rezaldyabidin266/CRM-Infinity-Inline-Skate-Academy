ALTER TABLE progress_template_items
ADD COLUMN category VARCHAR(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'FISIK'
AFTER kompetensi;

UPDATE progress_template_items
SET category = 'FISIK'
WHERE category IS NULL OR TRIM(category) = '';
