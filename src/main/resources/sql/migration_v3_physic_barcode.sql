-- ==========================================================
-- 药品/耗材条码支持
-- 适用数据库：MySQL 8.x
-- ==========================================================

ALTER TABLE `t_physic`
    ADD COLUMN `barcode` varchar(128) DEFAULT NULL COMMENT '药品/耗材条码' AFTER `alias`;

-- MySQL 唯一索引允许多个 NULL，因此未维护条码的旧数据不会互相冲突。
CREATE UNIQUE INDEX `uk_physic_tenant_barcode`
    ON `t_physic` (`tenant_id`, `barcode`);
