-- ==========================================================
-- 租户操作审计日志
-- ==========================================================

CREATE TABLE IF NOT EXISTS `t_audit_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(100) DEFAULT NULL COMMENT '操作人姓名',
  `operation` varchar(255) NOT NULL COMMENT '操作名称',
  `request_method` varchar(16) NOT NULL COMMENT '请求方法',
  `request_uri` varchar(512) NOT NULL COMMENT '请求地址',
  `parameters` longtext COMMENT '请求参数',
  `success` tinyint NOT NULL DEFAULT 0 COMMENT '是否成功 0否 1是',
  `error_message` varchar(1024) DEFAULT NULL COMMENT '失败原因',
  `client_ip` varchar(64) DEFAULT NULL COMMENT '客户端IP',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_audit_tenant_time` (`tenant_id`, `deleted`, `create_time`),
  KEY `idx_audit_operator_time` (`tenant_id`, `operator_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='操作审计日志';
