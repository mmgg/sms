-- ==========================================================
-- 多租户与逻辑删除迁移
-- 适用数据库：MySQL 8.x
-- ==========================================================

-- 1. 租户编码：登录时必须显式指定诊所，避免不同诊所手机号冲突。
ALTER TABLE `t_tenants`
    ADD COLUMN `code` varchar(64) NOT NULL DEFAULT '' COMMENT '诊所登录编码' AFTER `id`;

INSERT INTO `t_tenants` (`id`, `code`, `name`, `status`, `create_time`, `update_time`)
VALUES (1, 'default', '默认诊所', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    `code` = IF(`code` = '', 'default', `code`),
    `name` = IF(`name` = '', '默认诊所', `name`),
    `status` = 1,
    `update_time` = NOW();

INSERT INTO `t_tenants` (`id`, `code`, `name`, `status`, `create_time`, `update_time`)
VALUES (9, 'clinic-9', '诊所9', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    `code` = IF(`code` = '', 'clinic-9', `code`),
    `name` = IF(`name` = '', '诊所9', `name`),
    `status` = 1,
    `update_time` = NOW();

UPDATE `t_tenants`
SET `code` = CONCAT('clinic-', `id`)
WHERE `code` IS NULL OR `code` = '';

CREATE UNIQUE INDEX `uk_tenant_code` ON `t_tenants` (`code`);

-- 2. 用户表增加租户、权限角色和逻辑删除字段。
ALTER TABLE `t_user`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '所属诊所' AFTER `id`,
    ADD COLUMN `role` tinyint NOT NULL DEFAULT 20 COMMENT '权限角色 1平台管理员 10诊所管理员 20普通员工' AFTER `type`,
    ADD COLUMN `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否 1是' AFTER `status`;

UPDATE `t_user`
SET `tenant_id` = CASE
    WHEN `tid` IS NULL OR `tid` = 0 THEN 1
    ELSE `tid`
END;

UPDATE `t_user`
SET `deleted` = 1
WHERE `status` = -1;

UPDATE `t_user`
SET `status` = 1
WHERE `status` = 0 AND `deleted` = 0;

-- 3. 业务表统一增加 tenant_id 和 deleted。
ALTER TABLE `t_patient`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '所属诊所' AFTER `id`,
    ADD COLUMN `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否 1是' AFTER `status`;

ALTER TABLE `t_physic`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '所属诊所' AFTER `id`,
    ADD COLUMN `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否 1是' AFTER `status`;

ALTER TABLE `t_parchase_batch`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '所属诊所' AFTER `id`,
    ADD COLUMN `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否 1是' AFTER `status`;

ALTER TABLE `t_parchase_detail`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '所属诊所' AFTER `id`,
    ADD COLUMN `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否 1是' AFTER `status`;

ALTER TABLE `t_perscription`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '所属诊所' AFTER `id`,
    ADD COLUMN `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否 1是' AFTER `status`;

ALTER TABLE `t_prescription_physic`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '所属诊所' AFTER `id`,
    ADD COLUMN `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否 1是' AFTER `status`;

ALTER TABLE `t_selling_price`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '所属诊所' AFTER `id`,
    ADD COLUMN `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否 1是' AFTER `status`;

-- 4. 根据现有业务关系回填 tenant_id。
UPDATE `t_patient` SET `tenant_id` = 1 WHERE `tenant_id` = 0;
UPDATE `t_physic` SET `tenant_id` = 1 WHERE `tenant_id` = 0;

UPDATE `t_selling_price` sp
JOIN `t_physic` p ON p.`id` = sp.`physic`
SET sp.`tenant_id` = p.`tenant_id`
WHERE sp.`tenant_id` = 0;

UPDATE `t_parchase_batch` b
JOIN `t_user` u ON u.`id` = b.`user`
SET b.`tenant_id` = u.`tenant_id`
WHERE b.`tenant_id` = 0;

UPDATE `t_parchase_detail` d
JOIN `t_parchase_batch` b ON b.`id` = d.`batch`
SET d.`tenant_id` = b.`tenant_id`
WHERE d.`tenant_id` = 0;

UPDATE `t_perscription` p
JOIN `t_patient` patient ON patient.`id` = p.`patient`
SET p.`tenant_id` = patient.`tenant_id`
WHERE p.`tenant_id` = 0;

UPDATE `t_prescription_physic` pp
JOIN `t_perscription` p ON p.`id` = pp.`prescription`
SET pp.`tenant_id` = p.`tenant_id`
WHERE pp.`tenant_id` = 0;

UPDATE `t_parchase_batch` SET `tenant_id` = 1 WHERE `tenant_id` = 0;
UPDATE `t_parchase_detail` SET `tenant_id` = 1 WHERE `tenant_id` = 0;
UPDATE `t_perscription` SET `tenant_id` = 1 WHERE `tenant_id` = 0;
UPDATE `t_prescription_physic` SET `tenant_id` = 1 WHERE `tenant_id` = 0;
UPDATE `t_selling_price` SET `tenant_id` = 1 WHERE `tenant_id` = 0;

-- 5. 旧版本用 status=-1 表示删除，迁移为统一的 deleted 字段。
-- 售价表的 status=-1 表示历史价格版本，不视为逻辑删除，否则历史处方无法读取价格。
UPDATE `t_patient` SET `deleted` = 1 WHERE `status` = -1;
UPDATE `t_physic` SET `deleted` = 1 WHERE `status` = -1;
UPDATE `t_parchase_batch` SET `deleted` = 1 WHERE `status` = -1;
UPDATE `t_parchase_detail` SET `deleted` = 1 WHERE `status` = -1;
UPDATE `t_perscription` SET `deleted` = 1 WHERE `status` = -1;
UPDATE `t_prescription_physic` SET `deleted` = 1 WHERE `status` = -1;

-- 修复历史一致性：主处方已删除时，其明细不应继续参与报表，也不应继续占用库存。
UPDATE `t_prescription_physic` pp
JOIN `t_perscription` p ON p.`id` = pp.`prescription`
SET pp.`deleted` = 1
WHERE p.`deleted` = 1;

UPDATE `t_parchase_detail` pd
LEFT JOIN `t_prescription_physic` pp ON pp.`id` = pd.`prescription_physic`
SET pd.`status` = 1,
    pd.`prescription_physic` = 0
WHERE pd.`deleted` = 0
  AND pd.`status` = 2
  AND (pd.`prescription_physic` = 0 OR pp.`id` IS NULL OR pp.`deleted` = 1);

-- 6. 每个诊所至少保留一个管理员；用户 1 作为平台管理员用于创建新诊所。
UPDATE `t_user` SET `role` = 20;

UPDATE `t_user` u
JOIN (
    SELECT `tenant_id`, MIN(`id`) AS `admin_id`
    FROM `t_user`
    WHERE `deleted` = 0 AND `id` <> 1
    GROUP BY `tenant_id`
) admins ON admins.`admin_id` = u.`id`
SET u.`role` = 10;

UPDATE `t_user` SET `role` = 1 WHERE `id` = 1;

-- 7. 索引用于租户隔离和常用状态查询。
CREATE INDEX `idx_user_tenant` ON `t_user` (`tenant_id`, `deleted`, `status`);
CREATE INDEX `idx_patient_tenant` ON `t_patient` (`tenant_id`, `deleted`, `status`);
CREATE INDEX `idx_physic_tenant` ON `t_physic` (`tenant_id`, `deleted`, `status`);
CREATE INDEX `idx_purchase_batch_tenant` ON `t_parchase_batch` (`tenant_id`, `deleted`, `status`);
CREATE INDEX `idx_purchase_detail_tenant` ON `t_parchase_detail` (`tenant_id`, `deleted`, `physic`, `status`);
CREATE INDEX `idx_prescription_tenant` ON `t_perscription` (`tenant_id`, `deleted`, `patient`, `status`);
CREATE INDEX `idx_prescription_physic_tenant` ON `t_prescription_physic` (`tenant_id`, `deleted`, `prescription`, `status`);
CREATE INDEX `idx_selling_price_tenant` ON `t_selling_price` (`tenant_id`, `deleted`, `physic`, `status`);
