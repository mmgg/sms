package com.clwu.sms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.clwu.sms.enums.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@TableName("t_user")
@Data
public class User extends BaseTenantEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private Integer type;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

    private Integer status;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号码")
    private String phone;

    /**
     * 系统权限角色，和医生、护士等业务类型分开维护。
     */
    private Integer role;

    /** 密码，不持久化到数据库 */
    @TableField(exist = false)
    private String password;

    public UserRoleEnum getRoleEnum() {
        return UserRoleEnum.findByCode(role);
    }

    /**
     * 供页面模板判断当前用户是否显示审计日志菜单。
     */
    public boolean isAuditViewer() {
        UserRoleEnum roleEnum = getRoleEnum();
        return roleEnum != null && roleEnum.canViewAuditLog();
    }
}
