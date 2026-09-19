-- 同一租户下，同一药品或耗材只能保留一条有效售价。
-- 先保留最近更新的一条有效售价，其余历史重复记录做逻辑删除。
UPDATE `t_selling_price` stale
LEFT JOIN (
    SELECT current_price.`id`
    FROM `t_selling_price` current_price
    LEFT JOIN `t_selling_price` newer_price
        ON newer_price.`tenant_id` = current_price.`tenant_id`
        AND newer_price.`physic` = current_price.`physic`
        AND newer_price.`status` = 1
        AND newer_price.`deleted` = 0
        AND (
            newer_price.`update_time` > current_price.`update_time`
            OR (
                newer_price.`update_time` = current_price.`update_time`
                AND newer_price.`id` > current_price.`id`
            )
        )
    WHERE current_price.`status` = 1
        AND current_price.`deleted` = 0
        AND newer_price.`id` IS NULL
) keep_price ON stale.`id` = keep_price.`id`
SET stale.`status` = -1,
    stale.`deleted` = 1
WHERE stale.`status` = 1
    AND stale.`deleted` = 0
    AND keep_price.`id` IS NULL;

-- 生成列在有效售价时保存药品ID，逻辑删除或停用后为 NULL。
-- MySQL 唯一索引允许多个 NULL，因此只限制每条有效售价唯一。
ALTER TABLE `t_selling_price`
    ADD COLUMN `active_physic` bigint
        GENERATED ALWAYS AS (
            CASE
                WHEN `status` = 1 AND `deleted` = 0 THEN `physic`
                ELSE NULL
            END
        ) STORED COMMENT '有效售价药品标识，用于唯一约束';

CREATE UNIQUE INDEX `uk_selling_price_active_tenant_physic`
    ON `t_selling_price` (`tenant_id`, `active_physic`);
