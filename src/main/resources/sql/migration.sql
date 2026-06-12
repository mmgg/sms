-- ==========================================================
-- 数据库变更脚本 - 诊所管理系统
-- 适用数据库：MySQL 8.x
-- ==========================================================

-- 1. 用户认证表（存储登录密码哈希）
CREATE TABLE IF NOT EXISTS `t_user_auth` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `user_id` bigint NOT NULL COMMENT '用户 ID（关联 t_user.id）',
  `password_hash` varchar(255) NOT NULL DEFAULT '' COMMENT '密码 SHA-256 哈希值',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户认证信息';

-- 2. 为已有用户设置默认密码（密码为：123456）
-- SHA-256('123456') = 8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92
INSERT INTO `t_user_auth` (`user_id`, `password_hash`, `create_time`, `update_time`)
SELECT `id`, '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', NOW(), NOW()
FROM `t_user`
WHERE NOT EXISTS (SELECT 1 FROM `t_user_auth` WHERE `user_id` = `t_user`.`id`);
