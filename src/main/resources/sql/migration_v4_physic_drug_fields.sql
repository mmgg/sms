-- ==========================================================
-- 药品专业字段，来源：ShowAPI 66-24
-- 适用数据库：MySQL 8.x
-- ==========================================================

ALTER TABLE `t_physic`
    ADD COLUMN `spec` varchar(255) DEFAULT NULL COMMENT '规格' AFTER `barcode`,
    ADD COLUMN `trademark` varchar(255) DEFAULT NULL COMMENT '品牌名称/商标' AFTER `spec`,
    ADD COLUMN `manufacturer_address` varchar(512) DEFAULT NULL COMMENT '生产地址' AFTER `manufacturer`,
    ADD COLUMN `approval_number` varchar(128) DEFAULT NULL COMMENT '批准文号' AFTER `manufacturer_address`,
    ADD COLUMN `dosage` text DEFAULT NULL COMMENT '用法用量' AFTER `approval_number`,
    ADD COLUMN `indications` text DEFAULT NULL COMMENT '功能主治/适用范围' AFTER `dosage`,
    ADD COLUMN `main_ingredients` text DEFAULT NULL COMMENT '主要成分' AFTER `indications`,
    ADD COLUMN `contraindications` text DEFAULT NULL COMMENT '禁忌' AFTER `main_ingredients`,
    ADD COLUMN `precautions` text DEFAULT NULL COMMENT '注意事项' AFTER `contraindications`,
    ADD COLUMN `storage_condition` varchar(512) DEFAULT NULL COMMENT '贮藏条件' AFTER `precautions`,
    ADD COLUMN `validity_period` varchar(255) DEFAULT NULL COMMENT '有效期' AFTER `storage_condition`,
    ADD COLUMN `characteristics` varchar(512) DEFAULT NULL COMMENT '性状' AFTER `validity_period`,
    ADD COLUMN `other_notes` text DEFAULT NULL COMMENT '其它注意事项' AFTER `characteristics`,
    ADD COLUMN `note` text DEFAULT NULL COMMENT '备注' AFTER `other_notes`,
    ADD COLUMN `image_url` varchar(1024) DEFAULT NULL COMMENT '药品图片地址' AFTER `note`,
    ADD COLUMN `otc_type` tinyint DEFAULT NULL COMMENT 'OTC类型 1:OTC 2:非药品' AFTER `image_url`,
    ADD COLUMN `source_api` varchar(32) DEFAULT NULL COMMENT '数据来源接口' AFTER `otc_type`,
    ADD COLUMN `source_synced_at` datetime DEFAULT NULL COMMENT '外部数据同步时间' AFTER `source_api`,
    ADD COLUMN `source_payload` longtext DEFAULT NULL COMMENT '外部接口原始响应' AFTER `source_synced_at`,
    ADD COLUMN `data_verified` tinyint NOT NULL DEFAULT 0 COMMENT '是否已人工确认' AFTER `source_payload`;
