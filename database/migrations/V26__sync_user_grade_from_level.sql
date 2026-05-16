UPDATE users u
JOIN levels l ON l.uuid = u.level_uuid
SET u.grade_uuid = l.grade_uuid
WHERE u.level_uuid IS NOT NULL
  AND (u.grade_uuid IS NULL OR u.grade_uuid <> l.grade_uuid);
