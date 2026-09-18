-- ==========================================================
-- 租户授权有效期
-- ==========================================================

ALTER TABLE `t_tenants`
    ADD COLUMN `license_start_time` datetime DEFAULT NULL COMMENT '授权开始时间' AFTER `status`,
    ADD COLUMN `license_end_time` datetime DEFAULT NULL COMMENT '授权结束时间' AFTER `license_start_time`,
    ADD COLUMN `license_warning_days` int NOT NULL DEFAULT 30 COMMENT '到期前提醒天数' AFTER `license_end_time`;

UPDATE `t_tenants`
SET `license_start_time` = COALESCE(`license_start_time`, NOW()),
    `license_end_time` = COALESCE(`license_end_time`, DATE_ADD(NOW(), INTERVAL 1 YEAR)),
    `license_warning_days` = COALESCE(`license_warning_days`, 30);

ALTER TABLE `t_tenants`
    MODIFY COLUMN `license_start_time` datetime NOT NULL COMMENT '授权开始时间',
    MODIFY COLUMN `license_end_time` datetime NOT NULL COMMENT '授权结束时间';
