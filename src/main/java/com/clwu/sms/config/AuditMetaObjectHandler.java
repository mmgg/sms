package com.clwu.sms.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.clwu.sms.tenant.CurrentUserContext;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 统一填充创建人、更新人、创建时间和更新时间。
 */
@Component
public class AuditMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Long operatorId = CurrentUserContext.getUserIdOrSystem();
        LocalDateTime now = LocalDateTime.now();
        strictInsertFill(metaObject, "createUser", Long.class, operatorId);
        strictInsertFill(metaObject, "updateUser", Long.class, operatorId);
        strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        strictUpdateFill(metaObject, "updateUser", Long.class,
                CurrentUserContext.getUserIdOrSystem());
        strictUpdateFill(metaObject, "updateTime", LocalDateTime.class,
                LocalDateTime.now());
    }
}
